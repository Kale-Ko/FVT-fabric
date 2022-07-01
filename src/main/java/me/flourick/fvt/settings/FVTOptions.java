package me.flourick.fvt.settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonParser;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.option.SimpleOption.TooltipFactoryGetter;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

import me.flourick.fvt.FVT;
import me.flourick.fvt.utils.ISimpleOption;

/**
 * All features this mod offers, also handles loading & saving to file.
 * 
 * @author Flourick
 */
public class FVTOptions
{
	private File file;
	private Map<String, SimpleOption<?>> features;

	// all the FEATURES
	public final SimpleOption<ButtonPlacement> buttonPosition;
	public final SimpleOption<Boolean> featureToggleMessages;
	public final SimpleOption<Double> crosshairScale;
	public final SimpleOption<Boolean> crosshairStaticColor;
	public final SimpleOption<Integer> crosshairStaticColorRed;
	public final SimpleOption<Integer> crosshairStaticColorGreen;
	public final SimpleOption<Integer> crosshairStaticColorBlue;
	public final SimpleOption<Integer> crosshairStaticColorAlpha;
	public final SimpleOption<Boolean> disableWToSprint;
	public final SimpleOption<Boolean> sendDeathCoordinates;
	public final SimpleOption<CoordinatesPosition> coordinatesPosition;
	public final SimpleOption<Boolean> showHUDInfo;
	public final SimpleOption<Boolean> noToolBreaking;
	public final SimpleOption<Boolean> toolWarning;
	public final SimpleOption<Double>  toolWarningScale;
	public final SimpleOption<ToolWarningPosition> toolWarningPosition;
	public final SimpleOption<Integer> cloudHeight;
	public final SimpleOption<Boolean> entityOutline;
	public final SimpleOption<Boolean> entityOutlineAnimals;
	public final SimpleOption<Boolean> entityOutlineMobs;
	public final SimpleOption<Boolean> entityOutlinePlayers;
	public final SimpleOption<Boolean> entityOutlineMisc;
	public final SimpleOption<Boolean> fullbright;
	public final SimpleOption<Boolean> randomPlacement;
	public final SimpleOption<Boolean> noNetherFog;
	public final SimpleOption<Boolean> noBlockBreakParticles;
	public final SimpleOption<Boolean> noPotionParticles;
	public final SimpleOption<Boolean> noVignette;
	public final SimpleOption<Boolean> noSpyglassOverlay;
	public final SimpleOption<Boolean> autoReconnect;
	public final SimpleOption<Integer> autoReconnectAttempts;
	public final SimpleOption<Boolean> refillHand;
	public final SimpleOption<Boolean> autoEat;
	public final SimpleOption<Boolean> autoAttack;
	public final SimpleOption<Boolean> freecam;
	public final SimpleOption<Boolean> freecamHightlightPlayer;
	public final SimpleOption<Boolean> autoTotem;
	public final SimpleOption<Integer> useDelay;
	public final SimpleOption<Integer> creativeBreakDelay;
	public final SimpleOption<Boolean> placementLock;
	public final SimpleOption<Boolean> containerButtons;
	public final SimpleOption<Boolean> inventoryButton;
	public final SimpleOption<Boolean> horseStats;
	public final SimpleOption<Boolean> invisibleOffhand;
	public final SimpleOption<Boolean> autoHideHotbar;
	public final SimpleOption<HotbarMode> autoHideHotbarMode;
	public final SimpleOption<Integer> autoHideHotbarTimeout;
	public final SimpleOption<Boolean> autoHideHotbarUse;
	public final SimpleOption<Boolean> autoHideHotbarItem;
	public final SimpleOption<Boolean> attackThrough;
	public final SimpleOption<Boolean> autoElytra;
	public final SimpleOption<Boolean> fastTrade;
	public final SimpleOption<DamageTiltMode> damageTilt;
	public final SimpleOption<Boolean> noBreakSwapStop;

