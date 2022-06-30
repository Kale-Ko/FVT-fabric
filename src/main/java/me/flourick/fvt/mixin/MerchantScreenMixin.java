package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.village.TradeOffer;

import me.flourick.fvt.FVT;

/**
 * FEATURES: FastTrade
 * 
 * @author Flourick
 */
@Mixin(MerchantScreen.class)
abstract class MerchantScreenMixin extends HandledScreen<MerchantScreenHandler>
{
	@Shadow
	private int selectedIndex;

	@Inject(method = "syncRecipeIndex", at = @At("HEAD"))
	private void onSyncRecipeIndex(CallbackInfo info)
	{
		if(!FVT.OPTIONS.fastTrade.getValue()) {
			return;
		}

		TradeOffer trade = ((MerchantScreenHandler)this.handler).getRecipes().get(selectedIndex);

		if(trade != null && !trade.isDisabled() && Screen.hasShiftDown()) {
			// in case there is already something in the output slot
			if(this.handler.getSlot(2).getStack().getItem() == trade.getSellItem().getItem()) {
				FVT.MC.interactionManager.clickSlot(this.handler.syncId, 2, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
			}

			// very stupid, basically waits every time but simple means easy so...
			FVT.VARS.waitForTrade = true;
			FVT.VARS.tradeItem = trade.getSellItem().getItem();
		}
	}
	
	public MerchantScreenMixin(MerchantScreenHandler handler, PlayerInventory inventory, Text title) { super(handler, inventory, title); } // IGNORED
}
