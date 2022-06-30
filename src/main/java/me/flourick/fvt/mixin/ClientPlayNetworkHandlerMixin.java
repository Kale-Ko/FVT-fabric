package me.flourick.fvt.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import me.flourick.fvt.FVT;

/**
 * FEATURES: Chat Death Coordinates, AutoTotem, Hotbar Autohide, FastTrade
 * 
 * @author Flourick
 */
@Mixin(ClientPlayNetworkHandler.class)
abstract class ClientPlayNetworkHandlerMixin
{
	@Inject(method = "onScreenHandlerSlotUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/ScreenHandlerSlotUpdateS2CPacket;getRevision()I", ordinal = 1))
	private void onnScreenHandlerSlotUpdate(ScreenHandlerSlotUpdateS2CPacket packet, CallbackInfo info)
	{
		if(FVT.OPTIONS.fastTrade.getValue() && FVT.VARS.waitForTrade && packet.getSlot() == 2 && FVT.VARS.tradeItem != null && packet.getItemStack().getItem() == FVT.VARS.tradeItem) {
			if(Screen.hasShiftDown()) {
				FVT.MC.interactionManager.clickSlot(packet.getSyncId(), 2, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
			}

			FVT.VARS.waitForTrade = false;
			FVT.VARS.tradeItem = null;
		}
	}

	@Inject(method = "onDeathMessage", at = @At("HEAD"))
	private void onOnCombatEvent(DeathMessageS2CPacket packet, CallbackInfo info)
	{
		Entity entity = FVT.MC.world.getEntityById(packet.getEntityId());

		if(entity == FVT.MC.player) {
			FVT.VARS.setLastDeathCoordinates(FVT.MC.player.getX(), FVT.MC.player.getY(), FVT.MC.player.getZ(), FVT.MC.player.clientWorld.getRegistryKey().getValue().toString().split(":")[1].replace('_', ' '));
			FVT.VARS.isAfterDeath = true;
		}
	}

	@Inject(method = "onItemPickupAnimation", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void onOnItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo info, Entity entity, LivingEntity livingEntity, ItemEntity itemEntity)
	{
		if(FVT.OPTIONS.autoHideHotbarItem.getValue() && livingEntity == FVT.MC.player) {
			FVT.VARS.resetHotbarLastInteractionTime();
		}
	}

	@Inject(method = "onEntityStatus", at = @At("RETURN"))
	private void onOnEntityStatus(EntityStatusS2CPacket packet, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoTotem.getValue() && packet.getStatus() == 35 && packet.getEntity(FVT.MC.player.world).equals(FVT.MC.player)) {
			ClientPlayerEntity player  = FVT.MC.player;

			int activeIdx = -1;

			// TOTEM used in main hand (main hand first as it gets priority if totem in both hands)
			if(player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
				activeIdx = player.getInventory().selectedSlot + 36;
			} // TOTEM used in offhand
			else if(player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
				activeIdx = 45;
			}

			if(activeIdx != -1) {
				int sz = player.getInventory().main.size();

				for(int i = 0; i < sz; i++) {
					if(player.getInventory().main.get(i).getItem() == Items.TOTEM_OF_UNDYING && i != player.getInventory().selectedSlot) {
						// works by clicking on the totem first and then on the last known used totem position (either a hotbar slot or offhand)
						if(i < 9) { // hotbar
							FVT.MC.interactionManager.clickSlot(player.playerScreenHandler.syncId, i + 36, 0, SlotActionType.PICKUP, player);
						} // rest of inventory
						else {
							FVT.MC.interactionManager.clickSlot(player.playerScreenHandler.syncId, i, 0, SlotActionType.PICKUP, player);
						}

						FVT.MC.interactionManager.clickSlot(player.playerScreenHandler.syncId, activeIdx, 0, SlotActionType.PICKUP, player);

						break;
					}
				}
			}
		}
	}
}