	public FVTOptions()
	{
		this.file = new File(FVT.MC.runDirectory, "config/fvt.properties");
		this.features = new HashMap<String, SimpleOption<?>>();

		// FEATURES CREATION
		buttonPosition = new SimpleOption<ButtonPlacement>(
			"fvt.feature.name.button_position", 
			tooltip("fvt.feature.name.button_position.tooltip", ButtonPlacement.RIGHT), 
			SimpleOption.enumValueText(), 
			new SimpleOption.PotentialValuesBasedCallbacks<ButtonPlacement>(Arrays.asList(ButtonPlacement.values()), 
			Codec.INT.xmap(ButtonPlacement::byId, ButtonPlacement::getId)), ButtonPlacement.RIGHT, value -> {}
		);
		features.put("buttonPosition", buttonPosition);

		featureToggleMessages = SimpleOption.ofBoolean(
			"fvt.feature.name.feature_toggle_messages", 
			tooltip("fvt.feature.name.feature_toggle_messages.tooltip", true), 
			true
		);
		features.put("featureToggleMessages", featureToggleMessages);

		crosshairScale = new SimpleOption<Double>(
			"fvt.feature.name.crosshair_scale", 
			tooltip("fvt.feature.name.crosshair_scale.tooltip", 1.0), 
			FVTOptions::getPercentValueText, 
			new SimpleOption.ValidatingIntSliderCallbacks(0, 100).withModifier(value -> (double)value / 20.0, value -> (int)(value * 20.0)), 
			Codec.doubleRange(0.0, 5.0), 1.0, value -> {}
		);
		features.put("crosshairScale", crosshairScale);

		crosshairStaticColor = SimpleOption.ofBoolean(
			"fvt.feature.name.crosshair_static_color", 
			tooltip("fvt.feature.name.crosshair_static_color.tooltip", false), 
			false
		);
		features.put("crosshairStaticColor", crosshairStaticColor);

		crosshairStaticColorRed = new SimpleOption<Integer>(
			"fvt.feature.name.crosshair_static_color.red",
			tooltip("fvt.feature.name.crosshair_static_color.red.tooltip", 255),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 255, value -> {}
		);
		features.put("crosshairStaticColorRed", crosshairStaticColorRed);

		crosshairStaticColorGreen = new SimpleOption<Integer>(
			"fvt.feature.name.crosshair_static_color.green",
			tooltip("fvt.feature.name.crosshair_static_color.green.tooltip", 255),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 255, value -> {}
		);
		features.put("crosshairStaticColorGreen", crosshairStaticColorGreen);

		crosshairStaticColorBlue = new SimpleOption<Integer>(
			"fvt.feature.name.crosshair_static_color.blue",
			tooltip("fvt.feature.name.crosshair_static_color.blue.tooltip", 255),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 255, value -> {}
		);
		features.put("crosshairStaticColorBlue", crosshairStaticColorBlue);

		crosshairStaticColorAlpha = new SimpleOption<Integer>(
			"fvt.feature.name.crosshair_static_color.alpha",
			tooltip("fvt.feature.name.crosshair_static_color.alpha.tooltip", 255),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(0, 255), 255, value -> {}
		);
		features.put("crosshairStaticColorAlpha", crosshairStaticColorAlpha);

		disableWToSprint = SimpleOption.ofBoolean(
			"fvt.feature.name.disable_w_to_sprint", 
			tooltip("fvt.feature.name.disable_w_to_sprint.tooltip", true), 
			true
		);
		features.put("disableWToSprint", disableWToSprint);

		sendDeathCoordinates = SimpleOption.ofBoolean(
			"fvt.feature.name.send_death_coordinates", 
			tooltip("fvt.feature.name.send_death_coordinates.tooltip", true), 
			true
		);
		features.put("sendDeathCoordinates", sendDeathCoordinates);

		coordinatesPosition = new SimpleOption<CoordinatesPosition>(
			"fvt.feature.name.hud_coordinates", 
			tooltip("fvt.feature.name.hud_coordinates.tooltip", CoordinatesPosition.VERTICAL), 
			SimpleOption.enumValueText(), 
			new SimpleOption.PotentialValuesBasedCallbacks<CoordinatesPosition>(Arrays.asList(CoordinatesPosition.values()), 
			Codec.INT.xmap(CoordinatesPosition::byId, CoordinatesPosition::getId)), CoordinatesPosition.VERTICAL, value -> {}
		);
		features.put("coordinatesPosition", coordinatesPosition);

		showHUDInfo = SimpleOption.ofBoolean(
			"fvt.feature.name.show_info", 
			tooltip("fvt.feature.name.show_info.tooltip", false), 
			false
		);
		features.put("showHUDInfo", showHUDInfo);

		noToolBreaking = SimpleOption.ofBoolean(
			"fvt.feature.name.no_tool_breaking", 
			tooltip("fvt.feature.name.no_tool_breaking.tooltip", false), 
			false
		);
		features.put("noToolBreaking", noToolBreaking);

		toolWarning = SimpleOption.ofBoolean(
			"fvt.feature.name.tool_warning", 
			tooltip("fvt.feature.name.tool_warning.tooltip", true), 
			true
		);
		features.put("toolWarning", toolWarning);

		toolWarningScale = new SimpleOption<Double>(
			"fvt.feature.name.tool_warning.scale", 
			tooltip("fvt.feature.name.tool_warning.scale.tooltip", 1.5), 
			FVTOptions::getPercentValueText, 
			new SimpleOption.ValidatingIntSliderCallbacks(0, 80).withModifier(value -> (double)value / 20.0, value -> (int)(value * 20.0)), 
			Codec.doubleRange(0.0, 4.0), 1.5, value -> {}
		);
		features.put("toolWarningScale", toolWarningScale);

		toolWarningPosition = new SimpleOption<ToolWarningPosition>(
			"fvt.feature.name.tool_warning.position", 
			tooltip("fvt.feature.name.tool_warning.position.tooltip", ToolWarningPosition.BOTTOM), 
			SimpleOption.enumValueText(), 
			new SimpleOption.PotentialValuesBasedCallbacks<ToolWarningPosition>(Arrays.asList(ToolWarningPosition.values()), 
			Codec.INT.xmap(ToolWarningPosition::byId, ToolWarningPosition::getId)), ToolWarningPosition.BOTTOM, value -> {}
		);
		features.put("toolWarningPosition", toolWarningPosition);

		cloudHeight = new SimpleOption<Integer>(
			"fvt.feature.name.cloud_height",
			tooltip("fvt.feature.name.cloud_height.tooltip", 192),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(-64, 320), 192, value -> {}
		);
		features.put("cloudHeight", cloudHeight);

		entityOutline = SimpleOption.ofBoolean(
			"fvt.feature.name.entity_outline", 
			tooltip("fvt.feature.name.entity_outline.tooltip", false), 
			false
		);
		features.put("entityOutline", entityOutline);

		entityOutlineAnimals = SimpleOption.ofBoolean(
			"fvt.feature.name.entity_outline_animals", 
			tooltip("fvt.feature.name.entity_outline_animals.tooltip", true), 
			true
		);
		features.put("entityOutline", entityOutline);

		entityOutlineMobs = SimpleOption.ofBoolean(
			"fvt.feature.name.entity_outline_mobs", 
			tooltip("fvt.feature.name.entity_outline_mobs.tooltip", true), 
			true
		);
		features.put("entityOutline", entityOutline);

		entityOutlinePlayers = SimpleOption.ofBoolean(
			"fvt.feature.name.entity_outline_players", 
			tooltip("fvt.feature.name.entity_outline_players.tooltip", false), 
			false
		);
		features.put("entityOutline", entityOutline);

		entityOutlineMisc = SimpleOption.ofBoolean(
			"fvt.feature.name.entity_outline_misc", 
			tooltip("fvt.feature.name.entity_outline_misc.tooltip", false), 
			false
		);
		features.put("entityOutline", entityOutline);

		fullbright = SimpleOption.ofBoolean(
			"fvt.feature.name.fullbright", 
			tooltip("fvt.feature.name.fullbright.tooltip", false), 
			false
		);
		features.put("fullbright", fullbright);

		randomPlacement = SimpleOption.ofBoolean(
			"fvt.feature.name.random_placement", 
			tooltip("fvt.feature.name.random_placement.tooltip", false), 
			false
		);
		features.put("randomPlacement", randomPlacement);

		noNetherFog = SimpleOption.ofBoolean(
			"fvt.feature.name.no_nether_fog", 
			tooltip("fvt.feature.name.no_nether_fog.tooltip", false), 
			false
		);
		features.put("noNetherFog", noNetherFog);

		noBlockBreakParticles = SimpleOption.ofBoolean(
			"fvt.feature.name.no_block_break_particles", 
			tooltip("fvt.feature.name.no_block_break_particles.tooltip", false), 
			false
		);
		features.put("noBlockBreakParticles", noBlockBreakParticles);

		noPotionParticles = SimpleOption.ofBoolean(
			"fvt.feature.name.no_potion_particles", 
			tooltip("fvt.feature.name.no_potion_particles.tooltip", true), 
			true
		);
		features.put("noPotionParticles", noPotionParticles);

		noVignette = SimpleOption.ofBoolean(
			"fvt.feature.name.no_vignette", 
			tooltip("fvt.feature.name.no_vignette.tooltip", false), 
			false
		);
		features.put("noVignette", noVignette);

		noSpyglassOverlay = SimpleOption.ofBoolean(
			"fvt.feature.name.no_spyglass_overlay", 
			tooltip("fvt.feature.name.no_spyglass_overlay.tooltip", false), 
			false
		);
		features.put("noSpyglassOverlay", noSpyglassOverlay);

		autoReconnect = SimpleOption.ofBoolean(
			"fvt.feature.name.auto_reconnect", 
			tooltip("fvt.feature.name.auto_reconnect.tooltip", true), 
			true
		);
		features.put("autoReconnect", autoReconnect);

		autoReconnectAttempts = new SimpleOption<Integer>(
			"fvt.feature.name.auto_reconnect_attempts",
			tooltip("fvt.feature.name.auto_reconnect_attempts.tooltip", -1),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(-1, 50), -1, value -> {}
		);
		features.put("autoReconnectAttempts", autoReconnectAttempts);

		refillHand = SimpleOption.ofBoolean(
			"fvt.feature.name.refill_hand", 
			tooltip("fvt.feature.name.refill_hand.tooltip", false), 
			false
		);
		features.put("refillHand", refillHand);

		autoEat = SimpleOption.ofBoolean(
			"fvt.feature.name.autoeat", 
			tooltip("fvt.feature.name.autoeat.tooltip", false), 
			false
		);
		features.put("autoEat", autoEat);

		autoAttack = SimpleOption.ofBoolean(
			"fvt.feature.name.trigger_autoattack", 
			tooltip("fvt.feature.name.trigger_autoattack.tooltip", false), 
			false
		);
		features.put("autoAttack", autoAttack);

		freecam = SimpleOption.ofBoolean(
			"fvt.feature.name.freecam", 
			tooltip("fvt.feature.name.freecam.tooltip", false), 
			false
		);
		// features.put("freecam", freecam); // don't save as there is no point
		
        freecamHightlightPlayer = SimpleOption.ofBoolean(
			"fvt.feature.name.freecam_hightlight_player", 
			tooltip("fvt.feature.name.freecam_hightlight_player.tooltip", true), 
			true
		);

		autoTotem = SimpleOption.ofBoolean(
			"fvt.feature.name.autototem", 
			tooltip("fvt.feature.name.autototem.tooltip", false), 
			false
		);
		features.put("autoTotem", autoTotem);

		useDelay = new SimpleOption<Integer>(
			"fvt.feature.name.use_delay",
			tooltip("fvt.feature.name.use_delay.tooltip", 4),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(1, 20), 4, value -> {}
		);
		features.put("useDelay", useDelay);

		creativeBreakDelay = new SimpleOption<Integer>(
			"fvt.feature.name.creative_break_delay",
			tooltip("fvt.feature.name.creative_break_delay.tooltip", 4),
			FVTOptions::getValueText,
			new SimpleOption.ValidatingIntSliderCallbacks(1, 10), 6, value -> {}
		);
		features.put("creativeBreakDelay", creativeBreakDelay);

		placementLock = SimpleOption.ofBoolean(
			"fvt.feature.name.placement_lock", 
			tooltip("fvt.feature.name.placement_lock.tooltip", false), 
			false
		);
		features.put("placementLock", placementLock);

		containerButtons = SimpleOption.ofBoolean(
			"fvt.feature.name.container_buttons", 
			tooltip("fvt.feature.name.container_buttons.tooltip", false), 
			false
		);
		features.put("containerButtons", containerButtons);

		inventoryButton = SimpleOption.ofBoolean(
			"fvt.feature.name.inventory_button", 
			tooltip("fvt.feature.name.inventory_button.tooltip", false), 
			false
		);
		features.put("inventoryButton", inventoryButton);

		horseStats = SimpleOption.ofBoolean(
			"fvt.feature.name.horse_stats", 
			tooltip("fvt.feature.name.horse_stats.tooltip", true), 
			true
		);
		features.put("horseStats", horseStats);

		invisibleOffhand = SimpleOption.ofBoolean(
			"fvt.feature.name.invisible_offhand", 
			tooltip("fvt.feature.name.invisible_offhand.tooltip", false), 
			false
		);
		features.put("invisibleOffhand", invisibleOffhand);

		autoHideHotbar = SimpleOption.ofBoolean(
			"fvt.feature.name.auto_hide_hotbar", 
			tooltip("fvt.feature.name.auto_hide_hotbar.tooltip", false), 
			false
		);
		features.put("autoHideHotbar", autoHideHotbar);

		autoHideHotbarMode = new SimpleOption<HotbarMode>(
			"fvt.feature.name.auto_hide_hotbar_mode", 
			tooltip("fvt.feature.name.auto_hide_hotbar_mode.tooltip", HotbarMode.PARTIAL), 
			SimpleOption.enumValueText(), 
			new SimpleOption.PotentialValuesBasedCallbacks<HotbarMode>(Arrays.asList(HotbarMode.values()), 
			Codec.INT.xmap(HotbarMode::byId, HotbarMode::getId)), HotbarMode.PARTIAL, value -> {}
		);
		features.put("autoHideHotbarMode", autoHideHotbarMode);

		autoHideHotbarTimeout = new SimpleOption<Integer>(
			"fvt.feature.name.auto_hide_hotbar_timeout", 
			tooltipHT("fvt.feature.name.auto_hide_hotbar_timeout.tooltip", 20), 
			FVTOptions::getHTText, 
			new SimpleOption.ValidatingIntSliderCallbacks(10, 100), 20, value -> {}
		);
		features.put("autoHideHotbarTimeout", autoHideHotbarTimeout);

		autoHideHotbarUse = SimpleOption.ofBoolean(
			"fvt.feature.name.auto_hide_hotbar_use", 
			tooltip("fvt.feature.name.auto_hide_hotbar_use.tooltip", false), 
			false
		);
		features.put("autoHideHotbarUse", autoHideHotbarUse);

		autoHideHotbarItem = SimpleOption.ofBoolean(
			"fvt.feature.name.auto_hide_hotbar_item", 
			tooltip("fvt.feature.name.auto_hide_hotbar_item.tooltip", false), 
			false
		);
		features.put("autoHideHotbarItem", autoHideHotbarItem);

		attackThrough = SimpleOption.ofBoolean(
			"fvt.feature.name.attack_through", 
			tooltip("fvt.feature.name.attack_through.tooltip", true), 
			true
		);
		features.put("attackThrough", attackThrough);

		autoElytra = SimpleOption.ofBoolean(
			"fvt.feature.name.auto_elytra", 
			tooltip("fvt.feature.name.auto_elytra.tooltip", false), 
			false
		);
		features.put("autoElytra", autoElytra);

		fastTrade = SimpleOption.ofBoolean(
			"fvt.feature.name.fast_trade", 
			tooltip("fvt.feature.name.fast_trade.tooltip", true), 
			true
		);
		features.put("fastTrade", fastTrade);

		damageTilt = new SimpleOption<DamageTiltMode>(
			"fvt.feature.name.damage_tilt", 
			tooltip("fvt.feature.name.damage_tilt.tooltip", DamageTiltMode.DEFAULT), 
			SimpleOption.enumValueText(), 
			new SimpleOption.PotentialValuesBasedCallbacks<DamageTiltMode>(Arrays.asList(DamageTiltMode.values()), 
			Codec.INT.xmap(DamageTiltMode::byId, DamageTiltMode::getId)), DamageTiltMode.DEFAULT, value -> {}
		);
		features.put("damageTilt", damageTilt);

		noBreakSwapStop = SimpleOption.ofBoolean(
			"fvt.feature.name.no_break_swap_stop", 
			tooltip("fvt.feature.name.no_break_swap_stop.tooltip", true), 
			true
		);
		features.put("noBreakSwapStop", noBreakSwapStop);

		init();
	}

