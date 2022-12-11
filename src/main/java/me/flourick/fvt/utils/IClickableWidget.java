package me.flourick.fvt.utils;

import net.minecraft.client.util.math.MatrixStack;

/**
 * ClickableWidget render without tooltip accessor.
 * 
 * @author Flourick
 */
public interface IClickableWidget
{
	public void FVT_renderWithoutTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta);
}
