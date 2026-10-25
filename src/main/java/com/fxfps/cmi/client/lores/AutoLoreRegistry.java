package com.fxfps.cmi.client.lores;

import com.fxfps.cmi.CmiMod;
import com.fxfps.cmi.client.config.CmiConfigManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Registro y gestor de perfiles / presets de Auto-Lore de CMI.
 * Permite cargar, guardar y cambiar entre diferentes estilos de formateo de lore.
 */
public class AutoLoreRegistry {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, AutoLorePreset> PRESETS = new LinkedHashMap<>();
    private static Path autoloresDir;

    public static void init() {
        autoloresDir = CmiConfigManager.getConfigDir().resolve("autolores");
        try {
            Files.createDirectories(autoloresDir);
        } catch (Exception e) {
            CmiMod.LOGGER.error("Error al crear carpeta autolores de CMI", e);
        }

        registerDefaults();
        loadFromDisk();
    }

    private static void registerDefaults() {
        PRESETS.clear();

        // 1. LIMPIO (POR DEFECTO): SmallCaps activo por defecto, con las 3 secciones oficiales de CMI
        registerPreset(new AutoLorePreset(
                "clean",
                "Limpio (SmallCaps Oficial CMI)",
                "", // Sin cabecera global
                "&7&lENCANTAMIENTOS", // Renderiza &7&lᴇɴᴄᴀɴᴛᴀᴍɪᴇɴᴛᴏs
                "gold",
                "&7🍁 CUSTOM ENCHANTS", // Renderiza &7🍁 ᴄᴜsᴛᴏᴍ ᴇɴᴄʜᴀɴᴛs
                "frost",
                "&7&lATRIBUTOS", // Renderiza &7&lᴀᴛʀɪʙᴜᴛᴏs
                "silver",
                Collections.emptyList(), // Sin pie
                false, false, false, true, false // smallCaps = true por defecto
        ));

        // 2. RAINBOW RGB: Animación arcoíris
        registerPreset(new AutoLorePreset(
                "rainbow",
                "Rainbow RGB",
                "",
                "&c&lE&6&lN&e&lC&a&lA&b&lN&d&lT&5&lO&d&lS",
                "rainbow",
                "&7🍁 CUSTOM ENCHANTS",
                "frost",
                "&d&lATRIBUTOS",
                "candy",
                Collections.emptyList(),
                true, true, true, false, false
        ));

        // 3. FUEGO INFERNAL: Animación de fuego y lava
        registerPreset(new AutoLorePreset(
                "fire",
                "Fuego Infernal",
                "",
                "&c&lENCANTAMIENTOS",
                "fire",
                "&7🍁 CUSTOM ENCHANTS",
                "frost",
                "&6&lATRIBUTOS",
                "lava",
                Collections.emptyList(),
                true, true, true, false, false
        ));

        // 4. CELESTIAL ASTRAL: Animación celestial y aurora
        registerPreset(new AutoLorePreset(
                "celestial",
                "Celestial Astral",
                "",
                "&b&lENCANTAMIENTOS",
                "celestial",
                "&7🍁 CUSTOM ENCHANTS",
                "frost",
                "&3&lATRIBUTOS",
                "aurora",
                Collections.emptyList(),
                true, true, true, false, false
        ));

        // 5. MATRIX CYBER: Verde hacker y tóxico
        registerPreset(new AutoLorePreset(
                "matrix",
                "Matrix Cyber",
                "",
                "&a&lENCANTAMIENTOS",
                "matrix",
                "&7🍁 CUSTOM ENCHANTS",
                "frost",
                "&2&lATRIBUTOS",
                "toxic",
                Collections.emptyList(),
                true, true, true, false, false
        ));

        // 6. VOID SOMBRÍO: Negro abisal y plata
        registerPreset(new AutoLorePreset(
                "void",
                "Void Sombrío",
                "",
                "&8&lENCANTAMIENTOS",
                "void",
                "&7🍁 CUSTOM ENCHANTS",
                "frost",
                "&7&lATRIBUTOS",
                "silver",
                Collections.emptyList(),
                true, true, true, false, false
        ));

        // 7. DIAMANTE REAL: Diamante brillante
        registerPreset(new AutoLorePreset(
                "diamond",
                "Diamante Real",
                "",
                "&b&lENCANTAMIENTOS",
                "diamond",
                "&7🍁 CUSTOM ENCHANTS",
                "frost",
                "&f&lATRIBUTOS",
                "silver",
                Collections.emptyList(),
                true, true, true, false, false
        ));

        // 8. ARGENTINA NETWORK: Formato del servidor con bandera y cabecera
        registerPreset(new AutoLorePreset(
                "argnet",
                "Argentina Network",
                "\uE100ARGENTINA NETWORK",
                "&7&lENCANTAMIENTOS",
                "gold",
                "&7🍁 CUSTOM ENCHANTS",
                "frost",
                "&7&lATRIBUTOS",
                "silver",
                List.of("&7» CONSIGUE ESTE ITEM EN", "&c&nNETWORKARGENTINA.COM&r &a(&2\uE404&a)"),
                true, true, true, false, false
        ));
    }