	private static <T> TooltipFactoryGetter<T> tooltip(String key, T defaultValue)
	{
		return client -> value -> {
			List<OrderedText> lines = new ArrayList<>();
			lines.addAll(client.textRenderer.wrapLines(Text.translatable(key), 220));

			if(defaultValue instanceof Double) {
				// double is mostly used with percent so should be fine, just leaving this in case I forgot and rage why it shows percent even tho it should not
				lines.add(Text.translatable("fvt.feature.default", (int)((double)defaultValue * 100.0)).append("%").formatted(Formatting.GRAY).asOrderedText());
			}
			else if(defaultValue instanceof Boolean) {
				lines.add(Text.translatable("fvt.feature.default", (boolean)defaultValue ? ScreenTexts.ON : ScreenTexts.OFF).formatted(Formatting.GRAY).asOrderedText());
			}
			else if(defaultValue instanceof TranslatableOption) {
				lines.add(Text.translatable("fvt.feature.default", ((TranslatableOption) defaultValue).getText()).formatted(Formatting.GRAY).asOrderedText());
			}
			else {
				lines.add(Text.translatable("fvt.feature.default", defaultValue).formatted(Formatting.GRAY).asOrderedText());
			}

			return lines;
		};
	}

	private static <T> TooltipFactoryGetter<T> tooltipHT(String key, T defaultValue)
	{
		return client -> value -> {
			List<OrderedText> lines = new ArrayList<>();
			lines.addAll(client.textRenderer.wrapLines(Text.translatable(key), 220));
			lines.add(Text.translatable("fvt.feature.default", (Integer)defaultValue / 10.0D).formatted(Formatting.GRAY).asOrderedText());
			return lines;
		};
	}

