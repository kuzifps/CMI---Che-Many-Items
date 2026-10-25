package com.fxfps.cmi.client.config;

public class CmiConfig {
    // Tecla por defecto para abrir el menu de CMI: GLFW_KEY_U (85)
    public int menuKeyCode = 85; // 'U'
    public String menuKeyName = "U";

    // Tecla rapida para autotext / equipar kit: GLFW_KEY_V (86)
    public int quickKitKeyCode = 86; // 'V'
    public String quickKitKeyName = "V";

    // Tecla rapida para abrir directamente los ajustes de CMI
    public int settingsKeyCode = -1;
    public String settingsKeyName = "SIN ASIGNAR";

    // Modos de proteccion y visuales
    public boolean safeModeEnabled = true;
    public boolean shulkerSpyEnabled = true;
    public boolean dpsCalculatorEnabled = true;
    public boolean liveAnimationPreview = true;
    public boolean autoArgNetLore = true; // Auto-formato de lore estilo Argentina Network 🇦🇷
    public String activeAutoLorePreset = "clean"; // Preset activo de Auto-Lore ("clean", "rainbow", "fire", etc.)

    // Colores Globales de Modificadores (Persistencia inmediata)
    public String enchantsGlobalColor = "<gold>";
    public String customEnchantsGlobalColor = "<frost>";
    public String attributesGlobalColor = "<silver>";
    public boolean antiDuplicationHideNative = true;

    // Nombre de kit activo por defecto
    public String defaultKitName = "cmi_pvp";

    // Ítems Favoritos e Historial de ítems recientes persistentes
    public java.util.List<String> favoriteItemIds = new java.util.ArrayList<>(java.util.List.of(
            "minecraft:netherite_sword",
            "minecraft:netherite_helmet",
            "minecraft:netherite_chestplate",
            "minecraft:netherite_leggings",
            "minecraft:netherite_boots",
            "minecraft:diamond_sword",
            "minecraft:bow",
            "minecraft:crossbow",
            "minecraft:golden_apple",
            "minecraft:enchanted_golden_apple",
            "minecraft:totem_of_undying",
            "minecraft:end_crystal",
            "minecraft:respawn_anchor",
            "minecraft:glowstone",
            "minecraft:ender_pearl",
            "minecraft:water_bucket",
            "minecraft:lava_bucket",
            "minecraft:experience_bottle"
    ));
    public java.util.List<String> recentItemHistory = new java.util.ArrayList<>();
}
