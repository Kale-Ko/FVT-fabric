package me.flourick.fvt;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageSender;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.BuiltinRegistries;

import me.flourick.fvt.settings.FVTOptions;
import me.flourick.fvt.settings.FVTSettingsScreen;
import me.flourick.fvt.utils.FVTVars;

/**
 * Mod initializer, registers keybinds & their listeners and several tick callbacks.
 *
 * @author Flourick, jtenner
 */
public class FVT implements ClientModInitializer
{
	public static final Logger LOGGER = LoggerFactory.getLogger("FVT");

	public static FVT INSTANCE;
	public static MinecraftClient MC;
	public static FVTOptions OPTIONS;
	public static final FVTVars VARS = new FVTVars();

	private KeyBinding toolBreakingOverrideKeybind;

	@Override
	public void onInitializeClient()
	{
		INSTANCE = this;
		MC = MinecraftClient.getInstance();
		OPTIONS = new FVTOptions();

		registerKeybinds();
		registerCallbacks();
	}

	public boolean isToolBreakingOverriden()
	{
		return toolBreakingOverrideKeybind.isPressed();
	}

	private void handleFeatureKeybindPress(KeyBinding keybind, SimpleOption<Boolean> option, String key)
	{
		while(keybind.wasPressed()) {
			option.setValue(!option.getValue());

			if(FVT.OPTIONS.featureToggleMessages.getValue()) {
				if(option.getValue()) {
					FVT.MC.inGameHud.onChatMessage(BuiltinRegistries.MESSAGE_TYPE.get(MessageType.SYSTEM), Text.translatable("fvt.chat_messages_prefix", Text.translatable("fvt.feature.enabled", Text.translatable(key))), MessageSender.of(Text.of(null)));
				}
				else {
					FVT.MC.inGameHud.onChatMessage(BuiltinRegistries.MESSAGE_TYPE.get(MessageType.SYSTEM), Text.translatable("fvt.chat_messages_prefix", Text.translatable("fvt.feature.disabled", Text.translatable(key))), MessageSender.of(Text.of(null)));
				}
			}
		}
	}

