package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.client.commands.CmiCommands;
import com.fxfps.cmi.client.lores.ArgNetLoreManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * PANTALLA OFICIAL DE LOS 18 CUSTOM ENCHANTS DE GAMBLE 🍁
 *
 * Implementa el Design System Neo-Capsule Liquid Glass:
 * - Sin pausa in-game (shouldPause -> false).
 * - Ultra-transparencia a 60+ FPS sobre shaders de Minecraft.
 * - Categorías de herramientas con micro-puntos de color.
 * - Tarjetas Neo-Cards de cristal para cada encantamiento con nivel dinámico y cuadritos.
 * - Ejecución automática de comando de servidor /customenchant add <id> <lvl>.
 */
public class CmiCustomEnchantsScreen extends CmiScreen {

    private EditBox searchBox;
    private EditBox customEnchantColorBox;
    private String selectedCategory = "Todos";
    private int scrollOffset = 0;

    public static class CustomEnchantEntry {
        public final String id;
        public final String name;
        public final String category;
        public final String target;
        public final String description;
        public final int maxLevel;
        public final int colorHex;
        public final String squareGlyphs;
        public int selectedLevel;

        public CustomEnchantEntry(String id, String name, String category, String target, String description, int maxLevel, int colorHex, String squareGlyphs) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.target = target;
            this.description = description;
            this.maxLevel = maxLevel;
            this.colorHex = colorHex;
            this.squareGlyphs = squareGlyphs;
            this.selectedLevel = maxLevel;
        }

