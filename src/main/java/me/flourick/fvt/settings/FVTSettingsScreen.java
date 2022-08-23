package me.flourick.fvt.settings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;

/**
 * This mods settings screen.
 * 
 * @author Flourick
 */
public class FVTSettingsScreen extends Screen
{
	private final Screen parent;
	private FVTButtonListWidget list;

	private boolean tooltipsActive = false;

	// getter for ModMenu
	public static Screen getNewScreen(Screen parent)
	{
		return new FVTSettingsScreen(parent);
	}

	public FVTSettingsScreen(Screen parent)
	{
		super(Text.translatable("fvt.options_title"));
		this.parent = parent;
	}

	protected void init()
	{
		this.list = new FVTButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addOptionEntry(FVT.OPTIONS.buttonPosition, FVT.OPTIONS.featureToggleMessages);
		this.list.addCategoryEntry("fvt.feature_category.crosshair");
		this.list.addOptionEntry(FVT.OPTIONS.crosshairScale);
		this.list.addOptionEntry(FVT.OPTIONS.crosshairStaticColor);
		this.list.addOptionEntry(new SimpleOption<?>[] {FVT.OPTIONS.crosshairStaticColorRed, FVT.OPTIONS.crosshairStaticColorGreen, FVT.OPTIONS.crosshairStaticColorBlue, FVT.OPTIONS.crosshairStaticColorAlpha});
		this.list.addCategoryEntry("fvt.feature_category.hud"); 
		this.list.addOptionEntry(FVT.OPTIONS.showHUDInfo, FVT.OPTIONS.coordinatesPosition);
		this.list.addCategoryEntry("fvt.feature_category.hotbar"); 
		this.list.addOptionEntry(FVT.OPTIONS.autoHideHotbar);
		this.list.addOptionEntry(FVT.OPTIONS.autoHideHotbarTimeout, FVT.OPTIONS.autoHideHotbarMode);
		this.list.addOptionEntry(FVT.OPTIONS.autoHideHotbarUse, FVT.OPTIONS.autoHideHotbarItem);
		this.list.addCategoryEntry("fvt.feature_category.render");
		this.list.addOptionEntry(FVT.OPTIONS.noPotionParticles, FVT.OPTIONS.noBlockBreakParticles);
		this.list.addOptionEntry(FVT.OPTIONS.noNetherFog, FVT.OPTIONS.invisibleOffhand);
		this.list.addOptionEntry(FVT.OPTIONS.noVignette, FVT.OPTIONS.noSpyglassOverlay);
		this.list.addOptionEntry(FVT.OPTIONS.fullbright, FVT.OPTIONS.entityOutline);
		this.list.addOptionEntry(FVT.OPTIONS.cloudHeight, FVT.OPTIONS.damageTilt);
		this.list.addCategoryEntry("fvt.feature_category.tools");
		this.list.addOptionEntry(FVT.OPTIONS.toolWarning, FVT.OPTIONS.toolWarningPosition);
		this.list.addOptionEntry(FVT.OPTIONS.toolWarningScale);
		this.list.addOptionEntry(FVT.OPTIONS.noToolBreaking, FVT.OPTIONS.noBreakSwapStop);
		this.list.addCategoryEntry("fvt.feature_category.auto");
		this.list.addOptionEntry(FVT.OPTIONS.autoEat, FVT.OPTIONS.autoAttack);
		this.list.addOptionEntry(FVT.OPTIONS.autoTotem, FVT.OPTIONS.refillHand);
		this.list.addOptionEntry(FVT.OPTIONS.autoElytra, FVT.OPTIONS.fastTrade);
		this.list.addCategoryEntry("fvt.feature_category.placement");
		this.list.addOptionEntry(FVT.OPTIONS.randomPlacement, FVT.OPTIONS.useDelay);
		this.list.addOptionEntry(FVT.OPTIONS.creativeBreakDelay, FVT.OPTIONS.placementLock);
		this.list.addCategoryEntry("fvt.feature_category.misc");
		this.list.addOptionEntry(FVT.OPTIONS.chatHistoryLength);
		this.list.addOptionEntry(FVT.OPTIONS.disableWToSprint, FVT.OPTIONS.sendDeathCoordinates);
		this.list.addOptionEntry(FVT.OPTIONS.freecam, FVT.OPTIONS.attackThrough);
		this.list.addOptionEntry(FVT.OPTIONS.containerButtons, FVT.OPTIONS.inventoryButton);
		this.list.addOptionEntry(new SimpleOption<?>[] {FVT.OPTIONS.horseStats, null});
		this.addSelectableChild(this.list);
		
		// DEFAULTS button at the top left corner
		this.addDrawableChild(new ButtonWidget(6, 6, 55, 20, Text.translatable("fvt.options.defaults"), (buttonWidget) -> {
			FVT.OPTIONS.reset();
			this.client.setScreen(getNewScreen(parent));
		}, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, Text.translatable("fvt.options.defaults.tooltip").formatted(Formatting.YELLOW), i, j + 8);
		}));

		// TOOLTIP (?/-) button at the top right corner
		this.addDrawableChild(new ButtonWidget(this.width - 26, 6, 20, 20, Text.literal("?"), (buttonWidget) -> {
			tooltipsActive = !tooltipsActive;

			if(tooltipsActive) {
				buttonWidget.setMessage(Text.literal("-"));
			}
			else {
				buttonWidget.setMessage(Text.literal("?"));
			}
		}, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, (tooltipsActive ? Text.translatable("fvt.options.tooltips.hide") : Text.translatable("fvt.options.tooltips.show")), i, j + 8);
		}));

		// DONE button at the bottom
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.list.getBottom() + ((this.height - this.list.getBottom() - 20) / 2), 200, 20, ScreenTexts.DONE, (buttonWidget) -> {
			FVT.OPTIONS.write();
			this.client.setScreen(parent);
		}));
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrixStack);
		this.list.render(matrixStack, mouseX, mouseY, delta);
		drawCenteredText(matrixStack, this.textRenderer, this.title, this.width / 2, 12, Color.WHITE.getPacked());

		super.render(matrixStack, mouseX, mouseY, delta);

		List<OrderedText> tooltip = getHoveredButtonTooltip(mouseX, mouseY);
		if(tooltip != null && tooltipsActive) {
			this.renderOrderedTooltip(matrixStack, tooltip, mouseX, mouseY);
		}
	}

	public List<OrderedText> getHoveredButtonTooltip(int mouseX, int mouseY)
	{
		Optional<ClickableWidget> button = list.getHoveredButton((double)mouseX, (double)mouseY);
		return (button.isPresent() && button.get() instanceof OrderableTooltip ? ((OrderableTooltip)button.get()).getOrderedTooltip() : ImmutableList.of());
	}

	@Override
	public void removed()
	{
		FVT.OPTIONS.write();
	}

	@Override
	public void close()
	{
		this.client.setScreen(parent);
	}

	@Override
	public void renderOrderedTooltip(MatrixStack matrices, List<? extends OrderedText> lines, int x, int y)
	{
		// basically a copy paste just to adjust some annoying spacing, yeah
		if(!lines.isEmpty() && lines.size() > 2) {
			List<TooltipComponent> components = lines.stream().map(TooltipComponent::of).collect(Collectors.toList());
			TooltipComponent tooltipComponent2;
			int t;
			int k;
			if(components.isEmpty()) {
				return;
			}
			int i = 0;
			int j = components.size() == 1 ? -2 : 0;

			for(TooltipComponent tooltipComponent : components) {
				k = tooltipComponent.getWidth(this.textRenderer);
				if(k > i) {
					i = k;
				}
				j += tooltipComponent.getHeight();
			}

			int l = x + 12;
			int m = y - 12;
			k = i;
			int n = j;
			if(l + i > this.width) {
				l -= 28 + i;
			}
			if(m + n + 6 > this.height) {
				m = this.height - n - 6;
			}
			if(y - n - 8 < 0) {
				m = y + 8;
			}
			matrices.push();
			float f = this.itemRenderer.zOffset;
			this.itemRenderer.zOffset = 400.0f;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferBuilder = tessellator.getBuffer();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
			Matrix4f matrix4f = matrices.peek().getPositionMatrix();
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, m - 4, l + k + 3, m - 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, m + n + 3, l + k + 3, m + n + 4, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + k + 3, m + n + 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 4, m - 3, l - 3, m + n + 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l + k + 3, m - 3, l + k + 4, m + n + 3, 400, -267386864, -267386864);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, m - 3 + 1, l - 3 + 1, m + n + 3 - 1, 400, 0x505000FF, 1344798847);
			Screen.fillGradient(matrix4f, bufferBuilder, l + k + 2, m - 3 + 1, l + k + 3, m + n + 3 - 1, 400, 0x505000FF, 1344798847);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, m - 3, l + k + 3, m - 3 + 1, 400, 0x505000FF, 0x505000FF);
			Screen.fillGradient(matrix4f, bufferBuilder, l - 3, m + n + 2, l + k + 3, m + n + 3, 400, 1344798847, 1344798847);
			RenderSystem.enableDepthTest();
			RenderSystem.disableTexture();
			RenderSystem.enableBlend();
			RenderSystem.defaultBlendFunc();
			BufferRenderer.drawWithShader(bufferBuilder.end());
			RenderSystem.disableBlend();
			RenderSystem.enableTexture();
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			matrices.translate(0.0, 0.0, 400.0);
			int s = m;

			for(t = 0; t < components.size(); ++t) {
				tooltipComponent2 = components.get(t);
				tooltipComponent2.drawText(this.textRenderer, l, s, matrix4f, immediate);
				s += tooltipComponent2.getHeight() + (t == components.size()-2 ? 2 : 0);
			}
			immediate.draw();
			matrices.pop();
			s = m;
			for (t = 0; t < components.size(); ++t) {
				tooltipComponent2 = components.get(t);
				tooltipComponent2.drawItems(this.textRenderer, l, s, matrices, this.itemRenderer, 400);
				s += tooltipComponent2.getHeight() + (t == components.size()-2 ? 2 : 0);
			}
			this.itemRenderer.zOffset = f;
		}
		else {
			super.renderOrderedTooltip(matrices, lines, x, y);
		}
	}
}
