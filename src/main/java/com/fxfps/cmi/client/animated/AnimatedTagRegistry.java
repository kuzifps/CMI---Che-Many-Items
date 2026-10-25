package com.fxfps.cmi.client.animated;

import java.util.List;

/**
 * Registro oficial de los 21 AnimatedMessages (Argentina Network).
 * Basado exactamente en los shaders core (rendertype_text.vsh / text.vsh) del texture pack:
 * Cada animación corresponde a un color marcador: rgb(78, 92, 36 + id * 10)
 * 
 * id  0: rainbow   #4E5C24     id  7: void      #4E5C6A     id 14: neon      #4E5CB0
 * id  1: celestial #4E5C2E     id  8: silver    #4E5C74     id 15: candy     #4E5CBA
 * id  2: fire      #4E5C38     id  9: frost     #4E5C7E     id 16: galaxy    #4E5CC4
 * id  3: ocean     #4E5C42     id 10: lava      #4E5C88     id 17: glitch    #4E5CCE
 * id  4: gold      #4E5C4C     id 11: matrix    #4E5C92     id 18: blood     #4E5CD8
 * id  5: toxic     #4E5C56     id 12: aurora    #4E5C9C     id 19: emerald   #4E5CE2
 * id  6: sakura    #4E5C60     id 13: sunset    #4E5CA6     id 20: diamond   #4E5CEC
 */
public class AnimatedTagRegistry {

    public record AnimatedPreset(int id, String tag, String markerHex, int markerColor, String displayName) {}

    public static final List<AnimatedPreset> PRESETS = List.of(
            new AnimatedPreset(0, "rainbow", "#4E5C24", 0x4E5C24, "Rainbow"),
            new AnimatedPreset(1, "celestial", "#4E5C2E", 0x4E5C2E, "Celestial"),
            new AnimatedPreset(2, "fire", "#4E5C38", 0x4E5C38, "Fire"),
            new AnimatedPreset(3, "ocean", "#4E5C42", 0x4E5C42, "Ocean"),
            new AnimatedPreset(4, "gold", "#4E5C4C", 0x4E5C4C, "Gold"),
            new AnimatedPreset(5, "toxic", "#4E5C56", 0x4E5C56, "Toxic"),
            new AnimatedPreset(6, "sakura", "#4E5C60", 0x4E5C60, "Sakura"),
            new AnimatedPreset(7, "void", "#4E5C6A", 0x4E5C6A, "Void"),
            new AnimatedPreset(8, "silver", "#4E5C74", 0x4E5C74, "Silver"),
            new AnimatedPreset(9, "frost", "#4E5C7E", 0x4E5C7E, "Frost"),
            new AnimatedPreset(10, "lava", "#4E5C88", 0x4E5C88, "Lava"),
            new AnimatedPreset(11, "matrix", "#4E5C92", 0x4E5C92, "Matrix"),
            new AnimatedPreset(12, "aurora", "#4E5C9C", 0x4E5C9C, "Aurora"),
            new AnimatedPreset(13, "sunset", "#4E5CA6", 0x4E5CA6, "Sunset"),
            new AnimatedPreset(14, "neon", "#4E5CB0", 0x4E5CB0, "Neon"),
            new AnimatedPreset(15, "candy", "#4E5CBA", 0x4E5CBA, "Candy"),
            new AnimatedPreset(16, "galaxy", "#4E5CC4", 0x4E5CC4, "Galaxy"),
            new AnimatedPreset(17, "glitch", "#4E5CCE", 0x4E5CCE, "Glitch"),
            new AnimatedPreset(18, "blood", "#4E5CD8", 0x4E5CD8, "Blood"),
            new AnimatedPreset(19, "emerald", "#4E5CE2", 0x4E5CE2, "Emerald"),
            new AnimatedPreset(20, "diamond", "#4E5CEC", 0x4E5CEC, "Diamond")
    );

    private static final float TAU = 6.28318530718f;

