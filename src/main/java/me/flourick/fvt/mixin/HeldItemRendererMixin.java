package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import me.flourick.fvt.FVT;

/**
 * FEATURES: Invisible Offhand
 * 
 * @author Flourick
 */
@Mixin(HeldItemRenderer.class)
abstract class HeldItemRendererMixin
{
	@Inject(method = "renderFirstPersonItem", at = @At(value = "HEAD"), cancellable = true)
	public void onRenderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo info)
	{
		if(FVT.OPTIONS.invisibleOffhand.getValue() && hand == Hand.OFF_HAND) {
			info.cancel();
		}
	}
}
