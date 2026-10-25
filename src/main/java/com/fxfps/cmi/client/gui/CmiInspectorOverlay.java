package com.fxfps.cmi.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * Overlay del Modo Inspector y Live Tweaker In-Game para CheManyItems (CMI) 🇦🇷.
 *
 * Funcionalidades:
 * - Alternar con F6 en cualquier CmiScreen.
 * - Hitboxes de componentes (pasivo, hover con micro-badge, seleccionado con borde doble y manijas).
 * - Líneas guía proyectadas hacia los bordes de la pantalla con distancias en píxeles.
 * - HUD Flotante Neo-Capsule Liquid Glass siguiendo el cursor.
 * - Live Tweaker con flechas de dirección (1px / 5px con Shift) y Alt + Flechas para redimensionar.
 * - Guardado directo a ui_layout.json con Ctrl + S sin salir del juego.
 */
public class CmiInspectorOverlay {

    public static class Inspectable {
        public final String id;
        public final String screenName;
        private final IntSupplier getX;
        private final IntSupplier getY;
        private final IntSupplier getWidth;
        private final IntSupplier getHeight;
        private final IntConsumer setX;
        private final IntConsumer setY;
        private final IntConsumer setWidth;
        private final IntConsumer setHeight;
        public final String variant;
        public final boolean isContainer;

        public Inspectable(String id, String screenName,
                           IntSupplier getX, IntSupplier getY, IntSupplier getWidth, IntSupplier getHeight,
                           IntConsumer setX, IntConsumer setY, IntConsumer setWidth, IntConsumer setHeight,
                           String variant, boolean isContainer) {
            this.id = id;
            this.screenName = screenName;
            this.getX = getX;
            this.getY = getY;
            this.getWidth = getWidth;
            this.getHeight = getHeight;
            this.setX = setX;
            this.setY = setY;
            this.setWidth = setWidth;
            this.setHeight = setHeight;
            this.variant = (variant != null && !variant.isEmpty()) ? variant : "DEFAULT";
            this.isContainer = isContainer;
        }

        public int getX() { return getX.getAsInt(); }
        public int getY() { return getY.getAsInt(); }
        public int getWidth() { return getWidth.getAsInt(); }
        public int getHeight() { return getHeight.getAsInt(); }

        public void setX(int val) { setX.accept(val); }
        public void setY(int val) { setY.accept(val); }
        public void setWidth(int val) { setWidth.accept(val); }
        public void setHeight(int val) { setHeight.accept(val); }
    }

    private final CmiScreen screen;
    private boolean active = false;
    private Inspectable hovered = null;
    private Inspectable selected = null;

    private int toastTimer = 0;
    private String toastMessage = "";

    public CmiInspectorOverlay(CmiScreen screen) {
        this.screen = screen;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        if (!active) {
            this.selected = null;
            this.hovered = null;
        }
    }

    public void showToast(String message, int durationTicks) {
        this.toastMessage = message;
        this.toastTimer = durationTicks;
    }

    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (!active) return;

        List<Inspectable> list = screen.getInspectables();
        Font font = Minecraft.getInstance().font;

        // 1. Detectar componente hovered bajo el cursor (en orden inverso de registro)
        this.hovered = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            Inspectable ins = list.get(i);
            int x = ins.getX();
            int y = ins.getY();
            int w = ins.getWidth();
            int h = ins.getHeight();

