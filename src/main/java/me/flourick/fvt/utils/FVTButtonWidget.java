package me.flourick.fvt.utils;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import me.flourick.fvt.FVT;

/**
 * Updated version of vanilla's ButtonWidget that fixes custom button sizes and adds coloring option.
 * 
 * @author Flourick
 */
public class FVTButtonWidget extends ButtonWidget
{
	private Color messageColor;
	private Color buttonColor;

	public FVTButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress)
	{
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.messageColor = Color.WHITE;
		this.buttonColor = Color.WHITE;
	}

	public FVTButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress, Color buttonColor, Color messageColor)
	{
		super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
		this.messageColor = messageColor;
		this.buttonColor = buttonColor;
	}

	public Color getMessageColor()
	{
		return messageColor;
	}

	public void setMessageColor(Color messageColor)
	{
		this.messageColor = messageColor;
	}

	public Color getButtonColor()
	{
		return buttonColor;
	}

	public void setButtonColor(Color buttonColor)
	{
		this.buttonColor = buttonColor;
	}
}