	public static Text getHTText(Text prefix, int value)
	{
		return Text.translatable("options.generic_value", prefix, value / 10.0D);
	}

	public static Text getValueText(Text prefix, int value)
	{
		return Text.translatable("options.generic_value", prefix, value);
	}

	public static Text getValueText(Text prefix, double value)
	{
		return Text.translatable("options.generic_value", prefix, value);
	}

	private static Text getPercentValueText(Text prefix, double value)
	{
		if(value == 0.0) {
			return ScreenTexts.composeToggleText(prefix, false);
		}
		
		return Text.translatable("options.percent_value", prefix, (int)(value * 100.0));
	}

	private void init()
	{
		if(!file.exists()) {
			file.getParentFile().mkdirs();
			write();
		}
		else {
			read();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> void resetFeature(SimpleOption<T> feature)
	{
		((ISimpleOption<T>)(Object) feature).FVT_setValueToDefault();
	}

	public void reset()
	{
		for(SimpleOption<?> feature : features.values()) {
			resetFeature(feature);
		}
	}

	public void write()
	{
		try(PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));) {
			printWriter.println("# FVT configuration. Do not edit here unless you know what you're doing!");
			printWriter.println("# Last save: " + DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy").format(LocalDateTime.now()));

			for(Entry<String, SimpleOption<?>> feature : features.entrySet()) {
				printWriter.println(feature.getKey() + "=" + feature.getValue().getValue());
			}
		}
		catch(Exception e) {
			FVT.LOGGER.error("Failed to write to 'fvt.properties':", e.toString());
		}
	}

	private <T> void parseFeatureLine(SimpleOption<T> option, String value)
	{
		DataResult<T> dataResult = option.getCodec().parse(JsonOps.INSTANCE, JsonParser.parseString(value));
		dataResult.error().ifPresent(partialResult -> FVT.LOGGER.warn("Skipping bad config option (" + value + "): " + partialResult.message()));
		dataResult.result().ifPresent(option::setValue);
	}

	private void read()
	{
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
			bufferedReader.lines().forEach((line) -> {
				if(line.startsWith("#")) {
					// skips comments
					return;
				}

				String[] v = line.split("=");

				if(v.length != 2) {
					FVT.LOGGER.warn("Skipping bad config option line!");
					return;
				}

				String key = v[0];
				String value = v[1];

				SimpleOption<?> option = features.get(key);

				if(option == null || value.isEmpty()) {
					FVT.LOGGER.warn("Skipping bad config option (" + value + ")" + " for " + key);
				}
				else {
					parseFeatureLine(option, value);
				}
			});
		}
		catch(IOException e) {
			FVT.LOGGER.error("Failed to read from 'fvt.properties':", e.toString());
		}
	}

