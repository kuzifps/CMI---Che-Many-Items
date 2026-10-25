package com.fxfps.cmi.client.fonts;

import java.util.ArrayList;
import java.util.List;

/**
 * Generador y procesador de degradados de color (2 y 3 colores Hex)
 * Soporta interpolación RGB precisa, previsualización por carácter, formato &#RRGGBB y <gradient>.
 */
public class GradientHelper {

    public record GradientPreset(String name, int color1, int color2, int color3) {}

    public static final List<GradientPreset> PRESETS = List.of(
            new GradientPreset("🇦🇷 Argentina", 0x75AADB, 0xFFFFFF, 0x75AADB),
            new GradientPreset("🔥 Fuego", 0xFF2200, 0xFF7700, 0xFFDD00),
            new GradientPreset("💜 Cyberpunk", 0xFF007F, 0x8A2BE2, 0x00FFFF),
            new GradientPreset("👑 Oro Real", 0xFFAA00, 0xFFEE00, 0xFFFFAA),
            new GradientPreset("🌿 Esmeralda", 0x00FF88, 0x00AA55, 0x114422),
            new GradientPreset("⚡ Tormenta", 0x4A00E0, 0x8E2DE2, 0x00D2FF),
            new GradientPreset("🩸 Sangre", 0x800000, 0xD00000, 0xFF3333)
    );

    /**
     * Interpola suavemente entre dos colores enteros RGB.
     */
    public static int lerpColor(int c1, int c2, float factor) {
        factor = Math.clamp(factor, 0.0f, 1.0f);

        int r1 = (c1 >> 16) & 0xFF;
        int g1 = (c1 >> 8) & 0xFF;
        int b1 = c1 & 0xFF;

        int r2 = (c2 >> 16) & 0xFF;
        int g2 = (c2 >> 8) & 0xFF;
        int b2 = c2 & 0xFF;

        int r = (int) (r1 + factor * (r2 - r1));
        int g = (int) (g1 + factor * (g2 - g1));
        int b = (int) (b1 + factor * (b2 - b1));

        return (r << 16) | (g << 8) | b;
    }

    /**
     * Calcula el color de cada carácter para un degradado de 2 o 3 colores.
     */
    public static int[] getInterpolatedColors(String text, int c1, int c2, Integer c3) {
        int len = text.length();
        if (len == 0) return new int[0];
        if (len == 1) return new int[]{c1};

        int[] colors = new int[len];

        if (c3 == null) {
            // Degradado de 2 colores
            for (int i = 0; i < len; i++) {
                float factor = (float) i / (len - 1);
                colors[i] = lerpColor(c1, c2, factor);
            }
        } else {
            // Degradado de 3 colores (C1 -> C2 -> C3)
            for (int i = 0; i < len; i++) {
                float t = (float) i / (len - 1);
                if (t <= 0.5f) {
                    float subFactor = t * 2.0f;
                    colors[i] = lerpColor(c1, c2, subFactor);
                } else {
                    float subFactor = (t - 0.5f) * 2.0f;
                    colors[i] = lerpColor(c2, c3, subFactor);
                }
            }
        }

        return colors;
    }

    /**
     * Genera texto con formato de color Hex por carácter (&#RRGGBBc).
     * Compatible con Spigot, Paper, Essentials y BungeeCord.
     * Si bold es true, añade &l a cada carácter para evitar que el color resetee la negrita.
     */
    public static String applyHexGradient(String text, int c1, int c2, Integer c3, boolean bold) {
        if (text == null || text.isEmpty()) return "";

        int[] colors = getInterpolatedColors(text, c1, c2, c3);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            int color = colors[i];
            if (bold) {
                sb.append(String.format("&#%06X&l%c", (color & 0xFFFFFF), ch));
            } else {
                sb.append(String.format("&#%06X%c", (color & 0xFFFFFF), ch));
            }
        }

