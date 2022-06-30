package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.MerchantScreenHandler;

import me.flourick.fvt.FVT;

/**
 * FEATURES: FastTrade
 * 
 * @author Flourick
 */
@Mixin(MerchantScreenHandler.class)
abstract class MerchantScreenHandlerMixin
{
	@Inject(method = "close", at = @At("HEAD"))
	private void onClose(PlayerEntity player, CallbackInfo info)
	{
		// reset when closing trade screen
		FVT.VARS.waitForTrade = false;
		FVT.VARS.tradeItem = null;
	}
}
