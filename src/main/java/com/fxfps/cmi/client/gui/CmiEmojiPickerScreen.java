package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.client.fonts.ServerEmojiRegistry;
import com.fxfps.cmi.client.fonts.ServerEmojiRegistry.ServerEmoji;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * MODAL FLOTANTE DE SELECTOR DE EMOJIS & GLIFOS (+336 PUA) 🇦🇷
 *
 * Implementa el Design System Neo-Capsule Liquid Glass:
 * - Ventana modal flotante centrada con doble borde neón (drawModalBackdrop).
 * - Curvatura real orgánica (Border-Radius 10px).
 * - Píldoras de categoría con indicador activo.
 * - Modo de inserción alternable: Unicode directo (PUA) o :shortcode:.
 * - Cierre limpio con botón [ ✕ ] o tecla ESC.
 */
public class CmiEmojiPickerScreen extends CmiScreen {

    private final Consumer<String> onSelectEmoji;
    private EditBox searchBox;
    private String selectedCategory = "Todos";
    private int currentPage = 0;
    private boolean insertAsUnicode = true;

    private final List<ServerEmoji> filteredList = new ArrayList<>();
    private static final int COLUMNS = 12;
    private static final int ROWS = 5;
    private static final int PAGE_SIZE = COLUMNS * ROWS; // 60 emojis por página

    public CmiEmojiPickerScreen(Screen parentScreen, Consumer<String> onSelectEmoji) {
        super(Component.literal("Selector de Emojis · CMI 🇦🇷"), parentScreen);
        this.onSelectEmoji = onSelectEmoji;
        refreshFilteredList();
    }

    private void refreshFilteredList() {
        String query = (searchBox != null) ? searchBox.getValue() : "";
        filteredList.clear();
        filteredList.addAll(ServerEmojiRegistry.filter(selectedCategory, query));
    }

    @Override
    protected void init() {
        super.init();
        buildWidgets();
    }

    private void buildWidgets() {
        this.clearWidgets();

        int modalW = Math.min(this.width - 24, 460);
        int modalH = Math.min(this.height - 24, 270);
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - modalH) / 2;

        // 1. Buscador en tiempo real
        String prevSearch = (searchBox != null) ? searchBox.getValue() : "";
        int searchW = 200;
        int searchX = modalX + 16;
        int searchY = modalY + 30;

        searchBox = new EditBox(this.font, searchX, searchY, searchW, 18, Component.literal("Buscar"));
        searchBox.setHint(Component.literal(CmiUiTheme.ICON_SEARCH + " §8Buscar nombre o :code:..."));
        searchBox.setValue(prevSearch);
        searchBox.setResponder(val -> {
            currentPage = 0;
            refreshFilteredList();
            buildWidgets();
        });
        this.addRenderableWidget(searchBox);

        // Botón Limpiar Búsqueda
        this.addRenderableWidget(new CmiButton(
                searchX + searchW + 4, searchY, 20, 18,
                Component.literal("✕"),
                btn -> {
                    if (searchBox != null) {
                        searchBox.setValue("");
                    }
                },
                CmiButton.Variant.CHIP
        ));

        // Selector de Modo de Inserción
        this.addRenderableWidget(new CmiButton(
                searchX + searchW + 28, searchY, 110, 18,
                Component.literal(insertAsUnicode ? "§a[Modo: Unicode]" : "§e[Modo: :code:]"),
                btn -> {
                    insertAsUnicode = !insertAsUnicode;
                    btn.setMessage(Component.literal(insertAsUnicode ? "§a[Modo: Unicode]" : "§e[Modo: :code:]"));
                },
                CmiButton.Variant.TAB
        ));

        // Botón Cerrar Modal [ ✕ ]
        this.addRenderableWidget(new CmiButton(
                modalX + modalW - 26, modalY + 8, 18, 18,
                Component.literal("✕"),
                btn -> this.closeScreen(),
                CmiButton.Variant.DANGER
        ));

        // 2. Barra de Categorías Rápidas
        String[] cats = {"Todos", "Rangos", "Patria", "Combate", "Server UI", "Útiles", "Comidas", "Pociones", "Bloques"};
        int catY = searchY + 22;
        int catBtnW = Math.max(38, (modalW - 32) / cats.length - 2);

