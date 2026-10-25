package com.fxfps.cmi.client.fonts;

import com.fxfps.cmi.client.animated.AnimatedTagRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.HashMap;
import java.util.Map;

/**
 * Conversor de texto nativo a fuentes Unicode estilizadas (SmallCaps, Bold, Mono)
 * y parser integral de componentes de Minecraft con soporte para 21 AnimatedTags,
 * códigos HEX (&#RRGGBB / <#RRGGBB>), códigos clásicos (& / §) y emojis.
 */
public class UnicodeFontHelper {

    private static final Map<Character, Character> SMALL_CAPS = new HashMap<>();
    private static final Map<Character, Character> FROM_SMALL_CAPS = new HashMap<>();

    static {
        // Mapeo de minúsculas y mayúsculas a Small Capitals estándar
        String normal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String small  = "ᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢᴀʙᴄᴅᴇғɢʜɪᴊᴋʟᴍɴᴏᴘǫʀsᴛᴜᴠᴡxʏᴢ";

        for (int i = 0; i < normal.length(); i++) {
            char n = normal.charAt(i);
            char s = small.charAt(i);
            SMALL_CAPS.put(n, s);
            FROM_SMALL_CAPS.put(s, Character.toUpperCase(n));
        }

        // Idempotencia: los caracteres Small Caps mapean a sí mismos
        for (int i = 0; i < small.length(); i++) {
            char s = small.charAt(i);
            SMALL_CAPS.put(s, s);
        }

        // Soporte completo para caracteres con tilde en español y la ñ:
        // Se normalizan visualmente a los glifos Small Caps estándar de 5px
        char[][] accents = {
            {'á', 'ᴀ'}, {'Á', 'ᴀ'}, {'à', 'ᴀ'}, {'À', 'ᴀ'}, {'â', 'ᴀ'}, {'Â', 'ᴀ'},
            {'é', 'ᴇ'}, {'É', 'ᴇ'}, {'è', 'ᴇ'}, {'È', 'ᴇ'}, {'ê', 'ᴇ'}, {'Ê', 'ᴇ'},
            {'í', 'ɪ'}, {'Í', 'ɪ'}, {'ì', 'ɪ'}, {'Ì', 'ɪ'}, {'î', 'ɪ'}, {'Î', 'ɪ'},
            {'ó', 'ᴏ'}, {'Ó', 'ᴏ'}, {'ò', 'ᴏ'}, {'Ò', 'ᴏ'}, {'ô', 'ᴏ'}, {'Ô', 'ᴏ'},
            {'ú', 'ᴜ'}, {'Ú', 'ᴜ'}, {'ù', 'ᴜ'}, {'Ù', 'ᴜ'}, {'û', 'ᴜ'}, {'Û', 'ᴜ'},
            {'ü', 'ᴜ'}, {'Ü', 'ᴜ'},
            {'ñ', 'ɴ'}, {'Ñ', 'ɴ'}
        };
        for (char[] pair : accents) {
            SMALL_CAPS.put(pair[0], pair[1]);
        }
    }

    /**
     * Convierte cualquier cadena a Small Caps (ej: "Protección 4" -> "ᴘʀᴏᴛᴇᴄᴄɪᴏɴ 4")
     * Preservando códigos de color (&c, §c, #HEX) y tags (<gold>, <silver>, <fire>).
     */
    public static String toSmallCaps(String input) {
        if (input == null || input.isEmpty()) return input;

        StringBuilder sb = new StringBuilder();
        boolean insideTag = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Manejo de tags como <rainbow> o </rainbow> para no alterar el tag
            if (c == '<') {
                insideTag = true;
                sb.append(c);
                continue;
            } else if (c == '>') {
                insideTag = false;
                sb.append(c);
                continue;
            }

            // Manejo de códigos &c o §c
            if ((c == '&' || c == '§') && i + 1 < input.length()) {
                sb.append(c).append(input.charAt(i + 1));
                i++;
                continue;
            }

            if (insideTag) {
                sb.append(c);
            } else {
                sb.append(SMALL_CAPS.getOrDefault(c, c));
            }
        }

