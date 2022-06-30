package me.flourick.fvt.utils;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.Vec3d;

/**
 * Holder for various utility variables for different features.
 * 
 * @author Flourick
 */
public class FVTVars
{
	public boolean waitForTrade;
	public Item tradeItem;

	public boolean autoEating;

	public double freecamYaw;
	public double freecamPitch;

	public double playerYaw;
	public double playerPitch;
	public Vec3d playerVelocity;

	public double freecamX;
	public double freecamY;
	public double freecamZ;

	public double prevFreecamX;
	public double prevFreecamY;
	public double prevFreecamZ;

	public float freecamForwardSpeed;
	public float freecamSideSpeed;
	public float freecamUpSpeed;

	private double deathX;
	private double deathY;
	private double deathZ;
	private String deathWorld;
	public boolean isAfterDeath;

	private long hotbarLastInteractionTime;

	private int toolWarningTextTicksLeft;
	public int toolDurability;
	public ItemStack mainHandToolItemStack;
	public ItemStack offHandToolItemStack;
	public Hand toolHand;

	public FVTVars()
	{
		this.waitForTrade = false;
		this.tradeItem = null;

		this.autoEating = false;

		this.freecamYaw = 0.0d;
		this.freecamPitch = 0.0d;

		this.playerYaw = 0.0d;
		this.playerPitch = 0.0d;
		this.playerVelocity = Vec3d.ZERO;

		this.freecamX = 0.0d;
		this.freecamY = 0.0d;
		this.freecamZ = 0.0d;

		this.prevFreecamX = 0.0d;
		this.prevFreecamY = 0.0d;
		this.prevFreecamZ = 0.0d;

		this.freecamForwardSpeed = 0.0f;
		this.freecamSideSpeed = 0.0f;
		this.freecamUpSpeed = 0.0f;

		this.deathX = 0.0d;
		this.deathY = 0.0d;
		this.deathZ = 0.0d;
		this.deathWorld = "";
		this.isAfterDeath = false;

		this.hotbarLastInteractionTime = 0;
		
		this.toolWarningTextTicksLeft = 0;
		this.toolDurability = 0;
		this.mainHandToolItemStack = ItemStack.EMPTY;
		this.offHandToolItemStack = ItemStack.EMPTY;
		this.toolHand = Hand.MAIN_HAND;
	}

	public void setLastDeathCoordinates(double x, double y, double z, String world)
	{
		this.deathX = x;
		this.deathY = y;
		this.deathZ = z;
		this.deathWorld = world;
	}
  
	public double getLastDeathX()
	{
		return this.deathX;
	}

	public double getLastDeathY()
	{
		return this.deathY;
	}

	public double getLastDeathZ()
	{
		return this.deathZ;
	}

	public String getLastDeathWorld()
	{
		return this.deathWorld;
	}

	public long getHotbarLastInteractionTime()
	{
		return hotbarLastInteractionTime;
	}

	public void zeroHotbarLastInteractionTime()
	{
		hotbarLastInteractionTime = 0;
	}

	public void resetHotbarLastInteractionTime()
	{
		hotbarLastInteractionTime = Util.getMeasuringTimeMs();
	}

	public int getToolWarningTextTicksLeft()
	{
		return toolWarningTextTicksLeft;
	}

	public void resetToolWarningTicks()
	{
		toolWarningTextTicksLeft = 40;
	}

	public void tickToolWarningTicks()
	{
		if(toolWarningTextTicksLeft > 0) {
			toolWarningTextTicksLeft -= 1;
		}
	}
}
