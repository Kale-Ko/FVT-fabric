package me.flourick.fvt.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;

import me.flourick.fvt.utils.IScreen;

/**
 * FEATURES: Container Buttons
 * 
 * @author Flourick
 */
@Mixin(Screen.class)
abstract class ScreenMixin implements IScreen
{
	@Final
	@Shadow
	private List<Drawable> drawables;

	@Final
	@Shadow
	private List<Element> children;

	@Final
	@Shadow
	private List<Selectable> selectables;

	@Override
	public <T extends Element & Drawable & Selectable> T FVT_addDrawableSelectableChild(T child)
	{
		// cannot use Invoker becouse for some reason it cannot find those methods
		this.drawables.add(child);
		this.children.add(child);
		this.selectables.add(child);

		return child;
	}
}
