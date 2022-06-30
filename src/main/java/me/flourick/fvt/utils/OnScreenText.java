package me.flourick.fvt.utils;

import org.apache.commons.lang3.StringUtils;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;

import me.flourick.fvt.FVT;
import me.flourick.fvt.settings.FVTOptions.CoordinatesPosition;
import me.flourick.fvt.settings.FVTOptions.ToolWarningPosition;

/**
 * Holder for static functions that draw text on the ingame HUD.
 * 
 * @author Flourick
 */
public class OnScreenText
{
	public static void drawCoordinatesTextLower(MatrixStack matrixStack)
	{
		if(FVT.OPTIONS.coordinatesPosition.getValue() == CoordinatesPosition.VERTICAL) {
			final String X = String.format("X: %.01f", getCurrentX());	
			final String Y = String.format("Y: %.01f", getCurrentY());
			final String Z = String.format("Z: %.01f", getCurrentZ());

			FVT.MC.textRenderer.drawWithShadow(matrixStack, X, 2, FVT.MC.getWindow().getScaledHeight() - 3*FVT.MC.textRenderer.fontHeight - 2, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Y, 2, FVT.MC.getWindow().getScaledHeight() - 2*FVT.MC.textRenderer.fontHeight - 1, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Z, 2, FVT.MC.getWindow().getScaledHeight() - FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
		}
		else {
			final String curLocText = String.format("XYZ: %.01f %.01f %.01f", getCurrentX(), getCurrentY(), getCurrentZ());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, curLocText, 2, FVT.MC.getWindow().getScaledHeight() - FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
		}
	}

	public static void drawCoordinatesTextUpper(MatrixStack matrixStack)
	{
		if(FVT.OPTIONS.coordinatesPosition.getValue() == CoordinatesPosition.VERTICAL) {
			final String X = String.format("X: %.01f", getCurrentX());		
			final String Y = String.format("Y: %.01f", getCurrentY());
			final String Z = String.format("Z: %.01f", getCurrentZ());

			FVT.MC.textRenderer.drawWithShadow(matrixStack, X, 2, 2, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Y, 2, 3 + FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, Z, 2, 4 + 2*FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
		}
		else {
			final String curLocText = String.format("XYZ: %.01f %.01f %.01f", getCurrentX(), getCurrentY(), getCurrentZ());
			FVT.MC.textRenderer.drawWithShadow(matrixStack, curLocText, 2, 2, Color.WHITE.getPacked());
		}
	}

	public static void drawPFTextLower(MatrixStack matrixStack)
	{
		final String PFText = String.format("P: %.02f (%s)", FVT.MC.gameRenderer.getCamera().getPitch(), getFacingDirection());
		FVT.MC.textRenderer.drawWithShadow(matrixStack, PFText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(PFText) - 1, FVT.MC.getWindow().getScaledHeight() - FVT.MC.textRenderer.fontHeight, Color.WHITE.getPacked());
	}

	public static void drawPFTextUpper(MatrixStack matrixStack)
	{
		final String PFText = String.format("P: %.02f (%s)", FVT.MC.gameRenderer.getCamera().getPitch(), getFacingDirection());
		FVT.MC.textRenderer.drawWithShadow(matrixStack, PFText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(PFText) - 1, 2, Color.WHITE.getPacked());
	}

	public static void drawLightLevelTextLower(MatrixStack matrixStack)
	{
		final String curYPRText = String.format("BL: %d", getBlockLightLevel());
		FVT.MC.textRenderer.drawWithShadow(matrixStack, curYPRText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(curYPRText) - 1, FVT.MC.getWindow().getScaledHeight() - 2*FVT.MC.textRenderer.fontHeight - 1, Color.WHITE.getPacked());
	}

	public static void drawLightLevelTextUpper(MatrixStack matrixStack)
	{
		final String curYPRText = String.format("BL: %d", getBlockLightLevel());
		FVT.MC.textRenderer.drawWithShadow(matrixStack, curYPRText, FVT.MC.getWindow().getScaledWidth() - FVT.MC.textRenderer.getWidth(curYPRText) - 1, FVT.MC.textRenderer.fontHeight + 3, Color.WHITE.getPacked());
	}

	public static void drawToolWarningText(MatrixStack matrixStack)
	{
		// last half a second fade-out
		int alpha = MathHelper.clamp(MathHelper.ceil(25.5f * FVT.VARS.getToolWarningTextTicksLeft()), 0, 255);

		int y;
		if(FVT.OPTIONS.toolWarningPosition.getValue() == ToolWarningPosition.TOP) {
			y = (int)((-(FVT.MC.getWindow().getScaledHeight() / 2 * 1/FVT.OPTIONS.toolWarningScale.getValue())) + (2/FVT.OPTIONS.toolWarningScale.getValue()));
		}
		else {
			y = (int)(((FVT.MC.getWindow().getScaledHeight() / 2 * 1/FVT.OPTIONS.toolWarningScale.getValue())) - (FVT.MC.textRenderer.fontHeight + 60/FVT.OPTIONS.toolWarningScale.getValue()));
		}

		final String ToolWarningText = FVT.VARS.toolHand.equals(Hand.MAIN_HAND) ? Text.translatable("fvt.feature.name.tool_warning.text.main_hand", FVT.VARS.toolDurability).getString() : Text.translatable("fvt.feature.name.tool_warning.text.offhand", FVT.VARS.toolDurability).getString();
		FVT.MC.textRenderer.drawWithShadow(matrixStack, ToolWarningText, (float) -(FVT.MC.textRenderer.getWidth(ToolWarningText) / 2), y, new Color(alpha, 255, 0, 0).getPacked());
	}

	// FUNCTIONS TO GET VARIOUS VALUES TO HUD

	private static int getBlockLightLevel()
	{
		return FVT.MC.world.getChunkManager().getLightingProvider().get(LightType.BLOCK).getLightLevel(FVT.OPTIONS.freecam.getValue() ? new BlockPos(FVT.MC.gameRenderer.getCamera().getPos().x, FVT.MC.gameRenderer.getCamera().getPos().y, FVT.MC.gameRenderer.getCamera().getPos().z)  : FVT.MC.getCameraEntity().getBlockPos());
	}

	private static String getFacingDirection()
	{
		return StringUtils.capitalize(Direction.fromRotation(FVT.MC.gameRenderer.getCamera().getYaw()).asString());
	}

	private static double getCurrentX()
	{
		return FVT.OPTIONS.freecam.getValue() ? FVT.MC.gameRenderer.getCamera().getPos().x : FVT.MC.player.getX();
	}

	private static double getCurrentY()
	{
		return FVT.OPTIONS.freecam.getValue() ? FVT.MC.gameRenderer.getCamera().getPos().y : FVT.MC.player.getY();
	}

	private static double getCurrentZ()
	{
		return FVT.OPTIONS.freecam.getValue() ? FVT.MC.gameRenderer.getCamera().getPos().z : FVT.MC.player.getZ();
	}
}
