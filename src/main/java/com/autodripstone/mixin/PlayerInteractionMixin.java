package com.autodripstone.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.autodripstone.handler.DripstoneAutoFarmerHandler;

@Mixin(PlayerEntity.class)
public class PlayerInteractionMixin {
	
	@Inject(method = "tick", at = @At("HEAD"))
	private void onPlayerTick(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity) (Object) this;
		
		// Check if player is actively using item (holding right click)
		if (player.isUsingItem() && player.getActiveHand() == Hand.MAIN_HAND) {
			DripstoneAutoFarmerHandler.setPlayerRightClickState(player, true);
		} else {
			DripstoneAutoFarmerHandler.setPlayerRightClickState(player, false);
		}
	}
}
