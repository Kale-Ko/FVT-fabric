package me.flourick.fvt.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;

import me.flourick.fvt.FVT;

/**
 * <p>
 * FEATURES: Random Block Placement, Refill Hand, Creative Break Delay, No Break Swap Stop
 * </p>
 * 
 * @author Flourick
 */
@Mixin(ClientPlayerInteractionManager.class)
abstract class ClientPlayerInteractionManagerMixin
{
	@Shadow
	private int blockBreakingCooldown;

	@ModifyVariable(method = "isCurrentlyBreaking(Lnet/minecraft/util/math/BlockPos;)Z", at = @At("STORE"), ordinal = 0)
	private boolean breakStop(boolean bl)
	{
		if(FVT.OPTIONS.noBreakSwapStop.getValue()) {
			return true;
		}	

		return bl;
	}

	@Inject(method = "attackBlock", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 0, shift = At.Shift.AFTER))
	private void onAttackBlockCooldown(CallbackInfoReturnable<Boolean> info)
	{
		blockBreakingCooldown = FVT.OPTIONS.creativeBreakDelay.getValue(); // -1 is intentionally not here so the first block broken has always atleast some delay to make accidental double breaking not as common
	}

	@Inject(method = "updateBlockBreakingProgress", at = @At(value = "FIELD", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;blockBreakingCooldown:I", ordinal = 3, shift = At.Shift.AFTER))
	private void onUpdateBlockBreakingProgressCooldown(CallbackInfoReturnable<Boolean> info)
	{
		blockBreakingCooldown = FVT.OPTIONS.creativeBreakDelay.getValue() - 1;
	}

	@Inject(method = "interactBlock", at = @At("HEAD"))
	private void onInteractBlock(CallbackInfoReturnable<ActionResult> info)
	{
		if(FVT.OPTIONS.randomPlacement.getValue()) {
			PlayerInventory inventory  = FVT.MC.player.getInventory();

			// need to hold a block first for it to pick a block or empty hand
			if(inventory.getStack(inventory.selectedSlot).getItem() instanceof BlockItem || inventory.getStack(inventory.selectedSlot).getItem() == Items.AIR) {
				List<Integer> blockIndexes = new ArrayList<>();

				for(int i = 0; i < 9; i++) {
					if(inventory.getStack(i).getItem() instanceof BlockItem) {
						blockIndexes.add(i);
					}
				}

				if(blockIndexes.size() > 0) {
					inventory.selectedSlot = blockIndexes.get(new Random().nextInt(blockIndexes.size()));
				}
			}
		}

		if(FVT.OPTIONS.refillHand.getValue()) {
			PlayerInventory inventory  = FVT.MC.player.getInventory();

			if(inventory.getStack(inventory.selectedSlot).getItem() instanceof BlockItem) {
				ItemStack using = inventory.getStack(inventory.selectedSlot);

				if(2 * using.getCount() <= using.getMaxCount() && FVT.MC.player.playerScreenHandler.getCursorStack().isEmpty()) {
					// find the same item in inventory
					int sz = inventory.main.size();

					// if random placement enabled don't take items from hotbar
					int begIdx = FVT.OPTIONS.randomPlacement.getValue() ? 9 : 0;

					// reverse search to pick items from back of the inventory first rather than hotbar
					for(int i = sz-1; i >= begIdx; i--) {
						if(inventory.main.get(i).getItem() == using.getItem() && i != inventory.selectedSlot) {
							ItemStack found = inventory.main.get(i);

							// 0 for left-click to take all the items or 1 for right-click to split the stack
							int mouse = found.getCount() + using.getCount() <= using.getMaxCount() ? 0 : 1;

							// hotbar
							if(i < 9) {
								FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, i + 36, mouse, SlotActionType.PICKUP, FVT.MC.player);
							}
							else {
								FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, i, mouse, SlotActionType.PICKUP, FVT.MC.player);
							}
							
							FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, inventory.selectedSlot + 36, 0, SlotActionType.PICKUP, FVT.MC.player);

							break;
						}
					}
				}
			}
		}
	}
}
