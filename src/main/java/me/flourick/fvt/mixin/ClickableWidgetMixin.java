package me.flourick.fvt.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import me.flourick.fvt.utils.IClickableWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(ClickableWidget.class)
abstract class ClickableWidgetMixin implements IClickableWidget
{
	@Shadow
	private int x;
	@Shadow
    private int y;

	@Shadow
	protected int width;
	@Shadow
    protected int height;

	@Shadow
	protected boolean hovered;
	@Shadow
    public boolean visible;

	@Shadow
	public abstract void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta);

	@Override
	public void FVT_renderWithoutTooltip(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		if (!this.visible) {
            return;
        }

        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        this.renderButton(matrices, mouseX, mouseY, delta);
	}
}
