package com.autodripstone.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "autodripstone.json");
	
	public static class Config {
		public int trapdoorToggleSpeed = 2; // ticków między otwarciem/zamknięciem (niska liczba = szybciej)
		public int dripstoneSpawnChance = 1; // szansa spawnięcia dripstone (1 = zawsze)
		public boolean enabled = true;
	}
	
	private static Config config;
	
	public static void loadConfig() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				config = GSON.fromJson(reader, Config.class);
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = new Config();
		saveConfig();
	}
	
	public static void saveConfig() {
		try {
			CONFIG_FILE.getParentFile().mkdirs();
			try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
				GSON.toJson(config, writer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Config getConfig() {
		if (config == null) {
			loadConfig();
		}
		return config;
	}
}
