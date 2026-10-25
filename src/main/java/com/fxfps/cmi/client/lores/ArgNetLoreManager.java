package com.fxfps.cmi.client.lores;

import com.fxfps.cmi.client.fonts.UnicodeFontHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestor avanzado de formateo de Lore oficial de Argentina Network y Gamble para CMI ðŸ‡¦ðŸ‡·.
 *
 * Estructura oficial y orden estricto de secciones:
 *   1- ENCANTAMIENTOS (Vanilla / Registrados) -> Ãšnicamente si el Ã­tem tiene encantamientos
 *   2- &7\uD83C\uDF41 CUSTOM ENCHANTS (&l<frost>NAME LEVEL) -> Ãšnicamente si el Ã­tem tiene custom enchants
 *   3- ATRIBUTOS (&7&lATRIBUTOS con <silver>) -> Ãšnicamente si el Ã­tem tiene atributos
 */
public class ArgNetLoreManager {

    public static final String DEFAULT_HEADER = ""; // Por defecto sin cabecera (Limpio)
    public static final String RAW_LORE_TAG = "cmi_raw_lore";
    public static final String CUSTOM_ENCHANTS_TAG = "cmi_custom_enchants";
    public static final String HIDDEN_ENCHANTS_TAG = "cmi_hidden_enchants";
    private static final Map<ItemStack, String> RAW_LORE_FALLBACK_CACHE = new WeakHashMap<>();

    public static final String SECTION_ENCHANTS = "&7&lENCANTAMIENTOS";
    public static final String ATTR_TAG = "silver";
    public static final String ENCHANT_TAG = "gold";
    public static final String SECTION_CUSTOM_ENCHANTS = "&7\uD83C\uDF41 CUSTOM ENCHANTS";
    public static final String SECTION_ATTRIBUTES = "&7&lATRIBUTOS";
    public static final String CUSTOM_ENCHANT_TAG = "frost";
    public static final String FOOTER_LINE_1 = "";
    public static final String FOOTER_LINE_2 = "";

    // Iconos de Textura Argentina Network
    public static final String ICON_SHIELD = "\uE4F7";          // :shield:
    public static final String ICON_FIRE_SHIELD = "\uE417";     // :fire_resistance:
    public static final String ICON_HOURGLASS = "\uE4EB";       // :hourglass:
    public static final String ICON_ANVIL = "\uE4E0";           // :anvil:
    public static final String ICON_SWORD = "\uE4FB";           // :sword:
    public static final String ICON_HEART = "\uE4EA";           // :heart:
    public static final String ICON_HEALTH_BOOST = "\uE419";    // :health_boost: (Doble corazÃ³n)
    public static final String ICON_ABSORPTION = "\uE416";      // :absorption:
    public static final String ICON_HASTE = "\uE418";           // :haste:
    public static final String ICON_NIGHT_VISION = "\uE41B";    // :night_vision:
    public static final String ICON_DIAMOND = "\uE4E6";         // :diamond:
    public static final String ICON_SCROLL = "\uE4F6";          // :scroll:
    public static final String ICON_FLAG_AR = "\uE100";         // :flag_ar:
    public static final String ICON_CHECK = "\uE404";           // :check:

    public record EnchantInfo(String displayName, String icon) {}

    /**
     * Modelo para Custom Enchants oficiales (ej: &l<frost>VELOCIDAD 2).
     */
    public record CustomEnchant(String name, String level) {
        public CustomEnchant(String name, String level) {
            this.name = UnicodeFontHelper.fromSmallCaps(name != null ? name.trim() : "").toUpperCase();
            this.level = level != null ? level.trim() : "";
        }

        public String toLoreLine() {
            return toLoreLine(AutoLoreRegistry.getActivePreset());
        }

        public String toLoreLine(AutoLorePreset preset) {
            boolean smallCaps = (preset == null || preset.smallCaps);
            String id = stripAccents(name.toLowerCase(Locale.ROOT).replace(' ', '_'));
            return ItemModifierColorManager.formatCustomEnchant(id, name, level, smallCaps);
        }
    }

    /**
     * Modelo para Atributos vanilla / modificadores (ej: +10000 DaÃ±o de Ataque).
     */
    public record CustomAttribute(String name, String level, String icon) {
        public CustomAttribute(String name, String level, String icon) {
            this.name = UnicodeFontHelper.fromSmallCaps(name != null ? name.trim() : "").toUpperCase();
            this.level = level != null ? level.trim() : "";
            this.icon = icon;
        }

        public String toLoreLine() {
            return toLoreLine(AutoLoreRegistry.getActivePreset());
        }

        public String toLoreLine(AutoLorePreset preset) {
            String ic = (preset != null && preset.useIcons && icon != null) ? icon : "";
            String bold = (preset != null && preset.bold) ? "&l" : "";
            String tag = (preset != null && preset.attrTag != null && !preset.attrTag.isEmpty()) ? preset.attrTag : "silver";
            String dName;
            if (preset == null || preset.smallCaps) {
                dName = UnicodeFontHelper.toSmallCaps(name);
            } else if (preset.uppercase) {
                dName = name.toUpperCase();
            } else {
                dName = name;
            }

            String content;
            if (level != null && !level.isEmpty()) {
                if (level.startsWith("+") || level.startsWith("-")) {
                    content = level + " " + dName;
                } else {
                    content = dName + " " + level;
                }
            } else {
                content = dName;
            }

            return ic + bold + "<" + tag + ">" + content + "</" + tag + ">";
        }
    }

    private static final Map<String, EnchantInfo> ENCHANT_MAP = new HashMap<>();

    static {
        // Armaduras
        ENCHANT_MAP.put("protection", new EnchantInfo("PROTECCIÃ“N", ICON_SHIELD));
        ENCHANT_MAP.put("fire_protection", new EnchantInfo("PROTECCIÃ“N CONTRA EL FUEGO", ICON_FIRE_SHIELD));
        ENCHANT_MAP.put("feather_falling", new EnchantInfo("CAÃDA DE PLUMA", ICON_SHIELD));
        ENCHANT_MAP.put("blast_protection", new EnchantInfo("PROTECCIÃ“N CONTRA EXPLOSIONES", ICON_SHIELD));
        ENCHANT_MAP.put("projectile_protection", new EnchantInfo("PROTECCIÃ“N CONTRA PROYECTILES", ICON_SHIELD));
        ENCHANT_MAP.put("respiration", new EnchantInfo("RESPIRACIÃ“N", ICON_SHIELD));
        ENCHANT_MAP.put("aqua_affinity", new EnchantInfo("AFINIDAD ACUÃTICA", ICON_SHIELD));
        ENCHANT_MAP.put("thorns", new EnchantInfo("ESPINAS", ICON_SHIELD));
        ENCHANT_MAP.put("depth_strider", new EnchantInfo("AGILIDAD ACUÃTICA", ICON_SHIELD));
        ENCHANT_MAP.put("frost_walker", new EnchantInfo("PASO HELADO", ICON_SHIELD));
        ENCHANT_MAP.put("soul_speed", new EnchantInfo("VELOCIDAD DE ALMA", ICON_SHIELD));
        ENCHANT_MAP.put("swift_sneak", new EnchantInfo("SIGILO VELOZ", ICON_SHIELD));

        // Durabilidad y ReparaciÃ³n
        ENCHANT_MAP.put("unbreaking", new EnchantInfo("IRROMPIBILIDAD", ICON_HOURGLASS));
        ENCHANT_MAP.put("mending", new EnchantInfo("REPARACIÃ“N", ICON_ANVIL));

        // Armas Cuerpo a Cuerpo
        ENCHANT_MAP.put("sharpness", new EnchantInfo("FILO", ICON_SWORD));
        ENCHANT_MAP.put("smite", new EnchantInfo("CASTIGO", ICON_SWORD));
        ENCHANT_MAP.put("bane_of_arthropods", new EnchantInfo("PERDICIÃ“N DE LOS ARTRÃ“PODOS", ICON_SWORD));
        ENCHANT_MAP.put("knockback", new EnchantInfo("EMPUJE", ICON_SWORD));
        ENCHANT_MAP.put("fire_aspect", new EnchantInfo("ASPECTO ÃGNEO", ICON_FIRE_SHIELD));
        ENCHANT_MAP.put("looting", new EnchantInfo("BOTÃN", ICON_DIAMOND));
        ENCHANT_MAP.put("sweeping_edge", new EnchantInfo("FILO ARRASADOR", ICON_SWORD));
        ENCHANT_MAP.put("sweeping", new EnchantInfo("FILO ARRASADOR", ICON_SWORD));

        // Herramientas
        ENCHANT_MAP.put("efficiency", new EnchantInfo("EFICIENCIA", ICON_HASTE));
        ENCHANT_MAP.put("silk_touch", new EnchantInfo("TOQUE DE SEDA", ICON_SCROLL));
        ENCHANT_MAP.put("fortune", new EnchantInfo("FORTUNA", ICON_DIAMOND));

        // Arcos y Ballestas
        ENCHANT_MAP.put("power", new EnchantInfo("PODER", ICON_SWORD));
        ENCHANT_MAP.put("punch", new EnchantInfo("RETROCESO", ICON_SWORD));
        ENCHANT_MAP.put("flame", new EnchantInfo("FUEGO", ICON_FIRE_SHIELD));
        ENCHANT_MAP.put("infinity", new EnchantInfo("INFINIDAD", ICON_HOURGLASS));
        ENCHANT_MAP.put("luck_of_the_sea", new EnchantInfo("SUERTE MARINA", ICON_DIAMOND));
        ENCHANT_MAP.put("lure", new EnchantInfo("ATRACCIÃ“N", ICON_DIAMOND));
        ENCHANT_MAP.put("loyalty", new EnchantInfo("LEALTAD", ICON_SWORD));
        ENCHANT_MAP.put("impaling", new EnchantInfo("EMPALAMIENTO", ICON_SWORD));
        ENCHANT_MAP.put("riptide", new EnchantInfo("AGUAS REVUELTAS", ICON_SWORD));
        ENCHANT_MAP.put("channeling", new EnchantInfo("CONDUCTIVIDAD", ICON_SWORD));
        ENCHANT_MAP.put("multishot", new EnchantInfo("DISPARO MÃšLTIPLE", ICON_SWORD));
        ENCHANT_MAP.put("quick_charge", new EnchantInfo("CARGA RÃPIDA", ICON_HOURGLASS));
        ENCHANT_MAP.put("piercing", new EnchantInfo("PERFORACIÃ“N", ICON_SWORD));

        // Nuevos encantamientos 1.21
        ENCHANT_MAP.put("breach", new EnchantInfo("BRECHA", ICON_SWORD));
        ENCHANT_MAP.put("density", new EnchantInfo("DENSIDAD", ICON_SWORD));
        ENCHANT_MAP.put("wind_burst", new EnchantInfo("RÃFAGA DE VIENTO", ICON_SWORD));

        // Maldiciones
        ENCHANT_MAP.put("binding_curse", new EnchantInfo("MALDICIÃ“N DE LIGAMIENTO", ICON_SHIELD));
        ENCHANT_MAP.put("vanishing_curse", new EnchantInfo("MALDICIÃ“N DE DESAPARICIÃ“N", ICON_SHIELD));
    }

