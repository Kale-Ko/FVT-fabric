package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.entity.Entity;

import me.flourick.fvt.FVT;

/**
 * FEATURES: Freecam
 * 
 * @author Flourick
 */
@Mixin(PlayerEntityRenderer.class)
abstract class PlayerEntityRendererMixin<T extends Entity> extends EntityRenderer<T>
{
	@Override
	protected boolean hasLabel(T entity)
	{
		// while in freecam makes your own nametag visible
		if(FVT.OPTIONS.freecam.getValue() && entity == FVT.MC.player) {
			return true;
		}

		return super.hasLabel(entity);
	}

	protected PlayerEntityRendererMixin(Context ctx) { super(ctx); } // IGNORED
}
