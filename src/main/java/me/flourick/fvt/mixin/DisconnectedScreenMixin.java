package me.flourick.fvt.mixin;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
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
        if(FVT.OPTIONS.autoReconnect.getValueRaw()) {
			FVT.VARS.autoReconnectTicks = FVT.OPTIONS.autoReconnectTimeout.getValueAsInteger() * 20;

			if(FVT.OPTIONS.autoReconnectMaxTries.getValueAsInteger() == 51) {
				FVT.VARS.autoReconnectTries = 1;
			} else {
				FVT.VARS.autoReconnectTries += 1;
			}
		}
	}
	
	@Inject(method = "render", at = @At("RETURN"))
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info)
    {
		if(FVT.OPTIONS.autoReconnect.getValueRaw() && FVT.MC.currentScreen != null) {
			if(FVT.OPTIONS.autoReconnectMaxTries.getValueAsInteger() == 51) {
				DrawableHelper.drawCenteredText(matrices, FVT.MC.textRenderer, new TranslatableText("fvt.feature.name.autoreconnect.message_nomax", MathHelper.ceil(FVT.VARS.autoReconnectTicks / 20.0f)).getString(), FVT.MC.currentScreen.width / 2, FVT.MC.currentScreen.height - this.reasonHeight / 2 - 2*FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
			} else if(FVT.VARS.autoReconnectTries <= FVT.OPTIONS.autoReconnectMaxTries.getValueAsInteger() && FVT.VARS.autoReconnectTries > 0) {
				DrawableHelper.drawCenteredText(matrices, FVT.MC.textRenderer, new TranslatableText("fvt.feature.name.autoreconnect.message", MathHelper.ceil(FVT.VARS.autoReconnectTicks / 20.0f), (FVT.OPTIONS.autoReconnectMaxTries.getValueAsInteger() + 1 - FVT.VARS.autoReconnectTries)).getString(), FVT.MC.currentScreen.width / 2, FVT.MC.currentScreen.height - this.reasonHeight / 2 - 2*FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
			}
		}
    }
}