    // 17 Custom Enchants oficiales de Gamble
    public static final String[] GAMBLE_CUSTOM_ENCHANTS = {
            "vida extra", "vida_extra", "vidaextra", "extra corazones", "corazones extra", "corazones",
            "fuerza", "strength",
            "resistencia al fuego", "resistencia_al_fuego", "resistenciaalfuego", "resistencia fuego",
            "regeneracion", "regeneraciÃ³n", "regeneration",
            "velocidad", "speed",
            "salto", "jump",
            "respiracion acuatica", "respiraciÃ³n acuÃ¡tica", "respiracion_acuatica", "respiracionacuatica", "water breathing",
            "vision nocturna", "visiÃ³n nocturna", "vision_nocturna", "visionnocturna", "night vision",
            "invisibilidad", "invisibility",
            "saturacion", "saturaciÃ³n", "saturation",
            "excavador", "fundicion", "fundiciÃ³n", "smelt",
            "explosivo", "explosive", "vetas", "vein",
            "fertilizante", "rastro", "talador", "treecapitator"
    };

    private static final String[] KNOWN_CUSTOM_ENCHANT_KEYWORDS = {
            "regeneraci", "regeneration", "corazon", "corazones", "vida", "health",
            "absorci", "absorption", "resistencia", "resistance", "fuerza", "strength",
            "velocidad", "speed", "prisa", "haste", "vision nocturna", "night vision",
            "fuego", "fire", "ignifug", "auto reparaci", "reparador", "vampir",
            "salto", "jump", "saturaci", "saturation", "curaci", "invisib", "telepat",
            "implacable", "guardian", "anti knockback", "anti empuje", "overload",
            "decapita", "pesadilla", "aplastar", "esquivar", "rayo", "congelar",
            "excavador", "fundici", "explosivo", "vetas", "fertilizante", "rastro", "talador"
    };

