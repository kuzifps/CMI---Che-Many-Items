package com.fxfps.cmi.client.lores;

import com.fxfps.cmi.CmiMod;
import com.fxfps.cmi.client.config.CmiConfigManager;
import com.fxfps.cmi.client.fonts.GradientHelper;
import com.fxfps.cmi.client.fonts.UnicodeFontHelper;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestor Integral de Colores, Persistencia y Overrides (.txt) para:
 * 1. Encantamientos Vanilla (enchants_colors.txt)
 * 2. Custom Enchants (customenchants_colors.txt)
 * 3. Atributos de Ítems (attributes_colors.txt)
 *
 * Implementa la especificación técnica CMI:
 * - Color Global reactivo persistente en config.
 * - Overrides individuales con sintaxis clave-valor flexible.
 * - Soporte para <hide>, <level>, gradientes MiniMessage, Hex y Legacy.
 * - Fallback seguro sin excepciones (&7 / <gray>).
 */
public class ItemModifierColorManager {

    public static final String ENCHANTS_FILE = "enchants_colors.txt";
    public static final String CUSTOM_ENCHANTS_FILE = "customenchants_colors.txt";
    public static final String ATTRIBUTES_FILE = "attributes_colors.txt";

    private static final Map<String, String> ENCHANT_OVERRIDES = new HashMap<>();
    private static final Map<String, String> CUSTOM_ENCHANT_OVERRIDES = new HashMap<>();
    private static final Map<String, String> ATTRIBUTE_OVERRIDES = new HashMap<>();

