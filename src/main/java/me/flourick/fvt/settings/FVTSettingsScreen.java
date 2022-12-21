package me.flourick.fvt.settings;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
		this.list.addOptionEntry(FVT.OPTIONS.fullbright, FVT.OPTIONS.damageTilt);
        this.list.addOptionEntry(new SimpleOption<?>[] {FVT.OPTIONS.cloudHeight, null});
		this.list.addCategoryEntry("fvt.feature_category.entity_outline");
		this.list.addOptionEntry(FVT.OPTIONS.entityOutline);
		this.list.addOptionEntry(FVT.OPTIONS.entityOutlineAnimals, FVT.OPTIONS.entityOutlineMobs);
		this.list.addOptionEntry(FVT.OPTIONS.entityOutlinePlayers, FVT.OPTIONS.entityOutlineMisc);
		this.list.addCategoryEntry("fvt.feature_category.tools");
		this.list.addOptionEntry(FVT.OPTIONS.toolWarning, FVT.OPTIONS.toolWarningPosition);
		this.list.addOptionEntry(FVT.OPTIONS.toolWarningScale);
		this.list.addOptionEntry(FVT.OPTIONS.noToolBreaking, FVT.OPTIONS.noBreakSwapStop);
		this.list.addCategoryEntry("fvt.feature_category.auto");
		this.list.addOptionEntry(FVT.OPTIONS.autoReconnect, FVT.OPTIONS.autoReconnectAttempts);
		this.list.addOptionEntry(FVT.OPTIONS.autoEat, FVT.OPTIONS.autoAttack);
		this.list.addOptionEntry(FVT.OPTIONS.autoTotem, FVT.OPTIONS.refillHand);
		this.list.addOptionEntry(FVT.OPTIONS.autoElytra, FVT.OPTIONS.fastTrade);
		this.list.addCategoryEntry("fvt.feature_category.placement");
		this.list.addOptionEntry(FVT.OPTIONS.randomPlacement, FVT.OPTIONS.useDelay);
		this.list.addOptionEntry(FVT.OPTIONS.creativeBreakDelay, FVT.OPTIONS.placementLock);
		this.list.addCategoryEntry("fvt.feature_category.misc");
		this.list.addOptionEntry(FVT.OPTIONS.chatHistoryLength, FVT.OPTIONS.sendDeathCoordinates);
		this.list.addOptionEntry(FVT.OPTIONS.disableWToSprint, FVT.OPTIONS.attackThrough);
		this.list.addOptionEntry(FVT.OPTIONS.freecam, FVT.OPTIONS.freecamHightlightPlayer);
		this.list.addOptionEntry(FVT.OPTIONS.containerButtons, FVT.OPTIONS.inventoryButton);
		this.list.addOptionEntry(new SimpleOption<?>[] {FVT.OPTIONS.horseStats, null});
		this.addSelectableChild(this.list);

		// DEFAULTS button at the top left corner
		this.addDrawableChild(
			ButtonWidget.builder(Text.translatable("fvt.options.defaults"), (buttonWidget) -> {
				FVT.OPTIONS.reset();
				this.client.setScreen(getNewScreen(parent));
			})
			.dimensions(6, 6, 55, 20)
			.tooltip(Tooltip.of(Text.translatable("fvt.options.defaults.tooltip").formatted(Formatting.YELLOW)))
			.build()
		);

		// TOOLTIP (?/-) button at the top right corner
		this.addDrawableChild(
			ButtonWidget.builder(Text.literal("?"), (buttonWidget) -> {
				FVT.VARS.settingsShowTooltips = !FVT.VARS.settingsShowTooltips;
	
				if(FVT.VARS.settingsShowTooltips) {
					buttonWidget.setMessage(Text.literal("-"));
					buttonWidget.setTooltip(Tooltip.of(Text.translatable("fvt.options.tooltips.hide")));
				}
				else {
					buttonWidget.setMessage(Text.literal("?"));
					buttonWidget.setTooltip(Tooltip.of(Text.translatable("fvt.options.tooltips.show")));
				}
			})
			.dimensions(this.width - 26, 6, 20, 20)
			.tooltip(Tooltip.of(Text.translatable("fvt.options.tooltips.show")))
			.build()
		);

		// DONE button at the bottom
		this.addDrawableChild(
			ButtonWidget.builder(ScreenTexts.DONE, (buttonWidget) -> {
				FVT.OPTIONS.write();
				this.client.setScreen(parent);
			})
			.dimensions(this.width / 2 - 100, this.list.getBottom() + ((this.height - this.list.getBottom() - 20) / 2), 200, 20)
			.build()
		);
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta)
	{
		this.renderBackground(matrixStack);
		this.list.render(matrixStack, mouseX, mouseY, delta);
		drawCenteredText(matrixStack, this.textRenderer, this.title, this.width / 2, 12, Color.WHITE.getPacked());

		super.render(matrixStack, mouseX, mouseY, delta);
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
}
