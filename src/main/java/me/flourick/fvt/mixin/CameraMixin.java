package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;

import me.flourick.fvt.FVT;

/**
 * FEATURES: Freecam
 * 
 * @author Flourick
 */
@Mixin(Camera.class)
abstract class CameraMixin
{
	@Shadow
	private boolean ready;
	@Shadow
	private BlockView area;

	private boolean FVT_preFreecam = false;

	@Shadow
	abstract void setRotation(float yaw, float pitch);

	@Shadow
	abstract void setPos(double x, double y, double z);

	@Inject(method = "update", at = @At("HEAD"), cancellable = true)
	private void onUpdate(BlockView area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			if(!FVT_preFreecam) {
				FVT_preFreecam = true;
				FVT_freecamToggleCheck();
			}

			this.ready = true;
			this.area = area;

			this.setRotation((float)FVT.VARS.freecamYaw, (float)FVT.VARS.freecamPitch);
			this.setPos(MathHelper.lerp((double)tickDelta, FVT.VARS.prevFreecamX, FVT.VARS.freecamX), MathHelper.lerp((double)tickDelta, FVT.VARS.prevFreecamY, FVT.VARS.freecamY), MathHelper.lerp((double)tickDelta, FVT.VARS.prevFreecamZ, FVT.VARS.freecamZ));

			info.cancel();
		}
		else if(FVT_preFreecam) {
			FVT_preFreecam = false;
			FVT_freecamToggleCheck();
		}
	}

	// makes you able to see yourself while in freecam
	@Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true)
	private void onIsThirdPerson(CallbackInfoReturnable<Boolean> info)
	{
		if(FVT.OPTIONS.freecam.getValue()) {
			info.setReturnValue(true);
		}
	}

	// called on enable/disable of freecam to prepare/cleanup variables
	private void FVT_freecamToggleCheck()
	{
		if(FVT.OPTIONS.freecam.getValue() && FVT.MC.player != null) {
			FVT.MC.chunkCullingEnabled = false;

			if(FVT.MC.player.getVehicle() instanceof BoatEntity) {
				((BoatEntity)FVT.MC.player.getVehicle()).setInputs(false, false, false, false);
			}

			FVT.VARS.freecamPitch = FVT.MC.player.getPitch();
			FVT.VARS.freecamYaw = FVT.MC.player.getYaw();

			FVT.VARS.playerPitch = FVT.MC.player.getPitch();
			FVT.VARS.playerYaw = FVT.MC.player.getYaw();
			FVT.VARS.playerVelocity = FVT.MC.player.getVelocity();

			FVT.VARS.freecamX = FVT.VARS.prevFreecamX = FVT.MC.gameRenderer.getCamera().getPos().getX();
			FVT.VARS.freecamY = FVT.VARS.prevFreecamY = FVT.MC.gameRenderer.getCamera().getPos().getY();
			FVT.VARS.freecamZ = FVT.VARS.prevFreecamZ = FVT.MC.gameRenderer.getCamera().getPos().getZ();
		}
		else {
			FVT.MC.chunkCullingEnabled = true;

			FVT.MC.player.setPitch((float)FVT.VARS.playerPitch);
			FVT.MC.player.setYaw((float)FVT.VARS.playerYaw);

			FVT.VARS.freecamForwardSpeed = 0.0f;
			FVT.VARS.freecamUpSpeed = 0.0f;
			FVT.VARS.freecamSideSpeed = 0.0f;
		}
	}
}
