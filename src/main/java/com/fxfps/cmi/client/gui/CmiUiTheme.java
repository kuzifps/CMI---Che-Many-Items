package com.fxfps.cmi.client.gui;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.Random;

/**
 * SISTEMA MAESTRO DE DISEÑO VISUAL NEO-CAPSULE LIQUID GLASS 💎
 * Centraliza la paleta cromática unificada, el motor de curvatura real (Border-Radius 6-10px
 * y cápsulas píldora rounded-full), ultra-transparencia en tiempo real y micro-badges.
 */
public class CmiUiTheme {

    // =========================================================================
    // 1. PALETA CROMÁTICA UNIFICADA (OFICIAL CMI NEO-CAPSULE)
    // =========================================================================
    public static final int COLOR_CYAN_NEON       = 0xFF38BDF8; // Cyan Neón Base (#38bdf8)
    public static final int COLOR_CYAN_ACTIVE     = 0xFF06B6D4; // Pestaña Activa / Glow (#06b6d4)
    public static final int COLOR_AMBER           = 0xFFF59E0B; // Ámbar Cálido (#f59e0b)
    public static final int COLOR_AMBER_DARK      = 0xFFD97706; // Ámbar Profundo (#d97706)
    public static final int COLOR_EMERALD         = 0xFF10B981; // Verde Esmeralda Heroico (#10b981)
    public static final int COLOR_EMERALD_DARK    = 0xFF059669; // Esmeralda Sombra (#059669)
    public static final int COLOR_PURPLE          = 0xFFA855F7; // Púrpura Armaduras (#a855f7)
    public static final int COLOR_BLUE            = 0xFF3B82F6; // Azul Arcos & Rango (#3b82f6)
    public static final int COLOR_ROSE            = 0xFFF43F5E; // Rosa/Rojo Pociones (#f43f5e)
    public static final int COLOR_DANGER          = 0xFFE11D48; // Carmesí Peligro / Limpiar (#e11d48)
    public static final int COLOR_MUTED           = 0xFF94A3B8; // Texto Secundario Muted (#94a3b8)
    public static final int COLOR_WHITE           = 0xFFFFFFFF; // Blanco Puro
    public static final int COLOR_DARK_TEXT       = 0xFF1E293B; // Texto Oscuro en Badges

    // Alias de compatibilidad histórica
    public static final int COLOR_CMI_CYAN        = COLOR_CYAN_NEON;
    public static final int COLOR_CMI_BLUE        = 0xFF0284C7;
    public static final int COLOR_CMI_GLOW        = COLOR_CYAN_NEON;
    public static final int COLOR_CMI_GOLD        = COLOR_AMBER;
    public static final int COLOR_CMI_GREEN       = COLOR_EMERALD;
    public static final int COLOR_CMI_RED         = COLOR_DANGER;
    public static final int COLOR_CELESTE         = COLOR_CYAN_NEON;
    public static final int COLOR_GOLD            = COLOR_AMBER;
    public static final int COLOR_GREEN           = COLOR_EMERALD;
    public static final int COLOR_RED             = COLOR_DANGER;

    // Ultra-Transparencia Liquid Glass (10-15% Difuminado para ver Minecraft a 60+ FPS)
    public static final int BG_DIFFUSE_TOP        = 0x2A0A1424; // ~16% alpha
    public static final int BG_DIFFUSE_BOTTOM     = 0x3A0A162A; // ~23% alpha
    public static final int GLASS_BG_MAIN         = 0x470A1424; // rgba(10, 20, 36, 0.28)
    public static final int GLASS_BORDER_CYAN     = 0x6038BDF8; // rgba(56, 189, 248, 0.38)
    public static final int GLASS_BG_LIQUID       = 0x730A1424; // rgba(10, 20, 36, 0.45) Oficial Neo-Capsule Liquid Glass
    public static final int GLASS_BORDER_LIQUID   = 0x5938BDF8; // rgba(56, 189, 248, 0.35) Borde Cyan Translúcido
    public static final int GLASS_PANEL_BG        = GLASS_BG_LIQUID;
    public static final int GLASS_PANEL_BORDER    = GLASS_BORDER_LIQUID;
    public static final int PANEL_BG              = GLASS_PANEL_BG;
    public static final int PANEL_HEADER_BG       = 0x55101C32;
    public static final int PANEL_BORDER_BASE     = GLASS_PANEL_BORDER;
    public static final int PANEL_BORDER_ACCENT   = COLOR_CYAN_NEON;
    public static final int PANEL_BORDER_CYAN     = COLOR_CYAN_NEON;
    public static final int PANEL_BORDER_GOLD     = COLOR_AMBER;
    public static final int COLOR_DARK_SLOT       = 0x2810182A;

    // Gradientes de Botones
    public static final int COLOR_HERO_EMERALD_START = COLOR_EMERALD;
    public static final int COLOR_HERO_EMERALD_END   = COLOR_EMERALD_DARK;
    public static final int COLOR_ACTION_AMBER_START = COLOR_AMBER;
    public static final int COLOR_ACTION_AMBER_END   = COLOR_AMBER_DARK;
    public static final int COLOR_DANGER_RED         = COLOR_DANGER;

