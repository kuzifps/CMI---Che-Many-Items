package com.fxfps.cmi.client.shulker;

import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;

import java.util.ArrayList;
import java.util.List;

/**
 * GESTOR DE EMPAQUETADO EN SHULKER BOX (16 TINTES) Y GENERADOR /give 📦
 *
 * Cumple con la Sección 8 de CMI_SISTEMA_MAESTRO_COMPLETO.md:
 * - Soporte para los 16 tintes oficiales de Shulker Box de Minecraft.
 * - Empaquetado instantáneo de 27 slots de contenedor (DataComponents.CONTAINER).
 * - Generador optimizado del comando /give con NBT anidado (BlockEntityTag & cmi_raw_lore).
 * - Entrega directa en mano o copia al portapapeles.
 */
public class ShulkerPackerManager {

    public record ShulkerColorInfo(String id, String displayName, int colorHex, Item item) {}

    public static final List<ShulkerColorInfo> ALL_COLORS = new ArrayList<>();

    static {
        ALL_COLORS.add(new ShulkerColorInfo("cyan", "Cyan", 0xFF00E5FF, Items.CYAN_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("white", "Blanco", 0xFFFFFFFF, Items.WHITE_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("orange", "Naranja", 0xFFFF9500, Items.ORANGE_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("magenta", "Magenta", 0xFFFF2D78, Items.MAGENTA_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("light_blue", "Celeste", 0xFF38BDF8, Items.LIGHT_BLUE_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("yellow", "Amarillo", 0xFFFFD60A, Items.YELLOW_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("lime", "Lima", 0xFF30D158, Items.LIME_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("pink", "Rosa", 0xFFFF375F, Items.PINK_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("gray", "Gris", 0xFF8E8E93, Items.GRAY_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("light_gray", "Gris Claro", 0xFFD1D1D6, Items.LIGHT_GRAY_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("purple", "Púrpura", 0xFFAF52DE, Items.PURPLE_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("blue", "Azul", 0xFF0A84FF, Items.BLUE_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("brown", "Marrón", 0xFFAC8E68, Items.BROWN_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("green", "Verde", 0xFF34C759, Items.GREEN_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("red", "Rojo", 0xFFFF3B30, Items.RED_SHULKER_BOX));
        ALL_COLORS.add(new ShulkerColorInfo("black", "Negro", 0xFF1C1C1E, Items.BLACK_SHULKER_BOX));
    }

    public static ShulkerColorInfo getColorInfo(String colorName) {
        if (colorName == null) colorName = "cyan";
        String clean = colorName.trim().toLowerCase();
        for (ShulkerColorInfo sci : ALL_COLORS) {
            if (sci.id().equalsIgnoreCase(clean) || sci.displayName().equalsIgnoreCase(clean)) {
                return sci;
            }
        }
        return ALL_COLORS.get(0); // Cyan por defecto
    }

    /**
     * Devuelve el ítem de Shulker Box correspondiente al color indicado.
     */
    public static ItemStack createShulkerBox(String colorName) {
        ShulkerColorInfo info = getColorInfo(colorName);
        return new ItemStack(info.item());
    }

    /**
     * Empaqueta una lista de ítems (hasta 27) en una Shulker Box del color especificado.
     */
    public static ItemStack packItemsIntoShulker(List<ItemStack> items, String boxTitle, String colorName) {
        ItemStack shulker = createShulkerBox(colorName);

        if (boxTitle != null && !boxTitle.trim().isEmpty()) {
            shulker.set(DataComponents.CUSTOM_NAME, Component.literal(boxTitle));
        }

        List<ItemStack> packed = new ArrayList<>();
        int max = Math.min(27, items != null ? items.size() : 0);
        for (int i = 0; i < max; i++) {
            ItemStack st = items.get(i);
            packed.add(st != null ? st.copy() : ItemStack.EMPTY);
        }

        shulker.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(packed));
        return shulker;
    }

    /**
     * Genera la cadena de comando /give para la Shulker Box con NBT anidado BlockEntityTag.
     */
    public static String generateGiveCommand(List<ItemStack> items, String boxTitle, String colorName) {
        ShulkerColorInfo info = getColorInfo(colorName);
        String title = (boxTitle != null && !boxTitle.isEmpty()) ? boxTitle : "Shulker Box CheManyItems";

        StringBuilder sb = new StringBuilder();
        sb.append("/give @p minecraft:").append(info.id()).append("_shulker_box{");
        sb.append("display:{Name:'{\"text\":\"").append(title.replace("\"", "\\\"")).append("\",\"color\":\"gold\",\"bold\":true}'},");
        sb.append("BlockEntityTag:{Items:[");

        int max = Math.min(27, items != null ? items.size() : 0);
        boolean first = true;
        for (int i = 0; i < max; i++) {
            ItemStack st = items.get(i);
            if (st == null || st.isEmpty()) continue;

            if (!first) sb.append(",");
            first = false;

            String itemId = BuiltInRegistries.ITEM.getKey(st.getItem()).toString();
            sb.append("{Slot:").append(i).append("b,id:\"").append(itemId).append("\",Count:").append(st.getCount()).append("b");

            StringBuilder tagSb = new StringBuilder();
            if (st.has(DataComponents.CUSTOM_NAME)) {
                String nameStr = st.getHoverName().getString();
                tagSb.append("display:{Name:'{\"text\":\"").append(nameStr.replace("\"", "\\\"")).append("\"}'}");
            }
            if (st.has(DataComponents.CUSTOM_DATA)) {
                CustomData cd = st.get(DataComponents.CUSTOM_DATA);
                if (cd != null) {
                    CompoundTag ctag = cd.copyTag();
                    if (ctag.contains("cmi_raw_lore")) {
                        if (tagSb.length() > 0) tagSb.append(",");
                        tagSb.append("cmi_raw_lore:\"").append(ctag.getString("cmi_raw_lore").orElse("").replace("\"", "\\\"").replace("\n", "\\n")).append("\"");
                    }
                }
            }
            if (tagSb.length() > 0) {
                sb.append(",tag:{").append(tagSb).append("}");
            }
            sb.append("}");
        }

        sb.append("]}} 1");
        return sb.toString();
    }

    /**
     * Entrega directamente la Shulker Box empaquetada en la mano principal del jugador.
     */
    public static boolean giveShulkerToPlayer(Minecraft client, List<ItemStack> items, String boxTitle, String colorName) {
        if (client == null || client.player == null) return false;

        ItemStack shulker = packItemsIntoShulker(items, boxTitle, colorName);
        var inv = client.player.getInventory();
        int sel = inv.getSelectedSlot();
        inv.setItem(sel, shulker.copy());
        client.player.setItemInHand(InteractionHand.MAIN_HAND, shulker.copy());
        inv.setChanged();

        if (client.player.hasInfiniteMaterials() && client.gameMode != null) {
            client.gameMode.handleCreativeModeItemAdd(shulker.copy(), 36 + sel);
        }

        return true;
    }
}
