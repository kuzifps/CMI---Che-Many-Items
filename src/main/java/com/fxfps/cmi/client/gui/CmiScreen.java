package com.fxfps.cmi.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

/**
 * Pantalla base abstracta unificada para todas las interfaces de CheManyItems (CMI) 🇦🇷.
 *
 * Aplica de forma estricta e intransigente el Design System Neo-Capsule Liquid Glass:
 * 1. El juego nunca se pausa (isPauseScreen() / shouldPause() -> false).
 * 2. Fondo transparente en tiempo real (extractBackground transparente para ver shaders a 60+ FPS).
 * 3. Base difusa Glassmorphism (10-15%) con partículas flotantes orgánicas.
 * 4. Barra superior normalizada con píldoras de navegación activa (#06b6d4 glow).
 * 5. Cierre limpio con ESC hacia la pantalla anterior o al juego.
 * 6. Motor In-Game de Hot-Reload (F7 / Ctrl + R) e Inspector Visual (F6).
 */
public abstract class CmiScreen extends Screen {

    public static final String TAB_DISPLAY     = "display";
    public static final String TAB_ENCHANTS    = "enchants";
    public static final String TAB_ATTRIBUTES  = "attributes";
    public static final String TAB_KITS        = "kits";

    protected final Screen parentScreen;
    protected final int topBarHeight = 28;

    protected final List<CmiInspectorOverlay.Inspectable> inspectables = new ArrayList<>();
    protected final CmiInspectorOverlay inspector = new CmiInspectorOverlay(this);

    public CmiScreen(Component title) {
        this(title, null);
    }

    public CmiScreen(Component title, Screen parentScreen) {
        super(title);
        this.parentScreen = parentScreen;
    }

    public List<CmiInspectorOverlay.Inspectable> getInspectables() {
        return this.inspectables;
    }

    public CmiInspectorOverlay getInspector() {
        return this.inspector;
    }

    /**
     * Aplica las coordenadas y dimensiones de ui_layout.json y registra el widget para inspección y Live Tweaking.
     */
    public <T extends CmiButton> T applyAndRegisterLayout(String id, T button) {
        String screenName = this.getClass().getSimpleName();
        CmiUiLayoutManager.ComponentLayout layout = CmiUiLayoutManager.getComponent(screenName, id);

        if (layout != null) {
            button.setX(layout.x);
            button.setY(layout.y);
            button.setWidth(layout.width);
            button.setHeight(layout.height);
        } else {
            CmiUiLayoutManager.registerDefaultComponent(
                screenName,
                id,
                button.getX(),
                button.getY(),
                button.getWidth(),
                button.getHeight(),
                button.getVariant().name()
            );
        }

        this.inspectables.add(new CmiInspectorOverlay.Inspectable(
            id,
            screenName,
            button::getX,
            button::getY,
            button::getWidth,
            button::getHeight,
            button::setX,
            button::setY,
            button::setWidth,
            button::setHeight,
            button.getVariant().name(),
            false
        ));

        return button;
    }

    /**
     * Registra un contenedor, panel o columna para ser visualizado en el Inspector.
     */
    public void registerInspectableContainer(String id, int x, int y, int w, int h, String title) {
        final int[] bounds = new int[]{x, y, w, h};
        String screenName = this.getClass().getSimpleName();

        CmiUiLayoutManager.ComponentLayout layout = CmiUiLayoutManager.getComponent(screenName, id);
        if (layout != null) {
            bounds[0] = layout.x;
            bounds[1] = layout.y;
            bounds[2] = layout.width;
            bounds[3] = layout.height;
        } else {
            CmiUiLayoutManager.registerDefaultComponent(screenName, id, x, y, w, h, "CONTAINER");
        }

        this.inspectables.add(new CmiInspectorOverlay.Inspectable(
            id,
            screenName,
            () -> bounds[0],
            () -> bounds[1],
            () -> bounds[2],
            () -> bounds[3],
            val -> bounds[0] = val,
            val -> bounds[1] = val,
            val -> bounds[2] = val,
            val -> bounds[3] = val,
            "CONTAINER",
            true
        ));
    }

    /**
     * Reconstrucción segura en caliente de la pantalla activa.
     */
    public void rebuildScreenPreservingState() {
        saveScreenState();
        this.clearWidgets();
        this.inspectables.clear();
        this.init();
        restoreScreenState();
    }

    protected void saveScreenState() {}
    protected void restoreScreenState() {}

    /**
     * El juego no se pausa en singleplayer ni multiplayer.
     */
    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /**
     * Compatibilidad de especificación con el entorno Fabric Client.
     */
    public boolean shouldPause() {
        return false;
    }

    /**
     * Omitir el oscurecimiento o fondo de tierra por defecto de Minecraft.
     * Mantiene los shaders y el mundo de Minecraft en tiempo real a 60 FPS.
     */
    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Fondo transparente en tiempo real intencional.
    }

    /**
     * Renderizado principal: difuminado de cristal líquido, widgets y overlay del inspector.
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Difuminado difuso de cristal líquido sobre el mundo de bloques
        CmiUiTheme.drawScreenBackdrop(guiGraphics, this.width, this.height);

        // 2. Extraer render de los widgets hijos (cápsulas, botones, editores)
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // 3. Renderizar overlay de inspección visual en tiempo real
        if (this.inspector != null) {
            this.inspector.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    /**
     * Dibuja una columna interior estándar Neo-Capsule Liquid Glass.
     */
    protected void drawGlassColumn(GuiGraphicsExtractor guiGraphics, int x, int y, int w, int h, String title) {
        CmiUiTheme.drawGlassPanel(guiGraphics, x, y, w, h, CmiUiTheme.GLASS_PANEL_BORDER);

        if (title != null && !title.isEmpty()) {
            int headerH = 20;
            // Cabecera de la columna con borde inferior de 1px
            guiGraphics.fill(x + 1, y + 1, x + w - 1, y + headerH, CmiUiTheme.PANEL_HEADER_BG);
            guiGraphics.fill(x + 1, y + headerH, x + w - 1, y + headerH + 1, 0x4038BDF8);

            Font font = Minecraft.getInstance().font;
            guiGraphics.text(font, Component.literal(title), x + 8, y + 6, CmiUiTheme.COLOR_CYAN_NEON);
        }
    }

    /**
     * Cierra la pantalla actual y regresa al menú anterior o al juego.
     */
    public void closeScreen() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parentScreen);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDown) {
        if (this.inspector != null && this.inspector.mouseClicked(event, isDown)) {
            return true;
        }
        return super.mouseClicked(event, isDown);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // Atajo global de recarga en caliente: F7 o Ctrl + R
        boolean isCtrlR = (event.key() == GLFW.GLFW_KEY_R && (event.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0);
        if (event.key() == GLFW.GLFW_KEY_F7 || isCtrlR) {
            CmiUiLayoutManager.load();
            this.rebuildScreenPreservingState();
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.sendSystemMessage(Component.literal("§b[CMI LiveUI] §a⚡ Pantalla recargada desde ui_layout.json"));
            }
            return true;
        }

        // Delegar al modo Inspector (F6, flechas de movimiento, Ctrl + S)
        if (this.inspector != null && this.inspector.keyPressed(event)) {
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            closeScreen();
            return true;
        }
        return super.keyPressed(event);
    }

    public Screen getParentScreen() {
        return this.parentScreen;
    }
}