package me.flourick.fvt.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;

/**
 * FEATURES: AutoReconnect
 * 
 * @author Flourick
 */
@Mixin(DisconnectedScreen.class)
abstract class DisconnectedScreenMixin
{
	@Shadow
	private int reasonHeight;

	@Inject(method = "init", at = @At("HEAD"))
	private void onInit(CallbackInfo info)
	{
		if(FVT.OPTIONS.autoReconnect.getValue()) {
			FVT.VARS.autoReconnectTicks = 100;
			FVT.VARS.autoReconnectAttempts += 1;
		}
	}

	@Inject(method = "render", at = @At("RETURN"))
	private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoReconnect.getValue() && FVT.VARS.autoReconnectAttempts > 0 && (FVT.VARS.autoReconnectAttempts <= FVT.OPTIONS.autoReconnectAttempts.getValue() || FVT.OPTIONS.autoReconnectAttempts.getValue() == -1)) {
			if(FVT.OPTIONS.autoReconnectAttempts.getValue() != -1) {
                DrawableHelper.drawCenteredTextWithShadow(matrices, FVT.MC.textRenderer, Text.translatable("fvt.feature.name.auto_reconnect.message", MathHelper.ceil(FVT.VARS.autoReconnectTicks / 20f), ((FVT.OPTIONS.autoReconnectAttempts.getValue() + 1) - FVT.VARS.autoReconnectAttempts)).getString(), FVT.MC.currentScreen.width / 2, FVT.MC.currentScreen.height - this.reasonHeight / 2 - 2 * FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
            }
            else {
                DrawableHelper.drawCenteredTextWithShadow(matrices, FVT.MC.textRenderer, Text.translatable("fvt.feature.name.auto_reconnect.message_no_max", MathHelper.ceil(FVT.VARS.autoReconnectTicks / 20f)), FVT.MC.currentScreen.width / 2, FVT.MC.currentScreen.height - this.reasonHeight / 2 - 2 * FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
            }
		}
	}
}