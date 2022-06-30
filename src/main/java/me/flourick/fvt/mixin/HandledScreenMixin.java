package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.Color;
import me.flourick.fvt.utils.FVTButtonWidget;

/**
 * FEATURES: Inventory Button
 * 
 * @author Flourick
 */
@Mixin(HandledScreen.class)
abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>
{
	@Shadow
	protected int x;
	
	@Shadow
	protected int y;

	@Shadow
	protected int backgroundHeight;

	@Shadow
	protected int backgroundWidth;

	private FVTButtonWidget FVT_dropButton = new FVTButtonWidget(0, 0, 0, 0, null, null, null);

	private int FVT_getDropButtonX()
	{
		return this.x + this.backgroundWidth -2;
	}

	private int FVT_getDropButtonY()
	{
		return this.y + this.backgroundHeight - 85;
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void onInit(CallbackInfo info)
	{
		if(!FVT.OPTIONS.inventoryButton.getValue() || (Object)this instanceof CreativeInventoryScreen) {
			return;
		}

		int buttonWidth = 14;
		int buttonHeight = 12;

		FVT_dropButton = new FVTButtonWidget(FVT_getDropButtonX(), FVT_getDropButtonY(), buttonWidth, buttonHeight, Text.literal("⊽"), (buttonWidget) -> FVT_onDropButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			this.renderTooltip(matrixStack, Text.translatable("fvt.feature.name.inventory_button.drop.tooltip"), i, j + 8);
		}, new Color(255, 255, 255, 255), new Color(255, 255, 255, 255));

		this.addDrawableChild(FVT_dropButton);
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(CallbackInfo info)
	{
		FVT_dropButton.x = FVT_getDropButtonX();
		FVT_dropButton.y = FVT_getDropButtonY();
	}

	private void FVT_onDropButtonClick()
	{
		int begIdx = getScreenHandler().slots.size() - 36;
		int endIdx = getScreenHandler().slots.size();

		// since in player inventory screen the last slot is the offhand one so we have to shift by one
		if((Object) this instanceof InventoryScreen) {
			begIdx -= 1;
			endIdx -= 1;
		}

		for(int i = begIdx; i < endIdx; i++) {
			Slot slot = getScreenHandler().getSlot(i);

			if(slot.getStack().isEmpty()) {
				continue;
			}

			FVT.MC.interactionManager.clickSlot(getScreenHandler().syncId, i, 1, SlotActionType.THROW, FVT.MC.player);
		}
	}

	protected HandledScreenMixin(Text title) { super(title); } // IGNORED
}
