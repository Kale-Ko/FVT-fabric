package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.ContainerButtons;

/**
 * FEATURES: Container Buttons
 * 
 * @author Flourick
 */
@Mixin(GenericContainerScreen.class)
abstract class GenericContainerScreenMixin extends HandledScreen<GenericContainerScreenHandler>
{
	@Override
	protected void init()
	{
		super.init();

		if(!FVT.OPTIONS.containerButtons.getValue()) {
			return;
		}

		int baseX = ((this.width - this.backgroundWidth) / 2) + this.backgroundWidth - 19;
		int baseY = ((this.height - this.backgroundHeight) / 2) + 5;

		new ContainerButtons<GenericContainerScreenHandler>(this, baseX, baseY).create();
	}

	public GenericContainerScreenMixin(GenericContainerScreenHandler handler, PlayerInventory inventory, Text title) {super(handler, inventory, title);} // IGNORED
}