	// ENUMS for options
	public enum ButtonPlacement implements TranslatableOption
	{
		RIGHT(0, "fvt.feature.name.button_position.right"),
		LEFT(1, "fvt.feature.name.button_position.left"),
		CENTER(2, "fvt.feature.name.button_position.center"),
		OUTSIDE(3, "fvt.feature.name.button_position.outside"),
		HIDDEN(4, "fvt.feature.name.button_position.hidden");

		private static final ButtonPlacement[] VALUES = (ButtonPlacement[])Arrays.stream(ButtonPlacement.values()).sorted(Comparator.comparingInt(ButtonPlacement::getId)).toArray(ButtonPlacement[]::new);;
		private final String translationKey;
		private final int id;

		private ButtonPlacement(int id, String translationKey)
		{
			this.id = id;
			this.translationKey = translationKey;
		}
		
		@Override
		public String toString()
		{
			return Integer.toString(getId());
		}

		@Override
		public int getId()
		{
			return this.id;
		}

		@Override
		public String getTranslationKey()
		{
			return this.translationKey;
		}

		public static ButtonPlacement byId(int id)
		{
			return VALUES[MathHelper.floorMod(id, VALUES.length)];
		}
	}

	public enum CoordinatesPosition implements TranslatableOption
	{
		VERTICAL(0, "fvt.feature.name.hud_coordinates.vertical"),
		HORIZONTAL(1, "fvt.feature.name.hud_coordinates.horizontal");

