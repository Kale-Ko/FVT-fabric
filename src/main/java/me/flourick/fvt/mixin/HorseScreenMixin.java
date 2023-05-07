package me.flourick.fvt.mixin;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.FVTButtonWidget;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HorseScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.HorseScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

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
	private AbstractHorseEntity entity;

	@Override
	protected void init()
	{
		super.init();

		if(!FVT.OPTIONS.horseStats.getValue()) {
			return;
		}

		Text header = Text.translatable("fvt.feature.name.horse_stats.button");

		int buttonHeight = 14;
		int buttonWidth = FVT.MC.textRenderer.getWidth(header) + 8;

		List<Text> tooltip = new ArrayList<>();
		tooltip.add(Text.translatable("fvt.feature.name.horse_stats.button.tooltip.health", FVT_getHorseHealth()));
		tooltip.add(Text.translatable("fvt.feature.name.horse_stats.button.tooltip.speed", FVT_getHorseSpeed()));
		tooltip.add(Text.translatable("fvt.feature.name.horse_stats.button.tooltip.jump_height", FVT_getHorseJumpHeight()));

		int baseX = ((this.width - this.backgroundWidth) / 2) + this.backgroundWidth - buttonWidth - 7;
		int baseY = ((this.height - this.backgroundHeight) / 2) - 12;

		FVTButtonWidget button = new FVTButtonWidget(baseX, baseY, buttonWidth, buttonHeight, header, null);
		button.setTooltip(Tooltip.of(Texts.join(tooltip, Text.of("\n"))));
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

	public HorseScreenMixin(HorseScreenHandler handler, PlayerInventory inventory, Text title) { super(handler, inventory, title); } // IGNORED
}
