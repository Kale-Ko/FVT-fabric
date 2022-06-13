package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.ContainerButtons;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.text.Text;

/**
 * FEATURES: Container Buttons
 * 
 * @author Flourick
 */
@Mixin(ShulkerBoxScreen.class)
abstract class ShulkerBoxScreenMixin extends HandledScreen<ShulkerBoxScreenHandler>
{
	@Override
	protected void init()
	{
		super.init();

		if(!FVT.OPTIONS.containerButtons.getValueRaw()) {
			return;
		}

		int baseX = ((this.width - this.backgroundWidth) / 2) + this.backgroundWidth - 19;
		int baseY = ((this.height - this.backgroundHeight) / 2) + 5;

		new ContainerButtons<ShulkerBoxScreenHandler>(this, baseX, baseY).create();
	}

	public ShulkerBoxScreenMixin(ShulkerBoxScreenHandler handler, PlayerInventory inventory, Text title) {super(handler, inventory, title);} // IGNORED
}
