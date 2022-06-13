package me.flourick.fvt.utils;

import net.minecraft.util.math.MathHelper;

/**
 * Represents an ARGB color with various utilities.
 * 
 * @author Flourick
 */
public class Color
{
	public static final Color WHITE = new Color(255, 255, 255);

	private final int alpha, red, green, blue;
	private final int packed;

	public Color(int red, int green, int blue)
	{
		this(255, red, green, blue);
	}

	public Color(int alpha, int red, int green, int blue)
	{
		this.alpha = MathHelper.clamp(alpha, 0, 255);
		this.red = MathHelper.clamp(red, 0, 255);
		this.green = MathHelper.clamp(green, 0, 255);
		this.blue = MathHelper.clamp(blue, 0, 255);
		this.packed = (this.alpha << 24) | (this.red << 16) | (this.green << 8) | this.blue;
	}

	public Color(int packed)
	{
		this.alpha = packed >> 24;
		this.red = (packed >> 16) & 0xff;
		this.green = (packed >> 8) & 0xff;
		this.blue = packed & 0xff;
		this.packed = packed;
	}

	public float getNormAlpha()
	{
		return (float)alpha / 255;
	}
	public float getNormRed()
	{
		return (float)red / 255;
	}
	public float getNormGreen()
	{
		return (float)green / 255;
	}
	public float getNormBlue()
	{
		return (float)blue / 255;
	}

	public int getAlpha()
	{
		return alpha;
	}
	public int getRed()
	{
		return red;
	}
	public int getGreen()
	{
		return green;
	}
	public int getBlue()
	{
		return blue;
	}

	public int getPacked()
	{
		return packed;
	}
}
