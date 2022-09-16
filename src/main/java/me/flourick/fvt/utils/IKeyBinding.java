package me.flourick.fvt.utils;

/**
 * Additional methods for KeyBinding.
 * 
 * @author Flourick
 */
public interface IKeyBinding
{
	public void FVT_registerKeyDownListener(FVT_KeyDownListener listener);
	public void FVT_registerKeyUpListener(FVT_KeyUpListener listener);

	@FunctionalInterface
	public interface FVT_KeyDownListener
	{
		void keyDownListener();
	}

	@FunctionalInterface
	public interface FVT_KeyUpListener
	{
		void keyUpListener();
	}
}
