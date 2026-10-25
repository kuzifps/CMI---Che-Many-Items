package com.fxfps.cmi.client.gui;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

/**
 * Sistema Global de Pila de Historial (Undo / Redo) para CheManyItems (CMI) 💎
 * Administra instantáneas profundas de ítems, nombres, lores y configuraciones
 * para permitir retroceder (↩ Deshacer / Ctrl+Z) o avanzar (↪ Rehacer / Ctrl+Y).
 */
public class ItemStackHistory {

    public static class Snapshot {
        public final ItemStack stack;
        public final String customName;
        public final String rawLore;
        public final int count;
        public final boolean smallCaps;
        public final long timestamp;

        public Snapshot(ItemStack stack, String customName, String rawLore, int count, boolean smallCaps) {
            this.stack = (stack != null && !stack.isEmpty()) ? stack.copy() : ItemStack.EMPTY;
            this.customName = (customName != null) ? customName : "";
            this.rawLore = (rawLore != null) ? rawLore : "";
            this.count = Math.max(1, count);
            this.smallCaps = smallCaps;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isEquivalentTo(Snapshot other) {
            if (other == null) return false;
            if (this.count != other.count) return false;
            if (this.smallCaps != other.smallCaps) return false;
            if (!Objects.equals(this.customName, other.customName)) return false;
            if (!Objects.equals(this.rawLore, other.rawLore)) return false;
            return ItemStack.matches(this.stack, other.stack);
        }
    }

    private final Deque<Snapshot> undoStack = new ArrayDeque<>();
    private final Deque<Snapshot> redoStack = new ArrayDeque<>();
    private static final int MAX_DEPTH = 60;

    /**
     * Registra un nuevo estado en la pila de historial.
     * Si el estado es idéntico al actual en la cima, se ignora para evitar duplicados.
     */
    public void record(ItemStack stack, String customName, String rawLore, int count, boolean smallCaps) {
        Snapshot current = new Snapshot(stack, customName, rawLore, count, smallCaps);
        if (!undoStack.isEmpty() && undoStack.peek().isEquivalentTo(current)) {
            return;
        }
        undoStack.push(current);
        if (undoStack.size() > MAX_DEPTH) {
            ((ArrayDeque<Snapshot>) undoStack).removeLast();
        }
        redoStack.clear();
    }

    public boolean canUndo() {
        return undoStack.size() > 1;
    }

    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * Retrocede un paso en el historial.
     * @return El estado anterior restaurado, o null si no se puede deshacer.
     */
    public Snapshot undo() {
        if (!canUndo()) return null;
        Snapshot current = undoStack.pop();
        redoStack.push(current);
        return undoStack.peek();
    }

    /**
     * Avanza un paso deshecho en el historial.
     * @return El estado rehecho restaurado, o null si no se puede rehacer.
     */
    public Snapshot redo() {
        if (!canRedo()) return null;
        Snapshot next = redoStack.pop();
        undoStack.push(next);
        return next;
    }

    public void clear() {
        undoStack.clear();
        redoStack.clear();
    }

    public int getUndoCount() {
        return Math.max(0, undoStack.size() - 1);
    }

    public int getRedoCount() {
        return redoStack.size();
    }
}
