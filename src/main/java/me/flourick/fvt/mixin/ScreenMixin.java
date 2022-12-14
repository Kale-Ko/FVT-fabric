package me.flourick.fvt.mixin;

import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector2ic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

/**
 * FEATURES: Container Buttons
 * 
 * @author Flourick
 */
@Mixin(Screen.class)
abstract class ScreenMixin
{
	@Shadow
	protected ItemRenderer itemRenderer;

	@Shadow
	protected TextRenderer textRenderer;

	@Inject(method = "renderTooltipFromComponents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void renderTooltipFromComponents(MatrixStack matrices, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, CallbackInfo info, int i, int j, int l, int m, int k, int n, Vector2ic vector2ic, int o, float f, Tessellator tessellator, BufferBuilder bufferBuilder, Matrix4f matrix4f, VertexConsumerProvider.Immediate immediate)
	{
		// just to adjust the first line having extra spacing for no apparent reason
		TooltipComponent tooltipComponent2;
		int p = m;

		matrices.translate(0.0f, 0.0f, 400.0f);

        for(int q = 0; q < components.size(); ++q) {
            tooltipComponent2 = components.get(q);
            tooltipComponent2.drawText(this.textRenderer, l, p, matrix4f, immediate);
            p += tooltipComponent2.getHeight();
        }

        immediate.draw();
        matrices.pop();

        p = m;
        for(int q = 0; q < components.size(); ++q) {
            tooltipComponent2 = components.get(q);
            tooltipComponent2.drawItems(this.textRenderer, l, p, matrices, this.itemRenderer, 400);
            p += tooltipComponent2.getHeight();
        }
        this.itemRenderer.zOffset = f;

		info.cancel();
	}
}