            if (mouseX >= x && mouseX <= x + w && mouseY >= y && mouseY <= y + h) {
                this.hovered = ins;
                break;
            }
        }

        Inspectable targetGuide = (selected != null) ? selected : hovered;

        // 2. Líneas Guía de Alineación hacia los bordes de la pantalla
        if (targetGuide != null) {
            int gx = targetGuide.getX();
            int gy = targetGuide.getY();
            int gw = targetGuide.getWidth();
            int gh = targetGuide.getHeight();
            int midX = gx + gw / 2;
            int midY = gy + gh / 2;
            int guideColor = 0x4038BDF8;

            // Guía Izquierda
            guiGraphics.horizontalLine(0, gx, midY, guideColor);
            String distLeft = gx + "px";
            guiGraphics.text(font, distLeft, Math.max(4, gx / 2 - font.width(distLeft) / 2), midY - 10, 0xFF38BDF8);

            // Guía Derecha
            guiGraphics.horizontalLine(gx + gw, screen.width, midY, guideColor);
            int rightDistVal = screen.width - (gx + gw);
            String distRight = rightDistVal + "px";
            guiGraphics.text(font, distRight, (gx + gw) + Math.max(4, rightDistVal / 2 - font.width(distRight) / 2), midY - 10, 0xFF38BDF8);

            // Guía Superior
            guiGraphics.verticalLine(midX, 0, gy, guideColor);
            String distTop = gy + "px";
            guiGraphics.text(font, distTop, midX + 4, Math.max(2, gy / 2 - 4), 0xFF38BDF8);

            // Guía Inferior
            guiGraphics.verticalLine(midX, gy + gh, screen.height, guideColor);
            int bottomDistVal = screen.height - (gy + gh);
            String distBottom = bottomDistVal + "px";
            guiGraphics.text(font, distBottom, midX + 4, (gy + gh) + Math.max(2, bottomDistVal / 2 - 4), 0xFF38BDF8);
        }

        // 3. Dibujar Bounding Boxes para todos los inspectables
        for (Inspectable ins : list) {
            int x = ins.getX();
            int y = ins.getY();
            int w = ins.getWidth();
            int h = ins.getHeight();

            if (ins == selected) {
                // Seleccionado: Borde doble Esmeralda (#10b981) + fondo tintado + 4 manijas
                guiGraphics.fill(x, y, x + w, y + h, 0x2510B981);
                guiGraphics.outline(x, y, w, h, 0xFF10B981);
                guiGraphics.outline(x - 1, y - 1, w + 2, h + 2, 0x6010B981);

                // 4 Manijas blancas en las esquinas (4x4 px)
                guiGraphics.fill(x - 2, y - 2, x + 2, y + 2, 0xFFFFFFFF);
                guiGraphics.fill(x + w - 2, y - 2, x + w + 2, y + 2, 0xFFFFFFFF);
                guiGraphics.fill(x - 2, y + h - 2, x + 2, y + h + 2, 0xFFFFFFFF);
                guiGraphics.fill(x + w - 2, y + h - 2, x + w + 2, y + h + 2, 0xFFFFFFFF);

                // Badge superior de seleccionado
                String badge = "✔ " + ins.id + " [" + x + "," + y + " | " + w + "x" + h + "]";
                int badgeW = font.width(badge) + 8;
                int badgeY = Math.max(2, y - 12);
                guiGraphics.fill(x, badgeY, x + badgeW, badgeY + 11, 0xE0064E3B);
                guiGraphics.outline(x, badgeY, badgeW, 11, 0xFF10B981);
                guiGraphics.text(font, badge, x + 4, badgeY + 2, 0xFFFFFFFF);

            } else if (ins == hovered) {
                // Hover: Borde Cyan Neón (#38bdf8) + micro-badge
                guiGraphics.fill(x, y, x + w, y + h, 0x2038BDF8);
                guiGraphics.outline(x, y, w, h, 0xFF38BDF8);

                String badge = ins.id + " (" + w + "x" + h + ")";
                int badgeW = font.width(badge) + 6;
                int badgeY = Math.max(2, y - 11);
                guiGraphics.fill(x, badgeY, x + badgeW, badgeY + 10, 0xEE0A1424);
                guiGraphics.outline(x, badgeY, badgeW, 10, 0x8038BDF8);
                guiGraphics.text(font, badge, x + 3, badgeY + 1, 0xFF38BDF8);

            } else {
                // Pasivo: Borde sutil traslúcido
                int passColor = ins.isContainer ? 0x40F59E0B : 0x4038BDF8;
                guiGraphics.outline(x, y, w, h, passColor);
            }
        }

        // 4. HUD Flotante Neo-Capsule Liquid Glass
        int hudW = 216;
        int hudH = 92;
        int hudX = mouseX + 14;
        int hudY = mouseY + 14;

        if (hudX + hudW > screen.width - 4) hudX = mouseX - hudW - 8;
        if (hudY + hudH > screen.height - 4) hudY = mouseY - hudH - 8;
        if (hudX < 4) hudX = 4;
        if (hudY < 4) hudY = 4;

        CmiUiTheme.drawGlassPanel(guiGraphics, hudX, hudY, hudW, hudH, CmiUiTheme.COLOR_CYAN_NEON);

        guiGraphics.text(font, Component.literal("§b§lCMI UI INSPECTOR §a[ACTIVO]"), hudX + 8, hudY + 6, 0xFFFFFFFF);
        guiGraphics.fill(hudX + 6, hudY + 17, hudX + hudW - 6, hudY + 18, 0x4038BDF8);

        Inspectable displayTarget = (selected != null) ? selected : hovered;
        if (displayTarget != null) {
            guiGraphics.text(font, Component.literal("§7ID: §f" + displayTarget.id), hudX + 8, hudY + 22, 0xFFFFFFFF);
            guiGraphics.text(font, Component.literal("§7Pos: §eX=" + displayTarget.getX() + "  Y=" + displayTarget.getY()), hudX + 8, hudY + 34, 0xFFFFFFFF);
            guiGraphics.text(font, Component.literal("§7Dim: §aW=" + displayTarget.getWidth() + "  H=" + displayTarget.getHeight()), hudX + 8, hudY + 46, 0xFFFFFFFF);
            guiGraphics.text(font, Component.literal("§7Tema: §d" + displayTarget.variant), hudX + 8, hudY + 58, 0xFFFFFFFF);
        } else {
            guiGraphics.text(font, Component.literal("§7Pasa el cursor o haz clic"), hudX + 8, hudY + 28, 0xFF94A3B8);
            guiGraphics.text(font, Component.literal("§7para seleccionar un elemento"), hudX + 8, hudY + 42, 0xFF94A3B8);
        }

        guiGraphics.fill(hudX + 6, hudY + 70, hudX + hudW - 6, hudY + 71, 0x3038BDF8);
        guiGraphics.text(font, Component.literal("§8[Flechas] Mover · [Alt] Tam · [Ctrl+S] Guardar"), hudX + 8, hudY + 73, 0xFF94A3B8);
        guiGraphics.text(font, Component.literal("§8[Shift] x5px · [F6] Salir del Inspector"), hudX + 8, hudY + 82, 0xFF94A3B8);

        // 5. Banner Toast de Notificación
        if (toastTimer > 0) {
            toastTimer--;
            int bannerW = font.width(toastMessage) + 24;
            int bannerX = (screen.width - bannerW) / 2;
            int bannerY = 8;
            CmiUiTheme.drawGlassPanel(guiGraphics, bannerX, bannerY, bannerW, 20, 0xFF10B981);
            guiGraphics.centeredText(font, Component.literal(toastMessage), screen.width / 2, bannerY + 6, 0xFFFFFFFF);
        }
    }

    public boolean mouseClicked(MouseButtonEvent event, boolean isDown) {
        if (!active) return false;

        if (isDown && event.button() == 0) {
            if (this.hovered != null) {
                this.selected = this.hovered;
                showToast("§aElemento seleccionado: " + selected.id, 50);
                AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
                return true; // Consumir evento para que no active el botón inspeccionado
            } else {
                this.selected = null;
            }
        }
        return false;
    }

    public boolean keyPressed(KeyEvent event) {
        // F6: Alternar Inspector
        if (event.key() == GLFW.GLFW_KEY_F6) {
            setActive(!active);
            if (active) {
                showToast("§a[CMI Inspector] ACTIVADO §7(F6 para salir)", 60);
            } else {
                showToast("§c[CMI Inspector] DESACTIVADO", 40);
            }
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        if (!active) return false;

        // Ctrl + S: Guardar cambios directamente a ui_layout.json
        if (event.key() == GLFW.GLFW_KEY_S && (event.modifiers() & GLFW.GLFW_MOD_CONTROL) != 0) {
            for (Inspectable ins : screen.getInspectables()) {
                CmiUiLayoutManager.updateComponentLayout(
                    ins.screenName,
                    ins.id,
                    ins.getX(),
                    ins.getY(),
                    ins.getWidth(),
                    ins.getHeight(),
                    ins.variant
                );
            }
            CmiUiLayoutManager.saveToFile();
            showToast("§a✔ Layout guardado en ui_layout.json", 70);
            AbstractWidget.playButtonClickSound(Minecraft.getInstance().getSoundManager());

            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                client.player.sendSystemMessage(Component.literal("§b[CMI LiveUI] §a✔ Coordenadas guardadas en config/cmi/ui_layout.json"));
            }
            return true;
        }

        // Ajuste con Flechas de Dirección
        if (selected != null) {
            boolean resize = (event.modifiers() & GLFW.GLFW_MOD_ALT) != 0;
            int step = (event.modifiers() & GLFW.GLFW_MOD_SHIFT) != 0 ? 5 : 1;

            if (event.key() == GLFW.GLFW_KEY_LEFT) {
                if (resize) selected.setWidth(Math.max(8, selected.getWidth() - step));
                else selected.setX(selected.getX() - step);
                return true;
            } else if (event.key() == GLFW.GLFW_KEY_RIGHT) {
                if (resize) selected.setWidth(selected.getWidth() + step);
                else selected.setX(selected.getX() + step);
                return true;
            } else if (event.key() == GLFW.GLFW_KEY_UP) {
                if (resize) selected.setHeight(Math.max(8, selected.getHeight() - step));
                else selected.setY(selected.getY() - step);
                return true;
            } else if (event.key() == GLFW.GLFW_KEY_DOWN) {
                if (resize) selected.setHeight(selected.getHeight() + step);
                else selected.setY(selected.getY() + step);
                return true;
            }
        }

        return false;
    }
}