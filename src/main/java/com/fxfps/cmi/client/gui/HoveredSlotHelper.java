package com.fxfps.cmi.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Field;

/**
 * Utilidad de alto rendimiento para extraer el Slot y el ItemStack apuntados por el cursor
 * en cualquier pantalla de contenedor (Inventario, Creativo, Cofres, Shulkers, etc.).
 */
public class HoveredSlotHelper {

    private static Field hoveredSlotField;
    private static Field leftPosField;
    private static Field topPosField;

    static {
        try {
            hoveredSlotField = AbstractContainerScreen.class.getDeclaredField("hoveredSlot");
            hoveredSlotField.setAccessible(true);
        } catch (Exception e) {
            // Si el nombre fuera ofuscado en algún runtime, buscar por tipo Slot
            for (Field f : AbstractContainerScreen.class.getDeclaredFields()) {
                if (Slot.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    hoveredSlotField = f;
                    break;
                }
            }
        }

        try {
            leftPosField = AbstractContainerScreen.class.getDeclaredField("leftPos");
            leftPosField.setAccessible(true);
        } catch (Exception ignored) {}

        try {
            topPosField = AbstractContainerScreen.class.getDeclaredField("topPos");
            topPosField.setAccessible(true);
        } catch (Exception ignored) {}
    }

    /**
     * Retorna el Slot actualmente bajo el cursor en la pantalla dada, o null si no hay ninguno.
     */
    public static Slot getHoveredSlot(Screen screen) {
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return null;
        }

        // 1. Intentar obtener el slot directamente del campo hoveredSlot nativo de Minecraft
        if (hoveredSlotField != null) {
            try {
                Slot slot = (Slot) hoveredSlotField.get(containerScreen);
                if (slot != null) {
                    return slot;
                }
            } catch (Exception ignored) {}
        }

        // 2. Fallback de alta precisión calculando coordenadas del cursor del mouse sobre los slots
        try {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.mouseHandler != null && mc.getWindow() != null && containerScreen.getMenu() != null) {
                double mouseX = mc.mouseHandler.xpos() * (double) mc.getWindow().getGuiScaledWidth() / (double) mc.getWindow().getScreenWidth();
                double mouseY = mc.mouseHandler.ypos() * (double) mc.getWindow().getGuiScaledHeight() / (double) mc.getWindow().getScreenHeight();

                int left = 0;
                int top = 0;
                if (leftPosField != null && topPosField != null) {
                    left = leftPosField.getInt(containerScreen);
                    top = topPosField.getInt(containerScreen);
                } else {
                    left = (containerScreen.width - 176) / 2;
                    top = (containerScreen.height - 166) / 2;
                }

                for (Slot slot : containerScreen.getMenu().slots) {
                    if (slot != null && slot.isActive()) {
                        double sx = left + slot.x;
                        double sy = top + slot.y;
                        if (mouseX >= sx && mouseX < sx + 16 && mouseY >= sy && mouseY < sy + 16) {
                            return slot;
                        }
                    }
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    /**
     * Desenvuelve slots proxy o envoltorios (como CreativeModeInventoryScreen$SlotWrapper)
     * para obtener la ranura real subyacente en el inventario.
     */
    public static Slot unwrapSlot(Slot slot) {
        if (slot == null) return null;
        Slot current = slot;
        for (int depth = 0; depth < 5; depth++) {
            Slot inner = null;
            try {
                for (Field f : current.getClass().getDeclaredFields()) {
                    if (Slot.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        Slot s = (Slot) f.get(current);
                        if (s != null && s != current) {
                            inner = s;
                            break;
                        }
                    }
                }
            } catch (Exception ignored) {}
            if (inner != null) {
                current = inner;
            } else {
                break;
            }
        }
        return current;
    }

    /**
     * Retorna el ItemStack bajo el cursor, o ItemStack.EMPTY si no hay ranura o está vacía.
     */
    public static ItemStack getHoveredStack(Screen screen) {
        Slot slot = getHoveredSlot(screen);
        if (slot != null && slot.hasItem()) {
            return slot.getItem();
        }
        return ItemStack.EMPTY;
    }
}
