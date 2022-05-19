package me.flourick.fvt.mixin;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

/**
 * FEATURES: Prevent Tool Breaking, Freecam, Use Delay, Entity Outline, AutoReconnect, Placement Lock, Hotbar Autohide, Offhand AutoEat
 * 
 * @author Flourick
 */
@Mixin(value = MinecraftClient.class, priority = 999)
abstract class MinecraftClientMixin
{
	private List<BlockPos> FVT_placementHistory = new ArrayList<>();
	private boolean FVT_xAligned = false;
	private boolean FVT_yAligned = false;
	private boolean FVT_zAligned = false;
	private int FVT_xAlign = 0;
	private int FVT_yAlign = 0;
	private int FVT_zAlign = 0;

	@Shadow
	private ClientPlayerEntity player;

	@Shadow
	private ServerInfo currentServerEntry;

	@Shadow
	private int itemUseCooldown;

	@Shadow
	public HitResult crosshairTarget;

	@Inject(method = "doAttack", at = @At("HEAD"), cancellable = true)
	private void onDoAttack(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.noToolBreaking.getValueRaw() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.setReturnValue(false);
					}
				}
			}
		}

		if(FVT.OPTIONS.autoHideHotbarUse.getValueRaw()) {
			FVT.VARS.resetHotbarLastInteractionTime();
		}

		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.setReturnValue(false);
		}
	}

	@Inject(method = "handleBlockBreaking", at = @At("HEAD"), cancellable = true)
	private void onHandleBlockBreaking(boolean bl, CallbackInfo info)
	{
		if(FVT.OPTIONS.noToolBreaking.getValueRaw() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getMainHandStack();

			if(mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof SwordItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}

		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.cancel();
		}
	}

	@Inject(method = "doItemUse", at = @At("HEAD"), cancellable = true)
	private void onDoItemUseBefore(CallbackInfo info)
	{
		if(FVT.OPTIONS.noToolBreaking.getValueRaw() && !FVT.INSTANCE.isToolBreakingOverriden()) {
			ItemStack mainHandItem = player.getStackInHand(Hand.MAIN_HAND).isEmpty() ? null : player.getStackInHand(Hand.MAIN_HAND);
			ItemStack offHandItem = player.getStackInHand(Hand.OFF_HAND).isEmpty() ? null : player.getStackInHand(Hand.OFF_HAND);

			if(mainHandItem != null && mainHandItem.isDamaged()) {
				if(mainHandItem.getItem() instanceof MiningToolItem){
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof CrossbowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof TridentItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(mainHandItem.getItem() instanceof BowItem) {
					if(mainHandItem.getMaxDamage() - mainHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}

			if(offHandItem != null && offHandItem.isDamaged()) {
				if(offHandItem.getItem() instanceof MiningToolItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof CrossbowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 10) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof TridentItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
				else if(offHandItem.getItem() instanceof BowItem) {
					if(offHandItem.getMaxDamage() - offHandItem.getDamage() < 3) {
						info.cancel();
					}
				}
			}
		}

		if(FVT.OPTIONS.autoHideHotbarUse.getValueRaw()) {
			FVT.VARS.resetHotbarLastInteractionTime();
		}

		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.cancel();
		}
	}

	@Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;", ordinal = 0))
	private Hand[] hijackHandValues()
	{
		if(FVT.OPTIONS.autoEat.getValueRaw() && FVT.VARS.autoEating) {
			crosshairTarget = null; // so we don't target interactable blocks or entities while eating
			return new Hand[] {Hand.OFF_HAND};
		}

		return Hand.values();
	}

	@Inject(method = "doItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", ordinal = 0, shift = At.Shift.AFTER))
	private void onDoItemUseCooldown(CallbackInfo info)
	{
		itemUseCooldown = FVT.OPTIONS.useDelay.getValueAsInteger();
	}

	@Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getCount()I", ordinal = 0, shift = At.Shift.AFTER), cancellable = true)
	private void onDoItemUsePlaceBlock(CallbackInfo info)
	{
		// user placed three blocks so that's our cue to limit the placement!
		if(FVT.OPTIONS.placementLock.getValueRaw() && FVT_placementHistory.size() == 3) {
			BlockPos expected = ((BlockHitResult) crosshairTarget).getBlockPos().offset(((BlockHitResult) crosshairTarget).getSide());
			
			if((FVT_xAligned && FVT_xAlign != expected.getX()) || (FVT_yAligned && FVT_yAlign != expected.getY()) || (FVT_zAligned && FVT_zAlign != expected.getZ())) {
				info.cancel();
			}
		}
	}

	@Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ActionResult;isAccepted()Z", ordinal = 2))
	private void onInteractBlock(CallbackInfo info)
	{
		if(FVT.OPTIONS.placementLock.getValueRaw() && FVT_placementHistory.size() < 3) {
			BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;

			FVT_placementHistory.add(blockHitResult.getBlockPos().offset(blockHitResult.getSide()));

			if(FVT_placementHistory.size() == 3) {
				if(FVT_placementHistory.get(0).getX() == FVT_placementHistory.get(1).getX() && FVT_placementHistory.get(1).getX() == FVT_placementHistory.get(2).getX()) {
					FVT_xAligned = true;
					FVT_xAlign = FVT_placementHistory.get(0).getX();
				}

				if(FVT_placementHistory.get(0).getY() == FVT_placementHistory.get(1).getY() && FVT_placementHistory.get(1).getY() == FVT_placementHistory.get(2).getY()) {
					FVT_yAligned = true;
					FVT_yAlign = FVT_placementHistory.get(0).getY();
				}

				if(FVT_placementHistory.get(0).getZ() == FVT_placementHistory.get(1).getZ() && FVT_placementHistory.get(1).getZ() == FVT_placementHistory.get(2).getZ()) {
					FVT_zAligned = true;
					FVT_zAlign = FVT_placementHistory.get(0).getZ();
				}
			}
		}
	}

	@Inject(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doItemUse()V", ordinal = 0, shift = At.Shift.BEFORE))
	private void onHandleItemUse(CallbackInfo info)
	{
		// called when user presses the use key (aka will clear the placement history for every fresh keypress)
		FVT_placementHistory.clear();
		FVT_xAligned = false;
		FVT_yAligned = false;
		FVT_zAligned = false;
	}

	@Inject(method = "handleInputEvents", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I", ordinal = 0, shift = At.Shift.BEFORE))
	private void onHandleHotbarKeyPress(CallbackInfo info)
	{
		FVT.VARS.resetHotbarLastInteractionTime();
	}

	@Inject(method = "doItemPick", at = @At("HEAD"))
	private void onDoItemPick(CallbackInfo info)
	{
		FVT.VARS.resetHotbarLastInteractionTime();
	}

	@Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
	private void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> info)
	{
		if (FVT.OPTIONS.entityOutline.getValueRaw()) {
			if((FVT.OPTIONS.entityOutlinePlayers.getValueRaw() && entity.getType() == EntityType.PLAYER)
				|| (FVT.OPTIONS.entityOutlineAnimals.getValueRaw() && (entity.getType().getSpawnGroup() == SpawnGroup.CREATURE || entity.getType().getSpawnGroup() == SpawnGroup.WATER_CREATURE || entity.getType().getSpawnGroup() == SpawnGroup.UNDERGROUND_WATER_CREATURE || entity.getType().getSpawnGroup() == SpawnGroup.AXOLOTLS))
				|| (FVT.OPTIONS.entityOutlineMobs.getValueRaw() && entity.getType().getSpawnGroup() == SpawnGroup.MONSTER)
				|| FVT.OPTIONS.entityOutlineMisc.getValueRaw()) {
				info.setReturnValue(true);
			}
		}

		if (FVT.OPTIONS.freecam.getValueRaw() && FVT.OPTIONS.freecamHightlightPlayer.getValueRaw() && entity.getType() == EntityType.PLAYER) {
			info.setReturnValue(true);
		}
	}

	@Inject(method = "disconnect", at = @At("HEAD"), cancellable = true)
	private void onDisconnect(CallbackInfo info)
	{
		if(this.currentServerEntry != null) {
			FVT.VARS.lastJoinedServer = this.currentServerEntry;
		}
	}
}
