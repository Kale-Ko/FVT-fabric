package me.flourick.fvt.utils;

import java.util.HashSet;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import me.flourick.fvt.FVT;

/**
 * Adds three item management buttons to given screen.
 * 
 * @author Flourick
 */
public class ContainerButtons<T extends ScreenHandler>
{
	private final HandledScreen<T> screen;

	private final int buttonWidth;
	private final int buttonHeight;

	private int baseX;
	private int baseY;

	public ContainerButtons(HandledScreen<T> screen, int baseX, int baseY)
	{
		this.screen = screen;
		this.baseX = baseX;
		this.baseY = baseY;
		this.buttonWidth = 12;
		this.buttonHeight = 10;
	}

	public ContainerButtons(HandledScreen<T> screen, int baseX, int baseY, int buttonWidth, int buttonHeight)
	{
		this.screen = screen;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.baseX = baseX;
		this.baseY = baseY;
	}

	public void create()
	{
		((IScreen)screen).FVT_addDrawableSelectableChild(new FVTButtonWidget(baseX, baseY, buttonWidth, buttonHeight, Text.literal("⊽"), (buttonWidget) -> onDropButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			screen.renderTooltip(matrixStack, Text.translatable("fvt.feature.name.container_buttons.drop.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));

		((IScreen)screen).FVT_addDrawableSelectableChild(new FVTButtonWidget(baseX - buttonWidth - 2, baseY, buttonWidth, buttonHeight, Text.literal("⊻"), (buttonWidget) -> onGetButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			screen.renderTooltip(matrixStack, Text.translatable("fvt.feature.name.container_buttons.get.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));

		((IScreen)screen).FVT_addDrawableSelectableChild(new FVTButtonWidget(baseX - 2*buttonWidth - 4, baseY, buttonWidth, buttonHeight, Text.literal("⊼"), (buttonWidget) -> onStoreButtonClick()
		, (buttonWidget, matrixStack, i, j) -> {
			screen.renderTooltip(matrixStack, Text.translatable("fvt.feature.name.container_buttons.store.tooltip"), i, j + 8);
		}, new Color(150, 255, 255, 255), new Color(220, 255, 255, 255)));
	}

	public void setBasePos(int baseX, int baseY)
	{
		this.baseX = baseX;
		this.baseY = baseY;
	}

	private void onDropButtonClick()
	{
		int sz = screen.getScreenHandler().slots.size() - PlayerInventory.MAIN_SIZE;

		for(int i = 0; i < sz; i++) {
			Slot slot = screen.getScreenHandler().getSlot(i);

			if(slot.getStack().isEmpty()) {
				continue;
			}

			FVT.MC.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 1, SlotActionType.THROW, FVT.MC.player);
		}
	}

	private void onGetButtonClick()
	{
		int sz = screen.getScreenHandler().slots.size() - PlayerInventory.MAIN_SIZE;

		boolean onlyMatching = Screen.hasControlDown();
		HashSet<Item> inventoryItems = new HashSet<>();

		if(onlyMatching) {
			for(int i = sz; i < screen.getScreenHandler().slots.size(); i++) {
				Slot slot = screen.getScreenHandler().getSlot(i);
				ItemStack itemStack = slot.getStack();

				if(!itemStack.isEmpty()) {
					inventoryItems.add(itemStack.getItem());
				}
			}
		}

		for(int i = 0; i < sz; i++) {
			Slot slot = screen.getScreenHandler().getSlot(i);

			if(slot.getStack().isEmpty()) {
				continue;
			}

			if(onlyMatching) {
				if(inventoryItems.contains(slot.getStack().getItem())) {
					FVT.MC.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
				}

				continue;	
			}

			FVT.MC.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
		}
	}

	private void onStoreButtonClick()
	{
		int sz = screen.getScreenHandler().slots.size() - PlayerInventory.MAIN_SIZE;

		boolean onlyMatching = Screen.hasControlDown();
		HashSet<Item> inventoryItems = new HashSet<>();

		if(onlyMatching) {
			for(int i = 0; i < sz; i++) {
				Slot slot = screen.getScreenHandler().getSlot(i);
				ItemStack itemStack = slot.getStack();

				if(!itemStack.isEmpty()) {
					inventoryItems.add(itemStack.getItem());
				}
			}
		}

		for(int i = sz; i < screen.getScreenHandler().slots.size(); i++) {
			Slot slot = screen.getScreenHandler().getSlot(i);

			if(slot.getStack().isEmpty()) {
				continue;
			}

			if(onlyMatching) {
				if(inventoryItems.contains(slot.getStack().getItem())) {
					FVT.MC.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
				}

				continue;	
			}

			FVT.MC.interactionManager.clickSlot(screen.getScreenHandler().syncId, i, 0, SlotActionType.QUICK_MOVE, FVT.MC.player);
		}
	}
}
