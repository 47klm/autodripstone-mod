package com.autodripstone.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import com.autodripstone.config.ModConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class DripstoneAutoFarmerHandler {
	private static final Map<String, Integer> TRAPDOOR_TOGGLE_COUNTER = new HashMap<>();
	private static final WeakHashMap<PlayerEntity, Boolean> PLAYER_RIGHT_CLICK_STATE = new WeakHashMap<>();

	public static void register() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerWorld world : server.getWorlds()) {
				handleWorldTick(world);
			}
		});
	}

	private static void handleWorldTick(ServerWorld world) {
		ModConfig.Config config = ModConfig.getConfig();
		if (!config.enabled) return;

		world.getServer().getPlayerManager().getPlayerList().forEach(player -> {
			Boolean isRightClicking = PLAYER_RIGHT_CLICK_STATE.getOrDefault(player, false);
			if (!isRightClicking) {
				return;
			}

			BlockPos trapdoorPos = raycastTrapdoor(world, player);
			if (trapdoorPos == null) {
				return;
			}

			BlockState trapdoorState = world.getBlockState(trapdoorPos);
			if (!(trapdoorState.getBlock() instanceof TrapDoorBlock)) {
				return;
			}

			String key = trapdoorPos.getX() + "," + trapdoorPos.getY() + "," + trapdoorPos.getZ();
			int counter = TRAPDOOR_TOGGLE_COUNTER.getOrDefault(key, 0) + 1;

			if (counter >= config.trapdoorToggleSpeed) {
				// Check if player has dripstone in inventory
				if (!hasDripstoneInInventory(player)) {
					return;
				}

				// Place dripstone BELOW trapdoor
				BlockPos dripstonePos = trapdoorPos.down();
				BlockState currentState = world.getBlockState(dripstonePos);
				
				// Only place if air or replaceable
				if (currentState.isAir() || currentState.getMaterial().isReplaceable()) {
					// Place hanging dripstone
					world.setBlockState(dripstonePos, Blocks.POINTED_DRIPSTONE.getDefaultState()
						.with(PointedDripstoneBlock.VERTICAL_DIRECTION, Direction.DOWN)
						.with(PointedDripstoneBlock.THICKNESS, PointedDripstoneBlock.Thickness.TIP)
					);
					
					// Consume dripstone from player inventory
					consumeDripstoneFromInventory(player);
				}

				// Toggle trapdoor
				boolean isOpen = trapdoorState.get(TrapDoorBlock.OPEN);
				world.setBlockState(trapdoorPos, trapdoorState.with(TrapDoorBlock.OPEN, !isOpen));

				counter = 0;
			}

			TRAPDOOR_TOGGLE_COUNTER.put(key, counter);
		});
	}

	private static BlockPos raycastTrapdoor(World world, PlayerEntity player) {
		HitResult raycast = player.raycast(5.0, 0, false);
		if (raycast.getType() == HitResult.Type.BLOCK) {
			BlockPos pos = ((BlockHitResult) raycast).getBlockPos();
			BlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof TrapDoorBlock) {
				return pos;
			}
		}
		return null;
	}

	private static boolean hasDripstoneInInventory(PlayerEntity player) {
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.getItem() == Items.POINTED_DRIPSTONE && !stack.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	private static void consumeDripstoneFromInventory(PlayerEntity player) {
		Inventory inventory = player.getInventory();
		for (int i = 0; i < inventory.size(); i++) {
			ItemStack stack = inventory.getStack(i);
			if (stack.getItem() == Items.POINTED_DRIPSTONE && !stack.isEmpty()) {
				stack.decrement(1);
				return;
			}
		}
	}

	public static void setPlayerRightClickState(PlayerEntity player, boolean isRightClicking) {
		PLAYER_RIGHT_CLICK_STATE.put(player, isRightClicking);
	}
}