    public static void registerPreset(AutoLorePreset preset) {
        if (preset != null && preset.id != null) {
            PRESETS.put(preset.id.toLowerCase(), preset);
        }
    }

    public static void loadFromDisk() {
        if (autoloresDir == null || !Files.exists(autoloresDir)) return;

        File[] files = autoloresDir.toFile().listFiles((d, name) -> name.endsWith(".json"));
        if (files == null) return;

        for (File file : files) {
            try (FileReader reader = new FileReader(file)) {
                AutoLorePreset p = GSON.fromJson(reader, AutoLorePreset.class);
                if (p != null && p.id != null && !p.id.isEmpty()) {
                    p.custom = true;
                    PRESETS.put(p.id.toLowerCase(), p);
                }
            } catch (Exception e) {
                CmiMod.LOGGER.error("Error al cargar preset custom: " + file.getName(), e);
            }
        }
    }

    public static void savePreset(AutoLorePreset preset) {
        if (preset == null || preset.id == null || preset.id.isEmpty() || autoloresDir == null) return;
        try {
            preset.custom = true;
            File file = autoloresDir.resolve(preset.id.toLowerCase() + ".json").toFile();
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(preset, writer);
            }
            PRESETS.put(preset.id.toLowerCase(), preset);
        } catch (Exception e) {
            CmiMod.LOGGER.error("Error al guardar preset custom: " + preset.id, e);
        }
    }

    public static void saveCustomPreset(AutoLorePreset preset) {
        savePreset(preset);
    }

    public static void deletePreset(String id) {
        if (id == null || autoloresDir == null) return;
        String lower = id.toLowerCase();
        AutoLorePreset p = PRESETS.get(lower);
        if (p != null && p.custom) {
            PRESETS.remove(lower);
            File file = autoloresDir.resolve(lower + ".json").toFile();
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static void deleteCustomPreset(String id) {
        deletePreset(id);
    }

    public static List<AutoLorePreset> getAllPresets() {
        return new ArrayList<>(PRESETS.values());
    }

    public static List<AutoLorePreset> getPresets() {
        return getAllPresets();
    }

    public static AutoLorePreset getPreset(String id) {
        if (id == null) return PRESETS.get("clean");
        AutoLorePreset p = PRESETS.get(id.toLowerCase());
        return p != null ? p : PRESETS.get("clean");
    }

    public static AutoLorePreset getActivePreset() {
        String active = CmiConfigManager.getConfig().activeAutoLorePreset;
        AutoLorePreset p = (active != null) ? PRESETS.get(active.toLowerCase()) : PRESETS.get("clean");
        if (p == null) {
            p = PRESETS.get("clean");
        }
        return p != null ? p : new AutoLorePreset("clean", "Limpio", "", "&7&lENCANTAMIENTOS", "gold", "&7🍁 CUSTOM ENCHANTS", "frost", "&7&lATRIBUTOS", "silver", Collections.emptyList(), false, false, false, true, false);
    }

    public static void setActivePreset(String id) {
        if (id == null || id.isEmpty()) id = "clean";
        CmiConfigManager.getConfig().activeAutoLorePreset = id.toLowerCase();
        CmiConfigManager.save();
    }

    public static AutoLorePreset getPresetByIndex(int index) {
        List<AutoLorePreset> all = getAllPresets();
        if (all.isEmpty()) return getActivePreset();
        if (index < 0) index = 0;
        return all.get(index % all.size());
    }

    public static AutoLorePreset getPresetForTag(String tag) {
        if (tag == null) return getActivePreset();
        String clean = tag.replace("<", "").replace(">", "").trim().toLowerCase();
        if (clean.startsWith("autolore")) {
            String suffix = clean.substring("autolore".length());
            if (!suffix.isEmpty()) {
                try {
                    int idx = Integer.parseInt(suffix);
                    return getPresetByIndex(idx - 1);
                } catch (NumberFormatException ignored) {}
            }
        }
        return getPreset(clean);
    }
}
