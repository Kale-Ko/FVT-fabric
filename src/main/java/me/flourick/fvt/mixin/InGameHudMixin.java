package me.flourick.fvt.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.util.math.random.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.option.AttackIndicator;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import me.flourick.fvt.FVT;
import me.flourick.fvt.settings.FVTOptions.HotbarMode;
import me.flourick.fvt.utils.Color;
import me.flourick.fvt.utils.OnScreenText;

/**
 * FEATURES: Tool Breaking Warning, HUD Info, Mount Hunger, Crosshair, No Vignette, No Spyglass Overlay, Hotbar Autohide
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

	@Final
	@Shadow
	private static Identifier WIDGETS_TEXTURE;

	@Shadow
	private int ticks;

	@Shadow
	abstract LivingEntity getRiddenEntity();

	@Shadow
	abstract void renderVignetteOverlay(Entity entity);

	@Shadow
	abstract int getHeartCount(LivingEntity entity);

	@Shadow
	abstract int getHeartRows(int heartCount);

	@Shadow
	abstract void renderHotbarItem(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, int seed);

	@Shadow
	abstract PlayerEntity getCameraPlayer();

	private long FVT_firstHotbarOpenTimeLeft = 0L;
	private boolean FVT_firstHotbarOpen = true;

	private int FVT_getHotbarHideHeight()
	{
		// either entire hotbar + health, armor... or just the items part
		int adjustment = FVT.OPTIONS.autoHideHotbarMode.getValue() == HotbarMode.FULL ? 70 : 23;

		return (int)(adjustment - (adjustment * FVT_getHotbarInteractionScalar()));
	}

	private float FVT_getHotbarInteractionScalar()
	{
		long delay = MathHelper.ceil(FVT.OPTIONS.autoHideHotbarTimeout.getValue() * 100.0D); // 1-10s max time left opened
		long closeDelay = FVT.OPTIONS.autoHideHotbarMode.getValue() == HotbarMode.FULL ? 400L : 250L; // 400/250ms closing animation
		long openDelay = FVT.OPTIONS.autoHideHotbarMode.getValue() == HotbarMode.FULL ? 160L : 80L; // 160/80ms opening animation

		if(FVT_firstHotbarOpen) {
			FVT_firstHotbarOpen = false;
			FVT_firstHotbarOpenTimeLeft = FVT.VARS.getHotbarLastInteractionTime();
		}

		long timeLeft;

		if(FVT_firstHotbarOpenTimeLeft > 0) {
			timeLeft = FVT_firstHotbarOpenTimeLeft - Util.getMeasuringTimeMs() + delay;

			// if the open animation ended we use the current time left minus the time the opening animation takes
			if(timeLeft < delay - openDelay) {
				timeLeft = (FVT.VARS.getHotbarLastInteractionTime() - Util.getMeasuringTimeMs() + delay) - openDelay;
			}
		}
		else {
			timeLeft = FVT.VARS.getHotbarLastInteractionTime() - Util.getMeasuringTimeMs() + delay;
		}

		float scalar = MathHelper.clamp((timeLeft > delay - openDelay ? delay - timeLeft : timeLeft) * (((float)delay / (float)closeDelay) / (float)delay), 0.0F, 1.0F);

		if(scalar <= 0.0F) {
			FVT_firstHotbarOpen = true;
			FVT_firstHotbarOpenTimeLeft = 0L;
		}

		return scalar;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void onTick(CallbackInfo info)
	{
		FVT.VARS.tickToolWarningTicks();
	}

	@Inject(method = "renderSpyglassOverlay", at = @At("HEAD"), cancellable = true)
	private void onRenderSpyglassOverlay(CallbackInfo info)
	{
		if(FVT.OPTIONS.noSpyglassOverlay.getValue()) {
			info.cancel();
		}
	}

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(MatrixStack matrixStack, float f, CallbackInfo info)
	{
		// renders on screen text only if not in debug or hud is hidden or if options don't say so
		if(this.client.options.debugEnabled || this.client.options.hudHidden) {
			return;
		}

		if(FVT.OPTIONS.showHUDInfo.getValue()) {
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
		}

		if(FVT.VARS.getToolWarningTextTicksLeft() > 0) {
			matrixStack.push();
			matrixStack.translate((double)(this.client.getWindow().getScaledWidth() / 2.0d), (double)(this.client.getWindow().getScaledHeight() / 2.0d), (double)this.getZOffset());
			matrixStack.scale(FVT.OPTIONS.toolWarningScale.getValue().floatValue(), FVT.OPTIONS.toolWarningScale.getValue().floatValue(), 1.0f);
			OnScreenText.drawToolWarningText(matrixStack);
			matrixStack.pop();
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderVignetteOverlay(Lnet/minecraft/entity/Entity;)V", ordinal = 0))
	private void hijackRenderVignetteOverlay(InGameHud igHud, Entity entity)
	{
		if(!FVT.OPTIONS.noVignette.getValue()) {
			this.renderVignetteOverlay(entity);
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountJumpBar(Lnet/minecraft/client/util/math/MatrixStack;I)V", ordinal = 0))
	private void hijackRenderMountJumpBar(InGameHud igHud, MatrixStack matrices, int x)
	{
		// makes it so jump bar is only visible while actually jumping
		if(FVT.MC.options.jumpKey.isPressed()) {
			boolean autoHideHotbar = FVT.OPTIONS.autoHideHotbar.getValue();

			if(autoHideHotbar) {
				matrices.push();
				matrices.translate(0, FVT_getHotbarHideHeight(), this.getZOffset());
			}

			igHud.renderMountJumpBar(matrices, x);

			if(autoHideHotbar) {
				matrices.pop();
			}
		}
		else if(FVT.MC.interactionManager.hasExperienceBar()) {
			igHud.renderExperienceBar(matrices, x);
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

	@Inject(method = "renderStatusBars", at = @At("HEAD"))
	private void onRenderStatusBarsBegin(MatrixStack matrices, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoHideHotbar.getValue()) {
			matrices.push();
			matrices.translate(0, FVT_getHotbarHideHeight(), this.getZOffset());
		}
	}

	@Inject(method = "renderStatusBars", at = @At("RETURN"))
	private void onRenderStatusBarsEnd(MatrixStack matrices, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoHideHotbar.getValue()) {
			matrices.pop();
		}
	}
	
	@Inject(method = "renderMountHealth", at = @At("HEAD"), cancellable = true)
	private void onRenderMountHealth(MatrixStack matrices, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoHideHotbar.getValue()) {
			matrices.push();
			matrices.translate(0, FVT_getHotbarHideHeight(), this.getZOffset());
		}

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

			if(FVT.OPTIONS.autoHideHotbar.getValue()) {
				matrices.pop();
			}

			info.cancel();
		}
	}

	@Inject(method = "renderMountHealth", at = @At("RETURN"))
	private void onRenderMountHealthEnd(MatrixStack matrices, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoHideHotbar.getValue()) {
			matrices.pop();
		}
	}

	@Inject(method = "renderExperienceBar", at = @At("HEAD"))
	private void onRenderExperienceBarBegin(MatrixStack matrices, int x, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoHideHotbar.getValue()) {
			matrices.push();
			matrices.translate(0, FVT_getHotbarHideHeight(), this.getZOffset());
		}
	}

	@Inject(method = "renderExperienceBar", at = @At("RETURN"))
	private void onRenderExperienceBarEnd(MatrixStack matrices, int x, CallbackInfo info)
	{
		if(FVT.OPTIONS.autoHideHotbar.getValue()) {
			matrices.pop();
		}
	}

	@Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
	private void onRenderHotbar(float tickDelta, MatrixStack matrices, CallbackInfo info)
	{
		// couldn't just simply push & pop into matrices becouse only HALF OF THE FUCKING FUNCTION USES THEM, the other half is still on the old system... ugh.
		// so yeah enjoy the entire function being rewritten, glorious!
		if(FVT.OPTIONS.autoHideHotbar.getValue()) {
			PlayerEntity playerEntity = this.getCameraPlayer();
			
			if(playerEntity != null) {
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShader(GameRenderer::getPositionTexShader);
				RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);

				int scaledWidth = this.client.getWindow().getScaledWidth();
				int scaledHeight = this.client.getWindow().getScaledHeight() + FVT_getHotbarHideHeight();
				int scaledHalfWidth = scaledWidth / 2;

				ItemStack itemStack = playerEntity.getOffHandStack();
				Arm arm = playerEntity.getMainArm().getOpposite();
				
				int zOffset = this.getZOffset();
				
				this.setZOffset(-90);
				this.drawTexture(matrices, scaledHalfWidth - 91, scaledHeight - 22, 0, 0, 182, 22);
				this.drawTexture(matrices, scaledHalfWidth - 91 - 1 + playerEntity.getInventory().selectedSlot * 20, scaledHeight - 22 - 1, 0, 22, 24, 22);

				if(!itemStack.isEmpty()) {
					if(arm == Arm.LEFT) {
						this.drawTexture(matrices, scaledHalfWidth - 91 - 29, scaledHeight - 23, 24, 22, 29, 24);
					}
					else {
						this.drawTexture(matrices, scaledHalfWidth + 91, scaledHeight - 23, 53, 22, 29, 24);
					}
				}

				this.setZOffset(zOffset);
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();

				int m = 1;
				int q;
				int r;
				int s;

				for(q = 0; q < 9; ++q) {
					r = scaledHalfWidth - 90 + q * 20 + 2;
					s = scaledHeight - 16 - 3;
					this.renderHotbarItem(r, s, tickDelta, playerEntity, (ItemStack)playerEntity.getInventory().main.get(q), m++);
				}

				if(!itemStack.isEmpty()) {
					q = scaledHeight - 16 - 3;
					if (arm == Arm.LEFT) {
						this.renderHotbarItem(scaledHalfWidth - 91 - 26, q, tickDelta, playerEntity, itemStack, m++);
					} else {
						this.renderHotbarItem(scaledHalfWidth + 91 + 10, q, tickDelta, playerEntity, itemStack, m++);
					}
				}

				if(this.client.options.getAttackIndicator().getValue() == AttackIndicator.HOTBAR) {
					float f = this.client.player.getAttackCooldownProgress(0.0F);

					if(f < 1.0F) {
						r = scaledHeight - 20;
						s = scaledHalfWidth + 91 + 6;

						if(arm == Arm.RIGHT) {
							s = scaledHalfWidth - 91 - 22;
						}

						RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
						int t = (int)(f * 19.0F);
						RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
						
						this.drawTexture(matrices, s, r, 0, 94, 18, 18);
						this.drawTexture(matrices, s, r + 18 - t, 18, 112 - t, 18, t);
					}
				}

				RenderSystem.disableBlend();
			}

			info.cancel();
		}
	}

	@Redirect(method = "renderCrosshair", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V", ordinal = 0), require = 0)
	private void onRenderCrosshair(InGameHud igHud, MatrixStack matrixStack, int x, int y, int u, int v, int width, int height)
	{
		int scaledWidth = this.client.getWindow().getScaledWidth();
		int scaledHeight = this.client.getWindow().getScaledHeight();

		matrixStack.push();
		matrixStack.translate((float)(scaledWidth / 2), (float)(scaledHeight / 2), (float)this.getZOffset());

		if(FVT.OPTIONS.crosshairStaticColor.getValue()) {
			RenderSystem.setShaderColor(Color.normalize(FVT.OPTIONS.crosshairStaticColorRed.getValue()), Color.normalize(FVT.OPTIONS.crosshairStaticColorGreen.getValue()), Color.normalize(FVT.OPTIONS.crosshairStaticColorBlue.getValue()), Color.normalize(FVT.OPTIONS.crosshairStaticColorAlpha.getValue()));
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
		else {
			RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);
		}
	
		matrixStack.scale(FVT.OPTIONS.crosshairScale.getValue().floatValue(), FVT.OPTIONS.crosshairScale.getValue().floatValue(), 1.0f);
		this.drawTexture(matrixStack, -15/2, -15/2, 0, 0, 15, 15);
		matrixStack.pop();
	}
}