    // =========================================================================
    // 2. BADGES EXCLUSIVOS DE HERRAMIENTAS & ÍTEMS (.ref-tag & .tag-dot)
    // Cero textos de rangos de servidor (eliminados OWNER, VIP, LISTO, 36 ITEMS, etc.)
    // =========================================================================
    public static final int COLOR_BADGE_ESPADAS       = COLOR_CYAN_NEON; // Cyan (#38bdf8)
    public static final int COLOR_BADGE_HERRAMIENTAS  = COLOR_AMBER;     // Ámbar (#f59e0b)
    public static final int COLOR_BADGE_ARMADURAS     = COLOR_PURPLE;    // Púrpura (#a855f7)
    public static final int COLOR_BADGE_ARCOS         = COLOR_BLUE;      // Azul (#3b82f6)
    public static final int COLOR_BADGE_POCIONES      = COLOR_ROSE;      // Rosa (#f43f5e)
    public static final int COLOR_BADGE_BLOQUES       = COLOR_EMERALD;   // Verde (#10b981)

    public enum ToolCategory {
        ESPADAS("Espadas", "<Espadas>", COLOR_BADGE_ESPADAS, "netherite_sword"),
        HERRAMIENTAS("Herramientas", "<Herramientas>", COLOR_BADGE_HERRAMIENTAS, "netherite_pickaxe"),
        ARMADURAS("Armaduras", "<Armaduras>", COLOR_BADGE_ARMADURAS, "netherite_chestplate"),
        ARCOS("Arcos", "<Arcos>", COLOR_BADGE_ARCOS, "bow"),
        POCIONES("Pociones", "<Pociones>", COLOR_BADGE_POCIONES, "splash_potion"),
        BLOQUES("Bloques", "<Bloques>", COLOR_BADGE_BLOQUES, "grass_block");

        private final String displayName;
        private final String tag;
        private final int dotColor;
        private final String defaultIconId;

        ToolCategory(String displayName, String tag, int dotColor, String defaultIconId) {
            this.displayName = displayName;
            this.tag = tag;
            this.dotColor = dotColor;
            this.defaultIconId = defaultIconId;
        }

        public String getDisplayName() { return displayName; }
        public String getTag() { return tag; }
        public int getDotColor() { return dotColor; }
        public String getDefaultIconId() { return defaultIconId; }

        public static ToolCategory fromString(String str) {
            if (str == null) return HERRAMIENTAS;
            String clean = str.replace("<", "").replace(">", "").trim().toLowerCase();
            for (ToolCategory cat : values()) {
                if (cat.displayName.toLowerCase().equals(clean) || cat.name().toLowerCase().equals(clean)) {
                    return cat;
                }
            }
            return HERRAMIENTAS;
        }
    }

    // =========================================================================
    // 3. GLIFOS Y TEXTURAS OFICIALES CMI CHROME 3D
    // =========================================================================
    public static final String FLAG_AR            = "\uE100";
    public static final String LOGO_CMI           = "\uE301";
    public static final String ICON_MATE          = "\uE302";
    public static final String LOGO_FULL          = "\uE300";

    public static final String ICON_ARROW_DOWN    = "\uE400";
    public static final String ICON_ARROW_LEFT    = "\uE401";
    public static final String ICON_ARROW_RIGHT   = "\uE402";
    public static final String ICON_ARROW_UP      = "\uE403";
    public static final String ICON_CHECK         = "\uE404";
    public static final String ICON_CHECK_COLOR   = "\uE405";
    public static final String ICON_CLOSE         = "\uE406";
    public static final String ICON_CLOSE_COLOR   = "\uE407";
    public static final String ICON_COINS         = "\uE408";
    public static final String ICON_ALERT         = "\uE409";
    public static final String ICON_ALERT_COLOR   = "\uE40A";
    public static final String ICON_LOCK_CLOSED   = "\uE40B";
    public static final String ICON_LOCK_OPEN     = "\uE40C";
    public static final String ICON_MINUS         = "\uE40D";
    public static final String ICON_MINUS_COLOR   = "\uE40E";
    public static final String ICON_PLUS          = "\uE40F";
    public static final String ICON_PLUS_COLOR    = "\uE410";
    public static final String ICON_QUESTION      = "\uE411";
    public static final String ICON_QUESTION_COLOR= "\uE412";
    public static final String ICON_REFRESH       = "\uE413";
    public static final String ICON_SEARCH        = "\uE414";
    public static final String ICON_SETTINGS      = "\uE415";

    public static final String EFF_ABSORPTION     = "\uE416";
    public static final String EFF_FIRE_RES       = "\uE417";
    public static final String EFF_HASTE          = "\uE418";
    public static final String EFF_HEALTH_BOOST   = "\uE419";
    public static final String EFF_JUMP_BOOST     = "\uE41A";
    public static final String EFF_NIGHT_VISION   = "\uE41B";
    public static final String EFF_REGENERATION   = "\uE41C";
    public static final String EFF_RESISTANCE     = "\uE41D";
    public static final String EFF_SATURATION     = "\uE41E";
    public static final String EFF_SPEED          = "\uE41F";
    public static final String EFF_STRENGTH       = "\uE420";
    public static final String EFF_WATER_BREATH   = "\uE421";

    public static String get3dLetter(char c) {
        char upper = Character.toUpperCase(c);
        if (upper >= 'A' && upper <= 'Z') {
            return String.valueOf((char) (0xE500 + (upper - 'A')));
        }
        return String.valueOf(c);
    }

    public static final Identifier CMI_LOGO_ID     = Identifier.fromNamespaceAndPath("cmi", "textures/gui/cmi_logo.png");
    public static final Identifier CMI_LOGO_HERO   = Identifier.fromNamespaceAndPath("cmi", "textures/gui/cmi_logo_general_hero.png");
    public static final Identifier CMI_LOGO_ICON   = Identifier.fromNamespaceAndPath("cmi", "textures/gui/cmi_logo_general_icon.png");
    public static final Identifier CMI_LOGO_CHROME = Identifier.fromNamespaceAndPath("cmi", "textures/gui/cmi_logo_general_chrome.png");

