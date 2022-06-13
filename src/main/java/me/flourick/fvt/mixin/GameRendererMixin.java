package me.flourick.fvt.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;

/**
 * FEATURES: Freecam, Attack Through
 * 
 * @author Flourick
 */
@Mixin(GameRenderer.class)
abstract class GameRendererMixin
{
	@Inject(method = "updateTargetedEntity", at = @At("HEAD"), cancellable = true)
	public void onUpdateTargetedEntity(float tickDelta, CallbackInfo info)
	{
		if(!FVT.OPTIONS.attackThrough.getValueRaw()) {
			return;
		}

		Entity cameraEntity = FVT.MC.getCameraEntity();

		if(cameraEntity != null) {
			if(FVT.MC.world != null) {
				FVT.MC.getProfiler().push("pick");
				FVT.MC.targetedEntity = null;

				double reachDistance = FVT.MC.interactionManager.getReachDistance();
				double reachDistanceBase = FVT.MC.interactionManager.getReachDistance();
				FVT.MC.crosshairTarget = cameraEntity.raycast(reachDistance, tickDelta, false);
				Vec3d cameraPos = cameraEntity.getCameraPosVec(tickDelta);
				boolean entitiesOutOfReach = false;
				double calcReach = reachDistance;

				if(FVT.MC.interactionManager.hasExtendedReach()) {
					calcReach = reachDistance = 6.0D;
				}
				else if(reachDistance > 3.0D) {
					entitiesOutOfReach = true;
				}

				Vec3d cameraRotation = cameraEntity.getRotationVec(1.0F);
				Vec3d reachTo = cameraPos.add(cameraRotation.x * reachDistance, cameraRotation.y * reachDistance, cameraRotation.z * reachDistance);
				Box box = cameraEntity.getBoundingBox().stretch(cameraRotation.multiply(reachDistance)).expand(1.0D, 1.0D, 1.0D);

				// gets a collisionless block in reach or air at reach if none found
				BlockHitResult visibleResult = FVT.MC.world.raycast(new RaycastContext(cameraPos, cameraPos.add(cameraRotation.x * reachDistanceBase, cameraRotation.y * reachDistanceBase, cameraRotation.z * reachDistanceBase), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, cameraEntity));

				// limiting the distance to which an entity will be searched for (so we don't look behind solid blocks BUT IGNORE COLLISIONLESS ONES)
				if(visibleResult != null) {
					calcReach = visibleResult.getPos().squaredDistanceTo(cameraPos);
				}
				else {
					calcReach *= calcReach;
				}

				EntityHitResult entityHitResult = ProjectileUtil.raycast(cameraEntity, cameraPos, reachTo, box, (entityx) -> {
					return !entityx.isSpectator() && entityx.collides();
				}, calcReach);

				if(entityHitResult != null) {
					Entity hitEntity = entityHitResult.getEntity();
					Vec3d hitEntityPos = entityHitResult.getPos();
					double distanceToHitEntity = cameraPos.squaredDistanceTo(hitEntityPos);

					if(!entitiesOutOfReach || distanceToHitEntity <= 9.0D) {
						FVT.MC.crosshairTarget = entityHitResult;

						if(hitEntity instanceof LivingEntity || hitEntity instanceof ItemFrameEntity) {
							FVT.MC.targetedEntity = hitEntity;
						}
					}
				}

				FVT.MC.getProfiler().pop();
			}
		}

		info.cancel();
	}

	@Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
	private void removeHandRendering(CallbackInfo info)
	{
		if(FVT.OPTIONS.freecam.getValueRaw()) {
			info.cancel();
		}
	}
}
