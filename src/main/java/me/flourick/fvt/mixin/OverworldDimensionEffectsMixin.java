package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.render.DimensionEffects.Overworld;

import me.flourick.fvt.FVT;

/**
 * FEATURES: Cloud Height
 * 
 * @author Flourick
 */
@Mixin(Overworld.class)
abstract class OverworldDimensionEffectsMixin extends DimensionEffects
{
	@Override
	public float getCloudsHeight()
	{
		return FVT.OPTIONS.cloudHeight.getValue().floatValue();
	}

	public OverworldDimensionEffectsMixin(float cloudsHeight, boolean alternateSkyColor, SkyType skyType, boolean brightenLighting, boolean darkened) { super(cloudsHeight, alternateSkyColor, skyType, brightenLighting, darkened); } // IGNORED
}
