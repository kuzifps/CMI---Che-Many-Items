package com.fxfps.cmi.client.config;

import com.fxfps.cmi.CmiMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CmiConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static CmiConfig config = new CmiConfig();
    private static Path configDir;
    private static File configFile;

    public static void init() {
        configDir = FabricLoader.getInstance().getConfigDir().resolve("cmi");
        configFile = configDir.resolve("settings.json").toFile();

        try {
            // Crear carpetas necesarias
            Files.createDirectories(configDir);
            Files.createDirectories(configDir.resolve("kits"));
            Files.createDirectories(configDir.resolve("lores"));
            Files.createDirectories(configDir.resolve("autolores"));

            load();
            com.fxfps.cmi.client.lores.AutoLoreRegistry.init();
            com.fxfps.cmi.client.lores.ItemModifierColorManager.init();
        } catch (IOException e) {
            CmiMod.LOGGER.error("Error al inicializar carpetas de configuración de CMI", e);
        }
    }

    public static void load() {
        if (configFile != null && configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, CmiConfig.class);
                if (config == null) {
                    config = new CmiConfig();
                }
            } catch (Exception e) {
                CmiMod.LOGGER.error("Error al leer settings.json de CMI, usando valores por defecto", e);
                config = new CmiConfig();
            }
        } else {
            save();
        }
    }

    public static void save() {
        if (configFile == null) return;
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            CmiMod.LOGGER.error("Error al guardar settings.json de CMI", e);
        }
    }

    public static CmiConfig getConfig() {
        if (config == null) {
            config = new CmiConfig();
        }
        return config;
    }

    public static Path getConfigDir() {
        return configDir;
    }

    public static Path getKitsDir() {
        return configDir.resolve("kits");
    }

    public static Path getLoresDir() {
        return configDir.resolve("lores");
    }
}
