package me.flourick.fvt.settings;

import java.util.List;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.OrderableTooltip;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

public class FVTOptionButtonWidget extends ButtonWidget implements OrderableTooltip
{
	private final FVTOption<?> option;

	public FVTOptionButtonWidget(int x, int y, int width, int height, FVTOption<?> option, Text message, ButtonWidget.PressAction onPress)
	{
		super(x, y, width, height, message, onPress);
		this.option = option;
	}
 
	public List<OrderedText> getOrderedTooltip()
	{
		return option.getTooltip();
	}
}