    private static final Pattern PATTERN_ATTR = Pattern.compile("^([\\p{L}0-9_\\s]{2,30})\\s+(\\+?\\d+|[IVXLCDM]+)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_ATTR_PREFIX = Pattern.compile("^([+-]?\\d+%?)\\s+([\\p{L}0-9_\\s]{2,30})$", Pattern.CASE_INSENSITIVE);

    public static String stripAccents(String s) {
        if (s == null) return "";
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] words = input.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String w = words[i];
            if (w.isEmpty()) continue;
            if (sb.length() > 0) sb.append(" ");
            String lower = w.toLowerCase();
            if (i > 0 && (lower.equals("de") || lower.equals("del") || lower.equals("el") || lower.equals("la") || lower.equals("contra") || lower.equals("los") || lower.equals("las"))) {
                sb.append(lower);
            } else {
                sb.append(Character.toUpperCase(w.charAt(0)));
                if (w.length() > 1) {
                    sb.append(w.substring(1).toLowerCase());
                }
            }
        }
        return sb.toString();
    }

    public static boolean isVanillaEnchantmentName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        String clean = stripAccents(UnicodeFontHelper.fromSmallCaps(name).toLowerCase().trim());
        while (!clean.isEmpty()) {
            char c = clean.charAt(0);
            if ((c >= 0xE000 && c <= 0xF8FF) || c == ':' || c == '\u00bb' || c == '-' || c == '*' || c == '\u25a0') {
                clean = clean.substring(1).trim();
            } else {
                break;
            }
        }
        for (Map.Entry<String, EnchantInfo> entry : ENCHANT_MAP.entrySet()) {
            String key = stripAccents(entry.getKey().toLowerCase().replace('_', ' '));
            String dName = stripAccents(UnicodeFontHelper.fromSmallCaps(entry.getValue().displayName()).toLowerCase());
            if (clean.equals(key) || clean.equals(dName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEnchantmentLine(String line) {
        if (line == null || line.trim().isEmpty()) return false;
        String clean = stripFormatting(line).trim();
        if (clean.isEmpty()) return false;

        while (!clean.isEmpty()) {
            char c = clean.charAt(0);
            if ((c >= 0xE000 && c <= 0xF8FF) || c == ':' || c == '\u00bb' || c == '-' || c == '*' || c == '\u25a0') {
                clean = clean.substring(1).trim();
            } else {
                break;
            }
        }

        if (clean.startsWith("+") || clean.startsWith("-")) {
            return false;
        }

        CustomAttribute attr = parseAttribute(line);
        if (attr == null || attr.name().isEmpty()) return false;

        String name = attr.name();
        if (isVanillaEnchantmentName(name)) {
            return true;
        }

        String lower = line.toLowerCase();
        if ((lower.contains("<gold>") || lower.startsWith("&6") || lower.startsWith("Â§6")) && !attr.level().isEmpty()) {
            String norm = stripAccents(UnicodeFontHelper.fromSmallCaps(name).toLowerCase());
            for (String key : ENCHANT_MAP.keySet()) {
                if (norm.equals(key.replace('_', ' ')) || norm.contains(key.replace('_', ' '))) {
                    return true;
                }
            }
            for (EnchantInfo info : ENCHANT_MAP.values()) {
                String dNorm = stripAccents(UnicodeFontHelper.fromSmallCaps(info.displayName()).toLowerCase());
                if (norm.equals(dNorm) || norm.contains(dNorm)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String formatEnchantName(String name, AutoLorePreset preset) {
        if (name == null || name.isEmpty()) return "";
        String cleanNorm = stripAccents(UnicodeFontHelper.fromSmallCaps(name).trim().toLowerCase());

        String bestName = null;
        for (Map.Entry<String, EnchantInfo> entry : ENCHANT_MAP.entrySet()) {
            String key = stripAccents(entry.getKey().toLowerCase().replace('_', ' '));
            String dName = stripAccents(UnicodeFontHelper.fromSmallCaps(entry.getValue().displayName()).toLowerCase());
            if (cleanNorm.equals(key) || cleanNorm.equals(dName)) {
                bestName = entry.getValue().displayName();
                break;
            }
        }
        if (bestName == null) {
            bestName = name;
        }

        String capitalized = capitalizeWords(bestName);
        if (preset != null) {
            if (preset.smallCaps) {
                return UnicodeFontHelper.toSmallCaps(capitalized);
            } else if (preset.uppercase) {
                return capitalized.toUpperCase();
            }
        }
        return capitalized;
    }

    public static boolean isPlacementTag(String line) {
        if (line == null) return false;
        String t = line.trim().toLowerCase();
        return t.equals("<enchants>") || t.equals("<enchantments>") || t.equals("<encantos>") || t.startsWith("<enchants:")
                || t.equals("<customenchants>") || t.equals("<custom_enchants>") || t.equals("<ce>") || t.startsWith("<customenchants:")
                || t.equals("<atributes>") || t.equals("<attributes>") || t.equals("<atributos>") || t.equals("<attrs>") || t.startsWith("<attributes:")
                || t.equals("<autolore>") || t.startsWith("<autolore") || t.equals("<full_lore>") || t.equals("<lore_auto>");
    }

    public static boolean isNaturalAttribute(String name, String level) {
        if (name == null) return false;
        String norm = stripAccents(UnicodeFontHelper.fromSmallCaps(name).toLowerCase().trim());
        String lvl = level != null ? level.trim() : "";

        // Textos de ranura vanilla (ej: "En la mano principal", "When in Main Hand")
        if (norm.contains("mano principal") || norm.contains("main hand")
                || norm.contains("mano secundaria") || norm.contains("off hand")
                || norm.contains("cabeza") || norm.contains("head")
                || norm.contains("cuerpo") || norm.contains("body") || norm.contains("chest")
                || norm.contains("piernas") || norm.contains("legs")
                || norm.contains("pies") || norm.contains("feet")) {
            return true;
        }

        // DaÃ±o de ataque vanilla natural (valores tÃ­picos vanilla +1 a +12, no valores custom como +10000 o 32k)
        if (norm.equals("dano de ataque") || norm.equals("attack damage") || norm.contains("dano de ataque") || norm.contains("attack damage")) {
            if (lvl.isEmpty()) return true;
            try {
                double val = Double.parseDouble(lvl.replace("+", "").trim());
                if (val >= 0 && val <= 14) {
                    return true;
                }
            } catch (Exception ignored) {}
        }

        // Velocidad de ataque vanilla natural (ej: 1.6, 4.0, -2.4)
        if (norm.equals("velocidad de ataque") || norm.equals("attack speed") || norm.contains("velocidad de ataque") || norm.contains("attack speed")) {
            return true;
        }

        // Armadura vanilla natural (ej: +1 a +8)
        if (norm.equals("armadura") || norm.equals("puntos de armadura") || norm.equals("armor")) {
            if (lvl.isEmpty()) return true;
            try {
                double val = Double.parseDouble(lvl.replace("+", "").trim());
                if (val >= 0 && val <= 10) {
                    return true;
                }
            } catch (Exception ignored) {}
        }

        // Dureza de armadura vanilla natural (ej: +1 a +4)
        if (norm.equals("dureza de la armadura") || norm.equals("dureza") || norm.equals("armor toughness")) {
            if (lvl.isEmpty()) return true;
            try {
                double val = Double.parseDouble(lvl.replace("+", "").trim());
                if (val >= 0 && val <= 5) {
                    return true;
                }
            } catch (Exception ignored) {}
        }

        // Resistencia al empuje vanilla natural (ej: +0.1)
        if (norm.equals("resistencia al empuje") || norm.equals("knockback resistance")) {
            if (lvl.isEmpty()) return true;
            try {
                double val = Double.parseDouble(lvl.replace("+", "").trim());
                if (val >= 0 && val <= 1) {
                    return true;
                }
            } catch (Exception ignored) {}
        }

        return false;
    }

    public static boolean isNaturalAttributeLine(String line) {
        if (line == null || line.trim().isEmpty()) return false;
        String clean = stripFormatting(line).trim();
        String lower = stripAccents(clean.toLowerCase());

        if (lower.startsWith("al estar ") || lower.startsWith("when in ") || lower.startsWith("en la mano ")
                || lower.startsWith("en el cuerpo") || lower.startsWith("en las piernas") || lower.startsWith("en los pies")
                || lower.startsWith("en la cabeza")) {
            return true;
        }

        CustomAttribute ca = parseAttribute(line);
        if (ca != null && !ca.name().isEmpty()) {
            return isNaturalAttribute(ca.name(), ca.level());
        }

        return false;
    }

    /**
     * Determina si un nombre corresponde a un Custom Enchant.
     */
    public static boolean isCustomEnchantName(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        String lower = UnicodeFontHelper.fromSmallCaps(name).toLowerCase().trim();

        // Limpiar viÃ±etas o prefijos
        while (!lower.isEmpty()) {
            if (lower.startsWith("\uD83C\uDF41")) {
                lower = lower.substring("\uD83C\uDF41".length()).trim();
            } else {
                char c = lower.charAt(0);
                if (c == '+' || c == '-' || c == '\u00bb' || c == '*' || c == '\u25a0') {
                    lower = lower.substring(1).trim();
                } else {
                    break;
                }
            }
        }

        // Si empieza con modificador explÃ­cito de atributo como "+10000" o "daÃ±o de ataque", no es custom enchant
        if (lower.startsWith("+") || lower.startsWith("-") || lower.contains("daÃ±o de ataque") || lower.contains("attack damage")
                || lower.contains("velocidad de ataque") || lower.contains("puntos de armadura") || lower.contains("dureza")) {
            return false;
        }

        for (String ce : GAMBLE_CUSTOM_ENCHANTS) {
            if (lower.equals(ce) || lower.startsWith(ce + " ") || lower.startsWith(ce + "_") || lower.contains(ce)) {
                return true;
            }
        }
        for (String kw : KNOWN_CUSTOM_ENCHANT_KEYWORDS) {
            if (lower.contains(kw)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parsea una lÃ­nea a CustomEnchant (ej: "&l<frost>VELOCIDAD 2" o "Velocidad 2").
     */
    public static CustomEnchant parseCustomEnchant(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String clean = stripFormatting(line).trim();
        while (!clean.isEmpty()) {
            if (clean.startsWith("\uD83C\uDF41")) {
                clean = clean.substring("\uD83C\uDF41".length()).trim();
            } else {
                char c = clean.charAt(0);
                if ((c >= 0xE000 && c <= 0xF8FF) || c == ':' || c == '\u00bb' || c == '-' || c == '*' || c == '\u25a0') {
                    clean = clean.substring(1).trim();
                } else {
                    break;
                }
            }
        }
        String decoded = UnicodeFontHelper.fromSmallCaps(clean).trim();
        Matcher matcher = PATTERN_ATTR.matcher(decoded);
        if (matcher.matches()) {
            String name = matcher.group(1).trim().toUpperCase();
            String rawLvl = matcher.group(2);
            String level = normalizeLevel(rawLvl);
            return new CustomEnchant(name, level);
        }
        return new CustomEnchant(decoded.toUpperCase(), "");
    }

    /**
     * Devuelve el icono correspondiente para un atributo segÃºn palabras clave.
     */
    public static String getAttributeIcon(String name) {
        if (name == null) return ICON_HEART;
        String lower = UnicodeFontHelper.fromSmallCaps(name).toLowerCase();

        if (lower.contains("regenera") || lower.contains("curaci") || lower.contains("heal")
                || lower.contains("vampir") || lower.contains("saturac")) {
            return ICON_HEART;
        }
        if (lower.contains("extra coraz") || lower.contains("corazones") || lower.contains("vida")
                || lower.contains("health") || lower.contains("overload")) {
            return ICON_HEALTH_BOOST;
        }
        if (lower.contains("absorc")) {
            return ICON_ABSORPTION;
        }
        if (lower.contains("fuego") || lower.contains("fire") || lower.contains("ignifug")) {
            return ICON_FIRE_SHIELD;
        }
        if (lower.contains("resistencia") || lower.contains("guardian") || lower.contains("escudo")
                || lower.contains("knockback") || lower.contains("empuje")) {
            return ICON_SHIELD;
        }
        if (lower.contains("fuerza") || lower.contains("strength") || lower.contains("daÃ±o")
                || lower.contains("implacable") || lower.contains("pesadilla") || lower.contains("decapita")) {
            return ICON_SWORD;
        }
        if (lower.contains("velocidad") || lower.contains("speed") || lower.contains("salto") || lower.contains("jump")) {
            return ICON_HOURGLASS;
        }
        if (lower.contains("prisa") || lower.contains("haste") || lower.contains("mineria")) {
            return ICON_HASTE;
        }
        if (lower.contains("nocturna") || lower.contains("night_vision")) {
            return ICON_NIGHT_VISION;
        }
        if (lower.contains("repara") || lower.contains("repair")) {
            return ICON_ANVIL;
        }
        if (lower.contains("invisib") || lower.contains("telepat")) {
            return ICON_SCROLL;
        }

        return ICON_HEART;
    }

    /**
     * Parsea una lÃ­nea a CustomAttribute, extrayendo nombre, nivel e icono.
     */
    public static CustomAttribute parseAttribute(String line) {
        if (line == null || line.trim().isEmpty()) return null;

        String icon = null;

        // 1. Extraer posible icono unicode (\uE000 - \uF8FF) al inicio de la lÃ­nea
        for (int ci = 0; ci < line.length(); ci++) {
            char c = line.charAt(ci);
            if (c >= 0xE000 && c <= 0xF8FF) {
                icon = String.valueOf(c);
                break;
            } else if (c == '<' || c == '&' || c == '\u00a7') {
                continue;
            }
        }

        // 2. Limpiar cÃ³digos de color (& y Â§) y tags
        String clean = stripFormatting(line).trim();
        while (!clean.isEmpty() && (clean.charAt(0) >= 0xE000 && clean.charAt(0) <= 0xF8FF || clean.charAt(0) == ':' || clean.charAt(0) == '\u00bb' || clean.charAt(0) == '-' || clean.charAt(0) == '*' || clean.charAt(0) == '\u25a0')) {
            clean = clean.substring(1).trim();
        }

        // 3. Decodificar SmallCaps
        String decoded = UnicodeFontHelper.fromSmallCaps(clean).trim();

        Matcher matcherPrefix = PATTERN_ATTR_PREFIX.matcher(decoded);
        if (matcherPrefix.matches()) {
            String rawLvl = matcherPrefix.group(1);
            String name = matcherPrefix.group(2).trim().toUpperCase();
            String level = normalizeLevel(rawLvl);
            if (icon == null) {
                icon = getAttributeIcon(name);
            }
            return new CustomAttribute(name, level, icon);
        }

        Matcher matcher = PATTERN_ATTR.matcher(decoded);
        if (matcher.matches()) {
            String name = matcher.group(1).trim().toUpperCase();
            String rawLvl = matcher.group(2);
            String level = normalizeLevel(rawLvl);
            if (icon == null) {
                icon = getAttributeIcon(name);
            }
            return new CustomAttribute(name, level, icon);
        }

        String name = decoded.toUpperCase();
        if (icon == null) {
            icon = getAttributeIcon(name);
        }
        return new CustomAttribute(name, "", icon);
    }

    public static String normalizeLevel(String raw) {
        if (raw == null || raw.trim().isEmpty()) return "";
        raw = raw.trim();
        if (raw.startsWith("+")) {
            return raw;
        }
        try {
            int num = Integer.parseInt(raw);
            return String.valueOf(num);
        } catch (NumberFormatException ignored) {}

        int roman = parseRoman(raw.toUpperCase());
        if (roman > 0) {
            return String.valueOf(roman);
        }
        return raw;
    }

    private static int parseRoman(String s) {
        return switch (s) {
            case "I" -> 1;
            case "II" -> 2;
            case "III" -> 3;
            case "IV" -> 4;
            case "V" -> 5;
            case "VI" -> 6;
            case "VII" -> 7;
            case "VIII" -> 8;
            case "IX" -> 9;
            case "X" -> 10;
            case "XI" -> 11;
            case "XII" -> 12;
            case "XIII" -> 13;
            case "XIV" -> 14;
            case "XV" -> 15;
            case "XVI" -> 16;
            case "XVII" -> 17;
            case "XVIII" -> 18;
            case "XIX" -> 19;
            case "XX" -> 20;
            default -> -1;
        };
    }

    public static String stripFormatting(String input) {
        if (input == null) return "";
        return input.replaceAll("[&Â§][0-9a-fk-orA-FK-OR]", "")
                .replaceAll("<[^>]+>", "")
                .trim();
    }

    /**
     * Determina si una lÃ­nea es una lÃ­nea cruda inyectada por un plugin de servidor.
     */
    public static boolean isRawPluginLine(String line) {
        if (line == null || line.trim().isEmpty()) return false;
        String clean = stripFormatting(line).trim();
        if (clean.isEmpty()) return false;

        if (line.contains("<") && line.contains(">")) {
            for (var preset : com.fxfps.cmi.client.animated.AnimatedTagRegistry.PRESETS) {
                if (line.toLowerCase().contains("<" + preset.tag().toLowerCase() + ">")) return false;
            }
            if (line.toLowerCase().contains("<silver>") || line.toLowerCase().contains("<gold>")) return false;
        }

        String normClean = UnicodeFontHelper.fromSmallCaps(clean).toUpperCase();
        if (normClean.contains("ARGENTINA NETWORK") || normClean.contains("ENCANTAMIENTOS") || normClean.contains("CUSTOM ENCHANTS") || normClean.contains("ATRIBUTOS")) return false;
        if (normClean.contains("CONSIGUE ESTE ITEM") || normClean.contains("NETWORKARGENTINA.COM")) return false;

        if (clean.contains(":") || clean.startsWith("/") || clean.startsWith("(")) return false;
        String lower = normClean.toLowerCase();
        if (lower.startsWith("when ") || lower.startsWith("al estar ") || lower.startsWith("durab") || lower.startsWith("shift") || lower.startsWith("presiona")) return false;

        return isCustomEnchantName(clean);
    }

    public static boolean hasRawPluginLore(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.has(DataComponents.LORE)) return false;
        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore == null || lore.lines().isEmpty()) return false;

        AutoLorePreset preset = AutoLoreRegistry.getActivePreset();
        for (int i = 0; i < Math.min(6, lore.lines().size()); i++) {
            String raw = lore.lines().get(i).getString();
            String clean = stripFormatting(raw);
            String normClean = UnicodeFontHelper.fromSmallCaps(clean).toUpperCase();
            if (normClean.equals("ENCANTAMIENTOS") || normClean.contains("CUSTOM ENCHANTS") || normClean.equals("ATRIBUTOS")
                    || (preset.header != null && !preset.header.isEmpty() && raw.contains(preset.header))) {
                return false;
            }
            if (isRawPluginLine(raw)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reestructura y formatea el lore completo segÃºn el preset activo de Auto-Lore.
     *
     * Orden estricto requerido:
     *   1- ENCANTAMIENTOS (Ãšnicamente si existen encantamientos)
     *   2- &7\uD83C\uDF41 CUSTOM ENCHANTS (Ãšnicamente si existen custom enchants)
     *   3- ATRIBUTOS (Ãšnicamente si existen atributos)
     */
    public static List<String> restructureLore(List<String> existingLines, ItemStack stack) {
        return restructureLore(existingLines, stack, AutoLoreRegistry.getActivePreset(), null);
    }

    public static List<String> restructureLore(List<String> existingLines, ItemStack stack, AutoLorePreset preset) {
        return restructureLore(existingLines, stack, preset, null);
    }

    public static List<String> restructureLore(List<String> existingLines, ItemStack stack, AutoLorePreset preset, List<CustomAttribute> overrideAttributes) {
        if (preset == null) {
            preset = AutoLoreRegistry.getActivePreset();
        }

        String headerLine = null;
        if (preset.header != null && !preset.header.trim().isEmpty()) {
            headerLine = preset.header;
        }

        List<String> enchantLines = new ArrayList<>();
        List<String> rawPluginLines = new ArrayList<>();
        Map<String, CustomEnchant> customEnchants = new LinkedHashMap<>();
        Map<String, CustomAttribute> attributes = new LinkedHashMap<>();
        List<String> otherCustomLines = new ArrayList<>();
        List<String> footerLines = new ArrayList<>();

        boolean inEnchantsSection = false;
        boolean inCustomEnchantsSection = false;
        boolean inAttributesSection = false;

        boolean hasCustomPlacement = false;
        for (String l : existingLines) {
            if (isPlacementTag(l)) {
                hasCustomPlacement = true;
                break;
            }
        }

        // 1. Analizar las lÃ­neas existentes
        for (int i = 0; i < existingLines.size(); i++) {
            String line = existingLines.get(i);
            if (isPlacementTag(line)) {
                continue;
            }
            if (isNaturalAttributeLine(line)) {
                continue;
            }
            String clean = stripFormatting(line);

            if (clean.contains("CONSIGUE ESTE ITEM") || clean.contains("NETWORKARGENTINA.COM")) {
                continue;
            }

            if (clean.contains("ARGENTINA NETWORK") || line.contains("\uE100") || line.contains(":flag_ar:")) {
                if (preset.header != null && preset.header.contains("ARGENTINA NETWORK")) {
                    headerLine = preset.header;
                }
                continue;
            }

            // DetecciÃ³n de secciÃ³n 1: Encantamientos
            String normClean = UnicodeFontHelper.fromSmallCaps(clean).toUpperCase();
            String normEnchantHeader = UnicodeFontHelper.fromSmallCaps(stripFormatting(preset.enchantHeader)).toUpperCase();
            if (!normClean.isEmpty() && (normClean.equals("ENCANTAMIENTOS") || normClean.equals("ENCANTOS")
                    || normClean.equals("ENCHANTMENTS") || (!normEnchantHeader.isEmpty() && normClean.equals(normEnchantHeader)))) {
                inEnchantsSection = true;
                inCustomEnchantsSection = false;
                inAttributesSection = false;
                continue;
            }

            // DetecciÃ³n de secciÃ³n 2: Custom Enchants (&7\uD83C\uDF41 CUSTOM ENCHANTS)
            if (normClean.contains("CUSTOM ENCHANTS") || normClean.contains("CUSTOMENCHANTS")
                    || (clean.contains("\uD83C\uDF41") && normClean.contains("CUSTOM"))
                    || (preset.customEnchantHeader != null && normClean.equals(UnicodeFontHelper.fromSmallCaps(stripFormatting(preset.customEnchantHeader)).toUpperCase()))) {
                inCustomEnchantsSection = true;
                inEnchantsSection = false;
                inAttributesSection = false;
                continue;
            }

            // DetecciÃ³n de secciÃ³n 3: Atributos
            String normAttrHeader = UnicodeFontHelper.fromSmallCaps(stripFormatting(preset.attrHeader)).toUpperCase();
            if (normClean.equals("ATRIBUTOS") || normClean.equals("ATTRIBUTES") || normClean.equals(normAttrHeader)) {
                inAttributesSection = true;
                inEnchantsSection = false;
                inCustomEnchantsSection = false;
                continue;
            }

            // Separador vacÃ­o
            if (clean.isEmpty()) {
                inEnchantsSection = false;
                inCustomEnchantsSection = false;
                inAttributesSection = false;
                continue;
            }

            // Si aÃºn no estamos en ninguna secciÃ³n:
            if (!inEnchantsSection && !inCustomEnchantsSection && !inAttributesSection) {
                if (isEnchantmentLine(line)) {
                    CustomAttribute enchAttr = parseAttribute(line);
                    if (enchAttr != null && !enchAttr.name().isEmpty()) {
                        String eLvl = enchAttr.level().isEmpty() ? "1" : enchAttr.level();
                        String eTag = (preset.enchantTag != null && !preset.enchantTag.isEmpty()) ? preset.enchantTag : "gold";
                        String eBold = preset.bold ? "&l" : "";
                        String eIc = (preset.useIcons && enchAttr.icon() != null) ? enchAttr.icon() : "";
                        String eName = formatEnchantName(enchAttr.name(), preset);
                        enchantLines.add(eIc + eBold + "<" + eTag + ">" + eName + " " + eLvl);
                    }
                    continue;
                }
                if (isCustomEnchantName(clean)) {
                    CustomEnchant ce = parseCustomEnchant(line);
                    if (ce != null && !ce.name().isEmpty()) {
                        customEnchants.put(ce.name(), ce);
                    }
                    continue;
                }
                String aTag = (preset.attrTag != null && !preset.attrTag.isEmpty()) ? preset.attrTag.toLowerCase() : "silver";
                if (line.toLowerCase().contains("<" + aTag + ">")) {
                    CustomAttribute attr = parseAttribute(line);
                    if (attr != null && !attr.name().isEmpty() && !isNaturalAttribute(attr.name(), attr.level())) {
                        attributes.put(attr.name(), attr);
                    }
                    continue;
                }
            }

            // Si estamos dentro de la secciÃ³n 2: CUSTOM ENCHANTS
            if (inCustomEnchantsSection) {
                CustomEnchant ce = parseCustomEnchant(line);
                if (ce != null && !ce.name().isEmpty()) {
                    customEnchants.put(ce.name(), ce);
                }
                continue;
            }

            // Si estamos dentro de la secciÃ³n 3: ATRIBUTOS
            if (inAttributesSection) {
                if (isCustomEnchantName(clean)) {
                    CustomEnchant ce = parseCustomEnchant(line);
                    if (ce != null && !ce.name().isEmpty()) {
                        customEnchants.put(ce.name(), ce);
                    }
                } else {
                    CustomAttribute attr = parseAttribute(line);
                    if (attr != null && !attr.name().isEmpty() && !isNaturalAttribute(attr.name(), attr.level())) {
                        attributes.put(attr.name(), attr);
                    }
                }
                continue;
            }

            // Si estamos dentro de la secciÃ³n 1: ENCANTAMIENTOS
            if (inEnchantsSection) {
                CustomAttribute enchAttr = parseAttribute(line);
                if (enchAttr != null && !enchAttr.name().isEmpty()) {
                    String eLvl = enchAttr.level().isEmpty() ? "1" : enchAttr.level();
                    String eTag = (preset.enchantTag != null && !preset.enchantTag.isEmpty()) ? preset.enchantTag : "gold";
                    String eBold = preset.bold ? "&l" : "";
                    String eIc = (preset.useIcons && enchAttr.icon() != null) ? enchAttr.icon() : "";
                    String eName = formatEnchantName(enchAttr.name(), preset);
                    enchantLines.add(eIc + eBold + "<" + eTag + ">" + eName + " " + eLvl);
                }
                continue;
            }

            // Otras lÃ­neas de lore personalizadas
            otherCustomLines.add(line);
        }

        // 2. Procesar lÃ­neas crudas capturadas del plugin
        for (String raw : rawPluginLines) {
            if (isCustomEnchantName(raw)) {
                CustomEnchant ce = parseCustomEnchant(raw);
                if (ce != null && !ce.name().isEmpty()) {
                    customEnchants.put(ce.name(), ce);
                }
            } else {
                otherCustomLines.add(raw);
            }
        }

        // Cargar custom enchants persistidos en cmi_custom_enchants
        Map<String, Integer> tagCe = getCustomEnchantsMap(stack);
        for (Map.Entry<String, Integer> entry : tagCe.entrySet()) {
            String ceName = entry.getKey();
            if (!customEnchants.containsKey(ceName.toUpperCase(Locale.ROOT))) {
                customEnchants.put(ceName.toUpperCase(Locale.ROOT), new CustomEnchant(ceName, String.valueOf(entry.getValue())));
            }
        }

        // Si se especifican atributos por parÃ¡metro explÃ­cito (ej: al agregarlos desde el menÃº del mod):
        if (overrideAttributes != null) {
            attributes.clear();
            for (CustomAttribute ca : overrideAttributes) {
                if (ca != null && !ca.name().isEmpty() && !isNaturalAttribute(ca.name(), ca.level())) {
                    attributes.put(ca.name(), ca);
                }
            }
        } else if (attributes.isEmpty() && stack != null && !stack.isEmpty() && stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            ItemAttributeModifiers current = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (current != null && !current.modifiers().isEmpty()) {
                for (var entry : current.modifiers()) {
                    String name = getAttributeDisplayName(entry.attribute());
                    double val = entry.modifier().amount();
                    var op = entry.modifier().operation();

                    String valStr;
                    if (op == AttributeModifier.Operation.ADD_VALUE) {
                        valStr = (val >= 0 ? "+" : "") + (val == (long) val ? String.valueOf((long) val) : String.valueOf(val));
                    } else {
                        double pct = val * 100.0;
                        valStr = (pct >= 0 ? "+" : "") + (pct == (long) pct ? String.valueOf((long) pct) : String.format(Locale.US, "%.1f", pct)) + "%";
                    }

                    if (!isNaturalAttribute(name, valStr)) {
                        String icon = getAttributeIcon(name);
                        attributes.put(name, new CustomAttribute(name, valStr, icon));
                    }
                }
            }
        }

        // 3. Sincronizar encantamientos reales del ItemStack si existen
        if (stack != null && !stack.isEmpty()) {
            var enchants = stack.getOrDefault(
                    stack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS,
                    stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
            );

            if (!enchants.isEmpty()) {
                enchantLines.clear();

                List<Map.Entry<Holder<Enchantment>, Integer>> list = new ArrayList<>(enchants.entrySet());
                list.sort((e1, e2) -> {
                    String k1 = e1.getKey().unwrapKey().map(k -> k.identifier().getPath()).orElse("");
                    String k2 = e2.getKey().unwrapKey().map(k -> k.identifier().getPath()).orElse("");
                    return getEnchantPriority(k1) - getEnchantPriority(k2);
                });

                for (var entry : list) {
                    int lvl = entry.getValue();
                    if (lvl <= 0) continue;
                    Holder<Enchantment> holder = entry.getKey();
                    String path = holder.unwrapKey().map(k -> k.identifier().getPath()).orElse("");
                    
                    String dName;
                    try {
                        String desc = holder.value().description().getString();
                        if (desc != null && !desc.isEmpty() && !desc.startsWith("enchantment.")) {
                            dName = capitalizeWords(desc);
                        } else {
                            EnchantInfo info = ENCHANT_MAP.get(path.toLowerCase(Locale.ROOT));
                            dName = info != null ? capitalizeWords(info.displayName()) : capitalizeWords(path.replace('_', ' '));
                        }
                    } catch (Exception e) {
                        EnchantInfo info = ENCHANT_MAP.get(path.toLowerCase(Locale.ROOT));
                        dName = info != null ? capitalizeWords(info.displayName()) : capitalizeWords(path.replace('_', ' '));
                    }

                    String formatted = ItemModifierColorManager.formatEnchant(path, dName, String.valueOf(lvl), preset.smallCaps, preset.bold);
                    if (formatted != null && !formatted.isEmpty()) {
                        String enchKey = path.toLowerCase(Locale.ROOT);
                        if (!isEnchantHidden(stack, enchKey) && !isEnchantHidden(stack, dName)) {
                            String ic = "";
                            if (preset.useIcons) {
                                EnchantInfo info = ENCHANT_MAP.get(path.toLowerCase(Locale.ROOT));
                                ic = info != null ? info.icon() + " " : "";
                            }
                            enchantLines.add(ic + formatted);
                        }
                    }
                }
            }
        }

        // 4. Pie de preset
        if (preset.footer != null && !preset.footer.isEmpty()) {
            footerLines.addAll(preset.footer);
        }

        // 5. Ensamblado final
        if (hasCustomPlacement) {
            List<String> result = new ArrayList<>();
            boolean enchantsEmitted = false;
            boolean customEnchantsEmitted = false;
            boolean attributesEmitted = false;

            for (String line : existingLines) {
                String t = line.trim().toLowerCase();

                if (t.equals("<enchants>") || t.equals("<enchantments>")) {
                    if (!enchantLines.isEmpty()) {
                        String rawHeader = (preset.enchantHeader != null) ? preset.enchantHeader.trim() : "";
                        if (!rawHeader.isEmpty()) {
                            String header = (preset.smallCaps) ? UnicodeFontHelper.toSmallCaps(rawHeader) : rawHeader;
                            result.add(header);
                        }
                        result.addAll(enchantLines);
                    }
                    enchantsEmitted = true;
                    continue;
                }

                if (t.equals("<customenchants>") || t.equals("<custom_enchants>") || t.equals("<ce>") || t.startsWith("<customenchants:")) {
                    List<String> validCeLines = new ArrayList<>();
                    for (CustomEnchant ce : customEnchants.values()) {
                        if (isEnchantHidden(stack, ce.name()) || isEnchantHidden(stack, ce.name().toLowerCase(Locale.ROOT))) {
                            continue;
                        }
                        String l = ce.toLoreLine(preset);
                        if (l != null && !l.isEmpty()) {
                            validCeLines.add(l);
                        }
                    }
                    if (!validCeLines.isEmpty()) {
                        String rawCeHeader = (preset.customEnchantHeader != null && !preset.customEnchantHeader.isEmpty())
                                ? preset.customEnchantHeader : SECTION_CUSTOM_ENCHANTS;
                        String ceHeader = (preset.smallCaps) ? UnicodeFontHelper.toSmallCaps(rawCeHeader) : rawCeHeader;
                        if (!rawCeHeader.isEmpty() && !t.contains(":noheader")) {
                            result.add(ceHeader);
                        }
                        result.addAll(validCeLines);
                    }
                    customEnchantsEmitted = true;
                    continue;
                }

                if (t.equals("<atributes>") || t.equals("<attributes>") || t.equals("<atributos>") || t.equals("<attrs>") || t.startsWith("<attributes:") || t.startsWith("<atributes:")) {
                    List<String> validAttrLines = new ArrayList<>();
                    for (CustomAttribute ca : attributes.values()) {
                        if (isEnchantHidden(stack, ca.name()) || isEnchantHidden(stack, ca.name().toLowerCase(Locale.ROOT))) {
                            continue;
                        }
                        if (!isNaturalAttribute(ca.name(), ca.level())) {
                            String l = ca.toLoreLine(preset);
                            if (l != null && !l.isEmpty()) {
                                validAttrLines.add(l);
                            }
                        }
                    }
                    if (!validAttrLines.isEmpty()) {
                        String rawAttrHeader = (preset.attrHeader != null && !preset.attrHeader.isEmpty())
                                ? preset.attrHeader : SECTION_ATTRIBUTES;
                        String attrHeader = (preset.smallCaps) ? UnicodeFontHelper.toSmallCaps(rawAttrHeader) : rawAttrHeader;
                        if (!rawAttrHeader.isEmpty() && !t.contains(":noheader")) {
                            result.add(attrHeader);
                        }
                        result.addAll(validAttrLines);
                    }
                    attributesEmitted = true;
                    continue;
                }

                if (t.equals("<autolore>") || t.startsWith("<autolore")) {
                    // Interoperabilidad con kits.json: resolver plantilla de <autolore1>, <autolore2>, etc.
                    String kitTemplate = com.fxfps.cmi.client.kits.CmiKitManager.resolveAutoLoreTag(t);
                    if (kitTemplate != null && !kitTemplate.trim().isEmpty() && !kitTemplate.equalsIgnoreCase(t)) {
                        String[] tmplLines = kitTemplate.split("\n", -1);
                        for (String tmplLine : tmplLines) {
                            String subT = stripFormatting(tmplLine).trim().toLowerCase(Locale.ROOT);
                            if (subT.equals("<enchants>") || subT.equals("<enchantments>") || subT.equals("<encantos>")) {
                                if (!enchantLines.isEmpty() && !enchantsEmitted) {
                                    String rawHeader = (preset.enchantHeader != null && !preset.enchantHeader.isEmpty())
                                            ? preset.enchantHeader : SECTION_ENCHANTS;
                                    result.add(preset.smallCaps ? UnicodeFontHelper.toSmallCaps(rawHeader) : rawHeader);
                                    result.addAll(enchantLines);
                                    enchantsEmitted = true;
                                }
                            } else if (subT.equals("<customenchants>") || subT.equals("<custom_enchants>") || subT.equals("<customenchant>")) {
                                List<String> autoCeLines = new ArrayList<>();
                                for (CustomEnchant ce : customEnchants.values()) {
                                    if (!isEnchantHidden(stack, ce.name()) && !isEnchantHidden(stack, ce.name().toLowerCase(Locale.ROOT))) {
                                        autoCeLines.add(ce.toLoreLine(preset));
                                    }
                                }
                                if (!autoCeLines.isEmpty() && !customEnchantsEmitted) {
                                    String rawCeHeader = (preset.customEnchantHeader != null && !preset.customEnchantHeader.isEmpty())
                                            ? preset.customEnchantHeader : SECTION_CUSTOM_ENCHANTS;
                                    result.add(preset.smallCaps ? UnicodeFontHelper.toSmallCaps(rawCeHeader) : rawCeHeader);
                                    result.addAll(autoCeLines);
                                    customEnchantsEmitted = true;
                                }
                            } else if (subT.equals("<atributes>") || subT.equals("<attributes>") || subT.equals("<atributos>")) {
                                List<CustomAttribute> customAttrsOnly = new ArrayList<>();
                                for (CustomAttribute ca : attributes.values()) {
                                    if (!isNaturalAttribute(ca.name(), ca.level())
                                            && !isEnchantHidden(stack, ca.name())
                                            && !isEnchantHidden(stack, ca.name().toLowerCase(Locale.ROOT))) {
                                        customAttrsOnly.add(ca);
                                    }
                                }
                                if (!customAttrsOnly.isEmpty() && !attributesEmitted) {
                                    String rawAttrHeader = (preset.attrHeader != null && !preset.attrHeader.isEmpty())
                                            ? preset.attrHeader : SECTION_ATTRIBUTES;
                                    result.add(preset.smallCaps ? UnicodeFontHelper.toSmallCaps(rawAttrHeader) : rawAttrHeader);
                                    for (CustomAttribute ca : customAttrsOnly) {
                                        result.add(ca.toLoreLine(preset));
                                    }
                                    attributesEmitted = true;
                                }
                            } else if (!subT.equals("<autolore>") && !subT.startsWith("<autolore")) {
                                result.add(tmplLine);
                            }
                        }
                        continue;
                    }

                    AutoLorePreset tmplPreset = AutoLoreRegistry.getPresetForTag(t);
                    if (tmplPreset == null) tmplPreset = preset;

                    // Marcador global de kit <autolore>: resuelve dinámicamente las 3 secciones
                    if (!enchantLines.isEmpty()) {
                        String rawHeader = (tmplPreset.enchantHeader != null) ? tmplPreset.enchantHeader.trim() : "";
                        if (!rawHeader.isEmpty()) {
                            String header = (tmplPreset.smallCaps) ? UnicodeFontHelper.toSmallCaps(rawHeader) : rawHeader;
                            result.add(header);
                        }
                        result.addAll(enchantLines);
                        enchantsEmitted = true;
                    }
                    List<String> autoCeLines = new ArrayList<>();
                    for (CustomEnchant ce : customEnchants.values()) {
                        if (!isEnchantHidden(stack, ce.name()) && !isEnchantHidden(stack, ce.name().toLowerCase(Locale.ROOT))) {
                            autoCeLines.add(ce.toLoreLine(tmplPreset));
                        }
                    }
                    if (!autoCeLines.isEmpty()) {
                        String rawCeHeader = (tmplPreset.customEnchantHeader != null && !tmplPreset.customEnchantHeader.isEmpty())
                                ? tmplPreset.customEnchantHeader : SECTION_CUSTOM_ENCHANTS;
                        String ceHeader = (tmplPreset.smallCaps) ? UnicodeFontHelper.toSmallCaps(rawCeHeader) : rawCeHeader;
                        if (!rawCeHeader.isEmpty()) {
                            result.add(ceHeader);
                        }
                        result.addAll(autoCeLines);
                        customEnchantsEmitted = true;
                    }
                    List<CustomAttribute> customAttrsOnly = new ArrayList<>();
                    for (CustomAttribute ca : attributes.values()) {
                        if (!isNaturalAttribute(ca.name(), ca.level())
                                && !isEnchantHidden(stack, ca.name())
                                && !isEnchantHidden(stack, ca.name().toLowerCase(Locale.ROOT))) {
                            customAttrsOnly.add(ca);
                        }
                    }
                    if (!customAttrsOnly.isEmpty()) {
                        String rawAttrHeader = (tmplPreset.attrHeader != null && !tmplPreset.attrHeader.isEmpty())
                                ? tmplPreset.attrHeader : SECTION_ATTRIBUTES;
                        String attrHeader = (tmplPreset.smallCaps) ? UnicodeFontHelper.toSmallCaps(rawAttrHeader) : rawAttrHeader;
                        if (!rawAttrHeader.isEmpty()) {
                            result.add(attrHeader);
                        }
                        for (CustomAttribute ca : customAttrsOnly) {
                            result.add(ca.toLoreLine(tmplPreset));
                        }
                        attributesEmitted = true;
                    }
                    continue;
                }

                if (isEnchantmentLine(line)) {
                    continue;
                }
                if (isCustomEnchantName(stripFormatting(line))) {
                    continue;
                }
                if (isNaturalAttributeLine(line)) {
                    continue;
                }
                CustomAttribute ca = parseAttribute(line);
                if (ca != null && !ca.name().isEmpty() && attributes.containsKey(ca.name())) {
                    continue;
                }

                result.add(line);
            }

            // CERO AUTO-APPEND: Queda estrictamente prohibido añadir modificadores al final
            // si el usuario no incluyó el placeholder explícito en el lore.
            while (!result.isEmpty() && result.get(result.size() - 1).isEmpty()) {
                result.remove(result.size() - 1);
            }

            return result;
        }

        // CERO AUTO-APPEND cuando no se usan tags: El lore conserva ÚNICAMENTE las líneas personalizadas
        List<String> result = new ArrayList<>();
        for (String line : existingLines) {
            if (isEnchantmentLine(line)) continue;
            if (isCustomEnchantName(stripFormatting(line))) continue;
            if (isNaturalAttributeLine(line)) continue;
            result.add(line);
        }

        while (!result.isEmpty() && result.get(result.size() - 1).isEmpty()) {
            result.remove(result.size() - 1);
        }

        return result;
    }

    private static int getEnchantPriority(String path) {
        return switch (path.toLowerCase()) {
            case "protection" -> 1;
            case "fire_protection" -> 2;
            case "blast_protection" -> 3;
            case "projectile_protection" -> 4;
            case "feather_falling" -> 5;
            case "unbreaking" -> 6;
            case "mending" -> 7;
            case "thorns" -> 8;
            case "sharpness" -> 9;
            case "smite" -> 10;
            case "fire_aspect" -> 11;
            case "efficiency" -> 12;
            case "silk_touch" -> 13;
            case "fortune" -> 14;
            default -> 50;
        };
    }

    public static void syncEnchantmentsToLore(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;

        List<String> currentLines = new ArrayList<>();
        if (stack.has(DataComponents.LORE)) {
            ItemLore lore = stack.get(DataComponents.LORE);
            if (lore != null) {
                for (Component comp : lore.lines()) {
                    currentLines.add(UnicodeFontHelper.componentToFormattedString(comp));
                }
            }
        }

        List<String> restructured = restructureLore(currentLines, stack);
        List<Component> newComponents = new ArrayList<>();
        for (String line : restructured) {
            if (line.isEmpty()) {
                newComponents.add(Component.empty());
            } else {
                newComponents.add(UnicodeFontHelper.parseFormattedComponent(line));
            }
        }

        stack.set(DataComponents.LORE, new ItemLore(newComponents));

        TooltipDisplay td = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        td = td.withHidden(DataComponents.ENCHANTMENTS, true);
        td = td.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true);
        stack.set(DataComponents.TOOLTIP_DISPLAY, td);
    }

    public static void restructureItemStack(ItemStack stack) {
        String raw = getRawLore(stack);
        if (raw != null && !raw.trim().isEmpty()) {
            compileAndApplyLore(stack, raw, true);
            return;
        }
        syncEnchantmentsToLore(stack);
    }

    /**
     * Obtiene el texto original crudo (con tags <enchants>, <customenchants>, etc.)
     * persistido en el Ã­tem (CustomData o memoria fallback).
     */
    public static String getRawLore(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        try {
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null && customData.copyTag().contains(RAW_LORE_TAG)) {
                    Object val = customData.copyTag().getString(RAW_LORE_TAG);
                    if (val instanceof Optional<?> opt) {
                        return (String) opt.orElse(null);
                    }
                    return (val != null) ? val.toString() : null;
                }
            }
        } catch (Throwable ignored) {}
        return RAW_LORE_FALLBACK_CACHE.get(stack);
    }

    /**
     * Guarda el texto original crudo en el Ã­tem (CustomData "cmi_raw_lore")
     * garantizando que nunca se degraden los marcadores dinÃ¡micos.
     */
    public static void setRawLore(ItemStack stack, String rawText) {
        if (stack == null || stack.isEmpty()) return;
        if (rawText == null || rawText.trim().isEmpty()) {
            RAW_LORE_FALLBACK_CACHE.remove(stack);
            try {
                if (stack.has(DataComponents.CUSTOM_DATA)) {
                    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.remove(RAW_LORE_TAG));
                }
            } catch (Throwable ignored) {}
            return;
        }

        RAW_LORE_FALLBACK_CACHE.put(stack, rawText);
        try {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(RAW_LORE_TAG, rawText));
        } catch (Throwable ignored) {}
    }

    public static boolean hasRawLore(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        try {
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null && customData.copyTag().contains(RAW_LORE_TAG)) {
                    return true;
                }
            }
        } catch (Throwable ignored) {}
        return RAW_LORE_FALLBACK_CACHE.containsKey(stack);
    }

    // =========================================================================
    // PRESERVACIÓN TOTAL DE COMPONENTES AL CAMBIAR DE ÍTEM (CATÁLOGO)
    // =========================================================================

    /**
     * Transmuta un ItemStack cambiando ÚNICAMENTE su Item/Material base
     * y transfiriendo intactos al 100%:
     * 1. Encantamientos Vanilla (DataComponents.ENCHANTMENTS)
     * 2. Custom Enchants de Gamble (cmi_custom_enchants)
     * 3. Atributos custom (DataComponents.ATTRIBUTE_MODIFIERS)
     * 4. Lore crudo y compilado (cmi_raw_lore y DataComponents.LORE)
     * 5. Nombre personalizado si fue editado (DataComponents.CUSTOM_NAME)
     * 6. Estados de visibilidad de ojos 👁️ (cmi_hidden_enchants)
     */
    public static ItemStack transmuteItemPreservingComponents(ItemStack currentStack, net.minecraft.world.item.Item newItem, int count) {
        if (currentStack == null || currentStack.isEmpty()) {
            return new ItemStack(newItem, Math.max(1, count));
        }
        int effectiveCount = Math.max(1, count);
        ItemStack newStack = currentStack.transmuteCopy(newItem, effectiveCount);

        // Garantizar preservación total de los 6 componentes críticos:
        // 1. Encantamientos Vanilla
        if (currentStack.has(DataComponents.ENCHANTMENTS)) {
            newStack.set(DataComponents.ENCHANTMENTS, currentStack.get(DataComponents.ENCHANTMENTS));
        }
        if (currentStack.has(DataComponents.STORED_ENCHANTMENTS)) {
            newStack.set(DataComponents.STORED_ENCHANTMENTS, currentStack.get(DataComponents.STORED_ENCHANTMENTS));
        }

        // 2 & 6. Custom Enchants y Estados de visibilidad de ojos (cmi_custom_enchants, cmi_hidden_enchants, cmi_raw_lore)
        if (currentStack.has(DataComponents.CUSTOM_DATA)) {
            newStack.set(DataComponents.CUSTOM_DATA, currentStack.get(DataComponents.CUSTOM_DATA));
        }

        // 3. Atributos custom
        if (currentStack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            newStack.set(DataComponents.ATTRIBUTE_MODIFIERS, currentStack.get(DataComponents.ATTRIBUTE_MODIFIERS));
        }

        // 4. Lore crudo y compilado
        if (currentStack.has(DataComponents.LORE)) {
            newStack.set(DataComponents.LORE, currentStack.get(DataComponents.LORE));
        }

        // 5. Nombre personalizado si fue editado
        if (currentStack.has(DataComponents.CUSTOM_NAME)) {
            newStack.set(DataComponents.CUSTOM_NAME, currentStack.get(DataComponents.CUSTOM_NAME));
        }

        return newStack;
    }

    // =========================================================================
    // CUSTOM ENCHANTS EN CUSTOM_DATA ("cmi_custom_enchants")
    // =========================================================================

    public static Map<String, Integer> getCustomEnchantsMap(ItemStack stack) {
        Map<String, Integer> map = new LinkedHashMap<>();
        if (stack == null || stack.isEmpty()) return map;
        try {
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
                if (cd != null) {
                    CompoundTag tag = cd.copyTag();
                    if (tag.contains(CUSTOM_ENCHANTS_TAG)) {
                        CompoundTag ceTag = tag.getCompoundOrEmpty(CUSTOM_ENCHANTS_TAG);
                        for (String key : ceTag.keySet()) {
                            map.put(key, ceTag.getIntOr(key, 1));
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
        return map;
    }

    public static void setCustomEnchantTag(ItemStack stack, String id, int level) {
        if (stack == null || stack.isEmpty() || id == null || id.isEmpty()) return;
        try {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                CompoundTag ceTag = tag.getCompoundOrEmpty(CUSTOM_ENCHANTS_TAG);
                if (level <= 0) {
                    ceTag.remove(id.toLowerCase(Locale.ROOT));
                } else {
                    ceTag.putInt(id.toLowerCase(Locale.ROOT), level);
                }
                tag.put(CUSTOM_ENCHANTS_TAG, ceTag);
            });
        } catch (Throwable ignored) {}
    }

    public static void removeCustomEnchantTag(ItemStack stack, String id) {
        setCustomEnchantTag(stack, id, 0);
    }

    // =========================================================================
    // CONTROL DE VISIBILIDAD DE OJOS 👁️ EN CUSTOM_DATA ("cmi_hidden_enchants")
    // =========================================================================

    public static Set<String> getHiddenEnchants(ItemStack stack) {
        Set<String> set = new HashSet<>();
        if (stack == null || stack.isEmpty()) return set;
        try {
            if (stack.has(DataComponents.CUSTOM_DATA)) {
                CustomData cd = stack.get(DataComponents.CUSTOM_DATA);
                if (cd != null) {
                    CompoundTag tag = cd.copyTag();
                    if (tag.contains(HIDDEN_ENCHANTS_TAG)) {
                        CompoundTag hTag = tag.getCompoundOrEmpty(HIDDEN_ENCHANTS_TAG);
                        for (String key : hTag.keySet()) {
                            if (hTag.getBooleanOr(key, false)) {
                                set.add(key.toLowerCase(Locale.ROOT));
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
        return set;
    }

    public static void setEnchantHidden(ItemStack stack, String key, boolean hidden) {
        if (stack == null || stack.isEmpty() || key == null || key.isEmpty()) return;
        try {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                CompoundTag hTag = tag.getCompoundOrEmpty(HIDDEN_ENCHANTS_TAG);
                if (hidden) {
                    hTag.putBoolean(key.toLowerCase(Locale.ROOT), true);
                } else {
                    hTag.remove(key.toLowerCase(Locale.ROOT));
                }
                tag.put(HIDDEN_ENCHANTS_TAG, hTag);
            });
        } catch (Throwable ignored) {}
    }

    public static boolean isEnchantHidden(ItemStack stack, String key) {
        if (stack == null || stack.isEmpty() || key == null) return false;
        return getHiddenEnchants(stack).contains(key.trim().toLowerCase(Locale.ROOT));
    }

    public static void toggleEnchantVisibility(ItemStack stack, String key) {
        if (stack == null || stack.isEmpty() || key == null) return;
        boolean currentlyHidden = isEnchantHidden(stack, key);
        setEnchantHidden(stack, key, !currentlyHidden);
    }

    public static void toggleEnchantHidden(ItemStack stack, String key) {
        toggleEnchantVisibility(stack, key);
    }

    /**
     * Compila y aplica la especificaciÃ³n maestra de Lore unificado de CMI a un ItemStack:
     * 1. Persiste el texto crudo en cmi_raw_lore (para re-ediciÃ³n y evitar degradaciÃ³n).
     * 2. Resuelve de forma dinÃ¡mica los tags <enchants>, <customenchants>, <atributes> y <autolore>.
     * 3. Aplica SmallCaps si estÃ¡ habilitado (por defecto true).
     * 4. Convierte a componentes y asigna a DataComponents.LORE.
     */
    public static void compileAndApplyLore(ItemStack stack, String rawText) {
        compileAndApplyLore(stack, rawText, true);
    }

    public static void compileAndApplyLore(ItemStack stack, String rawText, boolean smallCaps) {
        if (stack == null || stack.isEmpty()) return;

        // 1. Guardar versiÃ³n cruda para garantizar re-ediciÃ³n
        setRawLore(stack, rawText);

        if (rawText == null || rawText.trim().isEmpty()) {
            stack.remove(DataComponents.LORE);
            return;
        }

        List<String> compiled = compileLore(stack, rawText, smallCaps);

        if (compiled.isEmpty()) {
            stack.remove(DataComponents.LORE);
        } else {
            List<Component> newComponents = new ArrayList<>();
            for (String line : compiled) {
                if (line.isEmpty()) {
                    newComponents.add(Component.empty());
                } else {
                    newComponents.add(UnicodeFontHelper.parseFormattedComponent(line));
                }
            }
            stack.set(DataComponents.LORE, new ItemLore(newComponents));
        }

        TooltipDisplay td = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        td = td.withHidden(DataComponents.ENCHANTMENTS, true);
        td = td.withHidden(DataComponents.ATTRIBUTE_MODIFIERS, true);
        stack.set(DataComponents.TOOLTIP_DISPLAY, td);
    }

    public static List<String> compileLore(ItemStack stack, String rawText, boolean smallCaps) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> rawLines = Arrays.asList(rawText.split("\n", -1));
        AutoLorePreset activePreset = AutoLoreRegistry.getActivePreset();
        if (activePreset == null) {
            activePreset = new AutoLorePreset();
        }

        AutoLorePreset effectivePreset = new AutoLorePreset(
                activePreset.id,
                activePreset.displayName,
                activePreset.header,
                activePreset.enchantHeader,
                activePreset.enchantTag,
                activePreset.customEnchantHeader,
                activePreset.customEnchantTag,
                activePreset.attrHeader,
                activePreset.attrTag,
                activePreset.footer,
                activePreset.useIcons,
                activePreset.bold,
                activePreset.uppercase,
                smallCaps,
                activePreset.custom
        );

        return restructureLore(rawLines, stack, effectivePreset, null);
    }

    /**
     * Agrega o actualiza un Custom Enchant directamente en el lore del ItemStack,
     * garantizando el orden 1- Encantamientos, 2- Custom Enchants, 3- Atributos.
     */
    public static void addOrUpdateCustomEnchant(ItemStack stack, String name, int level) {
        if (stack == null || stack.isEmpty()) return;

        List<String> currentLines = new ArrayList<>();
        if (stack.has(DataComponents.LORE)) {
            ItemLore lore = stack.get(DataComponents.LORE);
            if (lore != null) {
                for (Component comp : lore.lines()) {
                    currentLines.add(UnicodeFontHelper.componentToFormattedString(comp));
                }
            }
        }

        CustomEnchant newCe = new CustomEnchant(name, String.valueOf(level));
        String targetNorm = UnicodeFontHelper.fromSmallCaps(name).toUpperCase().trim();

        boolean replaced = false;
        for (int i = 0; i < currentLines.size(); i++) {
            String line = currentLines.get(i);
            String lineNorm = UnicodeFontHelper.fromSmallCaps(stripFormatting(line)).toUpperCase();
            if (lineNorm.contains(targetNorm)) {
                currentLines.set(i, newCe.toLoreLine());
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            currentLines.add(newCe.toLoreLine());
        }

        List<String> restructured = restructureLore(currentLines, stack);
        List<Component> newComponents = new ArrayList<>();
        for (String line : restructured) {
            if (line.isEmpty()) {
                newComponents.add(Component.empty());
            } else {
                newComponents.add(UnicodeFontHelper.parseFormattedComponent(line));
            }
        }
        stack.set(DataComponents.LORE, new ItemLore(newComponents));
    }

    /**
     * Agrega o actualiza un atributo personalizado directamente en el lore del ItemStack.
     */
    public static void addCustomAttribute(ItemStack stack, String name, String level) {
        if (stack == null || stack.isEmpty()) return;

        if (isCustomEnchantName(name)) {
            int lvl = 1;
            try {
                lvl = Integer.parseInt(normalizeLevel(level));
            } catch (Exception ignored) {}
            addOrUpdateCustomEnchant(stack, name, lvl);
            return;
        }

        List<String> currentLines = new ArrayList<>();
        if (stack.has(DataComponents.LORE)) {
            ItemLore lore = stack.get(DataComponents.LORE);
            if (lore != null) {
                for (Component comp : lore.lines()) {
                    currentLines.add(UnicodeFontHelper.componentToFormattedString(comp));
                }
            }
        }

        String icon = getAttributeIcon(name);
        CustomAttribute newAttr = new CustomAttribute(name, normalizeLevel(level), icon);

        boolean replaced = false;
        String targetNorm = UnicodeFontHelper.fromSmallCaps(name).toUpperCase().trim();
        for (int i = 0; i < currentLines.size(); i++) {
            String line = currentLines.get(i);
            String lineNorm = UnicodeFontHelper.fromSmallCaps(stripFormatting(line)).toUpperCase();
            if (lineNorm.contains(targetNorm)) {
                currentLines.set(i, newAttr.toLoreLine());
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            currentLines.add(newAttr.toLoreLine());
        }

        List<String> restructured = restructureLore(currentLines, stack);
        List<Component> newComponents = new ArrayList<>();
        for (String line : restructured) {
            if (line.isEmpty()) {
                newComponents.add(Component.empty());
            } else {
                newComponents.add(UnicodeFontHelper.parseFormattedComponent(line));
            }
        }
        stack.set(DataComponents.LORE, new ItemLore(newComponents));
    }

    public static String getAttributeDisplayName(Holder<Attribute> holder) {
        if (holder == null) return "Atributo";
        try {
            String key = holder.value().getDescriptionId();
            Component trans = Component.translatable(key);
            String str = trans.getString();
            if (str != null && !str.equals(key)) {
                return str;
            }
        } catch (Exception ignored) {}

        Identifier id = BuiltInRegistries.ATTRIBUTE.getKey(holder.value());
        if (id != null) {
            String path = id.getPath();
            return path.replace("generic.", "").replace("player.", "").replace("_", " ");
        }
        return "Atributo";
    }

    public static void clearCustomAttributes(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;
        stack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
        String raw = getRawLore(stack);
        if (raw != null) {
            compileAndApplyLore(stack, raw);
        }
    }

    public static List<CustomAttribute> getCustomAttributes(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            return Collections.emptyList();
        }
        ItemAttributeModifiers modifiers = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
        if (modifiers == null || modifiers.modifiers().isEmpty()) {
            return Collections.emptyList();
        }
        List<CustomAttribute> list = new ArrayList<>();
        for (var entry : modifiers.modifiers()) {
            String name = getAttributeDisplayName(entry.attribute());
            double val = entry.modifier().amount();
            String valStr = (val >= 0 ? "+" : "") + (val == (long) val ? String.valueOf((long) val) : String.valueOf(val));
            if (!isNaturalAttribute(name, valStr)) {
                String icon = getAttributeIcon(name);
                list.add(new CustomAttribute(name, valStr, icon));
            }
        }
        return list;
    }

    /**
     * Sincroniza los modificadores de atributos del ItemStack hacia la secciÃ³n oficial
     * &7&lATRIBUTOS del Lore, aplicando SmallCaps y el formato estricto con <silver>.
     * Si no hay atributos (o fueron eliminados), la secciÃ³n se omite por completo del Lore.
     */
    public static void syncAttributeModifiersToLore(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return;

        List<String> currentLines = new ArrayList<>();
        if (stack.has(DataComponents.LORE)) {
            ItemLore lore = stack.get(DataComponents.LORE);
            if (lore != null) {
                for (Component comp : lore.lines()) {
                    currentLines.add(UnicodeFontHelper.componentToFormattedString(comp));
                }
            }
        }

        ItemAttributeModifiers current = stack.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY);
        List<CustomAttribute> newAttrs = new ArrayList<>();

        if (!current.modifiers().isEmpty()) {
            for (var entry : current.modifiers()) {
                String name = getAttributeDisplayName(entry.attribute());
                double val = entry.modifier().amount();
                var op = entry.modifier().operation();

                String valStr;
                if (op == AttributeModifier.Operation.ADD_VALUE) {
                    valStr = (val >= 0 ? "+" : "") + (val == (long) val ? String.valueOf((long) val) : String.valueOf(val));
                } else {
                    double pct = val * 100.0;
                    valStr = (pct >= 0 ? "+" : "") + (pct == (long) pct ? String.valueOf((long) pct) : String.format(Locale.US, "%.1f", pct)) + "%";
                }

                String icon = getAttributeIcon(name);
                newAttrs.add(new CustomAttribute(name, valStr, icon));
            }
        }

        List<String> restructured = restructureLore(currentLines, stack, AutoLoreRegistry.getActivePreset(), newAttrs);
        if (restructured.isEmpty()) {
            stack.remove(DataComponents.LORE);
        } else {
            List<Component> newComponents = new ArrayList<>();
            for (String line : restructured) {
                if (line.isEmpty()) {
                    newComponents.add(Component.empty());
                } else {
                    newComponents.add(UnicodeFontHelper.parseFormattedComponent(line));
                }
            }
            stack.set(DataComponents.LORE, new ItemLore(newComponents));
        }
    }

    public static String itemLoreToString(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !stack.has(DataComponents.LORE)) {
            return "";
        }
        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore == null || lore.lines().isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lore.lines().size(); i++) {
            if (i > 0) sb.append("\n");
            Component comp = lore.lines().get(i);
            String raw = comp.getString();
            if (raw.contains("&") || raw.contains("<")) {
                sb.append(raw);
            } else {
                sb.append(UnicodeFontHelper.componentToFormattedString(comp));
            }
        }
        return sb.toString();
    }

    public static void processTooltipLines(ItemStack stack, List<Component> lines) {
        if (stack == null || stack.isEmpty() || lines.size() <= 1) return;
        if (!stack.has(DataComponents.LORE)) return;

        ItemLore lore = stack.get(DataComponents.LORE);
        if (lore == null || lore.lines().isEmpty()) return;

        // Las lÃ­neas de Lore en el tooltip de Minecraft estÃ¡n ubicadas exactamente entre el Ã­ndice 1 y lore.lines().size().
        // Las lÃ­neas posteriores son generadas nativamente por Minecraft (atributos nativos de daÃ±o/armadura,
        // encantamientos nativos, durabilidad, etc.) y DEBEN permanecer 100% intactas para preservar la tipografÃ­a,
        // fuente vanilla y atributos naturales del juego.
        int maxLoreIndex = Math.min(lines.size() - 1, lore.lines().size());
        for (int i = 1; i <= maxLoreIndex; i++) {
            String raw = UnicodeFontHelper.componentToFormattedString(lines.get(i));
            if (UnicodeFontHelper.hasCustomTagsOrShortcodes(raw)) {
                lines.set(i, UnicodeFontHelper.parseFormattedComponent(raw));
            }
        }
    }
}
