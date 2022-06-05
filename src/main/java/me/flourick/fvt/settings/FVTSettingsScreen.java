package me.flourick.fvt.settings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.Option;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Matrix4f;

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
		super(new TranslatableText("fvt.options_title"));
		this.parent = parent;
	}

	protected void init()
	{
		this.list = new FVTButtonListWidget(this.client, this.width, this.height, 32, this.height - 32, 25);
		this.list.addDualOptionEntry(FVT.OPTIONS.buttonPosition, FVT.OPTIONS.featureToggleMessages);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.crosshair"));
		this.list.addSingleOptionEntry(FVT.OPTIONS.crosshairScale);
		this.list.addSingleOptionEntry(FVT.OPTIONS.crosshairStaticColor);
		this.list.addEntry(new Option[] {FVT.OPTIONS.crosshairStaticColorRed, FVT.OPTIONS.crosshairStaticColorGreen, FVT.OPTIONS.crosshairStaticColorBlue, FVT.OPTIONS.crosshairStaticColorAlpha});
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.hud")); 
		this.list.addDualOptionEntry(FVT.OPTIONS.showInfo, FVT.OPTIONS.coordinatesPosition);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.hotbar")); 
		this.list.addSingleOptionEntry(FVT.OPTIONS.autoHideHotbar);
		this.list.addDualOptionEntry(FVT.OPTIONS.autoHideHotbarTimeout, FVT.OPTIONS.autoHideHotbarMode);
		this.list.addDualOptionEntry(FVT.OPTIONS.autoHideHotbarUse, FVT.OPTIONS.autoHideHotbarItem);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.render"));
		this.list.addDualOptionEntry(FVT.OPTIONS.noPotionParticles, FVT.OPTIONS.noBlockBreakParticles);
		this.list.addDualOptionEntry(FVT.OPTIONS.noNetherFog, FVT.OPTIONS.invisibleOffhand);
		this.list.addDualOptionEntry(FVT.OPTIONS.noVignette, FVT.OPTIONS.noSpyglassOverlay);
		this.list.addDualOptionEntry(FVT.OPTIONS.fullbright, FVT.OPTIONS.cloudHeight);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.entity_outline"));
		this.list.addSingleOptionEntry(FVT.OPTIONS.entityOutline);
		this.list.addDualOptionEntry(FVT.OPTIONS.entityOutlineAnimals, FVT.OPTIONS.entityOutlineMobs);
		this.list.addDualOptionEntry(FVT.OPTIONS.entityOutlinePlayers, FVT.OPTIONS.entityOutlineMisc);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.tools"));
		this.list.addDualOptionEntry(FVT.OPTIONS.noToolBreaking, FVT.OPTIONS.toolWarning);
		this.list.addDualOptionEntry(FVT.OPTIONS.toolWarningPosition, FVT.OPTIONS.toolWarningScale);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.auto"));
		this.list.addDualOptionEntry(FVT.OPTIONS.autoReconnect, FVT.OPTIONS.autoReconnectMaxTries);
		this.list.addSingleOptionEntry(FVT.OPTIONS.autoReconnectTimeout);
		this.list.addDualOptionEntry(FVT.OPTIONS.autoEat, FVT.OPTIONS.triggerBot);
		this.list.addDualOptionEntry(FVT.OPTIONS.autoTotem, FVT.OPTIONS.refillHand);
		this.list.addDualOptionEntry(FVT.OPTIONS.autoElytra, FVT.OPTIONS.fastTrade);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.placement"));
		this.list.addDualOptionEntry(FVT.OPTIONS.randomPlacement, FVT.OPTIONS.useDelay);
		this.list.addDualOptionEntry(FVT.OPTIONS.creativeBreakDelay, FVT.OPTIONS.placementLock);
		this.list.addSingleOptionEntry(new FTVCategoryOption("fvt.feature_category.misc"));
		this.list.addDualOptionEntry(FVT.OPTIONS.disableWToSprint, FVT.OPTIONS.sendDeathCoordinates);
		this.list.addDualOptionEntry(FVT.OPTIONS.freecam, FVT.OPTIONS.freecamHightlightPlayer);
		this.list.addDualOptionEntry(FVT.OPTIONS.attackThrough, FVT.OPTIONS.horseStats);
		this.list.addDualOptionEntry(FVT.OPTIONS.containerButtons, FVT.OPTIONS.inventoryButton);
		this.addSelectableChild(this.list);
		
		// DEFAULTS button at the top left corner
		this.addDrawableChild(new ButtonWidget(6, 6, 55, 20, new TranslatableText("fvt.options.defaults"), (buttonWidget) -> {
			FVT.OPTIONS.reset();
			this.client.setScreen(getNewScreen(parent));
		}, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, new TranslatableText("fvt.options.defaults.tooltip").formatted(Formatting.YELLOW), i, j + 8);
		}));

		// TOOLTIP (?/-) button at the top right corner
		this.addDrawableChild(new ButtonWidget(this.width - 26, 6, 20, 20, new LiteralText("?"), (buttonWidget) -> {
			tooltipsActive = !tooltipsActive;

			if(tooltipsActive) {
				buttonWidget.setMessage(new LiteralText("-"));
			}
			else {
				buttonWidget.setMessage(new LiteralText("?"));
			}
		}, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, (tooltipsActive ? new TranslatableText("fvt.options.tooltips.hide") : new TranslatableText("fvt.options.tooltips.show")), i, j + 8);
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
				r += tooltipComponent2.getHeight() + (s == components.size() - 2 ? 2 : 0);
			}
			immediate.draw();
			matrices.pop();
			r = tooltipComponent;
			for(s = 0; s < components.size(); ++s) {
				tooltipComponent2 = components.get(s);
				tooltipComponent2.drawItems(this.textRenderer, l, r, matrices, this.itemRenderer, 400);
				r += tooltipComponent2.getHeight() + (s == components.size() - 2 ? 2 : 0);
			}
			this.itemRenderer.zOffset = f;
		}
		else {
			super.renderOrderedTooltip(matrices, lines, x, y);
		}
	}
}