	private void registerKeybinds()
	{
		KeyBinding openSettingsMenuKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.options.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		toolBreakingOverrideKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.tool_breaking_override", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "FVT"));
		KeyBinding fullbrightKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.fullbright", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding freecamKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.freecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding randomPlacementKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.random_placement", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding entityOutlineKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.entity_outline", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding autoAttackKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.trigger_autoattack", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding placementLockKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.placement_lock", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding invisibleOffhandKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.invisible_offhand", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));
		KeyBinding autoEatKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("fvt.feature.name.autoeat", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "FVT"));

		ClientTickEvents.END_WORLD_TICK.register(client ->
		{
			while(openSettingsMenuKeybind.wasPressed()) {
				FVT.MC.setScreen(new FVTSettingsScreen(FVT.MC.currentScreen));
			}

			handleFeatureKeybindPress(fullbrightKeybind, FVT.OPTIONS.fullbright, "fvt.feature.name.fullbright");
			handleFeatureKeybindPress(entityOutlineKeybind, FVT.OPTIONS.entityOutline, "fvt.feature.name.entity_outline");
			handleFeatureKeybindPress(autoAttackKeybind, FVT.OPTIONS.autoAttack, "fvt.feature.name.trigger_autoattack");
			handleFeatureKeybindPress(freecamKeybind, FVT.OPTIONS.freecam, "fvt.feature.name.freecam");
			handleFeatureKeybindPress(randomPlacementKeybind, FVT.OPTIONS.randomPlacement, "fvt.feature.name.random_placement");
			handleFeatureKeybindPress(placementLockKeybind, FVT.OPTIONS.placementLock, "fvt.feature.name.placement_lock");
			handleFeatureKeybindPress(invisibleOffhandKeybind, FVT.OPTIONS.invisibleOffhand, "fvt.feature.name.invisible_offhand");
			handleFeatureKeybindPress(autoEatKeybind, FVT.OPTIONS.autoEat, "fvt.feature.name.autoeat");
		});
	}

	private void registerCallbacks()
	{
		ClientTickEvents.END_CLIENT_TICK.register(client ->
		{
			if(FVT.MC.player == null && FVT.OPTIONS.freecam.getValue()) {
				// disables freecam if leaving a world
				FVT.OPTIONS.freecam.setValue(false);
			}
		});

		ClientTickEvents.END_WORLD_TICK.register(clientWorld ->
		{
			if(FVT.OPTIONS.toolWarning.getValue()) {
				ItemStack mainHandItem = FVT.MC.player.getStackInHand(Hand.MAIN_HAND);
				ItemStack offHandItem = FVT.MC.player.getStackInHand(Hand.OFF_HAND);

				int mainHandDurability = mainHandItem.getMaxDamage() - mainHandItem.getDamage();;
				int offHandDurability = offHandItem.getMaxDamage() - offHandItem.getDamage();

				if(mainHandItem.isDamaged() && mainHandItem != FVT.VARS.mainHandToolItemStack) {
					if(MathHelper.floor(mainHandItem.getMaxDamage() * 0.9f) < mainHandItem.getDamage() + 1 && mainHandDurability < 13) {
						FVT.VARS.toolDurability = mainHandDurability;
						FVT.VARS.toolHand = Hand.MAIN_HAND;
						FVT.VARS.resetToolWarningTicks();
					}
				}

				if(offHandItem.isDamaged() && offHandItem != FVT.VARS.offHandToolItemStack) {
					if(MathHelper.floor(offHandItem.getMaxDamage() * 0.9f) < offHandItem.getDamage() + 1 && offHandDurability < 13) {
						if(mainHandDurability == 0 || offHandDurability < mainHandDurability) {
							FVT.VARS.toolDurability = offHandDurability;
							FVT.VARS.toolHand = Hand.OFF_HAND;
							FVT.VARS.resetToolWarningTicks();
						}
					}
				}

				FVT.VARS.mainHandToolItemStack = mainHandItem;
				FVT.VARS.offHandToolItemStack = offHandItem;
			}

			if(FVT.OPTIONS.autoEat.getValue()) {
				int foodLevel = FVT.MC.player.getHungerManager().getFoodLevel();

				// checks if we hungry and have food in your offhand
				if(foodLevel < 20 && FVT.MC.player.getOffHandStack().isFood()) {
					// either we low on health so eat anyway or hunger is low enough for the food to be fully utilized
					if(FVT.MC.player.getOffHandStack().getItem().getFoodComponent().getHunger() + foodLevel <= 20 || FVT.MC.player.getHealth() <= 12.0f) {
						FVT.MC.options.useKey.setPressed(true);
						FVT.VARS.autoEating = true;
					}
					else if(FVT.VARS.autoEating) {
						FVT.VARS.autoEating = false;
						FVT.MC.options.useKey.setPressed(false);
					}
				}
				else if(FVT.VARS.autoEating) {
					FVT.VARS.autoEating = false;
					FVT.MC.options.useKey.setPressed(false);
				}
			}
			else if(FVT.VARS.autoEating) {
				// reset in case user turned autoeat off mid-eating
				FVT.VARS.autoEating = false;
				FVT.MC.options.useKey.setPressed(false);
			}
		});

		// the entirety of AutoAttack
		ClientTickEvents.START_WORLD_TICK.register(clientWorld ->
		{
			if(FVT.OPTIONS.autoAttack.getValue() && FVT.MC.currentScreen == null) {
				if(FVT.MC.crosshairTarget != null && FVT.MC.crosshairTarget.getType() == Type.ENTITY && FVT.MC.player.getAttackCooldownProgress(0.0f) >= 1.0f) {
					if(((EntityHitResult)FVT.MC.crosshairTarget).getEntity() instanceof LivingEntity) {
						LivingEntity livingEntity = (LivingEntity)((EntityHitResult)FVT.MC.crosshairTarget).getEntity();

						if(livingEntity.isAttackable() && (livingEntity.hurtTime == 0 || livingEntity instanceof WitherEntity) && livingEntity.isAlive() && !(livingEntity instanceof PlayerEntity)) {
							FVT.MC.interactionManager.attackEntity(FVT.MC.player, livingEntity);
							FVT.MC.player.swingHand(Hand.MAIN_HAND);
						}
					}
				}
			}
		});
	}
}
