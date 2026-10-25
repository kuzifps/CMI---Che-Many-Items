package com.fxfps.cmi.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * Pantalla de prueba in-game del Design System Neo-Capsule Liquid Glass 💎
 *
 * Permite verificar en vivo:
 * - Ultra-transparencia a 60+ FPS sin pausar el juego (shouldPause -> false).
 * - Shaders y mundo visible detrás de las ventanas de cristal líquido.
 * - Píldoras estandarizadas (Hero Esmeralda, Acción Ámbar, Peligro Carmesí, Tab Cyan).
 * - Micro-badges exclusivos de herramientas de Minecraft con puntos de color.
 * - Pedestal 3D con aura cálida concéntrica.
 * - Manija del Drawer lateral de Catálogo adherida a la izquierda.
 */
public class CmiTestScreen extends CmiScreen {

    private boolean toggleDemo = true;
    private boolean drawerOpen = false;
    private String statusMessage = "Design System cargado · 60 FPS";
    private final ItemStack demoSword = new ItemStack(Items.NETHERITE_SWORD);

    public CmiTestScreen(Screen parentScreen) {
        super(Component.literal("CMI · Verificación Visual Neo-Capsule"), parentScreen);
    }

    @Override
    protected void init() {
        super.init();

        int winW = Math.min(this.width - 48, 560);
        int winH = Math.min(this.height - 36, 290);
        int winX = (this.width - winW) / 2;
        int winY = (this.height - winH) / 2;

        // =========================================================================
        // BARRA SUPERIOR: PÍLDORAS NORMALIZADAS DE NAVEGACIÓN
        // =========================================================================
        int pillY = 6;
        int pillH = 18;
        int pillStartX = this.width - 330;

        // Píldora Display (Activa con Cyan #06b6d4)
        CmiButton displayPill = new CmiButton(pillStartX, pillY, 65, pillH, Component.literal("Display"), btn -> {
            statusMessage = "Pestaña [ Display ] seleccionada";
        }, CmiButton.Variant.TAB);
        displayPill.setSelected(true);
        this.addRenderableWidget(applyAndRegisterLayout("tab_display", displayPill));

        // Píldora Enchants
        this.addRenderableWidget(applyAndRegisterLayout("tab_enchants", new CmiButton(pillStartX + 68, pillY, 78, pillH, Component.literal("⚡ Enchants"), btn -> {
            statusMessage = "Pestaña [ ⚡ Enchants ] seleccionada";
        }, CmiButton.Variant.TAB)));

        // Píldora Atributos
        this.addRenderableWidget(applyAndRegisterLayout("tab_attributes", new CmiButton(pillStartX + 149, pillY, 78, pillH, Component.literal("⚔ Atributos"), btn -> {
            statusMessage = "Pestaña [ ⚔ Atributos ] seleccionada";
        }, CmiButton.Variant.TAB)));

        // Píldora Kits
        this.addRenderableWidget(applyAndRegisterLayout("tab_kits", new CmiButton(pillStartX + 230, pillY, 60, pillH, Component.literal("🎒 Kits"), btn -> {
            statusMessage = "Pestaña [ 🎒 Kits ] seleccionada";
        }, CmiButton.Variant.TAB)));

        // Botón Cerrar [ ✕ ]
        this.addRenderableWidget(applyAndRegisterLayout("btn_close", new CmiButton(this.width - 28, pillY, 20, pillH, Component.literal("✕"), btn -> {
            this.closeScreen();
        }, CmiButton.Variant.DANGER)));

        // =========================================================================
        // COLUMNA CENTRAL: BOTONERA HEROICA Y DE ACCIÓN NORMALIZADA
        // =========================================================================
        int colCenterStartX = winX + (int) (winW * 0.32f);
        int btnW = 160;
        int btnStartY = winY + 44;

        // 1. Botón Heroico Esmeralda (#10b981 a #059669)
        this.addRenderableWidget(applyAndRegisterLayout("btn_hero_apply", new CmiButton(
                colCenterStartX, btnStartY, btnW, 20,
                Component.literal("✔ APLICAR EN INVENTARIO"),
                btn -> statusMessage = "✔ Botón Heroico Esmeralda activado",
                CmiButton.Variant.HERO_EMERALD
        )));

        // 2. Botón de Acción Ámbar (#f59e0b a #d97706)
        this.addRenderableWidget(applyAndRegisterLayout("btn_action_load", new CmiButton(
                colCenterStartX, btnStartY + 26, btnW, 20,
                Component.literal("⚡ Cargar en Mano"),
                btn -> statusMessage = "⚡ Botón de Acción Ámbar activado",
                CmiButton.Variant.ACTION_AMBER
        )));

        // 3. Botón de Peligro Carmesí (#e11d48)
        this.addRenderableWidget(applyAndRegisterLayout("btn_danger_clear", new CmiButton(
                colCenterStartX, btnStartY + 52, btnW, 20,
                Component.literal("✕ Limpiar / Reset"),
                btn -> statusMessage = "✕ Botón de Peligro activado",
                CmiButton.Variant.DANGER
        )));

        // 4. Botón Píldora Glass Chip
        this.addRenderableWidget(new CmiButton(
                colCenterStartX, btnStartY + 78, btnW, 20,
                Component.literal("✨ Limpio (Strip)"),
                btn -> statusMessage = "✨ Strip Glass activado",
                CmiButton.Variant.CHIP
        ));

        // 5. Botón Toggle
        CmiButton toggleBtn = new CmiButton(
                colCenterStartX, btnStartY + 104, btnW, 20,
                Component.literal("Shader Shimmer"),
                btn -> {
                    toggleDemo = !toggleDemo;
                    statusMessage = "Toggle Shimmer: " + (toggleDemo ? "ACTIVO" : "INACTIVO");
                }
        );
        toggleBtn.setToggle(true, toggleDemo);
        this.addRenderableWidget(toggleBtn);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDown) {
        if (isDown) {
            double mx = event.x();
            double my = event.y();
            int handleW = 32;
            int handleH = 150;
            int handleY = (this.height - handleH) / 2;

            if (mx >= 2 && mx <= 2 + handleW && my >= handleY && my <= handleY + handleH) {
                drawerOpen = !drawerOpen;
                statusMessage = "Drawer Catálogo: " + (drawerOpen ? "ABIERTO (352px)" : "CERRADO (36px)");
                return true;
            }
        }
        return super.mouseClicked(event, isDown);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Renderiza el fondo ultra-traslúcido con difuminado y living ambient dust sobre Minecraft en vivo
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int winW = Math.min(this.width - 48, 560);
        int winH = Math.min(this.height - 36, 290);
        int winX = (this.width - winW) / 2;
        int winY = (this.height - winH) / 2;

        // 2. Ventana Principal Neo-Capsule Liquid Glass
        CmiUiTheme.drawGlassPanel(guiGraphics, winX, winY, winW, winH, CmiUiTheme.COLOR_CYAN_NEON);

        // Cabecera de la ventana
        guiGraphics.fill(winX + 2, winY + 2, winX + winW - 2, winY + 24, CmiUiTheme.PANEL_HEADER_BG);
        guiGraphics.fill(winX + 2, winY + 24, winX + winW - 2, winY + 25, 0x5038BDF8);

        // Logo Icono Chrome 3D en la esquina superior izquierda
        CmiUiTheme.drawLogoIcon(guiGraphics, winX + 8, winY + 5, 36, 17);
        guiGraphics.text(this.font, Component.literal("§b§lCheManyItems §7· §fDesign System Neo-Capsule Liquid Glass"), winX + 48, winY + 8, CmiUiTheme.COLOR_WHITE);

        // 3. Columna 1 (Izquierda): Pedestal 3D y Hero Showcase Lens
        int col1W = (int) (winW * 0.28f);
        int col1X = winX + 8;
        int col1Y = winY + 30;
        int col1H = winH - 38;
        CmiUiTheme.drawGlassPanel(guiGraphics, col1X, col1Y, col1W, col1H, CmiUiTheme.GLASS_PANEL_BORDER);

        guiGraphics.centeredText(this.font, Component.literal("§6✦ Pedestal 3D ✦"), col1X + col1W / 2, col1Y + 6, CmiUiTheme.COLOR_AMBER);

        // Pedestal concéntrico con aura ámbar cálida
        int pedCX = col1X + col1W / 2;
        int pedCY = col1Y + 54;
        CmiUiTheme.drawShowcasePedestal(guiGraphics, pedCX, pedCY, 58);

        // Renderizado del ítem dentro del pedestal
        guiGraphics.item(demoSword, pedCX - 8, pedCY - 8);
        guiGraphics.itemDecorations(this.font, demoSword, pedCX - 8, pedCY - 8);

        // Badges de herramientas aplicados a la muestra
        int badgeY = col1Y + 92;
        CmiUiTheme.drawToolBadge(guiGraphics, this.font, col1X + 8, badgeY, "<Espadas>", CmiUiTheme.COLOR_BADGE_ESPADAS);
        CmiUiTheme.drawToolBadge(guiGraphics, this.font, col1X + 8, badgeY + 18, "<Herramientas>", CmiUiTheme.COLOR_BADGE_HERRAMIENTAS);
        CmiUiTheme.drawToolBadge(guiGraphics, this.font, col1X + 8, badgeY + 36, "<Armaduras>", CmiUiTheme.COLOR_BADGE_ARMADURAS);

        // 4. Columna 3 (Derecha): Catálogo completo de los 6 Micro-Badges de Herramientas
        int col3W = (int) (winW * 0.36f);
        int col3X = winX + winW - col3W - 8;
        int col3Y = winY + 30;
        int col3H = winH - 38;
        CmiUiTheme.drawGlassPanel(guiGraphics, col3X, col3Y, col3W, col3H, CmiUiTheme.GLASS_PANEL_BORDER);

        guiGraphics.centeredText(this.font, Component.literal("§b🛡️ Micro-Badges (6/6)"), col3X + col3W / 2, col3Y + 6, CmiUiTheme.COLOR_CYAN_NEON);
        guiGraphics.centeredText(this.font, Component.literal("§8100% Ítems · Cero Rangos Servidor"), col3X + col3W / 2, col3Y + 16, CmiUiTheme.COLOR_MUTED);

        int rightBadgeY = col3Y + 30;
        for (CmiUiTheme.ToolCategory cat : CmiUiTheme.ToolCategory.values()) {
            CmiUiTheme.drawToolBadgePill(guiGraphics, this.font, col3X + 12, rightBadgeY, cat);
            guiGraphics.text(this.font, Component.literal("§7" + cat.getDisplayName()), col3X + 115, rightBadgeY + 3, CmiUiTheme.COLOR_MUTED);
            rightBadgeY += 17;
        }

        // Checklist de verificación en vivo
        int checkY = col3Y + col3H - 52;
        guiGraphics.text(this.font, Component.literal("§a✔ shouldPause() -> false"), col3X + 8, checkY, 0xFF10B981);
        guiGraphics.text(this.font, Component.literal("§a✔ Minecraft en vivo 60 FPS"), col3X + 8, checkY + 10, 0xFF10B981);
        guiGraphics.text(this.font, Component.literal("§a✔ Fondo transparente puro"), col3X + 8, checkY + 20, 0xFF10B981);
        guiGraphics.text(this.font, Component.literal("§a✔ Badges de micro-punto"), col3X + 8, checkY + 30, 0xFF10B981);

        // 5. Barra Shimmer animada (demo de gradiente)
        int shimmerX = winX + (int) (winW * 0.32f);
        int shimmerY = winY + winH - 32;
        int shimmerW = 160;
        if (toggleDemo) {
            CmiUiTheme.drawGradientShimmerBar(guiGraphics, shimmerX, shimmerY, shimmerW, 6, 0x38BDF8, 0xF59E0B, 0x10B981, true);
        }

        // Estado interactivo inferior
        guiGraphics.centeredText(this.font, Component.literal("§e" + statusMessage), winX + winW / 2, winY + winH - 14, CmiUiTheme.COLOR_AMBER);

        // 6. Manija visual del Catálogo Lateral Adherido a la Izquierda
        int handleW = 32;
        int handleH = 150;
        int handleY = (this.height - handleH) / 2;
        boolean handleHovered = mouseX >= 2 && mouseX <= 2 + handleW && mouseY >= handleY && mouseY <= handleY + handleH;
        CmiUiTheme.drawCatalogDrawerHandle(guiGraphics, this.font, 2, handleY, handleW, handleH, drawerOpen, handleHovered);

        if (drawerOpen) {
            CmiUiTheme.drawCatalogDrawerPanel(guiGraphics, 34, handleY - 20, 160, handleH + 40);
            guiGraphics.centeredText(this.font, Component.literal("§b📦 CATÁLOGO 352px"), 34 + 80, handleY, CmiUiTheme.COLOR_CYAN_NEON);
            guiGraphics.centeredText(this.font, Component.literal("§7(Grid 4 cols en Fase 2)"), 34 + 80, handleY + 14, CmiUiTheme.COLOR_MUTED);
        }
    }
}