    private static float fract(float x) {
        return x - (float) Math.floor(x);
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    private static float mix(float a, float b, float t) {
        return a * (1.0f - t) + b * t;
    }

    private static float wave(float gt, float speed, float freq, float x) {
        return 0.5f + 0.5f * (float) Math.sin(TAU * gt * speed - x * freq);
    }

    private static float shineBand(float gt, float speed, float dirX, float dirY, float freq, float k, float px, float py) {
        float t = fract(gt * speed - (px * dirX + py * dirY) * freq);
        return (float) Math.pow(1.0f - Math.abs(t * 2.0f - 1.0f), k);
    }

    private static int hsv2rgbInt(float h, float s, float v) {
        float c = v * s;
        float x = c * (1.0f - Math.abs((h * 6.0f) % 2.0f - 1.0f));
        float m = v - c;
        float r = 0, g = 0, b = 0;
        int sector = (int) (h * 6.0f) % 6;
        switch (sector) {
            case 0 -> { r = c; g = x; b = 0; }
            case 1 -> { r = x; g = c; b = 0; }
            case 2 -> { r = 0; g = c; b = x; }
            case 3 -> { r = 0; g = x; b = c; }
            case 4 -> { r = x; g = 0; b = c; }
            case 5 -> { r = c; g = 0; b = x; }
        }
        int ir = (int) clamp((r + m) * 255.0f, 0, 255);
        int ig = (int) clamp((g + m) * 255.0f, 0, 255);
        int ib = (int) clamp((b + m) * 255.0f, 0, 255);
        return 0xFF000000 | (ir << 16) | (ig << 8) | ib;
    }

    private static int packRgb(float r, float g, float b) {
        int ir = (int) clamp(r * 255.0f, 0, 255);
        int ig = (int) clamp(g * 255.0f, 0, 255);
        int ib = (int) clamp(b * 255.0f, 0, 255);
        return 0xFF000000 | (ir << 16) | (ig << 8) | ib;
    }

    /**
     * Calcula el color animado exacto en tiempo real según la fórmula GLSL del texture pack.
     */
    public static int getAnimatedColor(int id, float x, float y) {
        // GameTime cíclico cada 20 minutos (1200000 ms) como en el shader
        float gt = (float) ((System.currentTimeMillis() % 1200000L) / 1200000.0);

        switch (id) {
            case 0 -> { // Rainbow
                return hsv2rgbInt(fract(gt * 300.0f + x * 0.02f), 0.85f, 1.0f);
            }
            case 1 -> { // Celestial
                float w = wave(gt, 300.0f, 0.06f, x);
                float r = mix(0.30f, 0.80f, w);
                float g = mix(0.35f, 0.45f, w);
                float b = 1.0f;
                return packRgb(r, g, b);
            }
            case 2 -> { // Fire
                float w = wave(gt, 600.0f, 0.12f, x);
                float f = wave(gt, 1800.0f, 0.5f, x * 1.3f);
                float r = 1.0f;
                float g = mix(0.18f, 0.55f, w) + f * f * 0.35f;
                float b = f * f * 0.25f;
                return packRgb(r, g, b);
            }
            case 3 -> { // Ocean
                float w = wave(gt, 240.0f, 0.07f, x);
                float r = mix(0.05f, 0.25f, w);
                float g = mix(0.35f, 1.00f, w);
                float b = mix(0.95f, 0.90f, w);
                return packRgb(r, g, b);
            }
            case 4 -> { // Gold
                float w = wave(gt, 300.0f, 0.05f, x);
                float shine = (float) Math.pow(Math.max(0, Math.sin(gt * 300.0f - x * 0.012f)), 4.0);
                float r = 1.0f;
                float g = mix(0.62f, 0.85f, w) + shine * 0.15f;
                float b = mix(0.10f, 0.30f, w) + shine * 0.50f;
                return packRgb(r, g, b);
            }
            case 5 -> { // Toxic
                float w = wave(gt, 400.0f, 0.10f, x);
                float pulse = 0.80f + 0.20f * wave(gt, 1200.0f, 0.0f, x);
                return packRgb(mix(0.10f, 0.80f, w) * pulse, pulse, 0.10f * pulse);
            }
            case 6 -> { // Sakura
                float w = wave(gt, 240.0f, 0.05f, x);
                return packRgb(1.0f, mix(0.55f, 0.90f, w), mix(0.75f, 0.95f, w));
            }
            case 7 -> { // Void
                float w = wave(gt, 400.0f, 0.08f, x);
                float pulse = 0.70f + 0.30f * wave(gt, 1200.0f, 0.2f, x);
                return packRgb(mix(0.45f, 0.95f, w) * pulse, mix(0.05f, 0.20f, w) * pulse, mix(0.90f, 0.75f, w) * pulse);
            }
            case 8 -> { // Silver
                float w = wave(gt, 300.0f, 0.05f, x);
                float val = mix(0.55f, 0.92f, w);
                return packRgb(val, val, val);
            }
            case 9 -> { // Frost
                float w = wave(gt, 240.0f, 0.07f, x + y * 0.5f);
                return packRgb(mix(0.55f, 0.92f, w), mix(0.80f, 0.98f, w), 1.0f);
            }
            case 10 -> { // Lava
                float w = wave(gt, 300.0f, 0.35f, y + x * 0.08f);
                return packRgb(mix(0.55f, 1.00f, w), mix(0.02f, 0.35f, w), 0.0f);
            }
            case 11 -> { // Matrix
                float rain = wave(gt, 600.0f, 0.5f, y + x * 0.1f);
                return packRgb(rain * 0.15f, mix(0.35f, 1.00f, rain * 0.8f), mix(0.05f, 0.25f, rain));
            }
            case 12 -> { // Aurora
                float h = 0.33f + 0.30f * wave(gt, 240.0f, 0.04f, x + y * 0.6f);
                float v = 0.75f + 0.25f * wave(gt, 480.0f, 0.09f, x * 0.7f - y);
                return hsv2rgbInt(h, 0.65f, v);
            }
            case 13 -> { // Sunset
                float t = Math.abs(fract(gt * 300.0f - (y * 0.06f + x * 0.004f)) * 2.0f - 1.0f);
                return packRgb(mix(1.00f, 0.55f, t), mix(0.50f, 0.25f, t), mix(0.10f, 0.95f, t));
            }
            case 14 -> { // Neon
                float w = wave(gt, 300.0f, 0.07f, x);
                return packRgb(mix(1.00f, 0.10f, w), mix(0.10f, 0.95f, w), 1.0f);
            }
            case 15 -> { // Candy
                float stripe = fract((x + y) * 0.04f - gt * 600.0f);
                float t = (stripe > 0.5f) ? 1.0f : 0.0f;
                return packRgb(mix(1.00f, 0.60f, t), mix(0.45f, 0.95f, t), mix(0.70f, 1.00f, t));
            }
            case 16 -> { // Galaxy
                float w = wave(gt, 240.0f, 0.05f, x + y * 0.4f);
                return packRgb(mix(0.12f, 0.65f, w), mix(0.05f, 0.15f, w), mix(0.45f, 0.85f, w));
            }
            case 17 -> { // Glitch
                float glitch = (float) Math.sin(gt * 2400.0f + x * 0.2f);
                if (glitch > 0.8f) return 0xFFFF00FF; // Magenta
                if (glitch < -0.8f) return 0xFF00FFFF; // Cian
                return 0xFFFFFFFF; // Blanco base
            }
            case 18 -> { // Blood
                float w = wave(gt, 300.0f, 0.05f, x);
                return packRgb(mix(0.45f, 0.90f, w), 0.02f, 0.05f);
            }
            case 19 -> { // Emerald
                float w = wave(gt, 300.0f, 0.06f, x);
                return packRgb(mix(0.00f, 0.20f, w), mix(0.55f, 0.95f, w), mix(0.25f, 0.50f, w));
            }
            case 20 -> { // Diamond
                float w = wave(gt, 400.0f, 0.08f, x);
                return packRgb(mix(0.50f, 0.85f, w), mix(0.90f, 1.00f, w), 1.0f);
            }
            default -> {
                return 0xFFFFFFFF;
            }
        }
    }
}
