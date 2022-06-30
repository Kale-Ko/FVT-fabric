package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;

import me.flourick.fvt.FVT;

/**
 * FEATURES: No Potion Particles
 * 
 * @author Flourick
 */
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin
{
	@Inject(method = "tickStatusEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"), cancellable = true)
	private void onTickStatusEffects(CallbackInfo info)
	{
		if((Object)this instanceof ClientPlayerEntity) {
			if(FVT.OPTIONS.noPotionParticles.getValue()) {
				info.cancel();
			}
		}
	}
}