        for (int i = 0; i < cats.length; i++) {
            final String catName = cats[i];
            boolean isSel = catName.equalsIgnoreCase(selectedCategory);

            CmiButton catBtn = new CmiButton(
                    modalX + 16 + i * (catBtnW + 2), catY, catBtnW, 15,
                    Component.literal(catName),
                    btn -> {
                        selectedCategory = catName;
                        currentPage = 0;
                        refreshFilteredList();
                        buildWidgets();
                    },
                    CmiButton.Variant.TAB
            );
            catBtn.setSelected(isSel);
            this.addRenderableWidget(catBtn);
        }

        // 3. Rejilla de Emojis / Glifos
        boolean isRangos = "Rangos".equalsIgnoreCase(selectedCategory);
        int cols = isRangos ? 4 : COLUMNS;
        int btnW = isRangos ? 96 : 24;
        int colSpacing = isRangos ? 102 : 26;
        int pageSize = isRangos ? 20 : PAGE_SIZE;

        int gridStartX = modalX + (modalW - (cols * colSpacing - (colSpacing - btnW))) / 2;
        int gridStartY = catY + 20;

        int startIndex = currentPage * pageSize;
        int endIndex = Math.min(startIndex + pageSize, filteredList.size());

        for (int i = startIndex; i < endIndex; i++) {
            final ServerEmoji emoji = filteredList.get(i);
            int slotIdx = i - startIndex;
            int col = slotIdx % cols;
            int row = slotIdx / cols;
            int bx = gridStartX + col * colSpacing;
            int by = gridStartY + row * 24;

            String label = isRangos ? ("§f" + emoji.unicode() + " §7" + emoji.displayName()) : ("§f" + emoji.unicode());
            CmiButton btn = new CmiButton(
                    bx, by, btnW, 20,
                    Component.literal(label),
                    b -> {
                        String insertText = insertAsUnicode ? (isRangos ? "&f" + emoji.unicode() : emoji.unicode()) : emoji.shortcode();
                        if (onSelectEmoji != null) {
                            onSelectEmoji.accept(insertText);
                        }
                        if (this.minecraft != null) {
                            this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                            if (this.minecraft.player != null) {
                                this.minecraft.player.sendSystemMessage(Component.literal("§a[CMI] ✔ Glifo insertado: §f" + emoji.unicode() + " §7(" + emoji.displayName() + ")"));
                            }
                        }
                        this.closeScreen();
                    },
                    CmiButton.Variant.CHIP
            );
            this.addRenderableWidget(btn);
        }

        // 4. Paginación Inferior
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredList.size() / pageSize));
        int navY = modalY + modalH - 24;

        this.addRenderableWidget(new CmiButton(
                modalX + 16, navY, 26, 16,
                Component.literal("◀"),
                btn -> {
                    if (currentPage > 0) {
                        currentPage--;
                        buildWidgets();
                    }
                },
                CmiButton.Variant.CHIP
        ));

        this.addRenderableWidget(new CmiButton(
                modalX + modalW - 42, navY, 26, 16,
                Component.literal("▶"),
                btn -> {
                    if (currentPage < totalPages - 1) {
                        currentPage++;
                        buildWidgets();
                    }
                },
                CmiButton.Variant.CHIP
        ));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Fondo translúcido común sobre Minecraft en vivo
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int modalW = Math.min(this.width - 24, 460);
        int modalH = Math.min(this.height - 24, 270);
        int modalX = (this.width - modalW) / 2;
        int modalY = (this.height - modalH) / 2;

        // Marco modal Glassmorphic flotante con bordes redondeados
        CmiUiTheme.drawModalBackdrop(guiGraphics, modalX, modalY, modalW, modalH);

        // Cabecera del modal
        CmiUiTheme.drawLogoIcon(guiGraphics, modalX + 14, modalY + 8, 28, 14);
        guiGraphics.text(this.font, Component.literal("§b§lCATÁLOGO DE EMOJIS & GLIFOS §7· " + CmiUiTheme.FLAG_AR), modalX + 48, modalY + 11, CmiUiTheme.COLOR_WHITE);

        // Indicador de Paginación centrado al pie
        boolean isRangos = "Rangos".equalsIgnoreCase(selectedCategory);
        int pageSize = isRangos ? 20 : PAGE_SIZE;
        int totalPages = Math.max(1, (int) Math.ceil((double) filteredList.size() / pageSize));
        String pageInfo = "§7Página §e" + (currentPage + 1) + "§7/§e" + totalPages + " §8(" + filteredList.size() + " glifos)";
        guiGraphics.centeredText(this.font, Component.literal(pageInfo), modalX + modalW / 2, modalY + modalH - 20, CmiUiTheme.COLOR_MUTED);
    }
}
