package me.flourick.fvt.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import me.flourick.fvt.utils.IKeyBinding;

/**
 * Adds an option to register listeners for key up and key down events.
 * 
 * @author Flourick
 */
@Mixin(KeyBinding.class)
abstract class KeyBindingMixin implements IKeyBinding
{
	@Shadow
	private boolean pressed;

	@Final
	@Shadow
	private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;

	private List<FVT_KeyDownListener> keyDownListeners = new ArrayList<>();
	private List<FVT_KeyUpListener> keyUpListeners = new ArrayList<>();

	private void FVT_onKeyDownEvent()
	{
		for(FVT_KeyDownListener l : keyDownListeners) {
			l.keyDownListener();
		}
	}

	private void FVT_onKeyUpEvent()
	{
		for(FVT_KeyUpListener l : keyUpListeners) {
			l.keyUpListener();
		}
	}
	
	@Override
	public void FVT_registerKeyDownListener(FVT_KeyDownListener listener)
	{
		keyDownListeners.add(listener);
	}

	@Override
	public void FVT_registerKeyUpListener(FVT_KeyUpListener listener)
	{
		keyUpListeners.add(listener);
	}

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void onSetKeyPressed(InputUtil.Key key, boolean pressed, CallbackInfo info)
	{
		KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);

		if(keyBinding != null) {
			if(pressed && !keyBinding.isPressed()) {
				((KeyBindingMixin)(Object)keyBinding).FVT_onKeyDownEvent();
			}
			else if(!pressed) {
				((KeyBindingMixin)(Object)keyBinding).FVT_onKeyUpEvent();
			}
		}
	}
}