        public String getLevelSquares() {
            if (this.squareGlyphs != null && !this.squareGlyphs.isEmpty()) {
                return this.squareGlyphs;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= maxLevel; i++) {
                sb.append(i <= selectedLevel ? "■" : "□");
            }
            return sb.toString();
        }
    }

    public static final List<CustomEnchantEntry> ALL_ENCHANTS = new ArrayList<>();

    static {
        // --- ARMADURA (10) ---
        ALL_ENCHANTS.add(new CustomEnchantEntry("vida_extra", "Vida Extra", "Armadura", "Cualquier armadura", "Corazones extra mientras está equipado", 4, 0xFFFF3B5C, "🟥🟥🟥🟥"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("fuerza", "Fuerza", "Armadura", "Cualquier armadura", "Strength mientras está equipado", 3, 0xFFFF2D55, "🟥🟥🟥"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("resistencia_al_fuego", "Resistencia al Fuego", "Armadura", "Cualquier armadura", "Fire Resistance mientras está equipado", 1, 0xFFFF9500, "🟧"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("regeneracion", "Regeneración", "Armadura", "Cualquier armadura", "Regeneration mientras está equipado", 2, 0xFFFF2D78, "🌸🌸"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("velocidad", "Velocidad", "Armadura", "Botas", "Speed mientras está equipado", 3, 0xFF00C7FF, "🟦🟦🟦"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("salto", "Salto", "Armadura", "Botas / Pantalón", "Jump Boost mientras está equipado", 3, 0xFF30D158, "🟩🟩🟩"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("respiracion_acuatica", "Respiración Acuática", "Armadura", "Casco", "Water Breathing mientras está equipado", 1, 0xFF0A84FF, "🟦"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("vision_nocturna", "Visión Nocturna", "Armadura", "Casco", "Night Vision mientras está equipado", 1, 0xFFBF5AF2, "🟪"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("invisibilidad", "Invisibilidad", "Armadura", "Pechera", "Invisibility mientras está equipado", 1, 0xFFE5E5EA, "⬜"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("saturacion", "Saturación", "Armadura", "Pechera", "Saturation mientras está equipado", 1, 0xFFFFD60A, "🟨"));

        // --- PICO (4) ---
        ALL_ENCHANTS.add(new CustomEnchantEntry("excavador", "Excavador", "Pico", "Pico", "Rompe en área (3×3 / 5×5) alrededor del bloque minado", 2, 0xFFFF9F0A, "🟧🟧"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("fundicion", "Fundición", "Pico", "Pico", "Los minerales que rompas salen ya fundidos", 1, 0xFFFF6B00, "🟧"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("explosivo", "Explosivo", "Pico", "Pico", "Mini explosión al minar, sin dañar entidades", 2, 0xFFFF3B30, "🟥🟥"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("vetas", "Vetas", "Pico", "Pico", "Rompe todo el filón del mismo mineral de una vez", 1, 0xFF64D2FF, "💠"));

        // --- PALA (3) ---
        ALL_ENCHANTS.add(new CustomEnchantEntry("excavador", "Excavador", "Pala", "Pala", "Rompe en área (3×3 / 5×5) tierra, arena y grava", 2, 0xFFFF9F0A, "🟧🟧"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("fertilizante", "Fertilizante", "Pala", "Pala", "Boost tipo bonemeal a los cultivos cercanos", 1, 0xFF32D74B, "🟩"));
        ALL_ENCHANTS.add(new CustomEnchantEntry("rastro", "Rastro", "Pala", "Pala", "Convierte el pasto en camino al caminar sobre él", 1, 0xFFD4A373, "🟫"));

        // --- HACHA (1) ---
        ALL_ENCHANTS.add(new CustomEnchantEntry("talador", "Talador", "Hacha", "Hacha", "Corta el árbol entero al romper la base", 1, 0xFF30D158, "🟩"));
    }

    private final List<CustomEnchantEntry> filteredList = new ArrayList<>();

    public CmiCustomEnchantsScreen(Screen parentScreen) {
        super(Component.literal("Custom Enchants de Gamble · CMI"), parentScreen);
        refreshFilteredList();
    }

    private void refreshFilteredList() {
        String query = (searchBox != null) ? searchBox.getValue().trim().toLowerCase() : "";
        filteredList.clear();
        for (CustomEnchantEntry ce : ALL_ENCHANTS) {
            boolean matchCat = selectedCategory.equalsIgnoreCase("Todos") || ce.category.equalsIgnoreCase(selectedCategory);
            boolean matchQuery = query.isEmpty() || ce.name.toLowerCase().contains(query) || ce.description.toLowerCase().contains(query) || ce.target.toLowerCase().contains(query);
            if (matchCat && matchQuery) {
                filteredList.add(ce);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        buildWidgets();
    }

    private void buildWidgets() {
        this.clearWidgets();

        int topY = 32;
        int winW = Math.min(this.width - 24, 520);
        int winH = Math.min(this.height - 36, 280);
        int winX = (this.width - winW) / 2;
        int winY = (this.height - winH) / 2;

        // Buscador superior
        String prevSearch = (searchBox != null) ? searchBox.getValue() : "";
        int searchW = 120;
        int searchX = winX + 14;
        int searchY = winY + 28;

        this.searchBox = new EditBox(this.font, searchX, searchY, searchW, 18, Component.literal("Buscar"));
        this.searchBox.setHint(Component.literal(CmiUiTheme.ICON_SEARCH + " §8Buscar..."));
        this.searchBox.setValue(prevSearch);
        this.searchBox.setResponder(val -> {
            scrollOffset = 0;
            refreshFilteredList();
            buildWidgets();
        });
        this.addRenderableWidget(this.searchBox);

        // Color Base Global
        int colorX = searchX + searchW + 6;
        int colorW = 105;
        this.customEnchantColorBox = new EditBox(this.font, colorX, searchY, colorW, 18, Component.literal("Color Base Custom"));
        this.customEnchantColorBox.setHint(Component.literal("🎨 Color (<frost>)..."));
        this.customEnchantColorBox.setValue(com.fxfps.cmi.client.config.CmiConfigManager.getConfig().customEnchantsGlobalColor);
        this.customEnchantColorBox.setResponder(val -> {
            com.fxfps.cmi.client.config.CmiConfigManager.getConfig().customEnchantsGlobalColor = val;
            com.fxfps.cmi.client.config.CmiConfigManager.save();
        });
        this.addRenderableWidget(this.customEnchantColorBox);

        // Botón Recargar .txt
        this.addRenderableWidget(new CmiButton(
                colorX + colorW + 4, searchY, 50, 18,
                Component.literal("🔄 .txt"),
                btn -> {
                    com.fxfps.cmi.client.lores.ItemModifierColorManager.reload();
                    if (this.minecraft != null && this.minecraft.player != null) {
                        this.minecraft.player.sendSystemMessage(Component.literal("§a[CMI] ✔ customenchants_colors.txt recargado"));
                    }
                },
                CmiButton.Variant.CHIP
        ));

        // Botón Cerrar
        this.addRenderableWidget(new CmiButton(
                winX + winW - 24, winY + 8, 18, 18,
                Component.literal("✕"),
                btn -> this.closeScreen(),
                CmiButton.Variant.DANGER
        ));

        // Píldoras de Categoría Neo-Capsule
        String[] cats = {"Todos", "Armadura", "Pico", "Pala", "Hacha"};
        int catY = searchY;
        int catStartX = colorX + colorW + 58;
        int catBtnW = 38;

        for (int i = 0; i < cats.length; i++) {
            final String catName = cats[i];
            boolean isSel = catName.equalsIgnoreCase(selectedCategory);

            CmiButton catBtn = new CmiButton(
                    catStartX + i * (catBtnW + 3), catY, catBtnW, 18,
                    Component.literal(catName),
                    btn -> {
                        selectedCategory = catName;
                        scrollOffset = 0;
                        refreshFilteredList();
                        buildWidgets();
                    },
                    CmiButton.Variant.TAB
            );
            catBtn.setSelected(isSel);
            this.addRenderableWidget(catBtn);
        }

        // Fila de Tarjetas Neo-Cards de Custom Enchants
        int cardStartY = winY + 54;
        int cardH = 34;
        int visibleCards = Math.max(1, (winH - 68) / (cardH + 4));
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + visibleCards, filteredList.size());

        for (int i = startIndex; i < endIndex; i++) {
            final CustomEnchantEntry ce = filteredList.get(i);
            int rowIdx = i - startIndex;
            int cardY = cardStartY + rowIdx * (cardH + 4);
            int cardW = winW - 28;
            int cardX = winX + 14;

            // Controles de Nivel [-] [lvl] [+]
            int ctrlX = cardX + cardW - 130;
            if (ce.maxLevel > 1) {
                this.addRenderableWidget(new CmiButton(
                        ctrlX, cardY + 7, 18, 18,
                        Component.literal("-"),
                        btn -> {
                            ce.selectedLevel = Math.max(1, ce.selectedLevel - 1);
                        },
                        CmiButton.Variant.TAB
                ));

                this.addRenderableWidget(new CmiButton(
                        ctrlX + 38, cardY + 7, 18, 18,
                        Component.literal("+"),
                        btn -> {
                            ce.selectedLevel = Math.min(ce.maxLevel, ce.selectedLevel + 1);
                        },
                        CmiButton.Variant.TAB
                ));
            }

            // Botón Aplicar Servidor / Lore
            this.addRenderableWidget(new CmiButton(
                    ctrlX + 60, cardY + 7, 66, 18,
                    Component.literal("⚡ Aplicar"),
                    btn -> {
                        applyCustomEnchant(ce);
                    },
                    CmiButton.Variant.ACTION_AMBER
            ));
        }
    }

    private void applyCustomEnchant(CustomEnchantEntry ce) {
        if (this.minecraft != null && this.minecraft.player != null) {
            ItemStack held = this.minecraft.player.getMainHandItem();
            if (!held.isEmpty()) {
                ArgNetLoreManager.addOrUpdateCustomEnchant(held, ce.name, ce.selectedLevel);
            }
            // Enviar comando al servidor
            String cmd = "customenchant add " + ce.id + " " + ce.selectedLevel;
            if (this.minecraft.player.connection != null) {
                this.minecraft.player.connection.sendCommand(cmd);
            }
            if (this.minecraft.player != null) {
                this.minecraft.player.sendSystemMessage(Component.literal("§a[CMI] 🍁 §7Custom Enchant aplicado: §b" + ce.name + " " + ce.selectedLevel));
            }
            if (this.minecraft != null) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, 1.0F));
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int winW = Math.min(this.width - 24, 520);
        int winH = Math.min(this.height - 36, 280);
        int winX = (this.width - winW) / 2;
        int winY = (this.height - winH) / 2;

        // Marco Glassmorphic flotante
        CmiUiTheme.drawModalBackdrop(guiGraphics, winX, winY, winW, winH);

        // Cabecera
        CmiUiTheme.drawLogoIcon(guiGraphics, winX + 14, winY + 8, 28, 14);
        guiGraphics.text(this.font, Component.literal("§6§lCUSTOM ENCHANTS DE GAMBLE §7· 18 Encantamientos"), winX + 48, winY + 11, CmiUiTheme.COLOR_WHITE);

        // Render de Tarjetas Neo-Cards
        int cardStartY = winY + 54;
        int cardH = 34;
        int visibleCards = Math.max(1, (winH - 68) / (cardH + 4));
        int startIndex = scrollOffset;
        int endIndex = Math.min(startIndex + visibleCards, filteredList.size());

        for (int i = startIndex; i < endIndex; i++) {
            CustomEnchantEntry ce = filteredList.get(i);
            int rowIdx = i - startIndex;
            int cardY = cardStartY + rowIdx * (cardH + 4);
            int cardW = winW - 28;
            int cardX = winX + 14;

            boolean hovered = mouseX >= cardX && mouseX <= cardX + cardW && mouseY >= cardY && mouseY <= cardY + cardH;

            // Panel de cristal translúcido para cada tarjeta
            CmiUiTheme.drawGlassPanel(guiGraphics, cardX, cardY, cardW, cardH, hovered ? CmiUiTheme.COLOR_CYAN_NEON : CmiUiTheme.GLASS_PANEL_BORDER);

            // Nombre y nivel
            guiGraphics.text(this.font, Component.literal("§f§l" + ce.name + " §e" + ce.selectedLevel + " §7" + ce.getLevelSquares()), cardX + 8, cardY + 5, ce.colorHex);
            guiGraphics.text(this.font, Component.literal("§8" + ce.target + " · §7" + ce.description), cardX + 8, cardY + 17, CmiUiTheme.COLOR_MUTED);

            // Nivel central
            int ctrlX = cardX + cardW - 130;
            if (ce.maxLevel > 1) {
                guiGraphics.centeredText(this.font, Component.literal("§b" + ce.selectedLevel), ctrlX + 28, cardY + 12, CmiUiTheme.COLOR_CYAN_NEON);
            }
        }

        // Paginación o Scroll
        if (filteredList.size() > visibleCards) {
            String countInfo = "§8" + (scrollOffset + 1) + "-" + endIndex + " de " + filteredList.size() + " (Usa rueda del ratón)";
            guiGraphics.centeredText(this.font, Component.literal(countInfo), winX + winW / 2, winY + winH - 12, CmiUiTheme.COLOR_MUTED);
        }
    }
}
