package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import me.flourick.fvt.FVT;
import net.minecraft.client.gui.hud.ChatHud;

/**
 * FEATURES: Chat History Length
 * 
 * @author Flourick
 */
@Mixin(ChatHud.class)
abstract class ChatHudMixin
{
	@ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = @Constant(intValue = 100))
	private int getChatHistoryLenght(int len)
	{
		return FVT.OPTIONS.chatHistoryLength.getValue();
	}
}
