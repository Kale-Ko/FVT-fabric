package me.flourick.fvt.mixin;

import java.util.List;
import java.util.Locale;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;

/**
 * Not a feature just a fix for when it crashes while trying to crash, yep that can happen.
 * 
 * @author Flourick
 */
@Mixin(Entity.class)
abstract class EntityMixin
{
	@Shadow
	private int id;

	@Shadow
	public World world;

	@Shadow
	public abstract Text getName();

	@Shadow
	public abstract EntityType<?> getType();

	@Shadow
	public abstract Vec3d getVelocity();

	@Shadow
	public abstract Entity getVehicle();

	@Final
	@Shadow
	public abstract List<Entity> getPassengerList();

	@Final
	@Shadow
	public abstract double getX();

	@Final
	@Shadow
	public abstract double getY();

	@Final
	@Shadow
	public abstract double getZ();

	@Inject(method = "populateCrashReport", at = @At("HEAD"), cancellable = true)
	private void onPopulateCrashReport(CrashReportSection section, CallbackInfo info)
	{
		section.add("Entity Type", () -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
		section.add("Entity ID", this.id);
		section.add("Entity Name", () -> this.getName().getString());
		section.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
		section.add("Entity's Block location", CrashReportSection.createPositionString((HeightLimitView)this.world, MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
		
		Vec3d vec3d = this.getVelocity();
		if(vec3d != null) {
			section.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
		}
		else {
			section.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", 0.12f, 0.12f, 0.12f));
		}
		section.add("Entity's Passengers", () -> this.getPassengerList().toString());
		section.add("Entity's Vehicle", () -> String.valueOf(this.getVehicle()));
		
		info.cancel();
	}
}