    private static final Pattern GRADIENT_PATTERN = Pattern.compile(
            "(?i)<gradient:(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6})(?::(#[0-9a-fA-F]{6}))?>"
    );
    private static final Pattern HEX_PATTERN = Pattern.compile(
            "(?i)(?:<#([0-9a-fA-F]{6})>|&#([0-9a-fA-F]{6})|#([0-9a-fA-F]{6}))"
    );

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        reload();
        initialized = true;
    }

    public static synchronized void reload() {
        Path dir = CmiConfigManager.getConfigDir();
        if (dir == null) return;

        try {
            Files.createDirectories(dir);
            loadCategoryFile(dir.resolve(ENCHANTS_FILE), ENCHANT_OVERRIDES, getDefaultEnchantsContent());
            loadCategoryFile(dir.resolve(CUSTOM_ENCHANTS_FILE), CUSTOM_ENCHANT_OVERRIDES, getDefaultCustomEnchantsContent());
            loadCategoryFile(dir.resolve(ATTRIBUTES_FILE), ATTRIBUTE_OVERRIDES, getDefaultAttributesContent());
            CmiMod.LOGGER.info("Archivos de colores de modificadores (.txt) cargados correctamente.");
        } catch (Exception e) {
            CmiMod.LOGGER.error("Error al cargar archivos de colores de modificadores (.txt)", e);
        }
    }

    private static void loadCategoryFile(Path path, Map<String, String> targetMap, String defaultContent) {
        targetMap.clear();

        if (!Files.exists(path)) {
            try {
                Files.writeString(path, defaultContent, StandardCharsets.UTF_8);
            } catch (IOException e) {
                CmiMod.LOGGER.error("No se pudo crear archivo por defecto: " + path.getFileName(), e);
            }
        }

        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("//")) {
                    continue;
                }

                int sepIdx = trimmed.indexOf('=');
                if (sepIdx == -1) {
                    sepIdx = trimmed.indexOf(':');
                }

                if (sepIdx != -1) {
                    String rawKey = trimmed.substring(0, sepIdx).trim().toLowerCase(Locale.ROOT);
                    String rawVal = trimmed.substring(sepIdx + 1).trim();

                    if (!rawKey.isEmpty()) {
                        String cleanKey = rawKey.startsWith("minecraft:") ? rawKey.substring(10) : rawKey;
                        targetMap.put(cleanKey, rawVal);
                        targetMap.put(rawKey, rawVal);
                    }
                }
            }
        } catch (IOException e) {
            CmiMod.LOGGER.error("Error al leer archivo de colores: " + path.getFileName(), e);
        }
    }

    // =========================================================================
    // CONSULTA Y FORMATEO DE ENCANTAMIENTOS VANILLA
    // =========================================================================

    public static boolean isEnchantHidden(String enchantKey) {
        if (enchantKey == null) return false;
        String val = lookupOverride(ENCHANT_OVERRIDES, enchantKey);
        return isHideValue(val);
    }

    /**
     * Formatea un encantamiento vanilla según los overrides o el color global.
     * Retorna null si el encantamiento está marcado con <hide>.
     */
    public static String formatEnchant(String enchantKey, String displayName, String level, boolean smallCaps, boolean bold) {
        if (isEnchantHidden(enchantKey)) {
            return null;
        }

        String override = lookupOverride(ENCHANT_OVERRIDES, enchantKey);
        String nameFormatted = smallCaps ? UnicodeFontHelper.toSmallCaps(displayName) : displayName;
        String boldPrefix = bold ? "&l" : "";

        if (override != null && !override.isEmpty()) {
            if (override.contains("<level>") || override.contains("{level}")) {
                String templated = override.replace("<level>", level).replace("{level}", level);
                return boldPrefix + safeApplyColorFallback(templated, nameFormatted);
            }
            return boldPrefix + safeApplyColor(override, nameFormatted + " " + level);
        }

        String globalColor = CmiConfigManager.getConfig().enchantsGlobalColor;
        if (globalColor == null || globalColor.trim().isEmpty()) {
            globalColor = "<gold>";
        }
        return boldPrefix + safeApplyColor(globalColor, nameFormatted + " " + level);
    }

    // =========================================================================
    // CONSULTA Y FORMATEO DE CUSTOM ENCHANTS
    // =========================================================================

    public static boolean isCustomEnchantHidden(String customEnchantId) {
        if (customEnchantId == null) return false;
        String val = lookupOverride(CUSTOM_ENCHANT_OVERRIDES, customEnchantId);
        return isHideValue(val);
    }

    public static String formatCustomEnchant(String id, String displayName, String level, boolean smallCaps) {
        if (isCustomEnchantHidden(id)) {
            return null;
        }

        String override = lookupOverride(CUSTOM_ENCHANT_OVERRIDES, id);
        String nameFormatted = smallCaps ? UnicodeFontHelper.toSmallCaps(displayName) : displayName;

        if (override != null && !override.isEmpty()) {
            if (override.contains("<level>") || override.contains("{level}")) {
                String templated = override.replace("<level>", level).replace("{level}", level);
                return "&l" + safeApplyColorFallback(templated, nameFormatted);
            }
            return "&l" + safeApplyColor(override, nameFormatted + " " + level);
        }

        String globalColor = CmiConfigManager.getConfig().customEnchantsGlobalColor;
        if (globalColor == null || globalColor.trim().isEmpty()) {
            globalColor = "<frost>";
        }
        return "&l" + safeApplyColor(globalColor, nameFormatted + " " + level);
    }

    // =========================================================================
    // CONSULTA Y FORMATEO DE ATRIBUTOS
    // =========================================================================

    public static boolean isAttributeHidden(String attributeKey) {
        if (attributeKey == null) return false;
        String val = lookupOverride(ATTRIBUTE_OVERRIDES, attributeKey);
        if (isHideValue(val)) return true;

        if (attributeKey.startsWith("generic.")) {
            String shortKey = attributeKey.substring(8);
            if (isHideValue(lookupOverride(ATTRIBUTE_OVERRIDES, shortKey))) {
                return true;
            }
        }
        return false;
    }

    public static String formatAttribute(String key, String displayName, String value, String icon, boolean smallCaps) {
        if (isAttributeHidden(key)) {
            return null;
        }

        String override = lookupOverride(ATTRIBUTE_OVERRIDES, key);
        if (override == null && key.startsWith("generic.")) {
            override = lookupOverride(ATTRIBUTE_OVERRIDES, key.substring(8));
        }

        String nameFormatted = smallCaps ? UnicodeFontHelper.toSmallCaps(displayName) : displayName;
        String iconPrefix = (icon != null && !icon.isEmpty()) ? icon + " " : "";

        if (override != null && !override.isEmpty()) {
            if (override.contains("<value>") || override.contains("{value}") || override.contains("<level>") || override.contains("{level}")) {
                String templated = override
                        .replace("<value>", value).replace("{value}", value)
                        .replace("<level>", value).replace("{level}", value);
                return iconPrefix + safeApplyColorFallback(templated, nameFormatted);
            }
            return iconPrefix + safeApplyColor(override, value + " " + nameFormatted);
        }

        String globalColor = CmiConfigManager.getConfig().attributesGlobalColor;
        if (globalColor == null || globalColor.trim().isEmpty()) {
            globalColor = "<silver>";
        }
        return iconPrefix + safeApplyColor(globalColor, value + " " + nameFormatted);
    }

    // =========================================================================
    // MOTOR DE COLOREADO SEGURO (FALLBACK NEUTRO &7 / <gray>)
    // =========================================================================

    public static String safeApplyColor(String colorSpec, String text) {
        if (text == null) return "";
        if (colorSpec == null || colorSpec.trim().isEmpty()) {
            return "&7" + text;
        }

        String spec = colorSpec.trim();

        // 1. Caso <hide>
        if (isHideValue(spec)) {
            return "";
        }

        // 2. Caso Códigos Legacy estándar (&a, §a, &6, etc.)
        if ((spec.startsWith("&") || spec.startsWith("§")) && spec.length() == 2) {
            return spec + text;
        }

        // 3. Caso MiniMessage Gradient (<gradient:#c1:#c2> o <gradient:#c1:#c2:#c3>)
        if (spec.toLowerCase(Locale.ROOT).startsWith("<gradient:")) {
            Matcher m = GRADIENT_PATTERN.matcher(spec);
            if (m.find()) {
                String gradTag = m.group(0);
                String full = gradTag + text + "</gradient>";
                try {
                    return GradientHelper.convertMiniMessageGradientsToHex(full);
                } catch (Exception ignored) {
                    return "&7" + text;
                }
            } else {
                // Formato roto: Fallback seguro neutro sin excepciones
                return "&7" + text;
            }
        }

        // 4. Caso Hex Directo (<#RRGGBB>, &#RRGGBB o #RRGGBB)
        Matcher hexMatcher = HEX_PATTERN.matcher(spec);
        if (hexMatcher.matches()) {
            String hex = hexMatcher.group(1);
            if (hex == null) hex = hexMatcher.group(2);
            if (hex == null) hex = hexMatcher.group(3);
            return "&#" + hex + text;
        }

        // 5. Caso Tags Animados / Nativos de CMI (<gold>, <silver>, <frost>, <rainbow>, <fire>, etc.)
        if (spec.startsWith("<") && spec.endsWith(">")) {
            String tagName = spec.substring(1, spec.length() - 1).trim().toLowerCase(Locale.ROOT);
            if (isValidTagName(tagName)) {
                return "<" + tagName + ">" + text + "</" + tagName + ">";
            } else {
                return "&7" + text;
            }
        }

        // 6. Texto de color plano o desconocido -> aplicar con prefijo si parece código
        if (spec.startsWith("&") || spec.startsWith("§")) {
            return spec + text;
        }

        // Fallback final
        return "&7" + text;
    }

    private static String safeApplyColorFallback(String template, String defaultFallback) {
        if (template == null || template.isEmpty()) return "&7" + defaultFallback;
        if (template.toLowerCase(Locale.ROOT).contains("<gradient:")) {
            try {
                return GradientHelper.convertMiniMessageGradientsToHex(template);
            } catch (Exception ignored) {
                return "&7" + defaultFallback;
            }
        }
        return template;
    }

    public static boolean isHideValue(String val) {
        if (val == null) return false;
        String v = val.trim().toLowerCase(Locale.ROOT);
        return v.equals("<hide>") || v.equals("hide") || v.equals("none") || v.equals("false") || v.equals("hidden");
    }

    private static boolean isValidTagName(String tagName) {
        if (tagName == null || tagName.isEmpty()) return false;
        if (tagName.equals("gold") || tagName.equals("silver") || tagName.equals("frost") || tagName.equals("animated") || tagName.equals("rainbow")) {
            return true;
        }
        return UnicodeFontHelper.getPresetByTag(tagName) != null;
    }

    private static String lookupOverride(Map<String, String> map, String key) {
        if (key == null) return null;
        String clean = key.toLowerCase(Locale.ROOT).trim();
        if (map.containsKey(clean)) {
            return map.get(clean);
        }
        if (clean.startsWith("minecraft:")) {
            String stripped = clean.substring(10);
            return map.get(stripped);
        }
        return null;
    }

    // =========================================================================
    // PERMISOS OPCIONALES
    // =========================================================================

    public static boolean canUseColorPermission(String colorSpec) {
        if (colorSpec == null || colorSpec.isEmpty()) return true;
        String lower = colorSpec.toLowerCase(Locale.ROOT);
        if (lower.contains("gradient") || lower.contains("rainbow")) {
            return checkPermission("cmi.color.gradient");
        }
        if (lower.contains("#")) {
            return checkPermission("cmi.color.hex");
        }
        return true;
    }

    private static boolean checkPermission(String perm) {
        return true; // En entorno cliente / CMI mod siempre concedido
    }

    // =========================================================================
    // CONTENIDO POR DEFECTO PARA ARCHIVOS .TXT
    // =========================================================================

    private static String getDefaultEnchantsContent() {
        return """
# ==============================================================================
# ARCHIVO DE CONFIGURACIÓN DE COLORES Y FORMATO: ENCANTAMIENTOS VANILLA
# ==============================================================================
# Sintaxis admitida (case-insensitive, acepta o no el prefijo 'minecraft:'):
#   <id_encantamiento> = <color|gradient|template|<hide>>
#
# Formatos soportados:
#   1. MiniMessage Gradient:  sharpness = <gradient:#ff0000:#aa0000>
#   2. Códigos Legacy:        minecraft:unbreaking = &b
#   3. Hexadecimal:           mending = <#55FF55>
#   4. Plantilla de reemplazo: protection = <#00FF88>Protección Divina <level>
#   5. Ocultar completamente:  curse_of_vanishing = <hide>
#
# Si un encantamiento no figura en este archivo, usará el Color Global del menú.
# ==============================================================================
sharpness = <gradient:#ff2244:#aa0011>
fire_aspect = <gradient:#ff7700:#ffdd00>
unbreaking = &b
mending = <#55ff55>
curse_of_vanishing = <hide>
curse_of_binding = <hide>
""";
    }

    private static String getDefaultCustomEnchantsContent() {
        return """
# ==============================================================================
# ARCHIVO DE CONFIGURACIÓN DE COLORES Y FORMATO: CUSTOM ENCHANTS (GAMBLE)
# ==============================================================================
# Sintaxis admitida (case-insensitive):
#   <id_customenchant> = <color|gradient|template|<hide>>
#
# Formatos soportados:
#   1. MiniMessage Gradient:  vida_extra = <gradient:#ff3b5c:#ff88a3>
#   2. Códigos Legacy:        velocidad = &b
#   3. Hexadecimal:           fuerza = <#ff2d55>
#   4. Plantilla de reemplazo: regeneracion = &dRegeneración Divina <level>
#   5. Ocultar completamente:  rastro = <hide>
#
# Si un custom enchant no figura aquí, usará el Color Global del menú (<frost>).
# ==============================================================================
vida_extra = <gradient:#ff3b5c:#ff88a3>
fuerza = <gradient:#ff2d55:#ff6b8b>
velocidad = &b
salto = &a
excavador = <gradient:#ff9f0a:#ffd60a>
""";
    }

    private static String getDefaultAttributesContent() {
        return """
# ==============================================================================
# ARCHIVO DE CONFIGURACIÓN DE COLORES Y FORMATO: ATRIBUTOS DE ÍTEMS
# ==============================================================================
# Sintaxis admitida (case-insensitive, acepta 'generic.attack_damage' o 'attack_damage'):
#   <id_atributo> = <color|gradient|template|<hide>>
#
# Formatos soportados:
#   1. MiniMessage Gradient:  generic.attack_damage = <gradient:#ff2244:#880011>
#   2. Códigos Legacy:        generic.max_health = &a
#   3. Hexadecimal:           generic.armor = <#3b82f6>
#   4. Plantilla de reemplazo: generic.movement_speed = &b+<value>% Rapidez
#   5. Ocultar completamente:  generic.luck = <hide>
#
# Si un atributo no figura aquí, usará el Color Global del menú (<silver>).
# ==============================================================================
generic.attack_damage = <gradient:#ff2244:#880011>
generic.max_health = <gradient:#30d158:#10b981>
generic.armor = <#3b82f6>
""";
    }
}
