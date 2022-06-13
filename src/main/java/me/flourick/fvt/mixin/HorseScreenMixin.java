package me.flourick.fvt.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.FVTButtonWidget;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Matrix4f;

/**
 * FEATURES: Horse Info
 * 
 * @author Flourick
 */
@Mixin(HorseScreen.class)
abstract class HorseScreenMixin extends HandledScreen<HorseScreenHandler>
{
	@Final
	@Shadow
	private HorseBaseEntity entity;

	List<OrderedText> FVT_tooltip;

	@Override
	protected void init()
	{
		super.init();

		if(!FVT.OPTIONS.horseStats.getValueRaw()) {
			return;
		}

		Text header = new TranslatableText("fvt.feature.name.horse_stats.button");

		int buttonHeight = 14;
		int buttonWidth = FVT.MC.textRenderer.getWidth(header) + 8;

		FVT_tooltip = new ArrayList<>();
		FVT_tooltip.add(new TranslatableText("fvt.feature.name.horse_stats.button.tooltip.health", FVT_getHorseHealth()).asOrderedText());
		FVT_tooltip.add(new TranslatableText("fvt.feature.name.horse_stats.button.tooltip.speed", FVT_getHorseSpeed()).asOrderedText());
		FVT_tooltip.add(new TranslatableText("fvt.feature.name.horse_stats.button.tooltip.jump_height", FVT_getHorseJumpHeight()).asOrderedText());

		int baseX = ((this.width - this.backgroundWidth) / 2) + this.backgroundWidth - buttonWidth - 7;
		int baseY = ((this.height - this.backgroundHeight) / 2) - 12;

		FVTButtonWidget button = new FVTButtonWidget(baseX, baseY, buttonWidth, buttonHeight, header, null
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderOrderedTooltip(matrixStack, FVT_tooltip, i, j - 8);
		});
		button.active = false;

		this.addDrawableChild(button);
	}

	private String FVT_getHorseHealth()
	{
		double horseHealth = entity.getMaxHealth();
		return String.format("%s%.0f", FVT_getColorCodeByBounds(15.0D, 30.0D, horseHealth), horseHealth);
	}

	private String FVT_getHorseSpeed()
	{
		double horseSpeedBlocks = entity.getAttributes().getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.157787584D;
		return String.format("%s%.02f", FVT_getColorCodeByBounds(4.742751103D, 14.228253309D, horseSpeedBlocks), horseSpeedBlocks);
	}

	private String FVT_getHorseJumpHeight()
	{
		double jumpStrength = entity.getJumpStrength();
		double jumpStrengthBlocks = -0.1817584952D * jumpStrength * jumpStrength * jumpStrength + 3.689713992D * jumpStrength * jumpStrength + 2.128599134D * jumpStrength - 0.343930367D;
		return String.format("%s%.02f", FVT_getColorCodeByBounds(1.08623D, 5.29262D, jumpStrengthBlocks), jumpStrengthBlocks);
	}

	private String FVT_getColorCodeByBounds(double min, double max, double value)
	{
		double third = (max - min) / 3.0D;

		if(value + 2*third < max) {
			return "§c";
		}
		else if(value + third < max) {
			return "§6";
		}
		else {
			return "§a";
		}
	}

	@Override
	public void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y)
	{
		// basically a copy paste just to adjust some annoying spacing, yeah
		if(!lines.isEmpty() && lines.size() > 2) {
			List<TooltipComponent> components = lines.stream().map(TooltipComponent::of).collect(Collectors.toList());

			TooltipComponent tooltipComponent2;
			int s;
			int k;
			if(components.isEmpty()) {
				return;
			}

			int i = 0;
			int j = components.size() == 1 ? -2 : 0;
			for(TooltipComponent tooltipComponent : components) {
				k = tooltipComponent.getWidth(this.textRenderer);
				if (k > i) {
					i = k;
				}
				j += tooltipComponent.getHeight();
			}
			int l = x + 12;
			int tooltipComponent = y - 12;
			k = i;
			int m = j;

			if(l + i > this.width) {
				l -= 28 + i;
			}

			if(tooltipComponent + m + 6 > this.height) {
				tooltipComponent = this.height - m - 6;
			}

			matrices.push();
			float f = this.itemRenderer.zOffset;
			this.itemRenderer.zOffset = 400.0f;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 4, l + k + 3, tooltipComponent - 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent + m + 3, l + k + 3, tooltipComponent + m + 4, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 3, l + k + 3, tooltipComponent + m + 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 4, tooltipComponent - 3, l - 3, tooltipComponent + m + 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l + k + 3, tooltipComponent - 3, l + k + 4, tooltipComponent + m + 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 3 + 1, l - 3 + 1, tooltipComponent + m + 3 - 1, 400, 0x505000FF, 1344798847);
			Screen.fillGradient(matrix4f, bufferBuilder, l + k + 2, tooltipComponent - 3 + 1, l + k + 3, tooltipComponent + m + 3 - 1, 400, 0x505000FF, 1344798847);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent - 3, l + k + 3, tooltipComponent - 3 + 1, 400, 0x505000FF, 0x505000FF);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, tooltipComponent + m + 2, l + k + 3, tooltipComponent + m + 3, 400, 1344798847, 1344798847);
			RenderSystem.enableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			bufferBuilder.end();
			BufferRenderer.draw(bufferBuilder);
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrices.translate(0.0, 0.0, 400.0);

			int r = tooltipComponent;
			for(s = 0; s < components.size(); ++s) {
				tooltipComponent2 = components.get(s);
				tooltipComponent2.drawText(this.textRenderer, l, r, matrix4f, immediate);
				r += tooltipComponent2.getHeight();
			}
			immediate.draw();
			matrices.pop();
			r = tooltipComponent;
			for(s = 0; s < components.size(); ++s) {
				tooltipComponent2 = components.get(s);
				tooltipComponent2.drawItems(this.textRenderer, l, r, matrices, this.itemRenderer, 400);
				r += tooltipComponent2.getHeight();
			}
			this.itemRenderer.zOffset = f;
		}
		else {
			super.renderOrderedTooltip(matrices, lines, x, y);
		}
	}

	public HorseScreenMixin(HorseScreenHandler handler, PlayerInventory inventory, Text title) { super(handler, inventory, title); } // IGNORED
}
