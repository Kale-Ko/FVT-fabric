package me.flourick.fvt.mixin;

import java.util.Random;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.OnScreenText;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;

/**
 * FEATURES: Tool Breaking Warning, HUD Info, Mount Hunger, Crosshair, No Vignette, No Spyglass Overlay
 * 
 * @author Flourick
 */
@Mixin(InGameHud.class)
abstract class InGameHudMixin extends DrawableHelper
{
	@Final
	@Shadow
	private MinecraftClient client;

	@Final
	@Shadow
	private Random random;

	@Shadow
	private int ticks;

	@Shadow
	abstract LivingEntity getRiddenEntity();

	@Shadow
	abstract void renderVignetteOverlay(Entity entity);

	@Shadow
	abstract  void renderSpyglassOverlay(float scale);

	@Shadow
	abstract int getHeartCount(LivingEntity entity);

	@Shadow
	abstract int getHeartRows(int heartCount);

	@Shadow
	abstract PlayerEntity getCameraPlayer();

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo info)
	{
		FVT.VARS.tickToolWarningTicks();
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(MatrixStack matrixStack, float f, CallbackInfo info)
	{
		// renders on screen text only if not in debug or hud is hidden or if options don't say so
		if(this.client.options.debugEnabled || this.client.options.hudHidden || !FVT.OPTIONS.showHUDInfo.getValueRaw()) {
			return;
		}

		// HUD info moves to the top if chat is open
		if(FVT.MC.currentScreen instanceof ChatScreen) {
			OnScreenText.drawCoordinatesTextUpper(matrixStack);
			OnScreenText.drawLightLevelTextUpper(matrixStack);
			OnScreenText.drawPFTextUpper(matrixStack);
		}
		else {
			OnScreenText.drawCoordinatesTextLower(matrixStack);
			OnScreenText.drawLightLevelTextLower(matrixStack);
			OnScreenText.drawPFTextLower(matrixStack);
		}

		if(FVT.VARS.getToolWarningTextTicksLeft() > 0) {
			matrixStack.push();
			matrixStack.translate((double)(this.client.getWindow().getScaledWidth() / 2.0d), (double)(this.client.getWindow().getScaledHeight() / 2.0d), (double)this.getZOffset());
			matrixStack.scale(FVT.OPTIONS.toolWarningScale.getValueRaw().floatValue(), FVT.OPTIONS.toolWarningScale.getValueRaw().floatValue(), 1.0f);
			OnScreenText.drawToolWarningText(matrixStack);
			matrixStack.pop();
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderVignetteOverlay(Lnet/minecraft/entity/Entity;)V", ordinal = 0))
	private void hijackRenderVignetteOverlay(InGameHud igHud, Entity entity)
	{
		if(!FVT.OPTIONS.noVignette.getValueRaw()) {
			this.renderVignetteOverlay(entity);
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderSpyglassOverlay(F)V", ordinal = 0))
	private void hijackRenderSpyglassOverlay(InGameHud igHud, float scale)
	{
		if(!FVT.OPTIONS.noSpyglassOverlay.getValueRaw()) {
			this.renderSpyglassOverlay(scale);
		}
	}

	@Redirect(method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartRows(I)I", ordinal = 0))
	private int hijackGetHeartRows(InGameHud igHud, int heartCount)
	{
		// super rare thing but the air bubbles would overlap mount health if shown (ex. popping out of water and straight onto a horse), so yeah this fixes that
		if(this.getCameraPlayer() != null && this.getHeartCount(this.getRiddenEntity()) != 0 && FVT.MC.interactionManager.hasStatusBars()) {
			return this.getHeartRows(heartCount) + 1;
		}
		else {
			return this.getHeartRows(heartCount);
		}
	}

	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
	private void onRenderMountHealth(MatrixStack matrices, CallbackInfo info)
	{
		PlayerEntity playerEntity = this.getCameraPlayer();
		LivingEntity livingEntity = this.getRiddenEntity();
		int riddenEntityHearts = this.getHeartCount(livingEntity);

		// custom behavior only if these, else use vanillas impl
		if(playerEntity != null && riddenEntityHearts != 0 && FVT.MC.interactionManager.hasStatusBars()) {
			int playerFoodLevel = playerEntity.getHungerManager().getFoodLevel();
			int foodRectY = FVT.MC.getWindow().getScaledHeight() - 39;
			int foodRectX = FVT.MC.getWindow().getScaledWidth() / 2 + 91;

			// PLAYER FOOD
			FVT.MC.getProfiler().swap("food");

			for(int i = 0; i < 10; ++i) {
				int currentRowY = foodRectY;
				int currentFoodX = foodRectX - i*8 - 9;
				int hungerEffectU = 16;
				int hungerEffectBackgroundU = 0;

				if(playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
					hungerEffectU += 36;
					hungerEffectBackgroundU = 13;
				}

				// hunger bar bobbing effect if no saturation
				if(playerEntity.getHungerManager().getSaturationLevel() <= 0.0F && this.ticks % (playerFoodLevel * 3 + 1) == 0) {
					currentRowY = foodRectY + (this.random.nextInt(3) - 1);
				}

				this.drawTexture(matrices, currentFoodX, currentRowY, 16 + hungerEffectBackgroundU * 9, 27, 9, 9);
				if(i*2 + 1 < playerFoodLevel) {
					this.drawTexture(matrices, currentFoodX, currentRowY, hungerEffectU + 36, 27, 9, 9);
				}

				if(i*2 + 1 == playerFoodLevel) {
					this.drawTexture(matrices, currentFoodX, currentRowY, hungerEffectU + 45, 27, 9, 9);
				}
			}

			// MOUNT HEALTH
			FVT.MC.getProfiler().swap("mountHealth");

			int subRiddenEntityHealth = riddenEntityHearts;
			int riddenEntityHealth = (int)Math.ceil((double)livingEntity.getHealth());
			int mountRectY = foodRectY - 10;
			int mountRectX = foodRectX;
			int currentRowY = mountRectY;

			for(int i = 0; subRiddenEntityHealth > 0; i += 20) {
				int riddenEntityHealthRowOffset = Math.min(subRiddenEntityHealth, 10);
				subRiddenEntityHealth -= riddenEntityHealthRowOffset;

				for(int j = 0; j < riddenEntityHealthRowOffset; ++j) {
					int currentHeartX = mountRectX - j * 8 - 9;

					this.drawTexture(matrices, currentHeartX, currentRowY, 52, 9, 9, 9);
					if(j*2 + 1 + i < riddenEntityHealth) {
						this.drawTexture(matrices, currentHeartX, currentRowY, 88, 9, 9, 9);
					}

					if(j*2 + 1 + i == riddenEntityHealth) {
						this.drawTexture(matrices, currentHeartX, currentRowY, 97, 9, 9, 9);
					}
				}

				currentRowY -= 10;
			}

			info.cancel();
		}
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0))
	private void onRenderCrosshair(InGameHud igHud, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height)
	{
		int scaledWidth = this.client.getWindow().getScaledWidth();
		int scaledHeight = this.client.getWindow().getScaledHeight();

		matrixStack.push();
		matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2), (float)this.getZOffset());

		RenderSystem.enableBlend();
		if(FVT.OPTIONS.crosshairStaticColor.getValueRaw()) {
			// no idea, but it got removed in 1.17 so
			GL14.glBlendColor(FVT.OPTIONS.crosshairRedComponent.getValueRawNormalized().floatValue(), FVT.OPTIONS.crosshairGreenComponent.getValueRawNormalized().floatValue(), FVT.OPTIONS.crosshairBlueComponent.getValueRawNormalized().floatValue(), 1.0f);
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.CONSTANT_COLOR, GlStateManager.DstFactor.ZERO, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
		else {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
		
		matrixStack.scale(FVT.OPTIONS.crosshairScale.getValueRaw().floatValue(), FVT.OPTIONS.crosshairScale.getValueRaw().floatValue(), 1.0f);
		this.drawTexture(matrixStack, -15/2, -15/2, 0, 0, 15, 15);
		RenderSystem.disableBlend();
		matrixStack.pop();
	}
}