		private static final CoordinatesPosition[] VALUES = (CoordinatesPosition[])Arrays.stream(CoordinatesPosition.values()).sorted(Comparator.comparingInt(CoordinatesPosition::getId)).toArray(CoordinatesPosition[]::new);;
		private final String translationKey;
		private final int id;

		private CoordinatesPosition(int id, String translationKey)
		{
			this.id = id;
			this.translationKey = translationKey;
		}
		
		@Override
		public String toString()
		{
			return Integer.toString(getId());
		}

		@Override
		public int getId()
		{
			return this.id;
		}

		@Override
		public String getTranslationKey()
		{
			return this.translationKey;
		}

		public static CoordinatesPosition byId(int id)
		{
			return VALUES[MathHelper.floorMod(id, VALUES.length)];
		}
	}

	public enum ToolWarningPosition implements TranslatableOption
	{
		TOP(0, "fvt.feature.name.tool_warning.position.top"),
		BOTTOM(1, "fvt.feature.name.tool_warning.position.bottom");

		private static final ToolWarningPosition[] VALUES = (ToolWarningPosition[])Arrays.stream(ToolWarningPosition.values()).sorted(Comparator.comparingInt(ToolWarningPosition::getId)).toArray(ToolWarningPosition[]::new);;
		private final String translationKey;
		private final int id;

