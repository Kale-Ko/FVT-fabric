package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.option.SimpleOption;

import me.flourick.fvt.utils.ISimpleOption;

/**
 * So mod features can be reset to default.
 * 
 * @author Flourick
 */
@Mixin(SimpleOption.class)
abstract class SimpleOptionMixin<T> implements ISimpleOption<T>
{
	@Final
	@Shadow
	private T defaultValue;

	@Shadow
	public abstract void setValue(T value);

	@Override
	public void FVT_setValueToDefault()
	{
		setValue(defaultValue);
	}

	@Override
	public T FVT_getDefaultValue()
	{
		return defaultValue;
	}
}