    private static final int PARTICLE_COUNT = 24;
    private static final float[] PARTICLE_SEED_X = new float[PARTICLE_COUNT];
    private static final float[] PARTICLE_SPEED  = new float[PARTICLE_COUNT];
    private static final float[] PARTICLE_SIZE   = new float[PARTICLE_COUNT];
    private static final int[]   PARTICLE_COLOR  = new int[PARTICLE_COUNT];

    static {
        Random rand = new Random(18100525L);
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            PARTICLE_SEED_X[i] = rand.nextFloat();
            PARTICLE_SPEED[i]  = 0.12f + rand.nextFloat() * 0.28f;
            PARTICLE_SIZE[i]   = (rand.nextFloat() > 0.75f) ? 2 : 1;
            PARTICLE_COLOR[i]  = (i % 3 == 0) ? 0x38BDF8 : ((i % 3 == 1) ? 0xFFFFFF : 0xF59E0B);
        }
    }

    // =========================================================================
    // 4. MOTOR DE CURVATURA REAL (BORDER-RADIUS 6-10px & CÁPSULAS PÍLDORA)
    // =========================================================================

    /**
     * Calcula la sangría (inset) horizontal exacta para una fila vertical $dy$
     * dentro de un cuadrante circular de radio $radius$.
     */
    public static int getArcInset(int dy, int radius) {
        if (dy < 0 || dy >= radius) return 0;
        double distFromCenter = (radius - 1) - dy;
        double dxExact = radius - Math.sqrt(Math.max(0, radius * radius - distFromCenter * distFromCenter));
        return (int) Math.round(dxExact);
    }

    /**
     * Rellena un rectángulo con esquinas redondeadas reales de radio especificado.
     */
    public static void fillRoundedRect(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int radius, int color) {
        if (w <= 0 || h <= 0) return;
        radius = Math.max(1, Math.min(radius, Math.min(w, h) / 2));

        // 1. Cuerpo central completo (entre las esquinas curvas superior e inferior)
        guiGraphics.fill(x, y + radius, x + w, y + h - radius, color);

        // 2. Líneas horizontales de los cuadrantes curvos superior e inferior
        for (int dy = 0; dy < radius; dy++) {
            int inset = getArcInset(dy, radius);
            // Cuadrante superior
            guiGraphics.fill(x + inset, y + dy, x + w - inset, y + dy + 1, color);
            // Cuadrante inferior
            guiGraphics.fill(x + inset, y + h - 1 - dy, x + w - inset, y + h - dy, color);
        }
    }

    /**
     * Rellena un rectángulo con esquinas redondeadas y gradiente vertical suave.
     */
    public static void fillGradientRoundedRect(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int radius, int colTop, int colBottom) {
        if (w <= 0 || h <= 0) return;
        radius = Math.max(1, Math.min(radius, Math.min(w, h) / 2));

        for (int i = 0; i < h; i++) {
            float f = (h > 1) ? (float) i / (float) (h - 1) : 0f;
            int col = lerpColor(colTop, colBottom, f);

            int inset = 0;
            if (i < radius) {
                inset = getArcInset(i, radius);
            } else if (i >= h - radius) {
                inset = getArcInset(h - 1 - i, radius);
            }

            guiGraphics.fill(x + inset, y + i, x + w - inset, y + i + 1, col);
        }
    }

    /**
     * Dibuja el borde exterior con curvatura real continua (Border-Radius).
     */
    public static void drawRoundedBorder(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int radius, int color) {
        if (w <= 0 || h <= 0) return;
        radius = Math.max(1, Math.min(radius, Math.min(w, h) / 2));

        // Líneas rectas
        guiGraphics.fill(x + radius, y, x + w - radius, y + 1, color);             // Superior
        guiGraphics.fill(x + radius, y + h - 1, x + w - radius, y + h, color);     // Inferior
        guiGraphics.fill(x, y + radius, x + 1, y + h - radius, color);             // Izquierda
        guiGraphics.fill(x + w - 1, y + radius, x + w, y + h - radius, color);     // Derecha

        // Arcos de las 4 esquinas
        for (int dy = 0; dy < radius; dy++) {
            int inset = getArcInset(dy, radius);
            int nextInset = (dy + 1 < radius) ? getArcInset(dy + 1, radius) : 0;
            int span = Math.max(1, inset - nextInset);

            // Superior-Izquierda
            guiGraphics.fill(x + inset - span + 1, y + dy, x + inset + 1, y + dy + 1, color);
            // Superior-Derecha
            guiGraphics.fill(x + w - 1 - inset, y + dy, x + w - inset + span - 1, y + dy + 1, color);
            // Inferior-Izquierda
            guiGraphics.fill(x + inset - span + 1, y + h - 1 - dy, x + inset + 1, y + h - dy, color);
            // Inferior-Derecha
            guiGraphics.fill(x + w - 1 - inset, y + h - 1 - dy, x + w - inset + span - 1, y + h - dy, color);
        }
    }

    /**
     * Compatibilidad: Redirige drawCapsuleBorder para generar curvatura real (Radius 8px).
     */
    public static void drawCapsuleBorder(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int color) {
        int radius = Math.min(8, Math.min(w, h) / 2);
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, color);
    }

    /**
     * Dibuja un rectángulo con bordes 90° rectos de 1px.
     */
    public static void drawRectBorder(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int color) {
        guiGraphics.fill(x, y, x + w, y + 1, color);
        guiGraphics.fill(x, y + h - 1, x + w, y + h, color);
        guiGraphics.fill(x, y + 1, x + 1, y + h, color);
        guiGraphics.fill(x + w - 1, y, x + w, y + h, color);
    }

    // =========================================================================
    // 5. PANELES Y FONDOS NEO-CAPSULE LIQUID GLASS
    // =========================================================================

    /**
     * Dibuja el fondo de pantalla con tono difuminado fluido y living ambient dust.
     */
    public static void drawScreenBackdrop(GuiGraphicsExtractor guiGraphics, int width, int height) {
        guiGraphics.fillGradient(0, 0, width, height, BG_DIFFUSE_TOP, BG_DIFFUSE_BOTTOM);
        guiGraphics.fillGradient(0, 0, width, height / 4, 0x14000000, 0x00000000);
        guiGraphics.fillGradient(0, height * 3 / 4, width, height, 0x00000000, 0x1A000000);

        guiGraphics.fill(0, 0, width, 1, 0x8038BDF8);
        guiGraphics.fill(0, 1, width, 2, 0x2538BDF8);

        long time = System.currentTimeMillis();
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            float speed = PARTICLE_SPEED[i];
            float yPos = ((height + 20) - ((time * 0.025f * speed + i * 24) % (height + 40)));
            float xOffset = (float) Math.sin((time * 0.0012f) + i) * 10.0f;
            int px = (int) (PARTICLE_SEED_X[i] * width + xOffset);
            int py = (int) yPos;

            if (px >= 0 && px < width && py >= 0 && py < height) {
                float alphaPulse = (float) (Math.sin(time * 0.0025 + i) * 0.3 + 0.5);
                int baseCol = PARTICLE_COLOR[i];
                int alpha = (int) (alphaPulse * 0x40) << 24;
                int finalColor = alpha | (baseCol & 0x00FFFFFF);

                int size = (int) PARTICLE_SIZE[i];
                guiGraphics.fill(px, py, px + size, py + size, finalColor);
            }
        }
    }

    /**
     * Dibuja un panel bajo el estándar Neo-Capsule Liquid Glass con curvatura real de píxeles/cuadrante (radius 6px a 10px).
     * Fondo: rgba(10, 20, 36, 0.45) y bordes: rgba(56, 189, 248, 0.35).
     */
    public static void drawLiquidPanel(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int radius, int borderColor) {
        if (w <= 0 || h <= 0) return;
        int r = Math.max(4, Math.min(12, radius));
        int bg = GLASS_BG_LIQUID;
        int border = (borderColor != 0) ? borderColor : GLASS_BORDER_LIQUID;
        fillRoundedRect(guiGraphics, x, y, w, h, r, bg);
        drawRoundedBorder(guiGraphics, x, y, w, h, r, border);
        guiGraphics.fill(x + r, y + 1, x + w - r, y + 2, 0x25FFFFFF);
    }

    public static void drawLiquidPanel(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int borderColor) {
        drawLiquidPanel(guiGraphics, x, y, w, h, 8, borderColor);
    }

    /**
     * Dibuja un panel con esquinas redondeadas orgánicas (Border-Radius 8px) y marco de cristal líquido.
     */
    public static void drawGlassPanel(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, int borderColor) {
        drawLiquidPanel(guiGraphics, x, y, w, h, 8, borderColor);
    }

    public static void drawModernCard(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, String title, int borderColor, boolean hasBorder) {
        drawGlassPanel(guiGraphics, x, y, w, h, borderColor != 0 ? borderColor : PANEL_BORDER_BASE);
    }

    /**
     * Dibuja un panel modal translúcido con curvatura de 10px y doble borde luminoso.
     */
    public static void drawModalBackdrop(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h) {
        int radius = 10;
        // Sombra exterior
        fillRoundedRect(guiGraphics, x - 2, y - 2, w + 4, h + 4, radius + 2, 0x30000000);
        // Fondo de cristal profundo
        fillRoundedRect(guiGraphics, x, y, w, h, radius, 0xE50B1322);
        // Bordes redondeados
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, COLOR_CYAN_NEON);
        drawRoundedBorder(guiGraphics, x + 1, y + 1, w - 2, h - 2, radius - 1, 0x4038BDF8);
        guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, 0x50FFFFFF);
    }

    // =========================================================================
    // 6. BOTONES Y PÍLDORAS OVALES (ROUNDED-FULL)
    // =========================================================================

    /**
     * Dibuja una cápsula píldora oval estándar (rounded-full con radio = h / 2).
     */
    public static void drawCapsulePill(GuiGraphicsExtractor guiGraphics, Font font,
                                       int x, int y, int w, int h, Component text,
                                       boolean hovered, boolean active, int primaryColor, int secondaryColor) {
        int radius = h / 2;
        int bg = active ? (hovered ? 0xB006B6D4 : 0x9006B6D4) : (hovered ? 0x6018263E : 0x350C1628);
        int border = active ? 0xFF38BDF8 : (hovered ? primaryColor : 0x5038BDF8);
        int textCol = active ? 0xFFFFFFFF : (hovered ? 0xFFFFFFFF : 0xFFCBD5E1);

        fillRoundedRect(guiGraphics, x, y, w, h, radius, bg);
        guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, active ? 0x80FFFFFF : (hovered ? 0x40FFFFFF : 0x18FFFFFF));
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);

        if (active) {
            guiGraphics.fill(x + radius, y + h - 2, x + w - radius, y + h, 0xFF00E5FF);
        }

        guiGraphics.centeredText(font, text, x + w / 2, y + (h - 8) / 2, textCol);
    }

    /**
     * Dibuja un botón Heroico Esmeralda (#10b981 a #059669) en formato cápsula oval.
     */
    public static void drawHeroEmeraldPill(GuiGraphicsExtractor guiGraphics, Font font,
                                           int x, int y, int w, int h, Component text, boolean hovered) {
        int radius = h / 2;
        int bgStart = hovered ? 0xFF059669 : 0xFF10B981;
        int bgEnd   = hovered ? 0xFF047857 : 0xFF059669;

        fillGradientRoundedRect(guiGraphics, x, y, w, h, radius, bgStart, bgEnd);
        int border = hovered ? 0xFF6EE7B7 : 0xFF34D399;
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);
        guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, 0x60FFFFFF);

        guiGraphics.centeredText(font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    /**
     * Dibuja un botón de acción en gradiente Ámbar cálido (#f59e0b a #d97706) en cápsula oval.
     */
    public static void drawActionAmberPill(GuiGraphicsExtractor guiGraphics, Font font,
                                          int x, int y, int w, int h, Component text, boolean hovered) {
        int radius = h / 2;
        int bgStart = hovered ? 0xFFD97706 : 0xFFF59E0B;
        int bgEnd   = hovered ? 0xFFB45309 : 0xFFD97706;

        fillGradientRoundedRect(guiGraphics, x, y, w, h, radius, bgStart, bgEnd);
        int border = hovered ? 0xFFFDE68A : 0xFFFCD34D;
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);
        guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, 0x50FFFFFF);

        guiGraphics.centeredText(font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    /**
     * Dibuja un botón de peligro en gradiente Carmesí (#e11d48 a #be123c) en cápsula oval.
     */
    public static void drawDangerPill(GuiGraphicsExtractor guiGraphics, Font font,
                                     int x, int y, int w, int h, Component text, boolean hovered) {
        int radius = h / 2;
        int bgStart = hovered ? 0xFFBE123C : 0xFFE11D48;
        int bgEnd   = hovered ? 0xFF9F1239 : 0xFFBE123C;

        fillGradientRoundedRect(guiGraphics, x, y, w, h, radius, bgStart, bgEnd);
        int border = hovered ? 0xFFFDA4AF : 0xFFFB7185;
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);
        guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, 0x50FFFFFF);

        guiGraphics.centeredText(font, text, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
    }

    // =========================================================================
    // 7. MICRO-BADGES DE HERRAMIENTAS (.ref-tag & .tag-dot)
    // =========================================================================

    public static void drawToolBadge(GuiGraphicsExtractor guiGraphics, Font font, int x, int y, String label, int dotColor) {
        int textW = font.width(label);
        int badgeW = textW + 18;
        int badgeH = 14;
        int radius = 5;

        fillRoundedRect(guiGraphics, x, y, badgeW, badgeH, radius, 0x850F172A);
        drawRoundedBorder(guiGraphics, x, y, badgeW, badgeH, radius, 0x5038BDF8);

        int dotX = x + 5;
        int dotY = y + 4;
        drawFilledCircle(guiGraphics, dotX + 2, dotY + 2, 2, dotColor);

        guiGraphics.text(font, Component.literal(label), x + 13, y + 3, 0xFFE2E8F0);
    }

    public static void drawToolBadgePill(GuiGraphicsExtractor guiGraphics, Font font, int x, int y, ToolCategory category) {
        drawToolBadge(guiGraphics, font, x, y, category.getTag(), category.getDotColor());
    }

    public static int getToolBadgeWidth(Font font, String label) {
        return font.width(label) + 18;
    }

    // =========================================================================
    // 8. PEDESTAL 3D Y HERO SHOWCASE LENS
    // =========================================================================

    public static void drawShowcasePedestal(GuiGraphicsExtractor guiGraphics, int cx, int cy, int size) {
        int half = size / 2;
        // Gradientes radiales concéntricos cálidos (#f59e0b)
        for (int r = half; r >= 4; r -= 4) {
            float f = (float) r / half;
            int alpha = (int) ((1f - f) * 0x35);
            int col = (alpha << 24) | 0xF59E0B;
            drawFilledCircle(guiGraphics, cx, cy, r, col);
        }
        drawCircleBorder(guiGraphics, cx, cy, half, 0x60F59E0B);
        drawCircleBorder(guiGraphics, cx, cy, half - 2, 0x4038BDF8);
    }

    public static void drawHeroShowcaseLens(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, boolean hovered) {
        int radius = 10;
        fillRoundedRect(guiGraphics, x, y, w, h, radius, 0x450A1424);
        int border = hovered ? 0xFF00E5FF : 0x5038BDF8;
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);

        int maxR = Math.min(w, h) / 2 - 4;
        int cx = x + w / 2;
        int cy = y + h / 2;
        for (int r = maxR; r >= 6; r -= 6) {
            float f = (float) r / maxR;
            int alpha = (int) ((1f - f) * 0x2A);
            int col = (alpha << 24) | 0xF59E0B;
            drawFilledCircle(guiGraphics, cx, cy, r, col);
        }
    }

    // =========================================================================
    // 9. DRAWER LATERAL DESLIZANTE DE CATÁLOGO (36px Manija & 352px Panel)
    // =========================================================================

    public static void drawCatalogDrawerHandle(GuiGraphicsExtractor guiGraphics, Font font,
                                               int x, int y, int w, int h, boolean isOpen, boolean hovered) {
        int bg = hovered ? 0xEA0D1B32 : 0xD50A1426;
        int border = hovered ? COLOR_CYAN_NEON : 0x8038BDF8;

        // Manija con esquinas redondeadas en el borde derecho
        guiGraphics.fill(x, y + 6, x + w, y + h - 6, bg);
        guiGraphics.fill(x, y + 3, x + w - 2, y + h - 3, bg);
        guiGraphics.fill(x, y, x + w - 5, y + h, bg);

        // Borde contorneado
        guiGraphics.fill(x, y, x + w - 5, y + 1, border);
        guiGraphics.fill(x, y + h - 1, x + w - 5, y + h, border);
        guiGraphics.fill(x + w - 1, y + 6, x + w, y + h - 6, border);
        guiGraphics.fill(x + w - 2, y + 3, x + w - 1, y + 6, border);
        guiGraphics.fill(x + w - 2, y + h - 6, x + w - 1, y + h - 3, border);

        // Indicador direccional (▶ cerrado / ◀ abierto)
        String arrow = isOpen ? "◀" : "▶";
        guiGraphics.centeredText(font, Component.literal(arrow), x + w / 2, y + 8, COLOR_CYAN_NEON);

        // Texto vertical: 📦 C A T Á L O G O
        String[] chars = {"📦", "C", "A", "T", "Á", "L", "O", "G", "O"};
        int charY = y + 26;
        for (String c : chars) {
            guiGraphics.centeredText(font, Component.literal(c), x + w / 2, charY, hovered ? 0xFFFFFFFF : 0xFFCBD5E1);
            charY += 15;
        }
    }

    public static void drawCatalogDrawerPanel(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h) {
        int radius = 12;
        // Panel con esquinas curvas hacia la derecha
        guiGraphics.fill(x, y, x + w - radius, y + h, 0xF20A1426);
        for (int dy = 0; dy < radius; dy++) {
            int inset = getArcInset(dy, radius);
            guiGraphics.fill(x + w - radius, y + dy, x + w - inset, y + dy + 1, 0xF20A1426);
            guiGraphics.fill(x + w - radius, y + h - 1 - dy, x + w - inset, y + h - dy, 0xF20A1426);
        }
        guiGraphics.fill(x + w - radius, y + radius, x + w, y + h - radius, 0xF20A1426);

        // Borde superior, inferior y derecho curvado
        guiGraphics.fill(x, y, x + w - radius, y + 1, COLOR_CYAN_NEON);
        guiGraphics.fill(x, y + h - 1, x + w - radius, y + h, COLOR_CYAN_NEON);
        guiGraphics.fill(x + w - 1, y + radius, x + w, y + h - radius, COLOR_CYAN_NEON);

        for (int dy = 0; dy < radius; dy++) {
            int inset = getArcInset(dy, radius);
            guiGraphics.fill(x + w - 1 - inset, y + dy, x + w - inset, y + dy + 1, COLOR_CYAN_NEON);
            guiGraphics.fill(x + w - 1 - inset, y + h - 1 - dy, x + w - inset, y + h - dy, COLOR_CYAN_NEON);
        }
    }

    // =========================================================================
    // 10. NOTEPAD CON GUTTER DE NÚMEROS DE LÍNEA
    // =========================================================================

    public static void drawNotepadEditorBackground(GuiGraphicsExtractor guiGraphics,
                                                   int x, int y, int w, int h, int gutterW) {
        int radius = 6;
        fillRoundedRect(guiGraphics, x, y, w, h, radius, 0xD5080E18);

        // Columna gutter con borde curvo a la izquierda
        guiGraphics.fill(x, y + radius, x + gutterW, y + h - radius, 0xEE050910);
        for (int dy = 0; dy < radius; dy++) {
            int inset = getArcInset(dy, radius);
            guiGraphics.fill(x + inset, y + dy, x + gutterW, y + dy + 1, 0xEE050910);
            guiGraphics.fill(x + inset, y + h - 1 - dy, x + gutterW, y + h - dy, 0xEE050910);
        }

        // Línea divisoria
        guiGraphics.fill(x + gutterW - 1, y, x + gutterW, y + h, 0x5038BDF8);
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, 0x5038BDF8);
    }

    public static void drawGutterLineNumbers(GuiGraphicsExtractor guiGraphics, Font font,
                                             int gutterX, int startY, int lineCount, int firstLineIdx, int lineHeight) {
        for (int i = 0; i < lineCount; i++) {
            int lineNum = firstLineIdx + i + 1;
            int ly = startY + (i * lineHeight);
            String numStr = String.valueOf(lineNum);
            guiGraphics.text(font, Component.literal(numStr), gutterX + 4, ly, 0xFF64748B);
        }
    }

    // =========================================================================
    // 11. SLOTS Y COFRE 54s
    // =========================================================================

    public static void drawSlot(GuiGraphicsExtractor guiGraphics, int x, int y, int size, boolean hovered) {
        int radius = 3;
        fillRoundedRect(guiGraphics, x, y, size, size, radius, hovered ? 0x6038BDF8 : COLOR_DARK_SLOT);
        int borderColor = hovered ? 0xFF38BDF8 : 0x4038BDF8;
        drawRoundedBorder(guiGraphics, x, y, size, size, radius, borderColor);
        guiGraphics.fill(x + 1, y + 1, x + size - 1, y + 2, 0x15FFFFFF);
    }

    public static void drawGlassSlot54(GuiGraphicsExtractor guiGraphics, int x, int y, boolean hovered) {
        int size = 18;
        int radius = 2;
        fillRoundedRect(guiGraphics, x, y, size, size, radius, hovered ? 0x6500D2FF : 0x300D1B30);
        int border = hovered ? 0xFF00E5FF : 0x4038BDF8;
        drawRoundedBorder(guiGraphics, x, y, size, size, radius, border);
        guiGraphics.fill(x + 1, y + 1, x + size - 1, y + 2, 0x20FFFFFF);
    }

    public static void drawCmiToggle(GuiGraphicsExtractor guiGraphics, int x, int y, boolean enabled, boolean hovered) {
        int w = 26;
        int h = 13;
        int radius = h / 2;
        int bgColor = enabled ? (hovered ? 0xFF0EA5E9 : 0xFF0284C7) : (hovered ? 0xFF1F293D : 0xFF111723);
        int borderColor = enabled ? (hovered ? 0xFF7DD3FC : 0xFF38BDF8) : (hovered ? 0xFF334155 : 0xFF1E293B);

        fillRoundedRect(guiGraphics, x, y, w, h, radius, bgColor);
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, borderColor);

        int knobX = enabled ? (x + w - 11) : (x + 2);
        int knobY = y + 2;
        int knobSize = 9;
        fillRoundedRect(guiGraphics, knobX, knobY, knobSize, knobSize, 4, 0xFFFFFFFF);
    }

    public static void drawCmiProgressBar(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, float progress, int barColor) {
        int radius = height / 2;
        fillRoundedRect(guiGraphics, x, y, width, height, radius, 0xFF0F1420);
        drawRoundedBorder(guiGraphics, x, y, width, height, radius, 0xFF1E2638);

        int fillW = (int) (width * Math.max(0f, Math.min(1f, progress)));
        if (fillW > 0) {
            fillRoundedRect(guiGraphics, x + 1, y + 1, fillW - 1, height - 2, radius - 1, barColor);
        }
    }

    public static void drawCmiSlider(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height, float progress, boolean hovered) {
        int trackH = 4;
        int trackY = y + (height - trackH) / 2;
        fillRoundedRect(guiGraphics, x, trackY, width, trackH, 2, 0xFF0F1522);
        drawRoundedBorder(guiGraphics, x, trackY, width, trackH, 2, 0xFF1E2638);

        int fillW = (int) (width * Math.max(0f, Math.min(1f, progress)));
        if (fillW > 0) {
            guiGraphics.fill(x + 1, trackY + 1, x + fillW, trackY + trackH - 1, COLOR_CYAN_NEON);
        }

        int knobW = 8;
        int knobH = 12;
        int knobX = x + fillW - knobW / 2;
        knobX = Math.max(x, Math.min(x + width - knobW, knobX));
        int knobY = y + (height - knobH) / 2;
        int knobColor = hovered ? 0xFFFFFFFF : 0xFFE0F7FF;

        fillRoundedRect(guiGraphics, knobX, knobY, knobW, knobH, 4, knobColor);
        drawRoundedBorder(guiGraphics, knobX, knobY, knobW, knobH, 4, COLOR_CYAN_NEON);
    }

    public static void drawGradientShimmerBar(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h,
                                             int col1, int col2, int col3, boolean threeColors) {
        long time = System.currentTimeMillis();
        float shimmerPos = ((time * 0.08f) % (w + 40)) - 20;

        for (int i = 0; i < w; i++) {
            float f = (float) i / w;
            int baseCol;
            if (threeColors) {
                if (f <= 0.5f) {
                    baseCol = lerpColor(col1, col2, f * 2.0f);
                } else {
                    baseCol = lerpColor(col2, col3, (f - 0.5f) * 2.0f);
                }
            } else {
                baseCol = lerpColor(col1, col2, f);
            }

            float distToShimmer = Math.abs(i - shimmerPos);
            if (distToShimmer < 16.0f) {
                float intensity = 1.0f - (distToShimmer / 16.0f);
                baseCol = lerpColor(baseCol, 0xFFFFFFFF, intensity * 0.55f);
            }

            guiGraphics.fill(x + i, y, x + i + 1, y + h, baseCol | 0xFF000000);
        }
        drawRoundedBorder(guiGraphics, x - 1, y - 1, w + 2, h + 2, 3, 0xFF1D2633);
    }

    public static void drawLogo(GuiGraphicsExtractor guiGraphics, int x, int y, int size) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CMI_LOGO_ID, x, y, 0f, 0f, size, size, 64, 64, 64, 64);
    }

    public static void drawLogoHero(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CMI_LOGO_HERO, x, y, 0f, 0f, width, height, 520, 250, 520, 250);
    }

    public static void drawLogoIcon(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CMI_LOGO_ICON, x, y, 0f, 0f, width, height, 160, 77, 160, 77);
    }

    public static void drawLogoChrome(GuiGraphicsExtractor guiGraphics, int x, int y, int width, int height) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, CMI_LOGO_CHROME, x, y, 0f, 0f, width, height, 986, 475, 986, 475);
    }

    public static void drawFilledCircle(GuiGraphicsExtractor guiGraphics, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy <= radius; dy++) {
            int dx = (int) Math.round(Math.sqrt(radius * radius - dy * dy));
            guiGraphics.fill(cx - dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
        }
    }

    public static void drawCircleBorder(GuiGraphicsExtractor guiGraphics, int cx, int cy, int radius, int color) {
        for (int dy = -radius; dy <= radius; dy++) {
            int dx = (int) Math.round(Math.sqrt(radius * radius - dy * dy));
            guiGraphics.fill(cx - dx, cy + dy, cx - dx + 1, cy + dy + 1, color);
            guiGraphics.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
        }
        for (int dx = -radius; dx <= radius; dx++) {
            int dy = (int) Math.round(Math.sqrt(radius * radius - dx * dx));
            guiGraphics.fill(cx + dx, cy - dy, cx + dx + 1, cy - dy + 1, color);
            guiGraphics.fill(cx + dx, cy + dy, cx + dx + 1, cy + dy + 1, color);
        }
    }

    public static int lerpColor(int col1, int col2, float f) {
        f = Math.max(0.0f, Math.min(1.0f, f));
        int a1 = (col1 >> 24) & 0xFF;
        int r1 = (col1 >> 16) & 0xFF;
        int g1 = (col1 >> 8) & 0xFF;
        int b1 = col1 & 0xFF;

        int a2 = (col2 >> 24) & 0xFF;
        int r2 = (col2 >> 16) & 0xFF;
        int g2 = (col2 >> 8) & 0xFF;
        int b2 = col2 & 0xFF;

        int a = (int) (a1 + f * (a2 - a1));
        int r = (int) (r1 + f * (r2 - r1));
        int g = (int) (g1 + f * (g2 - g1));
        int b = (int) (b1 + f * (b2 - b1));

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static String toRoman(int level) {
        if (level <= 0) return "0";
        if (level == 32767) return "32k";
        if (level > 10) return String.valueOf(level);
        String[] romans = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return romans[level];
    }

    // =========================================================================
    // 12. COMPONENTES VISUALES ADICIONALES (UNDO/REDO, OJOS 👁️, DROPDOWNS)
    // =========================================================================

    /**
     * Dibuja un botón de ojo interactivo [ 👁️ ON ] / [ 👁️ OFF ] para control de visibilidad en el lore.
     */
    public static void drawEyeToggle(GuiGraphicsExtractor guiGraphics, Font font, int x, int y, boolean isVisible, boolean hovered) {
        int w = 48;
        int h = 16;
        int radius = h / 2;
        int bg = isVisible
                ? (hovered ? 0xB006B6D4 : 0x8506B6D4)
                : (hovered ? 0x90BE123C : 0x659F1239);
        int border = isVisible ? COLOR_CYAN_NEON : COLOR_DANGER;
        int textCol = isVisible ? 0xFFFFFFFF : 0xFFFDA4AF;

        fillRoundedRect(guiGraphics, x, y, w, h, radius, bg);
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);
        guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, isVisible ? 0x60FFFFFF : 0x30FFFFFF);

        String label = isVisible ? "👁 ON" : "👁 OFF";
        guiGraphics.centeredText(font, Component.literal(label), x + w / 2, y + (h - 8) / 2, textCol);
    }

    /**
     * Dibuja un botón cápsula para Deshacer [ ↩ Deshacer ] o Rehacer [ ↪ Rehacer ].
     * Si canExecute es falso, se dibuja atenuado y deshabilitado.
     */
    public static void drawUndoRedoButton(GuiGraphicsExtractor guiGraphics, Font font,
                                          int x, int y, int w, int h, String text,
                                          boolean canExecute, boolean hovered) {
        int radius = h / 2;
        int bg;
        int border;
        int textCol;

        if (!canExecute) {
            bg = 0x250D131F;
            border = 0x2038BDF8;
            textCol = 0xFF475569;
        } else if (hovered) {
            bg = 0x9006B6D4;
            border = COLOR_CYAN_NEON;
            textCol = 0xFFFFFFFF;
        } else {
            bg = 0x450C1628;
            border = 0x6038BDF8;
            textCol = 0xFFE2E8F0;
        }

        fillRoundedRect(guiGraphics, x, y, w, h, radius, bg);
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);
        if (canExecute) {
            guiGraphics.fill(x + radius, y + 1, x + w - radius, y + 2, hovered ? 0x60FFFFFF : 0x25FFFFFF);
        }

        guiGraphics.centeredText(font, Component.literal(text), x + w / 2, y + (h - 8) / 2, textCol);
    }

    /**
     * Dibuja la cabecera de un selector Dropdown colapsable estilo cristal líquido.
     */
    public static void drawDropdownHeader(GuiGraphicsExtractor guiGraphics, Font font,
                                          int x, int y, int w, int h, String label,
                                          boolean isOpen, boolean hovered) {
        int radius = 6;
        int bg = hovered ? 0x850E1A2E : 0x550A1424;
        int border = isOpen ? COLOR_CYAN_ACTIVE : (hovered ? COLOR_CYAN_NEON : GLASS_PANEL_BORDER);

        fillRoundedRect(guiGraphics, x, y, w, h, radius, bg);
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, border);

        guiGraphics.text(font, Component.literal(label), x + 8, y + (h - 8) / 2, 0xFFE2E8F0);
        String arrow = isOpen ? "▲" : "▼";
        guiGraphics.text(font, Component.literal(arrow), x + w - 14, y + (h - 8) / 2, COLOR_CYAN_NEON);
    }

    /**
     * Dibuja un micro-badge de etiqueta con punto de color.
     */
    public static void drawMiniBadge(GuiGraphicsExtractor guiGraphics, Font font, int x, int y, String label, int color) {
        int textW = font.width(label);
        int w = textW + 14;
        int h = 13;
        int radius = 4;

        fillRoundedRect(guiGraphics, x, y, w, h, radius, 0x800F172A);
        drawRoundedBorder(guiGraphics, x, y, w, h, radius, 0x5038BDF8);
        drawFilledCircle(guiGraphics, x + 5, y + h / 2, 2, color);
        guiGraphics.text(font, Component.literal(label), x + 10, y + 2, 0xFFCBD5E1);
    }
}
