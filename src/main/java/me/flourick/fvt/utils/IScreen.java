package me.flourick.fvt.utils;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;

/**
 * Screen method accessor.
 * 
 * @author Flourick
 */
public interface IScreen
{
	public <T extends Element & Drawable & Selectable> void FVT_addDrawableSelectableChild(T drawableElement);
}