        return sb.toString();
    }

    /**
     * Decodifica una cadena con Small Caps de vuelta a texto estándar en MAYÚSCULAS
     * (ej: "ᴘʀᴏᴛᴇᴄᴄɪᴏɴ 4" -> "PROTECCION 4", "ᴇxᴛʀᴀ ᴄᴏʀᴀᴢᴏɴᴇs +2" -> "EXTRA CORAZONES +2").
     * Preserva códigos de formato (&c, §c) y tags (<gold>, <silver>).
     */
    public static String fromSmallCaps(String input) {
        if (input == null || input.isEmpty()) return input;

        StringBuilder sb = new StringBuilder();
        boolean insideTag = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '<') {
                insideTag = true;
                sb.append(c);
                continue;
            } else if (c == '>') {
                insideTag = false;
                sb.append(c);
                continue;
            }

            if ((c == '&' || c == '§') && i + 1 < input.length()) {
                sb.append(c).append(input.charAt(i + 1));
                i++;
                continue;
            }

            if (insideTag) {
                sb.append(c);
            } else if (FROM_SMALL_CAPS.containsKey(c)) {
                sb.append(FROM_SMALL_CAPS.get(c));
            } else {
                sb.append(Character.toUpperCase(c));
            }
        }

        return sb.toString();
    }

    /**
     * Verifica si una cadena contiene códigos de formato &, §, códigos HEX o tags animados.
     */
    public static boolean hasFormattingOrTags(String input) {
        if (input == null || input.isEmpty()) return false;
        if (input.contains("&") || input.contains("§") || input.contains("&#") || input.contains("<#") || input.toLowerCase().contains("<gradient:")) return true;
        for (var preset : AnimatedTagRegistry.PRESETS) {
            if (input.toLowerCase().contains("<" + preset.tag().toLowerCase() + ">")) return true;
        }
        if (input.toLowerCase().contains("<animated>") || input.toLowerCase().contains("<golden>")) return true;
        return ServerEmojiRegistry.hasAnyShortcode(input);
    }

    /**
     * Verifica estrictamente si una cadena contiene tags animados, códigos HEX, shortcodes de emojis
     * o sintaxis de formato de CMI (&).
     * IMPORTANTE: No se activa para códigos de formato vanilla (§7, §2, etc.) por sí solos, garantizando
     * que los atributos naturales de Minecraft y líneas nativas de tooltips no sean interceptadas ni alteradas.
     */
    public static boolean hasCustomTagsOrShortcodes(String input) {
        if (input == null || input.isEmpty()) return false;
        if (input.contains("&") || input.contains("&#") || input.contains("<#") || input.contains("§#") || input.toLowerCase().contains("<gradient:")) return true;
        if (input.contains("<") && input.contains(">")) {
            for (var preset : AnimatedTagRegistry.PRESETS) {
                if (input.toLowerCase().contains("<" + preset.tag().toLowerCase() + ">")) return true;
            }
            if (input.toLowerCase().contains("<animated>") || input.toLowerCase().contains("<golden>")
                    || input.toLowerCase().contains("<gold>") || input.toLowerCase().contains("<silver>")
                    || input.toLowerCase().contains("<frost>")) return true;
        }
        return ServerEmojiRegistry.hasAnyShortcode(input);
    }

    public static AnimatedTagRegistry.AnimatedPreset getPresetByTag(String tag) {
        if (tag == null) return null;
        if (tag.equalsIgnoreCase("golden")) {
            return AnimatedTagRegistry.PRESETS.get(4);
        }
        for (var p : AnimatedTagRegistry.PRESETS) {
            if (p.tag().equalsIgnoreCase(tag)) {
                return p;
            }
        }
        return null;
    }

    public static AnimatedTagRegistry.AnimatedPreset getPresetByColor(int color) {
        int rgb = color & 0xFFFFFF;
        for (var p : AnimatedTagRegistry.PRESETS) {
            if ((p.markerColor() & 0xFFFFFF) == rgb) {
                return p;
            }
        }
        return null;
    }

    public static String getCodeFromColor(int color) {
        return switch (color & 0xFFFFFF) {
            case 0x000000 -> "0";
            case 0x0000AA -> "1";
            case 0x00AA00 -> "2";
            case 0x00AAAA -> "3";
            case 0xAA0000 -> "4";
            case 0xAA00AA -> "5";
            case 0xFFAA00 -> "6";
            case 0xAAAAAA -> "7";
            case 0x555555 -> "8";
            case 0x5555FF -> "9";
            case 0x55FF55 -> "a";
            case 0x55FFFF -> "b";
            case 0xFF5555 -> "c";
            case 0xFF55FF -> "d";
            case 0xFFFF55 -> "e";
            case 0xFFFFFF -> "f";
            default -> null;
        };
    }

    /**
     * Reconstruye una representación en texto con códigos de formato (&) y tags (<animated>)
     * a partir de un Component nativo de Minecraft.
     */
    public static String componentToFormattedString(Component component) {
        if (component == null) return "";
        StringBuilder sb = new StringBuilder();
        appendComponentRecursive(component, sb);
        return sb.toString();
    }

    private static void appendComponentRecursive(Component component, StringBuilder sb) {
        if (component == null) return;
        Style style = component.getStyle();
        boolean hasStyle = style != null && !style.isEmpty();
        if (hasStyle) {
            TextColor textColor = style.getColor();
            if (textColor != null) {
                int colorVal = textColor.getValue();
                AnimatedTagRegistry.AnimatedPreset anim = getPresetByColor(colorVal);
                if (anim != null) {
                    sb.append("<").append(anim.tag()).append(">");
                } else {
                    String vanillaCode = getCodeFromColor(colorVal);
                    if (vanillaCode != null) {
                        sb.append("&").append(vanillaCode);
                    } else {
                        sb.append(String.format("&#%06X", colorVal));
                    }
                }
            }
            if (style.isBold()) sb.append("&l");
            if (style.isItalic()) sb.append("&o");
            if (style.isUnderlined()) sb.append("&n");
            if (style.isStrikethrough()) sb.append("&m");
            if (style.isObfuscated()) sb.append("&k");
        }

        String text = "";
        if (component.getContents() instanceof net.minecraft.network.chat.contents.PlainTextContents plain) {
            text = plain.text();
        } else if (component.getSiblings().isEmpty()) {
            text = component.getString();
        }
        if (!text.isEmpty()) {
            sb.append(text.replace('§', '&'));
        }

        for (Component sibling : component.getSiblings()) {
            appendComponentRecursive(sibling, sb);
        }
    }

    /**
     * Parsea cualquier texto enriquecido (con tags animados como <void>, <rainbow>, códigos &l, &6, &#HEX)
     * y genera un MutableComponent de Minecraft con el estilo visual exacto, color del shader y tipografía.
     */
    public static Component parseFormattedComponent(String rawInput) {
        if (rawInput == null || rawInput.isEmpty()) {
            return Component.empty();
        }

        // 1. Reemplazar emojis / shortcodes (:corona:, :fuego:, etc.)
        String text = ServerEmojiRegistry.replaceShortcodes(rawInput);
        text = GradientHelper.convertMiniMessageGradientsToHex(text);
        text = GradientHelper.convertCmiGradientsToHex(text);

        // 2. Iteración y parsing de estilos en línea (soporta múltiples tags y códigos & combinados)
        MutableComponent root = Component.empty();
        StringBuilder buffer = new StringBuilder();

        int curColor = 0xFFFFFF;
        boolean curBold = false;
        boolean curItalic = false;
        boolean curUnderlined = false;
        boolean curStrikethrough = false;
        boolean curObfuscated = false;

        int i = 0;
        int len = text.length();

        while (i < len) {
            char c = text.charAt(i);

            // Soporte <#RRGGBB>
            if (c == '<' && i + 8 < len && text.charAt(i + 1) == '#' && text.charAt(i + 8) == '>') {
                String hex = text.substring(i + 2, i + 8);
                try {
                    int parsed = Integer.parseInt(hex, 16);
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curColor = parsed;
                    i += 9;
                    continue;
                } catch (NumberFormatException ignored) {}
            }

            // Soporte Tags Animados: <animated>, <rainbow>, <fire>, etc. y cierres </animated>, </fire>, </>
            if (c == '<') {
                int closeIdx = text.indexOf('>', i);
                if (closeIdx != -1 && closeIdx - i <= 24) {
                    String tagContent = text.substring(i + 1, closeIdx).trim();
                    if (tagContent.startsWith("/")) {
                        String closeTag = tagContent.substring(1).trim().toLowerCase();
                        if (closeTag.isEmpty() || closeTag.equals("animated") || getPresetByTag(closeTag) != null) {
                            if (buffer.length() > 0) {
                                root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                                buffer.setLength(0);
                            }
                            curColor = 0xFFFFFF;
                            i = closeIdx + 1;
                            continue;
                        }
                    } else if (tagContent.equalsIgnoreCase("animated")) {
                        if (buffer.length() > 0) {
                            root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                            buffer.setLength(0);
                        }
                        curColor = AnimatedTagRegistry.PRESETS.get(0).markerColor(); // #4E5C24 (Rainbow)
                        i = closeIdx + 1;
                        if (i < len && text.charAt(i) == ' ' && root.getSiblings().isEmpty() && buffer.length() == 0) {
                            i++;
                        }
                        continue;
                    } else {
                        var preset = getPresetByTag(tagContent);
                        if (preset != null) {
                            if (buffer.length() > 0) {
                                root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                                buffer.setLength(0);
                            }
                            curColor = preset.markerColor();
                            i = closeIdx + 1;
                            if (i < len && text.charAt(i) == ' ' && root.getSiblings().isEmpty() && buffer.length() == 0) {
                                i++;
                            }
                            continue;
                        }
                    }
                }
            }

            // Soporte &#RRGGBB o §#RRGGBB
            if ((c == '&' || c == '§') && i + 7 < len && text.charAt(i + 1) == '#') {
                String hex = text.substring(i + 2, i + 8);
                try {
                    int parsed = Integer.parseInt(hex, 16);
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curColor = parsed;
                    i += 8;
                    continue;
                } catch (NumberFormatException ignored) {}
            }

            // Códigos estándar &x o §x
            if ((c == '&' || c == '§') && i + 1 < len) {
                char code = Character.toLowerCase(text.charAt(i + 1));
                Integer colorVal = getColorFromCode(code);
                if (colorVal != null) {
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curColor = colorVal;
                    curBold = false;
                    curItalic = false;
                    curUnderlined = false;
                    curStrikethrough = false;
                    curObfuscated = false;
                    i += 2;
                    continue;
                } else if (code == 'l') {
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curBold = true;
                    i += 2;
                    continue;
                } else if (code == 'o') {
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curItalic = true;
                    i += 2;
                    continue;
                } else if (code == 'n') {
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curUnderlined = true;
                    i += 2;
                    continue;
                } else if (code == 'm') {
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curStrikethrough = true;
                    i += 2;
                    continue;
                } else if (code == 'k') {
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curObfuscated = true;
                    i += 2;
                    continue;
                } else if (code == 'r') {
                    if (buffer.length() > 0) {
                        root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
                        buffer.setLength(0);
                    }
                    curColor = 0xFFFFFF;
                    curBold = false;
                    curItalic = false;
                    curUnderlined = false;
                    curStrikethrough = false;
                    curObfuscated = false;
                    i += 2;
                    continue;
                }
            }

            buffer.append(c);
            i++;
        }

        if (buffer.length() > 0) {
            root.append(createStyledSegment(buffer.toString(), curColor, curBold, curItalic, curUnderlined, curStrikethrough, curObfuscated));
        }

        return root;
    }

    private static Component createStyledSegment(String text, int color, boolean bold, boolean italic, boolean underline, boolean strikethrough, boolean obfuscated) {
        Style style = Style.EMPTY
                .withColor(color)
                .withBold(bold)
                .withItalic(italic)
                .withUnderlined(underline)
                .withStrikethrough(strikethrough)
                .withObfuscated(obfuscated);
        return Component.literal(text).withStyle(style);
    }

    private static Integer getColorFromCode(char code) {
        return switch (code) {
            case '0' -> 0x000000;
            case '1' -> 0x0000AA;
            case '2' -> 0x00AA00;
            case '3' -> 0x00AAAA;
            case '4' -> 0xAA0000;
            case '5' -> 0xAA00AA;
            case '6' -> 0xFFAA00;
            case '7' -> 0xAAAAAA;
            case '8' -> 0x555555;
            case '9' -> 0x5555FF;
            case 'a' -> 0x55FF55;
            case 'b' -> 0x55FFFF;
            case 'c' -> 0xFF5555;
            case 'd' -> 0xFF55FF;
            case 'e' -> 0xFFFF55;
            case 'f' -> 0xFFFFFF;
            default -> null;
        };
    }
}
