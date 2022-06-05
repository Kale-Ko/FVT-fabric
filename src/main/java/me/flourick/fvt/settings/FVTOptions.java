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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import me.flourick.fvt.FVT;

import net.minecraft.text.TranslatableText;

/**
 * All features this mod offers, also handles loading & saving to file.
 * 
 * @author Flourick
 */
public class FVTOptions
{
	private File optionsFile;
	private Map<String, FVTOption<?>> savedFeatures;

	// all the FEATURES
	public final FVTCyclingOption buttonPosition;
	public final FVTBooleanOption featureToggleMessages;
	public final FVTDoubleOption  crosshairScale;
	public final FVTBooleanOption crosshairStaticColor;
	public final FVTDoubleOption  crosshairStaticColorRed;
	public final FVTDoubleOption  crosshairStaticColorGreen;
	public final FVTDoubleOption  crosshairStaticColorBlue;
	public final FVTDoubleOption  crosshairStaticColorAlpha;
	public final FVTBooleanOption disableWToSprint;
	public final FVTBooleanOption sendDeathCoordinates;
	public final FVTBooleanOption coordinatesPosition;
	public final FVTBooleanOption showInfo;
	public final FVTBooleanOption noToolBreaking;
	public final FVTBooleanOption toolWarning;
	public final FVTDoubleOption  toolWarningScale;
	public final FVTBooleanOption toolWarningPosition;
	public final FVTDoubleOption  cloudHeight;
	public final FVTBooleanOption entityOutline;
	public final FVTBooleanOption entityOutlineAnimals;
	public final FVTBooleanOption entityOutlineMobs;
	public final FVTBooleanOption entityOutlinePlayers;
	public final FVTBooleanOption entityOutlineMisc;
	public final FVTBooleanOption fullbright;
	public final FVTBooleanOption randomPlacement;
	public final FVTBooleanOption noNetherFog;
	public final FVTBooleanOption noBlockBreakParticles;
	public final FVTBooleanOption noPotionParticles;
	public final FVTBooleanOption noVignette;
	public final FVTBooleanOption noSpyglassOverlay;
	public final FVTBooleanOption refillHand;
	public final FVTBooleanOption autoReconnect;
	public final FVTDoubleOption  autoReconnectMaxTries;
	public final FVTDoubleOption  autoReconnectTimeout;
	public final FVTBooleanOption autoEat;
	public final FVTBooleanOption triggerBot;
	public final FVTBooleanOption freecam;
	public final FVTBooleanOption freecamHightlightPlayer;
	public final FVTBooleanOption autoTotem;
	public final FVTDoubleOption  useDelay;
	public final FVTDoubleOption  creativeBreakDelay;
	public final FVTBooleanOption placementLock;
	public final FVTBooleanOption containerButtons;
	public final FVTBooleanOption inventoryButton;
	public final FVTBooleanOption horseStats;
	public final FVTBooleanOption invisibleOffhand;
	public final FVTBooleanOption autoHideHotbar;
	public final FVTBooleanOption autoHideHotbarMode;
	public final FVTDoubleOption  autoHideHotbarTimeout;
	public final FVTBooleanOption autoHideHotbarUse;
	public final FVTBooleanOption autoHideHotbarItem;
	public final FVTBooleanOption attackThrough;
	public final FVTBooleanOption autoElytra;
	public final FVTBooleanOption fastTrade;

