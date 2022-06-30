package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;

import me.flourick.fvt.FVT;

/**
 * FEATURES: Hotbar Autohide
 * 
 * @author Flourick
 */
@Mixin(CreativeInventoryScreen.class)
abstract class CreativeInventoryScreenMixin
{
	@Inject(method = "onHotbarKeyPress", at = @At("HEAD"))
	private static void onOnHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save, CallbackInfo info)
	{
		if(restore) {
			FVT.VARS.resetHotbarLastInteractionTime();
		}
	}
}
