package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import me.flourick.fvt.FVT;
import net.minecraft.client.render.DimensionEffects.Nether;

/**
 * FEATURES: No Nether Thick Fog
 * 
 * @author Flourick
 */
@Mixin(Nether.class)
abstract class NetherDimensionEffectsMixin
{
	@Inject(method = "useThickFog", at = @At("HEAD"), cancellable = true)
	private void onUseThickFog(CallbackInfoReturnable<Boolean> info)
	{
		info.setReturnValue(!FVT.OPTIONS.noNetherFog.getValueRaw());
	}
}
