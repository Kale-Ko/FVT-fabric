package me.flourick.fvt.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.MessageType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.stat.StatHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.MathHelper;

import java.util.UUID;

import com.ibm.icu.math.BigDecimal;
import com.mojang.authlib.GameProfile;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;

/**
 * FEATURES: AutoReconnect, Chat Death Coordinates, Disable 'W' To Sprint, Freecam, Hotbar Autohide, AutoElytra
 *
 * @author Flourick
 */
@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity
{
	private boolean FVT_prevFallFlying = false;

	@Shadow
	abstract boolean hasJumpingMount();

	@Shadow
	public Input input;

	@Shadow
	private int ticksLeftToDoubleTapSprint;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onConstructor(MinecraftClient client, ClientWorld world, ClientPlayNetworkHandler networkHandler, StatHandler stats, ClientRecipeBook recipeBook, boolean lastSneaking, boolean lastSprinting, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoReconnect.getValueRaw()) {
			FVT.VARS.autoReconnectTries = 0;
		}
	}

	@Inject(method = "setShowsDeathScreen", at = @At("HEAD"))
	private void onSetShowsDeathScreen(CallbackInfo info)
	{
		if(FVT.VARS.isAfterDeath && FVT.OPTIONS.sendDeathCoordinates.getValueRaw()) {
			FVT.VARS.isAfterDeath = false;
			FVT.MC.inGameHud.addChatMessage(
				MessageType.CHAT,
				new TranslatableText("fvt.chat_messages_prefix", new TranslatableText("fvt.feature.name.send_death_coordinates.message", BigDecimal.valueOf(FVT.VARS.getLastDeathX()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue(), BigDecimal.valueOf(FVT.VARS.getLastDeathZ()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue(), BigDecimal.valueOf(FVT.VARS.getLastDeathY()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue(), FVT.VARS.getLastDeathWorld())),
				UUID.fromString("00000000-0000-0000-0000-000000000000")
			);
		}
	}

	@Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
	private void onDropSelectedItem(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.setReturnValue(false);
		}
		else if(FVT.OPTIONS.autoHideHotbarItem.getValueRaw()) {
			FVT.VARS.resetHotbarLastInteractionTime();
		}
	}

	@Inject(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"))
	private void onTickMovementGetEquippedStack(CallbackInfo info)
	{
		if(!FVT.OPTIONS.autoElytra.getValueRaw()) {
			return;
		}

		// this switches to elytra if attemting to fly like you had it equipped in the first place (this if statement is false when elytra is already equipped)
		if(this.input.jumping && !this.getAbilities().flying && !this.onGround && !this.isFallFlying() && !this.isTouchingWater() && !this.hasVehicle() && !this.isClimbing() && !this.hasStatusEffect(StatusEffects.LEVITATION)) {
			PlayerInventory inventory  = FVT.MC.player.getInventory();
			int sz = inventory.main.size();
			int idx = -1;

			// reverse search since it's more likely elytra is in the back of the inventory
			for(idx = sz-1; idx >= 0; idx--) {
				ItemStack itemStack = inventory.main.get(idx);

				if(itemStack.getItem() == Items.ELYTRA && ElytraItem.isUsable(itemStack)) {
					break;
				}
			}

			// clicks on the chestplate slot then elytra then chestplate again
			FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, FVT.MC.player);

			if(idx < 9) {
				FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, idx + 36, 0, SlotActionType.PICKUP, FVT.MC.player);
			}
			else {
				FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, idx, 0, SlotActionType.PICKUP, FVT.MC.player);
			}

			FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, FVT.MC.player);
		}
	}

	@Inject(method = "tickMovement", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isFallFlying()Z"))
	private void onTickMovementIsFallFlying(CallbackInfo info)
	{
		if(!FVT.OPTIONS.autoElytra.getValueRaw()) {
			return;
		}

		PlayerInventory inventory  = FVT.MC.player.getInventory();

		// called when player landed so if he has elytra we switch back to a chestplate
		if(FVT_prevFallFlying && !this.isFallFlying() && inventory.getArmorStack(2).getItem() == Items.ELYTRA) {

			int sz = inventory.main.size();
			int idx = -1;

			// reverse search since it's more likely a chestplate is in the back
			for(idx = sz-1; idx >= 0; idx--) {
				ItemStack itemStack = inventory.main.get(idx);

				if(itemStack.getItem() instanceof ArmorItem && ((ArmorItem)itemStack.getItem()).getSlotType() == EquipmentSlot.CHEST) {
					break;
				}
			}

			// clicks on the chestplate slot then elytra then chestplate again
			FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, FVT.MC.player);

			if(idx < 9) {
				FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, idx + 36, 0, SlotActionType.PICKUP, FVT.MC.player);
			}
			else {
				FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, idx, 0, SlotActionType.PICKUP, FVT.MC.player);
			}

			FVT.MC.interactionManager.clickSlot(FVT.MC.player.playerScreenHandler.syncId, 6, 0, SlotActionType.PICKUP, FVT.MC.player);
		}

		FVT_prevFallFlying = this.isFallFlying();
	}

	@Inject(method = "tickMovement", at = @At("HEAD"))
	private void onTickMovement(CallbackInfo info)
	{
		if(FVT.OPTIONS.disableWToSprint.getValueRaw()) {
			this.ticksLeftToDoubleTapSprint = -1;
		}

		if(FVT.OPTIONS.freecam.getValueRaw()) {
			this.setVelocity(FVT.VARS.playerVelocity);

			float forward = FVT.MC.player.input.movementForward;
			float up = (FVT.MC.player.input.jumping ? 1.0f : 0.0f) - (FVT.MC.player.input.sneaking ? 1.0f : 0.0f);
			float side = FVT.MC.player.input.movementSideways;

			FVT.VARS.freecamForwardSpeed = forward != 0 ? FVT_updateMotion(FVT.VARS.freecamForwardSpeed, forward) : FVT.VARS.freecamForwardSpeed * 0.5f;
			FVT.VARS.freecamUpSpeed = up != 0 ?  FVT_updateMotion(FVT.VARS.freecamUpSpeed, up) : FVT.VARS.freecamUpSpeed * 0.5f;
			FVT.VARS.freecamSideSpeed = side != 0 ?  FVT_updateMotion(FVT.VARS.freecamSideSpeed , side) : FVT.VARS.freecamSideSpeed * 0.5f;

			double rotateX = Math.sin(FVT.VARS.freecamYaw * Math.PI / 180.0D);
			double rotateZ = Math.cos(FVT.VARS.freecamYaw * Math.PI / 180.0D);
			double speed = FVT.MC.player.isSprinting() ? 1.2D : 0.55D;

			FVT.VARS.prevFreecamX = FVT.VARS.freecamX;
			FVT.VARS.prevFreecamY = FVT.VARS.freecamY;
			FVT.VARS.prevFreecamZ = FVT.VARS.freecamZ;

			FVT.VARS.freecamX += (FVT.VARS.freecamSideSpeed * rotateZ - FVT.VARS.freecamForwardSpeed * rotateX) * speed;
			FVT.VARS.freecamY += FVT.VARS.freecamUpSpeed * speed;
			FVT.VARS.freecamZ += (FVT.VARS.freecamForwardSpeed * rotateZ + FVT.VARS.freecamSideSpeed * rotateX) * speed;
		}
	}

	private float FVT_updateMotion(float motion, float direction)
	{
		return (direction + motion == 0) ? 0.0f : MathHelper.clamp(motion + ((direction < 0) ? -0.35f : 0.35f), -1f, 1f);
	}

	// PREVENTS SENDING VEHICLE MOVEMENT PACKETS TO SERVER (freecam)
	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasVehicle()Z", ordinal = 0))
	private boolean hijackHasVehicle(ClientPlayerEntity player)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			return false;
		}

		return this.hasVehicle();
	}

	// PREVENTS HORSES FROM JUMPING (freecam)
	@Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;hasJumpingMount()Z", ordinal = 0))
	private boolean hijackHasJumpingMount(ClientPlayerEntity player)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			return false;
		}

		return hasJumpingMount();
	}

	// PREVENTS BOAT MOVEMENT (freecam)
	@Inject(method = "tickRiding", at = @At("HEAD"), cancellable = true)
	private void onTickRiding(CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			super.tickRiding();
			info.cancel();
		}
	}

	// PREVENTS MOVEMENT (freecam)
	@Inject(method = "move", at = @At("HEAD"), cancellable = true)
	private void onMove(CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.cancel();
		}
	}

	// PREVENTS MORE MOVEMENT (freecam)
	@Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
	private void onIsCamera(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.setReturnValue(false);
		}
	}

	// PREVENTS SNEAKING (freecam)
	@Inject(method = "isSneaking", at = @At("HEAD"), cancellable = true)
	private void onIsSneaking(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.setReturnValue(false);
		}
	}

	// UPDATES FREECAM YAW AND PITCH ACCORDING TO HEAD (freecam)
	@Override
	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			FVT.VARS.freecamYaw += cursorDeltaX * 0.15D;
			FVT.VARS.freecamPitch = MathHelper.clamp(FVT.VARS.freecamPitch + cursorDeltaY * 0.15D, -90, 90);
		}
		else {
			super.changeLookDirection(cursorDeltaX, cursorDeltaY);
		}
	}

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) { super(world, profile); } // IGNORED
}