		private ToolWarningPosition(int id, String translationKey)
		{
			this.id = id;
			this.translationKey = translationKey;
		}
		
		@Override
		public String toString()
		{
			return Integer.toString(getId());
		}

		@Override
		public int getId()
		{
			return this.id;
		}

		@Override
		public String getTranslationKey()
		{
			return this.translationKey;
		}

		public static ToolWarningPosition byId(int id)
		{
			return VALUES[MathHelper.floorMod(id, VALUES.length)];
		}
	}

	public enum HotbarMode implements TranslatableOption
	{
		FULL(0, "fvt.feature.name.auto_hide_hotbar_mode.full"),
		PARTIAL(1, "fvt.feature.name.auto_hide_hotbar_mode.partial");

		private static final HotbarMode[] VALUES = (HotbarMode[])Arrays.stream(HotbarMode.values()).sorted(Comparator.comparingInt(HotbarMode::getId)).toArray(HotbarMode[]::new);;
		private final String translationKey;
		private final int id;

		private HotbarMode(int id, String translationKey)
		{
			this.id = id;
			this.translationKey = translationKey;
		}
		
		@Override
		public String toString()
		{
			return Integer.toString(getId());
		}

		@Override
		public int getId()
		{
			return this.id;
		}

		@Override
		public String getTranslationKey()
		{
			return this.translationKey;
		}

		public static HotbarMode byId(int id)
		{
			return VALUES[MathHelper.floorMod(id, VALUES.length)];
		}
	}
	
	public enum DamageTiltMode implements TranslatableOption
	{
		OFF(0, "options.off"),
		MINIMAL(1, "options.particles.minimal"),
		DEFAULT(1, "options.gamma.default");

		private static final DamageTiltMode[] VALUES = (DamageTiltMode[])Arrays.stream(DamageTiltMode.values()).sorted(Comparator.comparingInt(DamageTiltMode::getId)).toArray(DamageTiltMode[]::new);;
		private final String translationKey;
		private final int id;

		private DamageTiltMode(int id, String translationKey)
		{
			this.id = id;
			this.translationKey = translationKey;
		}
		
		@Override
		public String toString()
		{
			return Integer.toString(getId());
		}

		@Override
		public int getId()
		{
			return this.id;
		}

		@Override
		public String getTranslationKey()
		{
			return this.translationKey;
		}

		public static DamageTiltMode byId(int id)
		{
			return VALUES[MathHelper.floorMod(id, VALUES.length)];
		}
	}
}
