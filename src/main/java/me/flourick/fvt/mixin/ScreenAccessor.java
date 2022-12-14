package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;

/**
 * Screen method accessor.
 * 
 * @author Flourick
 */
@Mixin(Screen.class)
public interface ScreenAccessor
{
	@Invoker
	<T extends Element & Drawable> T callAddDrawableChild(T drawableElement);
}