	public FVTOptions()
	{
		this.optionsFile = new File(FVT.MC.runDirectory, "config/fvt/fvt.properties");
		this.savedFeatures = new HashMap<String, FVTOption<?>>();

		// FEATURES CREATION
		buttonPosition = new FVTCyclingOption(
			"fvt.feature.name.button_position",
			"fvt.feature.name.button_position.tooltip",
			Arrays.asList(new TranslatableText[] {new TranslatableText("fvt.feature.name.button_position.right"), new TranslatableText("fvt.feature.name.button_position.left"), new TranslatableText("fvt.feature.name.button_position.center"), new TranslatableText("fvt.feature.name.button_position.outside"), new TranslatableText("fvt.feature.name.button_position.hidden")})
		);
		savedFeatures.put("buttonPosition", buttonPosition);

		featureToggleMessages = new FVTBooleanOption(
			"fvt.feature.name.feature_toggle_messages",
			"fvt.feature.name.feature_toggle_messages.tooltip",
			true
		);
		savedFeatures.put("featureToggleMessages", featureToggleMessages);

		crosshairScale = new FVTDoubleOption(
			"fvt.feature.name.crosshair_scale",
			"fvt.feature.name.crosshair_scale.tooltip",
			0.0d, 5.0d, 0.01d, 1.0d, FVTDoubleOption.Mode.PERCENT
		);
		savedFeatures.put("crosshairScale", crosshairScale);

		crosshairStaticColor = new FVTBooleanOption(
			"fvt.feature.name.crosshair_static_color",
			"fvt.feature.name.crosshair_static_color.tooltip",
			false
		);
		savedFeatures.put("crosshairStaticColor", crosshairStaticColor);

		crosshairStaticColorRed = new FVTDoubleOption(
			"fvt.feature.name.crosshair_static_color.red",
			"fvt.feature.name.crosshair_static_color.red.tooltip",
			0.0d, 255.0d, 1.0d, 255.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("crosshairStaticColorRed", crosshairStaticColorRed);

		crosshairStaticColorGreen = new FVTDoubleOption(
			"fvt.feature.name.crosshair_static_color.green",
			"fvt.feature.name.crosshair_static_color.green.tooltip",
			0.0d, 255.0d, 1.0d, 255.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("crosshairStaticColorGreen", crosshairStaticColorGreen);

		crosshairStaticColorBlue = new FVTDoubleOption(
			"fvt.feature.name.crosshair_static_color.blue",
			"fvt.feature.name.crosshair_static_color.blue.tooltip",
			0.0d, 255.0d, 1.0d, 255.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("crosshairStaticColorBlue", crosshairStaticColorBlue);

		crosshairStaticColorAlpha = new FVTDoubleOption(
			"fvt.feature.name.crosshair_static_color.alpha",
			"fvt.feature.name.crosshair_static_color.alpha.tooltip",
			0.0d, 255.0d, 1.0d, 255.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("crosshairStaticColorAlpha", crosshairStaticColorAlpha);

		disableWToSprint = new FVTBooleanOption(
			"fvt.feature.name.disable_w_to_sprint",
			"fvt.feature.name.disable_w_to_sprint.tooltip",
			true
		);
		savedFeatures.put("disableWToSprint", disableWToSprint);

		sendDeathCoordinates = new FVTBooleanOption(
			"fvt.feature.name.send_death_coordinates",
			"fvt.feature.name.send_death_coordinates.tooltip",
			true
		);
		savedFeatures.put("sendDeathCoordinates", sendDeathCoordinates);

		coordinatesPosition = new FVTBooleanOption(
			"fvt.feature.name.hud_coordinates",
			"fvt.feature.name.hud_coordinates.tooltip",
			true,
			new TranslatableText("fvt.feature.name.hud_coordinates.vertical"),
			new TranslatableText("fvt.feature.name.hud_coordinates.horizontal")
		);
		savedFeatures.put("coordinatesPosition", coordinatesPosition);

		showInfo = new FVTBooleanOption(
			"fvt.feature.name.show_info",
			"fvt.feature.name.show_info.tooltip",
			true,
			new TranslatableText("fvt.feature.name.show_info.visible"),
			new TranslatableText("fvt.feature.name.show_info.hidden")
		);
		savedFeatures.put("showHUDInfo", showInfo);

		noToolBreaking = new FVTBooleanOption(
			"fvt.feature.name.no_tool_breaking",
			"fvt.feature.name.no_tool_breaking.tooltip",
			false
		);
		savedFeatures.put("noToolBreaking", noToolBreaking);

		toolWarning = new FVTBooleanOption(
			"fvt.feature.name.tool_warning",
			"fvt.feature.name.tool_warning.tooltip",
			true
		);
		savedFeatures.put("toolWarning", toolWarning);

		toolWarningScale = new FVTDoubleOption(
			"fvt.feature.name.tool_warning.scale",
			"fvt.feature.name.tool_warning.scale.tooltip",
			0.0d, 4.0d, 0.01d, 1.5d, FVTDoubleOption.Mode.PERCENT
		);
		savedFeatures.put("toolWarningScale", toolWarningScale);

		toolWarningPosition = new FVTBooleanOption(
			"fvt.feature.name.tool_warning.position",
			"fvt.feature.name.tool_warning.position.tooltip",
			false,
			new TranslatableText("fvt.feature.name.tool_warning.position.top"),
			new TranslatableText("fvt.feature.name.tool_warning.position.bottom")
		);
		savedFeatures.put("toolWarningPosition", toolWarningPosition);

		cloudHeight = new FVTDoubleOption(
			"fvt.feature.name.cloud_height",
			"fvt.feature.name.cloud_height.tooltip",
			-64.0d, 320.0d, 1.0d, 192.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("cloudHeight", cloudHeight);

		entityOutline = new FVTBooleanOption(
			"fvt.feature.name.entity_outline",
			"fvt.feature.name.entity_outline.tooltip",
			false
		);
		savedFeatures.put("entityOutline", entityOutline);

		entityOutlineAnimals = new FVTBooleanOption(
			"fvt.feature.name.entity_outline_animals",
			"fvt.feature.name.entity_outline_animals.tooltip",
			true
		);
		savedFeatures.put("entityOutlineAnimals", entityOutlineAnimals);

		entityOutlineMobs = new FVTBooleanOption(
			"fvt.feature.name.entity_outline_mobs",
			"fvt.feature.name.entity_outline_mobs.tooltip",
			true
		);
		savedFeatures.put("entityOutlineMobs", entityOutlineMobs);

		entityOutlinePlayers = new FVTBooleanOption(
			"fvt.feature.name.entity_outline_players",
			"fvt.feature.name.entity_outline_players.tooltip",
			false
		);
		savedFeatures.put("entityOutlinePlayers", entityOutlinePlayers);

		entityOutlineMisc = new FVTBooleanOption(
			"fvt.feature.name.entity_outline_misc",
			"fvt.feature.name.entity_outline_misc.tooltip",
			false
		);
		savedFeatures.put("entityOutlineMisc", entityOutlineMisc);

		fullbright = new FVTBooleanOption(
			"fvt.feature.name.fullbright",
			"fvt.feature.name.fullbright.tooltip",
			false
		);
		savedFeatures.put("fullbright", fullbright);

		randomPlacement = new FVTBooleanOption(
			"fvt.feature.name.random_placement",
			"fvt.feature.name.random_placement.tooltip",
			false
		);
		savedFeatures.put("randomPlacement", randomPlacement);

		noNetherFog = new FVTBooleanOption(
			"fvt.feature.name.no_nether_fog",
			"fvt.feature.name.no_nether_fog.tooltip",
			false
		);
		savedFeatures.put("noNetherFog", noNetherFog);

		noBlockBreakParticles = new FVTBooleanOption(
			"fvt.feature.name.no_block_break_particles",
			"fvt.feature.name.no_block_break_particles.tooltip",
			false
		);
		savedFeatures.put("noBlockBreakParticles", noBlockBreakParticles);

		noPotionParticles = new FVTBooleanOption(
			"fvt.feature.name.no_potion_particles",
			"fvt.feature.name.no_potion_particles.tooltip",
			true
		);
		savedFeatures.put("noPotionParticles", noPotionParticles);

		noVignette = new FVTBooleanOption(
			"fvt.feature.name.no_vignette",
			"fvt.feature.name.no_vignette.tooltip",
			false
		);
		savedFeatures.put("noVignette", noVignette);

		noSpyglassOverlay = new FVTBooleanOption(
			"fvt.feature.name.no_spyglass_overlay",
			"fvt.feature.name.no_spyglass_overlay.tooltip",
			false
		);
		savedFeatures.put("noSpyglassOverlay", noSpyglassOverlay);

		refillHand = new FVTBooleanOption(
			"fvt.feature.name.refill_hand",
			"fvt.feature.name.refill_hand.tooltip",
			false
		);
		savedFeatures.put("refillHand", refillHand);

		autoReconnect = new FVTBooleanOption(
			"fvt.feature.name.autoreconnect",
			"fvt.feature.name.autoreconnect.tooltip",
			false
		);
		savedFeatures.put("autoReconnect", autoReconnect);

		autoReconnectMaxTries = new FVTDoubleOption(
			"fvt.feature.name.autoreconnect.tries",
			"fvt.feature.name.autoreconnect.tries.tooltip",
			0.0d, 51.0d, 1.0d, 3.0d, FVTDoubleOption.Mode.WHOLE, "fvt.feature.name.autoreconnect.tries_max"
		);
		savedFeatures.put("autoReconnectMaxTries", autoReconnectMaxTries);

		autoReconnectTimeout = new FVTDoubleOption(
			"fvt.feature.name.autoreconnect.timeout",
			"fvt.feature.name.autoreconnect.timeout.tooltip",
			1.0d, 300.0d, 1.0d, 5.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("autoReconnectTimeout", autoReconnectTimeout);

		autoEat = new FVTBooleanOption(
			"fvt.feature.name.autoeat",
			"fvt.feature.name.autoeat.tooltip",
			false
		);
		savedFeatures.put("autoEat", autoEat);

		triggerBot = new FVTBooleanOption(
			"fvt.feature.name.trigger_autoattack",
			"fvt.feature.name.trigger_autoattack.tooltip",
			false
		);
		savedFeatures.put("triggerBot", triggerBot);

		freecam = new FVTBooleanOption(
			"fvt.feature.name.freecam",
			"fvt.feature.name.freecam.tooltip",
			false
		);
		//features.put("freecam", freecam);

		freecamHightlightPlayer = new FVTBooleanOption(
			"fvt.feature.name.freecam_hightlight_player",
			"fvt.feature.name.freecam_hightlight_player.tooltip",
			true
		);
		savedFeatures.put("freecamHightlightPlayer", freecamHightlightPlayer);

		autoTotem = new FVTBooleanOption(
			"fvt.feature.name.autototem",
			"fvt.feature.name.autototem.tooltip",
			false
		);
		savedFeatures.put("autoTotem", autoTotem);

		useDelay = new FVTDoubleOption(
			"fvt.feature.name.use_delay",
			"fvt.feature.name.use_delay.tooltip",
			1.0d, 20.0d, 1.0d, 4.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("useDelay", useDelay);

		creativeBreakDelay = new FVTDoubleOption(
			"fvt.feature.name.creative_break_delay",
			"fvt.feature.name.creative_break_delay.tooltip",
			1.0d, 10.0d, 1.0d, 6.0d, FVTDoubleOption.Mode.WHOLE
		);
		savedFeatures.put("creativeBreakDelay", creativeBreakDelay);

		placementLock = new FVTBooleanOption(
			"fvt.feature.name.placement_lock",
			"fvt.feature.name.placement_lock.tooltip",
			false
		);
		savedFeatures.put("placementLock", placementLock);

		containerButtons = new FVTBooleanOption(
			"fvt.feature.name.container_buttons",
			"fvt.feature.name.container_buttons.tooltip",
			true
		);
		savedFeatures.put("containerButtons", containerButtons);

		inventoryButton = new FVTBooleanOption(
			"fvt.feature.name.inventory_button",
			"fvt.feature.name.inventory_button.tooltip",
			true
		);
		savedFeatures.put("inventoryButton", inventoryButton);

		horseStats = new FVTBooleanOption(
			"fvt.feature.name.horse_stats",
			"fvt.feature.name.horse_stats.tooltip",
			true
		);
		savedFeatures.put("horseStats", horseStats);

		invisibleOffhand = new FVTBooleanOption(
			"fvt.feature.name.invisible_offhand",
			"fvt.feature.name.invisible_offhand.tooltip",
			false
		);
		savedFeatures.put("invisibleOffhand", invisibleOffhand);

		autoHideHotbar = new FVTBooleanOption(
			"fvt.feature.name.auto_hide_hotbar",
			"fvt.feature.name.auto_hide_hotbar.tooltip",
			false
		);
		savedFeatures.put("autoHideHotbar", autoHideHotbar);

		autoHideHotbarMode = new FVTBooleanOption(
			"fvt.feature.name.auto_hide_hotbar_mode",
			"fvt.feature.name.auto_hide_hotbar_mode.tooltip",
			false,
			new TranslatableText("fvt.feature.name.auto_hide_hotbar_mode.full"),
			new TranslatableText("fvt.feature.name.auto_hide_hotbar_mode.partial")
		);
		savedFeatures.put("autoHideHotbarMode", autoHideHotbarMode);

		autoHideHotbarTimeout = new FVTDoubleOption(
			"fvt.feature.name.auto_hide_hotbar_timeout",
			"fvt.feature.name.auto_hide_hotbar_timeout.tooltip",
			1.0d, 10.0d, 0.2d, 1.6d, FVTDoubleOption.Mode.NORMAL
		);
		savedFeatures.put("autoHideHotbarTimeout", autoHideHotbarTimeout);

		autoHideHotbarUse = new FVTBooleanOption(
			"fvt.feature.name.auto_hide_hotbar_use",
			"fvt.feature.name.auto_hide_hotbar_use.tooltip",
			false
		);
		savedFeatures.put("autoHideHotbarUse", autoHideHotbarUse);

		autoHideHotbarItem = new FVTBooleanOption(
			"fvt.feature.name.auto_hide_hotbar_item",
			"fvt.feature.name.auto_hide_hotbar_item.tooltip",
			false
		);
		savedFeatures.put("autoHideHotbarItem", autoHideHotbarItem);

		attackThrough = new FVTBooleanOption(
			"fvt.feature.name.attack_through",
			"fvt.feature.name.attack_through.tooltip",
			false
		);
		savedFeatures.put("attackThrough", attackThrough);

		autoElytra = new FVTBooleanOption(
			"fvt.feature.name.auto_elytra",
			"fvt.feature.name.auto_elytra.tooltip",
			false
		);
		savedFeatures.put("autoElytra", autoElytra);

		fastTrade = new FVTBooleanOption(
			"fvt.feature.name.fast_trade",
			"fvt.feature.name.fast_trade.tooltip",
			false
		);
		savedFeatures.put("fastTrade", fastTrade);

		init();
	}

	private void init()
	{
		if(!optionsFile.exists()) {
			optionsFile.getParentFile().mkdirs();
			write();
		}
		else {
			read();
		}
	}

	public void reset()
	{
		for(FVTOption<?> feature : savedFeatures.values()) {
			feature.setValueDefault();
		}

		// manually since it does not get saved
		freecam.setValueDefault();
	}

	public void write()
	{
		try(PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(optionsFile), StandardCharsets.UTF_8));) {
			printWriter.println("# FVT configuration. Do not edit here unless you know what you're doing!");
			printWriter.println("# Last save: " + DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy").format(LocalDateTime.now()));

			for(Entry<String, FVTOption<?>> feature : savedFeatures.entrySet()) {
				printWriter.println(feature.getKey() + "=" + feature.getValue().getValueAsString());
			}
		}
		catch(Exception e) {
			FVT.LOGGER.error("Failed to write to 'fvt.properties':", e.toString());
		}
	}

	private void read()
	{
		try(BufferedReader bufferedReader = new BufferedReader(new FileReader(optionsFile, StandardCharsets.UTF_8))) {
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

				if(savedFeatures.get(key) == null || !savedFeatures.get(key).setValueFromString(value)) {
					FVT.LOGGER.warn("Skipping bad config option (" + value + ")" + " for " + key);
				}
			});
		}
		catch(IOException e) {
			FVT.LOGGER.error("Failed to read from 'fvt.properties':", e.toString());
		}
	}
}