        return sb.toString();
    }

    public static String applyHexGradient(String text, int c1, int c2, Integer c3) {
        return applyHexGradient(text, c1, c2, c3, false);
    }

    /**
     * Genera la sintaxis nativa de degradados del plugin CMI:
     * 2 colores: {#HEX1>}Texto{#HEX2<}
     * 3 colores: {#HEX1>}Texto1{#HEX2>}{#HEX2>}Texto2{#HEX3<}
     */
    public static String applyCmiGradient(String text, int c1, int c2, Integer c3, boolean bold) {
        if (text == null || text.isEmpty()) return "";
        String h1 = String.format("#%06X", (c1 & 0xFFFFFF));
        String h2 = String.format("#%06X", (c2 & 0xFFFFFF));
        String b = bold ? "&l" : "";

        if (c3 == null) {
            return "{" + h1 + ">}" + b + text + "{" + h2 + "<}";
        } else {
            String h3 = String.format("#%06X", (c3 & 0xFFFFFF));
            int mid = text.length() / 2;
            if (mid == 0) mid = 1;
            String part1 = text.substring(0, mid);
            String part2 = text.substring(mid);
            return "{" + h1 + ">}" + b + part1 + "{" + h2 + ">}" + b + part2 + "{" + h3 + "<}";
        }
    }

    /**
     * Genera formato Legacy Hex de BungeeCord / Spigot (&x&r&r&g&g&b&b).
     */
    public static String applyLegacyHexGradient(String text, int c1, int c2, Integer c3, boolean bold) {
        if (text == null || text.isEmpty()) return "";
        int[] colors = getInterpolatedColors(text, c1, c2, c3);
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            int color = colors[i] & 0xFFFFFF;
            String hex = String.format("%06X", color);
            sb.append("&x");
            for (char hc : hex.toCharArray()) {
                sb.append('&').append(hc);
            }
            if (bold) sb.append("&l");
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * Genera la etiqueta moderna de Adventure / MiniMessage:
     * <gradient:#HEX1:#HEX2:#HEX3>Texto</gradient>
     */
    public static String applyMiniMessageGradient(String text, int c1, int c2, Integer c3, boolean bold) {
        if (text == null || text.isEmpty()) return "";

        String h1 = String.format("#%06X", (c1 & 0xFFFFFF));
        String h2 = String.format("#%06X", (c2 & 0xFFFFFF));
        String b = bold ? "&l" : "";

        if (c3 != null) {
            String h3 = String.format("#%06X", (c3 & 0xFFFFFF));
            return "<gradient:" + h1 + ":" + h2 + ":" + h3 + ">" + b + text + "</gradient>";
        } else {
            return "<gradient:" + h1 + ":" + h2 + ">" + b + text + "</gradient>";
        }
    }

    public static String applyMiniMessageGradient(String text, int c1, int c2, Integer c3) {
        return applyMiniMessageGradient(text, c1, c2, c3, false);
    }

    /**
     * Convierte cualquier <gradient:#HEX1:#HEX2>Texto</gradient> a &#HEX universal carácter por carácter
     * para máxima compatibilidad al enviar /rename al servidor.
     */
    public static String convertMiniMessageGradientsToHex(String input) {
        if (input == null || !input.toLowerCase().contains("<gradient:")) return input;

        java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                "(?i)<gradient:(#[0-9a-fA-F]{6}):(#[0-9a-fA-F]{6})(?::(#[0-9a-fA-F]{6}))?>(.*?)</gradient>"
        );
        java.util.regex.Matcher m = p.matcher(input);
        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (m.find()) {
            sb.append(input, lastEnd, m.start());
            try {
                int c1 = Integer.parseInt(m.group(1).substring(1), 16);
                int c2 = Integer.parseInt(m.group(2).substring(1), 16);
                Integer c3 = (m.group(3) != null) ? Integer.parseInt(m.group(3).substring(1), 16) : null;
                String inner = m.group(4);
                boolean bold = inner.startsWith("&l") || inner.startsWith("§l");
                if (bold) inner = inner.substring(2);
                sb.append(applyHexGradient(inner, c1, c2, c3, bold));
            } catch (Exception ignored) {
                sb.append(m.group(0));
            }
            lastEnd = m.end();
        }
        sb.append(input.substring(lastEnd));
        return sb.toString();
    }

    /**
     * Convierte cualquier degradado de formato CMI ({#HEX1>}Texto{#HEX2<}) a &#HEX universal.
     */
    public static String convertCmiGradientsToHex(String input) {
        if (input == null || !input.contains("{#")) return input;

        // 3 colores
        java.util.regex.Pattern p3 = java.util.regex.Pattern.compile(
                "(?i)\\{#([0-9a-fA-F]{6})>\\}(.*?)\\{#([0-9a-fA-F]{6})>\\}(.*?)\\{#([0-9a-fA-F]{6})<\\}"
        );
        java.util.regex.Matcher m3 = p3.matcher(input);
        if (m3.find()) {
            try {
                int c1 = Integer.parseInt(m3.group(1), 16);
                int c2 = Integer.parseInt(m3.group(3), 16);
                int c3 = Integer.parseInt(m3.group(5), 16);
                String part1 = m3.group(2);
                String part2 = m3.group(4);
                boolean bold = part1.startsWith("&l") || part1.startsWith("§l");
                if (bold) part1 = part1.substring(2);
                if (part2.startsWith("&l") || part2.startsWith("§l")) part2 = part2.substring(2);
                String full = part1 + part2;
                return input.substring(0, m3.start()) + applyHexGradient(full, c1, c2, c3, bold) + input.substring(m3.end());
            } catch (Exception ignored) {}
        }

        // 2 colores
        java.util.regex.Pattern p2 = java.util.regex.Pattern.compile(
                "(?i)\\{#([0-9a-fA-F]{6})>\\}(.*?)\\{#([0-9a-fA-F]{6})<\\}"
        );
        java.util.regex.Matcher m2 = p2.matcher(input);
        if (m2.find()) {
            try {
                int c1 = Integer.parseInt(m2.group(1), 16);
                int c2 = Integer.parseInt(m2.group(3), 16);
                String inner = m2.group(2);
                boolean bold = inner.startsWith("&l") || inner.startsWith("§l");
                if (bold) inner = inner.substring(2);
                return input.substring(0, m2.start()) + applyHexGradient(inner, c1, c2, null, bold) + input.substring(m2.end());
            } catch (Exception ignored) {}
        }

        return input;
    }

    public static String colorToHex(int color) {
        return String.format("#%06X", (color & 0xFFFFFF));
    }
}
