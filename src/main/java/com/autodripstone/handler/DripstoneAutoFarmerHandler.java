package com.autodripstone.handler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TrapDoorBlock;
import net.minecraft.block.PointedDripstoneBlock;
import net.minecraft.entity.player.PlayerEntity;
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
				// Spawn dripstone above trapdoor
				BlockPos dripstonePos = trapdoorPos.up();
				BlockState currentState = world.getBlockState(dripstonePos);
				
				// Only spawn if air or replaceable
				if (currentState.isAir() || currentState.getMaterial().isReplaceable()) {
					world.setBlockState(dripstonePos, Blocks.POINTED_DRIPSTONE.getDefaultState()
						.with(PointedDripstoneBlock.VERTICAL_DIRECTION, Direction.DOWN)
						.with(PointedDripstoneBlock.THICKNESS, PointedDripstoneBlock.Thickness.TIP)
					);
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

	public static void setPlayerRightClickState(PlayerEntity player, boolean isRightClicking) {
		PLAYER_RIGHT_CLICK_STATE.put(player, isRightClicking);
	}
}
