package com.fxfps.cmi.client.kits;

import com.fxfps.cmi.CmiMod;
import com.fxfps.cmi.client.config.CmiConfigManager;
import com.fxfps.cmi.client.fonts.UnicodeFontHelper;
import com.fxfps.cmi.client.lores.ArgNetLoreManager;
import com.fxfps.cmi.client.shulker.ShulkerPackerManager;
import com.google.gson.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Gestor Maestro de Kits de CheManyItems (CMI) ðŸ‡¦ðŸ‡·
 * Cumple al 100% con la especificaciÃ³n cmi_sistema_maestro_completo.md:
 * - 0 Cooldowns (0 Segundos): Carga y guardado instantÃ¡neos en cualquier momento.
 * - Sin Bloqueos de Rango: Acceso ilimitado a todos los kits.
 * - Persistencia Dual: %appdata%/.minecraft/config/cmi/kits.json y NBT cmi_raw_lore.
 * - Empaquetador de Shulker Box de 16 tintes y generador del comando /give.
 * - AutoLore dinÃ¡mico de lote para todo el kit.
 */
public class CmiKitManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, KitData> KITS = new LinkedHashMap<>();
    private static String activeKitId = "kit_combate";
    private static String selectedShulkerColor = "cyan";

    public static final String[] SHULKER_COLORS = {
            "cyan", "white", "orange", "magenta", "light_blue",
            "yellow", "lime", "pink", "gray", "light_gray",
            "purple", "blue", "brown", "green", "red", "black"
    };

    public static class ToolBadge {
        public String icon;
        public String label;
        public String color;

        public ToolBadge(String icon, String label, String color) {
            this.icon = icon;
            this.label = label;
            this.color = color;
        }
    }

    public static class KitData {
        public String id;
        public String name;
        public String category; // Espadas, Herramientas, Armaduras, Arcos, Pociones, Bloques
        public String icon;
        public List<ToolBadge> toolBadges = new ArrayList<>();
        public String autoLoreTemplate;
        public Map<String, String> autoLoreTemplates = new LinkedHashMap<>();
        public boolean smallCaps = true;
        public int itemCount = 36;
        public List<ItemStack> items = new ArrayList<>();

        public KitData(String id, String name, String category, String icon, String autoLoreTemplate) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.icon = icon;
            this.autoLoreTemplate = (autoLoreTemplate != null) ? autoLoreTemplate : "";
            this.autoLoreTemplates.put("1", this.autoLoreTemplate);
            this.autoLoreTemplates.put("2", "&6✦ " + name.toUpperCase() + " SECUNDARIO ✦\n<enchants>\n<customenchants>\n&8AutoLore Personal · CMI");
            this.autoLoreTemplates.put("3", "&b✦ LOADOUT RUSH ✦\n<enchants>\n<customenchants>\n<atributes>\n&7Argentina Network");
        }

        public String getAutoLoreTemplate(int index) {
            String key = String.valueOf(index);
            if (autoLoreTemplates.containsKey(key)) {
                return autoLoreTemplates.get(key);
            }
            if (index == 1 && autoLoreTemplate != null && !autoLoreTemplate.isEmpty()) {
                return autoLoreTemplate;
            }
            return "&6✦ " + name.toUpperCase() + " #" + index + " ✦\n<enchants>\n<customenchants>\n&8Loadout CMI";
        }

        public void setAutoLoreTemplate(int index, String template) {
            String clean = (template != null) ? template : "";
            this.autoLoreTemplates.put(String.valueOf(index), clean);
            if (index == 1) {
                this.autoLoreTemplate = clean;
            }
        }
    }

    static {
        init();
    }

    public static void init() {
        KITS.clear();
        loadKits();
        if (KITS.isEmpty()) {
            createDefaultOfficialKits();
            saveKits();
        }
        if (!KITS.containsKey(activeKitId) && !KITS.isEmpty()) {
            activeKitId = KITS.keySet().iterator().next();
        }
    }

    public static String getActiveKitName() {
        KitData kd = KITS.get(activeKitId);
        return kd != null ? kd.name : activeKitId;
    }

    public static String getActiveKitId() {
        return activeKitId;
    }

    public static void setActiveKitName(String idOrName) {
        if (idOrName == null || idOrName.trim().isEmpty()) return;
        String trimmed = idOrName.trim();
        for (KitData kd : KITS.values()) {
            if (kd.id.equalsIgnoreCase(trimmed) || kd.name.equalsIgnoreCase(trimmed)) {
                activeKitId = kd.id;
                return;
            }
        }
        // Si no existe, crear nuevo
        String safeId = trimmed.toLowerCase().replace(' ', '_');
        KitData newKit = new KitData(safeId, trimmed, "Espadas", "netherite_sword", "&6âœ¦ " + trimmed.toUpperCase() + " âœ¦\n<autolore>\n&8Loadout Personal Â· CheManyItems");
        newKit.toolBadges.add(new ToolBadge("netherite_sword", "Espadas", "#38bdf8"));
        newKit.toolBadges.add(new ToolBadge("chestplate", "Kit Personal", "#a855f7"));
        KITS.put(safeId, newKit);
        activeKitId = safeId;
        saveKits();
    }

    public static KitData getActiveKit() {
        KitData kd = KITS.get(activeKitId);
        if (kd == null && !KITS.isEmpty()) {
            kd = KITS.values().iterator().next();
            activeKitId = kd.id;
        }
        return kd;
    }

    public static KitData getKit(String id) {
        if (id == null) return getActiveKit();
        KitData kd = KITS.get(id);
        if (kd == null) {
            for (KitData k : KITS.values()) {
                if (k.name.equalsIgnoreCase(id)) return k;
            }
        }
        return kd != null ? kd : getActiveKit();
    }

    /**
     * Resuelve de forma dinámica e interoperable cualquier tag <autolore>, <autolore1>, <autolore2>, etc.
     * vinculándolo con la plantilla correspondiente del kit activo.
     */
    public static String resolveAutoLoreTag(String tag) {
        if (tag == null || tag.isEmpty()) return "";
        String clean = tag.toLowerCase(Locale.ROOT).trim();
        int index = 1;
        if (clean.startsWith("<autolore") && clean.endsWith(">")) {
            String numPart = clean.substring("<autolore".length(), clean.length() - 1).trim();
            if (!numPart.isEmpty()) {
                try {
                    index = Integer.parseInt(numPart);
                } catch (Exception ignored) {}
            }
        }
        KitData active = getActiveKit();
        if (active != null) {
            return active.getAutoLoreTemplate(index);
        }
        return "&6✦ EXCALIBUR DEL CHE ✦\n<enchants>\n<customenchants>\n<atributes>\n&8Loadout Personal · CheManyItems";
    }

    /**
     * Sistema de Auto-Rename Masivo de Kits (REGLA NÚCLEO 6):
     * Renombra instantáneamente todas las piezas del kit (armadura, offhand e inventario) en 1 solo clic.
     * Soporta variables dinámicas {item}, {kit}, códigos de color & y gradientes <gradient:#c1:#c2>.
     */
    public static int massAutoRenameKit(String kitId, String template) {
        KitData kd = getKit(kitId);
        if (kd == null || kd.items == null || kd.items.isEmpty()) return 0;
        String tpl = (template != null && !template.trim().isEmpty()) ? template.trim() : "&6&l{kit} · &f{item}";

        int renamedCount = 0;
        for (ItemStack stack : kd.items) {
            if (stack != null && !stack.isEmpty()) {
                String vanillaName = stack.getItem().getName(stack).getString();
                String customName = tpl.replace("{kit}", kd.name).replace("{item}", vanillaName);
                stack.set(DataComponents.CUSTOM_NAME, UnicodeFontHelper.parseFormattedComponent(customName));
                renamedCount++;
            }
        }
        saveKits();
        return renamedCount;
    }

    public static List<KitData> getAllKits() {
        return new ArrayList<>(KITS.values());
    }

    public static List<String> getKitNames() {
        List<String> names = new ArrayList<>();
        for (KitData kd : KITS.values()) {
            names.add(kd.name);
        }
        return names;
    }

    public static List<ItemStack> getKitItems(String idOrName) {
        KitData kd = getKit(idOrName);
        return kd != null ? kd.items : Collections.emptyList();
    }

    public static String getSelectedShulkerColor() {
        return selectedShulkerColor;
    }

    public static void setSelectedShulkerColor(String color) {
        if (color != null && !color.isEmpty()) {
            selectedShulkerColor = color.toLowerCase();
        }
    }

    public static void cycleShulkerColor() {
        for (int i = 0; i < SHULKER_COLORS.length; i++) {
            if (SHULKER_COLORS[i].equalsIgnoreCase(selectedShulkerColor)) {
                selectedShulkerColor = SHULKER_COLORS[(i + 1) % SHULKER_COLORS.length];
                return;
            }
        }
        selectedShulkerColor = "cyan";
    }

    public static void addItemToKit(String kitId, ItemStack item) {
        if (item == null || item.isEmpty()) return;
        KitData kd = getKit(kitId);
        if (kd != null) {
            kd.items.add(item.copy());
            kd.itemCount = kd.items.size();
            saveKits();
        }
    }

    public static void removeItemFromKit(String kitId, int index) {
        KitData kd = getKit(kitId);
        if (kd != null && index >= 0 && index < kd.items.size()) {
            kd.items.remove(index);
            kd.itemCount = kd.items.size();
            saveKits();
        }
    }

    public static void updateItemInKit(String kitId, int index, ItemStack newStack) {
        KitData kd = getKit(kitId);
        if (kd != null && index >= 0 && index < kd.items.size() && newStack != null) {
            kd.items.set(index, newStack.copy());
            saveKits();
        }
    }

    public static boolean renameKit(String oldIdOrName, String newName) {
        if (newName == null || newName.trim().isEmpty()) return false;
        KitData kd = getKit(oldIdOrName);
        if (kd != null) {
            kd.name = newName.trim();
            saveKits();
            return true;
        }
        return false;
    }

    public static String cloneKit(String kitId) {
        KitData kd = getKit(kitId);
        if (kd == null) return activeKitId;

        String cloneId = kd.id + "_copia";
        String cloneName = kd.name + " (Copia)";
        int c = 2;
        while (KITS.containsKey(cloneId)) {
            cloneId = kd.id + "_copia" + c;
            cloneName = kd.name + " (Copia " + c + ")";
            c++;
        }

        KitData clone = new KitData(cloneId, cloneName, kd.category, kd.icon, kd.autoLoreTemplate);
        for (ToolBadge tb : kd.toolBadges) {
            clone.toolBadges.add(new ToolBadge(tb.icon, tb.label, tb.color));
        }
        for (ItemStack st : kd.items) {
            clone.items.add(st.copy());
        }
        clone.itemCount = clone.items.size();
        KITS.put(cloneId, clone);
        activeKitId = cloneId;
        saveKits();
        return cloneName;
    }

    public static void deleteKit(String kitId) {
        KitData kd = getKit(kitId);
        if (kd != null) {
            KITS.remove(kd.id);
            if (KITS.isEmpty()) {
                createDefaultOfficialKits();
            }
            activeKitId = KITS.keySet().iterator().next();
            saveKits();
        }
    }

    public static void saveFullKit(String kitId, List<ItemStack> items) {
        KitData kd = getKit(kitId);
        if (kd != null) {
            kd.items.clear();
            if (items != null) {
                for (ItemStack st : items) {
                    if (st != null && !st.isEmpty()) {
                        kd.items.add(st.copy());
                    }
                }
            }
            kd.itemCount = kd.items.size();
            saveKits();
        }
    }

    // =========================================================================
    // ACCIONES EN JUEGO: EQUIPAR INSTANTÃNEO (0s COOLDOWN)
    // =========================================================================
    public static void equipKitToInventory(Minecraft client, String kitId) {
        if (client == null || client.player == null || client.gameMode == null) return;
        KitData kd = getKit(kitId);
        if (kd == null || kd.items.isEmpty()) {
            client.player.sendSystemMessage(Component.literal("Â§c[CMI] El kit estÃ¡ vacÃ­o."));
            return;
        }

        int slotIdx = 9; // Slots 9-35 de inventario principal
        int hotbarIdx = 36; // Slots 36-44 de hotbar

        for (ItemStack st : kd.items) {
            ItemStack copy = st.copy();
            // Detectar si es armadura y equipar en ranuras nativas si estÃ¡n libres
            String path = BuiltInRegistries.ITEM.getKey(copy.getItem()).getPath();
            if (path.contains("helmet") && client.player.getInventory().getItem(39).isEmpty()) {
                client.gameMode.handleCreativeModeItemAdd(copy, 5);
            } else if (path.contains("chestplate") && client.player.getInventory().getItem(38).isEmpty()) {
                client.gameMode.handleCreativeModeItemAdd(copy, 6);
            } else if (path.contains("leggings") && client.player.getInventory().getItem(37).isEmpty()) {
                client.gameMode.handleCreativeModeItemAdd(copy, 7);
            } else if (path.contains("boots") && client.player.getInventory().getItem(36).isEmpty()) {
                client.gameMode.handleCreativeModeItemAdd(copy, 8);
            } else if (copy.is(Items.SHIELD) && client.player.getInventory().getItem(40).isEmpty()) {
                client.gameMode.handleCreativeModeItemAdd(copy, 45); // Offhand
            } else if (hotbarIdx <= 44) {
                client.gameMode.handleCreativeModeItemAdd(copy, hotbarIdx++);
            } else if (slotIdx <= 35) {
                client.gameMode.handleCreativeModeItemAdd(copy, slotIdx++);
            }
        }

        client.player.sendSystemMessage(
                Component.literal("Â§a[CMI] âš¡ Â¡Kit Â§6" + kd.name + " Â§aequipado instantÃ¡neamente en tu inventario (0s Cooldown)!")
        );
    }

    // =========================================================================
    // EMPAQUETADO EN SHULKER BOX (16 TINTES)
    // =========================================================================
    public static void giveKitShulkerBox(Minecraft client, String kitId) {
        giveKitShulkerBox(client, kitId, selectedShulkerColor);
    }

    public static void giveCustomShulkerBox(Minecraft client, String name, List<ItemStack> slots, String color) {
        if (client == null || client.player == null || client.gameMode == null) return;
        if (slots == null || slots.isEmpty()) return;
        if (color == null || color.isEmpty()) color = selectedShulkerColor;

        ItemStack shulker = ShulkerPackerManager.createShulkerBox(color);
        shulker.set(DataComponents.CUSTOM_NAME, Component.literal("§6§l" + name));

        List<ItemStack> packed = new ArrayList<>();
        int max = Math.min(27, slots.size());
        for (int i = 0; i < max; i++) {
            ItemStack s = slots.get(i);
            packed.add((s != null) ? s.copy() : ItemStack.EMPTY);
        }
        shulker.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(packed));

        int heldSlot = client.player.getInventory().getSelectedSlot() + 36;
        client.gameMode.handleCreativeModeItemAdd(shulker, heldSlot);

        client.player.sendSystemMessage(
                Component.literal("§a[CMI] 📦 ¡Shulker Box §b" + color + " §acon disposición manual entregada en tu mano!")
        );
    }

    public static String generateGiveCustomShulkerCommand(String name, List<ItemStack> slots, String color) {
        if (slots == null) return "";
        if (color == null || color.isEmpty()) color = selectedShulkerColor;

        StringBuilder sb = new StringBuilder();
        sb.append("/give @p minecraft:").append(color.toLowerCase()).append("_shulker_box{");
        sb.append("display:{Name:'{\"text\":\"").append(name.replace("\"", "\\\"")).append("\",\"color\":\"gold\",\"bold\":true}'},");
        sb.append("BlockEntityTag:{Items:[");

        boolean first = true;
        int max = Math.min(27, slots.size());
        for (int i = 0; i < max; i++) {
            ItemStack st = slots.get(i);
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
            String rawLore = ArgNetLoreManager.getRawLore(st);
            if (rawLore != null && !rawLore.isEmpty()) {
                if (!tagSb.isEmpty()) tagSb.append(",");
                tagSb.append("cmi_raw_lore:\"").append(rawLore.replace("\"", "\\\"").replace("\n", "\\n")).append("\"");
            }
            if (!tagSb.isEmpty()) {
                sb.append(",tag:{").append(tagSb).append("}");
            }
            sb.append("}");
        }
        sb.append("]}} 1");
        return sb.toString();
    }

    public static void giveKitShulkerBox(Minecraft client, String kitId, String color) {
        if (client == null || client.player == null || client.gameMode == null) return;
        KitData kd = getKit(kitId);
        if (kd == null || kd.items.isEmpty()) {
            client.player.sendSystemMessage(Component.literal("Â§c[CMI] El kit estÃ¡ vacÃ­o."));
            return;
        }

        ItemStack shulker = ShulkerPackerManager.createShulkerBox(color);
        shulker.set(DataComponents.CUSTOM_NAME, Component.literal("Â§6Â§l" + kd.name));

        // Empaquetar hasta 27 Ã­tems en el contenedor de la Shulker
        List<ItemStack> packed = new ArrayList<>();
        int max = Math.min(27, kd.items.size());
        for (int i = 0; i < max; i++) {
            packed.add(kd.items.get(i).copy());
        }
        shulker.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(packed));

        int heldSlot = client.player.getInventory().getSelectedSlot() + 36;
        client.gameMode.handleCreativeModeItemAdd(shulker, heldSlot);

        client.player.sendSystemMessage(
                Component.literal("Â§a[CMI] ðŸ“¦ Â¡Kit Â§6" + kd.name + " Â§aempaquetado en una Shulker Box Â§b" + color + " Â§aentregada en tu mano!")
        );
    }

    // =========================================================================
    // GENERADOR DE COMANDO /give PARA SHULKER CON NBT ANIDADO
    // =========================================================================
    public static String generateGiveShulkerCommand(String kitId, String color) {
        KitData kd = getKit(kitId);
        if (kd == null) return "";
        if (color == null || color.isEmpty()) color = selectedShulkerColor;

        StringBuilder sb = new StringBuilder();
        sb.append("/give @p minecraft:").append(color.toLowerCase()).append("_shulker_box{");
        sb.append("display:{Name:'{\"text\":\"").append(kd.name.replace("\"", "\\\"")).append("\",\"color\":\"gold\",\"bold\":true}'},");
        sb.append("BlockEntityTag:{Items:[");

        int max = Math.min(27, kd.items.size());
        for (int i = 0; i < max; i++) {
            ItemStack st = kd.items.get(i);
            if (i > 0) sb.append(",");
            String itemId = BuiltInRegistries.ITEM.getKey(st.getItem()).toString();
            sb.append("{Slot:").append(i).append("b,id:\"").append(itemId).append("\",Count:").append(st.getCount()).append("b");

            // Tags internos
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

    public static void copyGiveShulkerCommand(Minecraft client, String kitId, String color) {
        String cmd = generateGiveShulkerCommand(kitId, color);
        if (client != null && !cmd.isEmpty()) {
            client.keyboardHandler.setClipboard(cmd);
            if (client.player != null) {
                client.player.sendSystemMessage(
                        Component.literal("Â§a[CMI] ðŸ“‹ Â¡Comando Â§6/give Â§ade Shulker Box copiado exitosamente al portapapeles!")
                );
            }
        }
    }

    // =========================================================================
    // APLICAR AUTOLORE DINÃMICO A TODO EL KIT (36 SLOTS)
    // =========================================================================
    public static void applyAutoLoreToKit(String kitId, String template) {
        applyAutoLoreToKit(kitId, template, true);
    }

    public static void applyAutoLoreToKit(String kitId, String template, boolean smallCaps) {
        KitData kd = getKit(kitId);
        if (kd == null || kd.items.isEmpty()) return;
        if (template == null || template.trim().isEmpty()) {
            template = kd.autoLoreTemplate;
        }
        if (template == null || template.trim().isEmpty()) return;

        kd.autoLoreTemplate = template;
        for (ItemStack st : kd.items) {
            if (st == null || st.isEmpty()) continue;
            ArgNetLoreManager.compileAndApplyLore(st, template, smallCaps);
        }
        saveKits();
    }

    // =========================================================================
    // AUTO-RENAME MASIVO DE TODO EL KIT (36 SLOTS)
    // =========================================================================
    public static int autoRenameKit(String kitId, String template) {
        KitData kd = getKit(kitId);
        if (kd == null || kd.items.isEmpty()) return 0;
        if (template == null || template.trim().isEmpty()) {
            template = "&6✦ {kit} &7· &b{item}";
        }

        int count = 0;
        for (int i = 0; i < kd.items.size(); i++) {
            ItemStack st = kd.items.get(i);
            if (st == null || st.isEmpty()) continue;

            String baseName = st.getItem().getName(st).getString();
            String resolved = template
                    .replace("{kit}", kd.name != null ? kd.name : kitId)
                    .replace("{item}", baseName)
                    .replace("{index}", String.valueOf(i + 1))
                    .replace("{slot}", String.valueOf(i + 1));

            Component comp = UnicodeFontHelper.parseFormattedComponent(resolved);
            st.set(DataComponents.CUSTOM_NAME, comp);
            count++;
        }
        saveKits();
        return count;
    }

    // =========================================================================
    // PERSISTENCIA EN %appdata%/.minecraft/config/cmi/kits.json
    // =========================================================================
    public static Path getKitsJsonPath() {
        Path configDir = CmiConfigManager.getConfigDir();
        return (configDir != null) ? configDir.resolve("kits.json") : null;
    }

    public static void saveKits() {
        Path path = getKitsJsonPath();
        if (path == null) return;

        try {
            Files.createDirectories(path.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("mod", "CheManyItems");
            root.addProperty("version", "2.5.0");
            root.addProperty("texturePack", "Argentina Network (925 Glyphs PUA)");
            root.addProperty("enableFastEquip", true);
            root.addProperty("enableShulkerGifting", true);
            root.addProperty("zeroCooldowns", true);
            root.addProperty("badgesType", "tools_and_items_only");
            root.addProperty("smallCapsDefault", true);
            root.addProperty("liveBackgroundMode", "minecraft_realtime_canvas");

            JsonArray kitsArr = new JsonArray();
            for (KitData kd : KITS.values()) {
                JsonObject kObj = new JsonObject();
                kObj.addProperty("id", kd.id);
                kObj.addProperty("name", kd.name);
                kObj.addProperty("category", kd.category);
                kObj.addProperty("icon", kd.icon);

                JsonArray badgesArr = new JsonArray();
                for (ToolBadge tb : kd.toolBadges) {
                    JsonObject bObj = new JsonObject();
                    bObj.addProperty("icon", tb.icon);
                    bObj.addProperty("label", tb.label);
                    bObj.addProperty("color", tb.color);
                    badgesArr.add(bObj);
                }
                kObj.add("toolBadges", badgesArr);
                kObj.addProperty("autoLoreTemplate", kd.autoLoreTemplate != null ? kd.autoLoreTemplate : "");
                kObj.addProperty("smallCaps", kd.smallCaps);
                kObj.addProperty("itemCount", kd.items.size());

                JsonObject tmplObj = new JsonObject();
                for (var e : kd.autoLoreTemplates.entrySet()) {
                    tmplObj.addProperty(e.getKey(), e.getValue());
                }
                kObj.add("autoLoreTemplates", tmplObj);

                JsonArray itemsArr = new JsonArray();
                for (int i = 0; i < kd.items.size(); i++) {
                    ItemStack st = kd.items.get(i);
                    JsonObject itObj = serializeItemStack(st, i);
                    itemsArr.add(itObj);
                }
                kObj.add("items", itemsArr);
                kitsArr.add(kObj);
            }
            root.add("kits", kitsArr);

            try (FileWriter writer = new FileWriter(path.toFile())) {
                GSON.toJson(root, writer);
            }
        } catch (Exception e) {
            CmiMod.LOGGER.error("Error al guardar kits.json", e);
        }
    }

    public static void loadKits() {
        Path path = getKitsJsonPath();
        if (path == null || !Files.exists(path)) {
            // Intentar migrar desde archivos individuales antiguos
            migrateOldIndividualKits();
            return;
        }

        try (FileReader reader = new FileReader(path.toFile())) {
            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            if (root == null || !root.has("kits")) return;

            JsonArray arr = root.getAsJsonArray("kits");
            for (JsonElement el : arr) {
                JsonObject kObj = el.getAsJsonObject();
                String id = kObj.has("id") ? kObj.get("id").getAsString() : "kit_1";
                String name = kObj.has("name") ? kObj.get("name").getAsString() : id;
                String category = kObj.has("category") ? kObj.get("category").getAsString() : "Espadas";
                String icon = kObj.has("icon") ? kObj.get("icon").getAsString() : "netherite_sword";
                String template = kObj.has("autoLoreTemplate") ? kObj.get("autoLoreTemplate").getAsString() : "";

                KitData kd = new KitData(id, name, category, icon, template);
                if (kObj.has("smallCaps")) {
                    kd.smallCaps = kObj.get("smallCaps").getAsBoolean();
                }
                if (kObj.has("autoLoreTemplates")) {
                    JsonObject tObj = kObj.getAsJsonObject("autoLoreTemplates");
                    for (String key : tObj.keySet()) {
                        kd.autoLoreTemplates.put(key, tObj.get(key).getAsString());
                    }
                    if (kd.autoLoreTemplates.containsKey("1")) {
                        kd.autoLoreTemplate = kd.autoLoreTemplates.get("1");
                    }
                }
                if (kObj.has("toolBadges")) {
                    for (JsonElement bEl : kObj.getAsJsonArray("toolBadges")) {
                        JsonObject bObj = bEl.getAsJsonObject();
                        kd.toolBadges.add(new ToolBadge(
                                bObj.has("icon") ? bObj.get("icon").getAsString() : "sword",
                                bObj.has("label") ? bObj.get("label").getAsString() : "Tag",
                                bObj.has("color") ? bObj.get("color").getAsString() : "#38bdf8"
                        ));
                    }
                }
                if (kObj.has("items")) {
                    for (JsonElement iEl : kObj.getAsJsonArray("items")) {
                        ItemStack st = deserializeItemStack(iEl.getAsJsonObject());
                        if (!st.isEmpty()) {
                            kd.items.add(st);
                        }
                    }
                }
                kd.itemCount = kd.items.size();
                KITS.put(id, kd);
            }
        } catch (Exception e) {
            CmiMod.LOGGER.error("Error al cargar kits.json", e);
        }
    }

    private static void migrateOldIndividualKits() {
        Path kitsDir = CmiConfigManager.getKitsDir();
        if (kitsDir != null && Files.exists(kitsDir)) {
            File[] files = kitsDir.toFile().listFiles((d, n) -> n.endsWith(".json"));
            if (files != null && files.length > 0) {
                for (File f : files) {
                    try (FileReader reader = new FileReader(f)) {
                        JsonObject obj = GSON.fromJson(reader, JsonObject.class);
                        if (obj != null && obj.has("name")) {
                            String name = obj.get("name").getAsString();
                            String id = name.toLowerCase().replace(' ', '_');
                            KitData kd = new KitData(id, name, "Espadas", "netherite_sword", "&6âœ¦ " + name.toUpperCase() + " âœ¦\n<autolore>\n&8Loadout Personal Â· CheManyItems");
                            kd.toolBadges.add(new ToolBadge("netherite_sword", "Espadas", "#38bdf8"));
                            if (obj.has("items")) {
                                for (JsonElement iEl : obj.getAsJsonArray("items")) {
                                    ItemStack st = deserializeItemStack(iEl.getAsJsonObject());
                                    if (!st.isEmpty()) kd.items.add(st);
                                }
                            }
                            kd.itemCount = kd.items.size();
                            KITS.put(id, kd);
                        }
                    } catch (Exception ignored) {}
                }
                saveKits();
            }
        }
    }

    private static JsonObject serializeItemStack(ItemStack st, int slot) {
        JsonObject itObj = new JsonObject();
        itObj.addProperty("slot", slot);
        itObj.addProperty("id", BuiltInRegistries.ITEM.getKey(st.getItem()).toString());
        itObj.addProperty("count", st.getCount());

        if (st.has(DataComponents.CUSTOM_NAME)) {
            Component c = st.get(DataComponents.CUSTOM_NAME);
            if (c != null) {
                itObj.addProperty("name", UnicodeFontHelper.componentToFormattedString(c));
            }
        }

        if (st.has(DataComponents.CUSTOM_DATA)) {
            CustomData cd = st.get(DataComponents.CUSTOM_DATA);
            if (cd != null) {
                CompoundTag tag = cd.copyTag();
                if (tag.contains("cmi_raw_lore")) {
                    itObj.addProperty("cmi_raw_lore", tag.getString("cmi_raw_lore").orElse(""));
                }
            }
        }

        if (st.has(DataComponents.LORE)) {
            ItemLore lore = st.get(DataComponents.LORE);
            if (lore != null) {
                JsonArray loreArr = new JsonArray();
                for (Component comp : lore.lines()) {
                    loreArr.add(UnicodeFontHelper.componentToFormattedString(comp));
                }
                itObj.add("lore", loreArr);
            }
        }

        boolean isBook = st.is(Items.ENCHANTED_BOOK);
        var compKey = isBook ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
        if (st.has(compKey)) {
            ItemEnchantments ench = st.get(compKey);
            if (ench != null && !ench.isEmpty()) {
                JsonObject enchObj = new JsonObject();
                for (var entry : ench.entrySet()) {
                    enchObj.addProperty(entry.getKey().getRegisteredName(), entry.getIntValue());
                }
                itObj.add("enchants", enchObj);
            }
        }

        if (st.has(DataComponents.UNBREAKABLE)) {
            itObj.addProperty("unbreakable", true);
        }

        return itObj;
    }

    private static ItemStack deserializeItemStack(JsonObject obj) {
        if (!obj.has("id")) return ItemStack.EMPTY;
        String idStr = obj.get("id").getAsString();
        Identifier id = Identifier.tryParse(idStr);
        if (id == null) return ItemStack.EMPTY;

        var opt = BuiltInRegistries.ITEM.get(id);
        if (opt.isEmpty()) return ItemStack.EMPTY;

        int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
        ItemStack st = new ItemStack(opt.get().value(), Math.max(1, count));

        if (obj.has("name")) {
            st.set(DataComponents.CUSTOM_NAME, UnicodeFontHelper.parseFormattedComponent(obj.get("name").getAsString()));
        }

        if (obj.has("cmi_raw_lore")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("cmi_raw_lore", obj.get("cmi_raw_lore").getAsString());
            st.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        if (obj.has("lore")) {
            List<Component> loreList = new ArrayList<>();
            for (JsonElement el : obj.getAsJsonArray("lore")) {
                loreList.add(UnicodeFontHelper.parseFormattedComponent(el.getAsString()));
            }
            st.set(DataComponents.LORE, new ItemLore(loreList));
        }

        if (obj.has("unbreakable") && obj.get("unbreakable").getAsBoolean()) {
            st.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
        }

        return st;
    }

    // =========================================================================
    // CREACIÃ“N DEL CATÃLOGO OFICIAL DE LOS 6 KITS PERSONALES (SECCIÃ“N 7.2)
    // =========================================================================
    private static void createDefaultOfficialKits() {
        // 1. Kit Combate Netherite
        KitData kCombate = new KitData(
                "kit_combate", "Kit Combate Netherite", "Espadas", "netherite_sword",
                "&6âœ¦ EXCALIBUR DEL CHE âœ¦\n<enchants>\n<customenchants>\n<atributes>\n&8Loadout Personal Â· CheManyItems"
        );
        kCombate.toolBadges.add(new ToolBadge("netherite_sword", "Espadas", "#38bdf8"));
        kCombate.toolBadges.add(new ToolBadge("chestplate", "Full Prot IV", "#a855f7"));

        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        sword.set(DataComponents.CUSTOM_NAME, UnicodeFontHelper.parseFormattedComponent("&6âœ¦ Espada Netherite 32k âœ¦"));
        kCombate.items.add(sword);
        kCombate.items.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 64));
        kCombate.items.add(new ItemStack(Items.TOTEM_OF_UNDYING, 1));
        kCombate.items.add(new ItemStack(Items.ENDER_PEARL, 16));
        kCombate.items.add(new ItemStack(Items.NETHERITE_HELMET));
        kCombate.items.add(new ItemStack(Items.NETHERITE_CHESTPLATE));
        kCombate.items.add(new ItemStack(Items.NETHERITE_LEGGINGS));
        kCombate.items.add(new ItemStack(Items.NETHERITE_BOOTS));
        kCombate.items.add(new ItemStack(Items.SHIELD));
        kCombate.items.add(new ItemStack(Items.EXPERIENCE_BOTTLE, 64));
        kCombate.items.add(new ItemStack(Items.COOKED_BEEF, 64));
        kCombate.items.add(new ItemStack(Items.END_CRYSTAL, 64));
        kCombate.items.add(new ItemStack(Items.RESPAWN_ANCHOR, 64));
        kCombate.items.add(new ItemStack(Items.GLOWSTONE, 64));
        kCombate.itemCount = kCombate.items.size();
        KITS.put(kCombate.id, kCombate);

        // 2. Kit MinerÃ­a & ExcavaciÃ³n
        KitData kMineria = new KitData(
                "kit_herramientas", "Kit MinerÃ­a & ExcavaciÃ³n", "Herramientas", "netherite_pickaxe",
                "&6âœ¦ TALADRO TITÃNICO âœ¦\n<enchants>\n<customenchants>\n<atributes>\n&8MinerÃ­a CheManyItems"
        );
        kMineria.toolBadges.add(new ToolBadge("netherite_pickaxe", "Herramientas", "#f59e0b"));
        kMineria.toolBadges.add(new ToolBadge("book", "Fortuna III", "#10b981"));
        kMineria.items.add(new ItemStack(Items.NETHERITE_PICKAXE));
        kMineria.items.add(new ItemStack(Items.NETHERITE_SHOVEL));
        kMineria.items.add(new ItemStack(Items.NETHERITE_AXE));
        kMineria.items.add(new ItemStack(Items.TORCH, 64));
        kMineria.items.add(new ItemStack(Items.ENDER_CHEST, 8));
        kMineria.items.add(new ItemStack(Items.GOLDEN_CARROT, 64));
        kMineria.items.add(new ItemStack(Items.IRON_INGOT, 64));
        kMineria.items.add(new ItemStack(Items.WATER_BUCKET));
        kMineria.itemCount = kMineria.items.size();
        KITS.put(kMineria.id, kMineria);

        // 3. Kit Defensa & Blindaje
        KitData kDefensa = new KitData(
                "kit_defensa", "Kit Defensa & Blindaje", "Armaduras", "netherite_chestplate",
                "&bâœ¦ BALUARTE IMPENETRABLE âœ¦\n<enchants>\n<customenchants>\n<atributes>\n&8Blindaje Pesado CMI"
        );
        kDefensa.toolBadges.add(new ToolBadge("netherite_chestplate", "Armaduras", "#a855f7"));
        kDefensa.toolBadges.add(new ToolBadge("totem", "TÃ³tem + Escudo", "#38bdf8"));
        kDefensa.items.add(new ItemStack(Items.SHIELD));
        for (int i = 0; i < 6; i++) kDefensa.items.add(new ItemStack(Items.TOTEM_OF_UNDYING));
        kDefensa.items.add(new ItemStack(Items.GOLDEN_APPLE, 64));
        kDefensa.items.add(new ItemStack(Items.NETHERITE_CHESTPLATE));
        kDefensa.items.add(new ItemStack(Items.NETHERITE_LEGGINGS));
        kDefensa.itemCount = kDefensa.items.size();
        KITS.put(kDefensa.id, kDefensa);

        // 4. Kit ArquerÃ­a & Rango
        KitData kArcos = new KitData(
                "kit_arcos", "Kit ArquerÃ­a & Rango", "Arcos", "bow",
                "&eâœ¦ FRANCOTIRADOR CELESTE âœ¦\n<enchants>\n<customenchants>\n<atributes>\n&8ArquerÃ­a CMI"
        );
        kArcos.toolBadges.add(new ToolBadge("bow", "Arcos", "#3b82f6"));
        kArcos.toolBadges.add(new ToolBadge("target", "Infinidad I", "#f59e0b"));
        kArcos.items.add(new ItemStack(Items.BOW));
        kArcos.items.add(new ItemStack(Items.CROSSBOW));
        kArcos.items.add(new ItemStack(Items.ELYTRA));
        kArcos.items.add(new ItemStack(Items.FIREWORK_ROCKET, 64));
        kArcos.items.add(new ItemStack(Items.SPECTRAL_ARROW, 64));
        kArcos.items.add(new ItemStack(Items.ARROW, 64));
        kArcos.itemCount = kArcos.items.size();
        KITS.put(kArcos.id, kArcos);

        // 5. Kit Pociones & Alquimia
        KitData kPociones = new KitData(
                "kit_pociones", "Kit Pociones & Alquimia", "Pociones", "potion",
                "&dâœ¦ ELÃXIR DEL MAESTRO âœ¦\n<enchants>\n<customenchants>\n<atributes>\n&8Alquimia CMI"
        );
        kPociones.toolBadges.add(new ToolBadge("potion", "Pociones", "#f43f5e"));
        kPociones.toolBadges.add(new ToolBadge("wine", "CuraciÃ³n II", "#f43f5e"));
        for (int i = 0; i < 8; i++) kPociones.items.add(new ItemStack(Items.SPLASH_POTION));
        kPociones.items.add(new ItemStack(Items.GOLDEN_APPLE, 32));
        kPociones.items.add(new ItemStack(Items.ENDER_PEARL, 16));
        kPociones.itemCount = kPociones.items.size();
        KITS.put(kPociones.id, kPociones);

        // 6. Kit Constructor & Shulker
        KitData kConstructor = new KitData(
                "kit_constructor", "Kit Constructor & Shulker", "Bloques", "shulker_box",
                "&aâœ¦ ARQUITECTO SUPREMO âœ¦\n<enchants>\n<customenchants>\n<atributes>\n&8ConstrucciÃ³n CMI"
        );
        kConstructor.toolBadges.add(new ToolBadge("shulker_box", "Bloques", "#10b981"));
        kConstructor.toolBadges.add(new ToolBadge("chest", "Shulker Box", "#10b981"));
        kConstructor.items.add(new ItemStack(Items.CRAFTING_TABLE, 16));
        kConstructor.items.add(new ItemStack(Items.CYAN_SHULKER_BOX, 4));
        kConstructor.items.add(new ItemStack(Items.WHITE_SHULKER_BOX, 4));
        kConstructor.items.add(new ItemStack(Items.SCAFFOLDING, 64));
        kConstructor.items.add(new ItemStack(Items.STONE_BRICKS, 64));
        kConstructor.items.add(new ItemStack(Items.GLASS, 64));
        kConstructor.items.add(new ItemStack(Items.BREAD, 64));
        kConstructor.itemCount = kConstructor.items.size();
        KITS.put(kConstructor.id, kConstructor);
    }

    public static void resolvePendingEnchantments(Minecraft mc) {
        // En 26.1 los componentes de encantamiento se resuelven de forma nativa
    }
}