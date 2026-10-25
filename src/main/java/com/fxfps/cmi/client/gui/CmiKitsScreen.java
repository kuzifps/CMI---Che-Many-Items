package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.client.fonts.UnicodeFontHelper;
import com.fxfps.cmi.client.kits.CmiKitManager;
import com.fxfps.cmi.client.kits.CmiKitManager.KitData;
import com.fxfps.cmi.client.lores.ArgNetLoreManager;
import com.fxfps.cmi.client.lores.AutoLorePreset;
import com.fxfps.cmi.client.lores.AutoLoreRegistry;
import com.fxfps.cmi.client.shulker.ShulkerPackerManager;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * GESTOR DE KITS PERSONALES DE CHEMANYITEMS (CMI) 💎
 *
 * Arquitectura Oficial Unificada Neo-Capsule Liquid Glass:
 * - 0% de oscurecimiento vanilla, shaders y juego a 60 FPS detrás del cristal (shouldPause -> false).
 * - Sistema Global de Flechas Deshacer / Rehacer [ ↩ Deshacer ] y [ ↪ Rehacer ] (Ctrl+Z / Ctrl+Y).
 * - Dropdown Multi-AutoLore para gestionar <autolore1>, <autolore2>, etc., con toggle SmallCaps independiente.
 * - Auto-Rename Masivo con plantilla ({item}, {kit}) y botón [ ⚡ Auto-Rename Todo el Kit (36 Slots) ].
 * - Dropdown Shulker Box colapsable de 27 casillas (3x9) con selector de 16 tintes.
 * - Bandeja Central Inferior con soporte de Drag & Drop y Shift+Clic hacia la Shulker Box,
 *   vinculado al generador del comando /give.
 * - Modos de visualización: Neo-Cards redondeadas y Modo Cofre de 54 casillas (6x9).
 */
public class CmiKitsScreen extends CmiScreen {

    private String selectedKitId;
    private String selectedCategory = "Todos";
    private boolean viewMode54s = false;
    private int cardsScrollOffset = 0;

    // Sistema de Historial Deshacer / Rehacer
    private final ItemStackHistory history = new ItemStackHistory();
    private boolean isHistoryRestoring = false;

    // Pestaña activa en Columna 3: 0 = Multi-AutoLore, 1 = Shulker Box (27 Slots)
    private int col3Tab = 0;

    // Multi-AutoLore Dropdown: perfil seleccionado (1, 2, 3...)
    private int selectedAutoLoreIndex = 1;
    private boolean autoLoreDropdownOpen = false;
    private boolean autoLoreSmallCaps = true;

    // Auto-Rename Masivo
    private EditBox autoRenameInputBox;

    // Shulker Box de 27 casillas (3x9) vinculada
    private final List<ItemStack> shulkerSlots = new ArrayList<>(Collections.nCopies(27, ItemStack.EMPTY));
    private String selectedShulkerColor = "cyan";

    // Drag & Drop
    private ItemStack draggedStack = ItemStack.EMPTY;
    private int draggedSourceIndex = -1;
    private boolean draggedFromShulker = false;

    // Widgets interactivos
    private EditBox kitSearchBox;
    private MultiLineEditBox autoLoreBox;

    // Botones Undo / Redo
    private CmiButton undoBtn;
    private CmiButton redoBtn;

    // Toast de notificación temporal
    private String statusToast = "Listo · Gestor de Kits 60 FPS";
    private long statusToastTime = 0;

    public CmiKitsScreen(Screen parentScreen) {
        super(Component.literal("CMI · Gestor de Kits Personales"), parentScreen);
        this.selectedKitId = CmiKitManager.getActiveKitId();
        this.selectedShulkerColor = CmiKitManager.getSelectedShulkerColor();
        loadInitialShulkerItems();
    }

    private void loadInitialShulkerItems() {
        KitData kd = CmiKitManager.getKit(selectedKitId);
        if (kd != null) {
            for (int i = 0; i < 27; i++) {
                if (i < kd.items.size() && kd.items.get(i) != null) {
                    shulkerSlots.set(i, kd.items.get(i).copy());
                } else {
                    shulkerSlots.set(i, ItemStack.EMPTY);
                }
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int topY = 32;
        int footerH = 34;
        int bodyH = this.height - topY - footerH - 4;

        // Distribución oficial de 3 columnas (25% / 45% / 30%)
        int col1W = Math.max(140, (int) (this.width * 0.25f));
        int col2W = Math.max(220, (int) (this.width * 0.45f));
        int col3W = this.width - col1W - col2W - 24;

        int col1X = 8;
        int col2X = col1X + col1W + 6;
        int col3X = col2X + col2W + 6;

        KitData activeKd = CmiKitManager.getKit(selectedKitId);
        if (activeKd == null) {
            activeKd = CmiKitManager.getActiveKit();
            selectedKitId = (activeKd != null) ? activeKd.id : "kit_combate";
        }
        final KitData finalActiveKd = activeKd;
        final String kitIdFinal = (finalActiveKd != null) ? finalActiveKd.id : selectedKitId;

        // =========================================================================
        // BARRA SUPERIOR: FLECHAS UNDO / REDO, PÍLDORAS DE NAVEGACIÓN Y CONTROLES
        // =========================================================================
        int pillY = 5;
        int pillH = 20;

        // 1. Botones Cápsula Deshacer / Rehacer
        int undoX = 180;
        this.undoBtn = new CmiButton(undoX, pillY, 78, pillH, Component.literal("↩ Deshacer"), btn -> handleUndo(), CmiButton.Variant.TAB);
        this.undoBtn.active = history.canUndo();
        this.addRenderableWidget(this.undoBtn);

        this.redoBtn = new CmiButton(undoX + 82, pillY, 74, pillH, Component.literal("↪ Rehacer"), btn -> handleRedo(), CmiButton.Variant.TAB);
        this.redoBtn.active = history.canRedo();
        this.addRenderableWidget(this.redoBtn);

        // 2. Píldoras de Navegación
        int navPillsX = undoX + 164;

        // [ Display ]
        this.addRenderableWidget(new CmiButton(
                navPillsX, pillY, 58, pillH,
                Component.literal("Display"),
                btn -> {
                    ItemStack st = (finalActiveKd != null && !finalActiveKd.items.isEmpty()) ? finalActiveKd.items.get(0) : ItemStack.EMPTY;
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new CmiEditorScreen(this, st, -1, kitIdFinal, 0));
                    }
                },
                CmiButton.Variant.TAB
        ));

        // [ ⚡ Enchants ]
        this.addRenderableWidget(new CmiButton(
                navPillsX + 62, pillY, 76, pillH,
                Component.literal("⚡ Enchants"),
                btn -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new CmiCustomEnchantsScreen(this));
                    }
                },
                CmiButton.Variant.TAB
        ));

        // [ 🎒 Kits ] (Activa #06b6d4)
        CmiButton kitsPill = new CmiButton(
                navPillsX + 142, pillY, 58, pillH,
                Component.literal("🎒 Kits"),
                btn -> {},
                CmiButton.Variant.TAB
        );
        kitsPill.setSelected(true);
        this.addRenderableWidget(kitsPill);

        // [ ⚙ ] y [ ✕ ]
        this.addRenderableWidget(new CmiButton(this.width - 48, pillY, 20, pillH, Component.literal("⚙"), btn -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new CmiSettingsScreen(this));
            }
        }, CmiButton.Variant.TAB));

        this.addRenderableWidget(new CmiButton(this.width - 24, pillY, 20, pillH, Component.literal("✕"), btn -> {
            this.closeScreen();
        }, CmiButton.Variant.DANGER));

        // =========================================================================
        // COLUMNA 1: CONTROLES PRIMARIOS DE HERO SHOWCASE LENS
        // =========================================================================
        int c1BtnY = topY + bodyH - 96;
        int c1BtnW = col1W - 16;

        this.addRenderableWidget(new CmiButton(
                col1X + 8, c1BtnY, c1BtnW, 20,
                Component.literal("⚡ Equipar en Inventario"),
                btn -> {
                    if (this.minecraft != null) {
                        CmiKitManager.equipKitToInventory(this.minecraft, kitIdFinal);
                        showToast("✔ Kit " + kitIdFinal + " equipado en inventario (0s Cooldown)");
                    }
                },
                CmiButton.Variant.ACTION_AMBER
        ));

        this.addRenderableWidget(new CmiButton(
                col1X + 8, c1BtnY + 24, c1BtnW, 20,
                Component.literal("📦 Empaquetar a Shulker"),
                btn -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new CmiShulkerPackerScreen(this, kitIdFinal));
                    }
                },
                CmiButton.Variant.TAB
        ));

        int halfW = (c1BtnW - 4) / 2;
        this.addRenderableWidget(new CmiButton(
                col1X + 8, c1BtnY + 48, halfW, 18,
                Component.literal("✏ Filtro"),
                btn -> cycleKitCategory(kitIdFinal),
                CmiButton.Variant.CHIP
        ));

        this.addRenderableWidget(new CmiButton(
                col1X + 10 + halfW, c1BtnY + 48, halfW, 18,
                Component.literal("💾 Guardar Inv"),
                btn -> saveInventoryToCurrentKit(kitIdFinal),
                CmiButton.Variant.CHIP
        ));

        // =========================================================================
        // COLUMNA 2: BUSCADOR, MODO CARDS / 54s, FILTROS Y AUTO-RENAME MASIVO
        // =========================================================================
        int col2InnerX = col2X + 8;
        int col2InnerW = col2W - 16;

        this.kitSearchBox = new EditBox(this.font, col2InnerX, topY + 28, col2InnerW - 100, 18, Component.literal("Buscar Kit"));
        this.kitSearchBox.setHint(Component.literal("🔍 Buscar Kit..."));
        this.kitSearchBox.setResponder(val -> this.rebuildWidgets());
        this.addRenderableWidget(this.kitSearchBox);

        this.addRenderableWidget(new CmiButton(
                col2InnerX + col2InnerW - 96, topY + 28, 96, 18,
                Component.literal(viewMode54s ? "Modo: 54s ▼" : "Modo: CARDS ▼"),
                btn -> {
                    viewMode54s = !viewMode54s;
                    this.rebuildWidgets();
                },
                viewMode54s ? CmiButton.Variant.ACTION_AMBER : CmiButton.Variant.TAB
        ));

        int filterY = topY + 50;
        String[] cats = {"Todos", "Espadas", "Herram.", "Armaduras", "Arcos", "Pociones", "Bloques"};
        int catBtnW = Math.max(26, (col2InnerW - (cats.length - 1) * 3) / cats.length);
        int curCatX = col2InnerX;

        for (String cat : cats) {
            final String fullCat = cat.equals("Herram.") ? "Herramientas" : cat;
            boolean isSel = (fullCat.equalsIgnoreCase(selectedCategory) || (cat.equals("Todos") && selectedCategory.equals("Todos")));
            CmiButton catBtn = new CmiButton(
                    curCatX, filterY, catBtnW, 16,
                    Component.literal(cat),
                    btn -> {
                        selectedCategory = fullCat;
                        cardsScrollOffset = 0;
                        this.rebuildWidgets();
                    },
                    isSel ? CmiButton.Variant.TAB : CmiButton.Variant.CHIP
            );
            catBtn.setSelected(isSel);
            this.addRenderableWidget(catBtn);
            curCatX += catBtnW + 3;
        }

        // AUTO-RENAME MASIVO: Input de Plantilla + Botón [ ⚡ Auto-Rename Todo el Kit (36 Slots) ]
        int renameY = topY + bodyH - 46;
        this.autoRenameInputBox = new EditBox(this.font, col2InnerX, renameY, col2InnerW - 170, 18, Component.literal("Plantilla Rename"));
        this.autoRenameInputBox.setValue("&6✦ {kit} &7· &b{item}");
        this.autoRenameInputBox.setHint(Component.literal("Plantilla: {item}, {kit}, {index}..."));
        this.addRenderableWidget(this.autoRenameInputBox);

        this.addRenderableWidget(new CmiButton(
                col2InnerX + col2InnerW - 190, renameY, 190, 18,
                Component.literal("⚡ Auto-Rename Todo el Kit (36 Slots)"),
                btn -> {
                    String tmpl = (autoRenameInputBox != null) ? autoRenameInputBox.getValue() : "&6✦ {kit} &7· &b{item}";
                    int renamed = CmiKitManager.massAutoRenameKit(kitIdFinal, tmpl);
                    showToast("⚡ ¡Renombradas " + renamed + " piezas del kit en 1 clic!");
                },
                CmiButton.Variant.ACTION_AMBER
        ));

        // =========================================================================
        // COLUMNA 3: CABECERA DUAL: [ 🍁 Multi-AutoLore ] Y [ 📦 Shulker Box ]
        // =========================================================================
        int col3InnerX = col3X + 6;
        int col3InnerW = col3W - 12;

        int halfC3 = (col3InnerW - 4) / 2;
        CmiButton tabLoreBtn = new CmiButton(col3InnerX, topY + 4, halfC3, 16, Component.literal("🍁 Multi-AutoLore"), b -> {
            col3Tab = 0;
            rebuildWidgets();
        }, (col3Tab == 0) ? CmiButton.Variant.TAB : CmiButton.Variant.CHIP);
        tabLoreBtn.setSelected(col3Tab == 0);
        this.addRenderableWidget(tabLoreBtn);

        CmiButton tabShulkerBtn = new CmiButton(col3InnerX + halfC3 + 4, topY + 4, halfC3, 16, Component.literal("📦 Shulker 27s"), b -> {
            col3Tab = 1;
            rebuildWidgets();
        }, (col3Tab == 1) ? CmiButton.Variant.TAB : CmiButton.Variant.CHIP);
        tabShulkerBtn.setSelected(col3Tab == 1);
        this.addRenderableWidget(tabShulkerBtn);

        if (col3Tab == 0) {
            // MODO 0: MULTI-AUTOLORE DROPDOWN & EDITOR DE PLANTILLA
            int dropY = topY + 24;

            // Selector Dropdown Multi-AutoLore (<autolore1>, <autolore2>, etc.)
            String dropLabel = "Perfil: <autolore" + selectedAutoLoreIndex + ">";
            this.addRenderableWidget(new CmiButton(col3InnerX, dropY, col3InnerW - 80, 16, Component.literal(dropLabel), b -> {
                autoLoreDropdownOpen = !autoLoreDropdownOpen;
                rebuildWidgets();
            }, CmiButton.Variant.TAB));

            // Toggle SmallCaps independiente
            CmiButton scBtn = new CmiButton(col3InnerX + col3InnerW - 76, dropY, 76, 16, Component.literal(autoLoreSmallCaps ? "🛡️ SC: ON" : "🛡️ SC: OFF"), b -> {
                autoLoreSmallCaps = !autoLoreSmallCaps;
                b.setMessage(Component.literal(autoLoreSmallCaps ? "🛡️ SC: ON" : "🛡️ SC: OFF"));
            }, CmiButton.Variant.CHIP);
            scBtn.setSelected(autoLoreSmallCaps);
            this.addRenderableWidget(scBtn);

            // Caja de edición de plantilla
            int loreEditorY = dropY + 22;
            int loreEditorH = bodyH - 120;

            this.autoLoreBox = MultiLineEditBox.builder()
                    .setX(col3InnerX)
                    .setY(loreEditorY)
                    .setShowBackground(true)
                    .setShowDecorations(true)
                    .setPlaceholder(Component.literal("§8Plantilla <autolore" + selectedAutoLoreIndex + ">...\nUsa <enchants>, <customenchants>, <atributes>"))
                    .build(this.font, col3InnerW, Math.max(50, loreEditorH), Component.literal("AutoLore Template"));

            this.autoLoreBox.setCharacterLimit(32767);
            String initialTmpl = (finalActiveKd != null) ? finalActiveKd.getAutoLoreTemplate(selectedAutoLoreIndex) : "";
            this.autoLoreBox.setValue(initialTmpl);
            this.autoLoreBox.setValueListener(val -> {
                if (finalActiveKd != null) {
                    finalActiveKd.setAutoLoreTemplate(selectedAutoLoreIndex, val);
                    CmiKitManager.saveKits();
                }
            });
            this.addRenderableWidget(this.autoLoreBox);

            // Menú flotante cuando el dropdown está abierto
            if (autoLoreDropdownOpen) {
                int dropMenuY = dropY + 18;
                for (int p = 1; p <= 3; p++) {
                    final int pIdx = p;
                    boolean isP = (selectedAutoLoreIndex == pIdx);
                    String pLabel = "<autolore" + pIdx + ">: " + (pIdx == 1 ? "Principal" : (pIdx == 2 ? "Secundario" : "Rush"));
                    this.addRenderableWidget(new CmiButton(col3InnerX, dropMenuY + (p - 1) * 18, col3InnerW - 80, 17, Component.literal(pLabel), b -> {
                        selectedAutoLoreIndex = pIdx;
                        autoLoreDropdownOpen = false;
                        if (finalActiveKd != null) {
                            autoLoreBox.setValue(finalActiveKd.getAutoLoreTemplate(selectedAutoLoreIndex));
                        }
                        rebuildWidgets();
                    }, isP ? CmiButton.Variant.TAB : CmiButton.Variant.CHIP));
                }
            }

            // Fila de botones de tags rápidos
            int tagBtnY = loreEditorY + Math.max(50, loreEditorH) + 4;
            int tBtnW = Math.max(34, (col3InnerW - 12) / 4);

            this.addRenderableWidget(new CmiButton(col3InnerX, tagBtnY, tBtnW, 16, Component.literal("+<autolore>"), btn -> insertTagIntoAutoLore("\n<autolore" + selectedAutoLoreIndex + ">"), CmiButton.Variant.CHIP));
            this.addRenderableWidget(new CmiButton(col3InnerX + tBtnW + 4, tagBtnY, tBtnW, 16, Component.literal("+<enchants>"), btn -> insertTagIntoAutoLore("\n<enchants>"), CmiButton.Variant.CHIP));
            this.addRenderableWidget(new CmiButton(col3InnerX + (tBtnW + 4) * 2, tagBtnY, tBtnW, 16, Component.literal("+<custom..>"), btn -> insertTagIntoAutoLore("\n<customenchants>"), CmiButton.Variant.CHIP));
            this.addRenderableWidget(new CmiButton(col3InnerX + (tBtnW + 4) * 3, tagBtnY, tBtnW, 16, Component.literal("+<atributes>"), btn -> insertTagIntoAutoLore("\n<atributes>"), CmiButton.Variant.CHIP));

            // Botón Heroico Esmeralda: [ ✔ APLICAR AUTOLORE A TODO EL KIT (36 SLOTS) ]
            this.addRenderableWidget(new CmiButton(
                    col3InnerX, tagBtnY + 20, col3InnerW, 22,
                    Component.literal("✔ APLICAR AUTOLORE (36 SLOTS)"),
                    btn -> {
                        if (this.autoLoreBox != null) {
                            String tmpl = this.autoLoreBox.getValue();
                            CmiKitManager.applyAutoLoreToKit(kitIdFinal, tmpl, autoLoreSmallCaps);
                            showToast("✔ AutoLore aplicado a los 36 slots del kit");
                        }
                    },
                    CmiButton.Variant.HERO_EMERALD
            ));

        } else {
            // MODO 1: DROPDOWN SHULKER BOX (PANEL 3x9 = 27 SLOTS) & SELECTOR DE 16 TINTES
            int shulkerTopY = topY + 24;

            // Selector de 16 Tintes en píldoras circulares
            int dyeBtnW = Math.max(16, (col3InnerW - 15 * 2) / 16);
            for (int i = 0; i < 16; i++) {
                final String colorName = CmiKitManager.SHULKER_COLORS[i];
                boolean isSelColor = colorName.equalsIgnoreCase(selectedShulkerColor);
                CmiButton dyeBtn = new CmiButton(col3InnerX + i * (dyeBtnW + 2), shulkerTopY, dyeBtnW, 14, Component.literal("●"), b -> {
                    selectedShulkerColor = colorName;
                    CmiKitManager.setSelectedShulkerColor(colorName);
                    showToast("🎨 Shulker Color: " + colorName);
                    rebuildWidgets();
                }, isSelColor ? CmiButton.Variant.TAB : CmiButton.Variant.CHIP);
                dyeBtn.setSelected(isSelColor);
                this.addRenderableWidget(dyeBtn);
            }

            int actShulkerY = topY + bodyH - 32;
            this.addRenderableWidget(new CmiButton(col3InnerX, actShulkerY, col3InnerW / 2 - 2, 20, Component.literal("📦 Dar Shulker"), b -> {
                if (this.minecraft != null) {
                    String kitName = (finalActiveKd != null) ? finalActiveKd.name : kitIdFinal;
                    CmiKitManager.giveCustomShulkerBox(this.minecraft, kitName, shulkerSlots, selectedShulkerColor);
                    showToast("📦 Shulker entregada en tu mano");
                }
            }, CmiButton.Variant.HERO_EMERALD));

            this.addRenderableWidget(new CmiButton(col3InnerX + col3InnerW / 2 + 2, actShulkerY, col3InnerW / 2 - 2, 20, Component.literal("📋 /give Cmd"), b -> {
                if (this.minecraft != null) {
                    String kitName = (finalActiveKd != null) ? finalActiveKd.name : kitIdFinal;
                    String cmd = CmiKitManager.generateGiveCustomShulkerCommand(kitName, shulkerSlots, selectedShulkerColor);
                    this.minecraft.keyboardHandler.setClipboard(cmd);
                    showToast("📋 Comando /give Shulker copiado al portapapeles");
                }
            }, CmiButton.Variant.ACTION_AMBER));
        }

        // =========================================================================
        // BARRA INFERIOR PERSISTENTE (ACCIONES GLOBALES)
        // =========================================================================
        int botY = this.height - 28;

        this.addRenderableWidget(new CmiButton(
                col1X, botY, 180, 22,
                Component.literal("➕ Guardar Inv como Nuevo Kit"),
                btn -> createNewKitFromInventory(),
                CmiButton.Variant.HERO_EMERALD
        ));

        this.addRenderableWidget(new CmiButton(
                col1X + 186, botY, 150, 22,
                Component.literal("📋 Copiar /give Shulker"),
                btn -> {
                    if (this.minecraft != null) {
                        CmiKitManager.copyGiveShulkerCommand(this.minecraft, kitIdFinal, selectedShulkerColor);
                        showToast("✔ Comando /give Shulker copiado al portapapeles");
                    }
                },
                CmiButton.Variant.TAB
        ));

        this.addRenderableWidget(new CmiButton(
                col1X + 342, botY, 150, 22,
                Component.literal("🎨 Shulker (16 Tintes)"),
                btn -> {
                    col3Tab = 1;
                    rebuildWidgets();
                },
                CmiButton.Variant.ACTION_AMBER
        ));
    }

    private void handleUndo() {
        if (!history.canUndo()) return;
        ItemStackHistory.Snapshot s = history.undo();
        if (s != null) {
            showToast("↩ Deshecho");
            updateUndoRedoButtonStates();
        }
    }

    private void handleRedo() {
        if (!history.canRedo()) return;
        ItemStackHistory.Snapshot s = history.redo();
        if (s != null) {
            showToast("↪ Rehecho");
            updateUndoRedoButtonStates();
        }
    }

    private void updateUndoRedoButtonStates() {
        if (undoBtn != null) undoBtn.active = history.canUndo();
        if (redoBtn != null) redoBtn.active = history.canRedo();
    }

    private void insertTagIntoAutoLore(String tag) {
        if (autoLoreBox != null) {
            String val = autoLoreBox.getValue();
            autoLoreBox.setValue(val + tag);
        }
    }

    private List<KitData> getFilteredKits() {
        List<KitData> all = CmiKitManager.getAllKits();
        String q = (kitSearchBox != null) ? kitSearchBox.getValue().trim().toLowerCase() : "";
        List<KitData> res = new ArrayList<>();
        for (KitData kd : all) {
            boolean matchCat = selectedCategory.equalsIgnoreCase("Todos") || kd.category.equalsIgnoreCase(selectedCategory);
            boolean matchQuery = q.isEmpty() || kd.name.toLowerCase().contains(q) || kd.id.toLowerCase().contains(q);
            if (matchCat && matchQuery) {
                res.add(kd);
            }
        }
        return res;
    }

    private void cycleKitCategory(String kitId) {
        KitData kd = CmiKitManager.getKit(kitId);
        if (kd == null) return;
        String[] cats = {"Espadas", "Herramientas", "Armaduras", "Arcos", "Pociones", "Bloques"};
        for (int i = 0; i < cats.length; i++) {
            if (cats[i].equalsIgnoreCase(kd.category)) {
                kd.category = cats[(i + 1) % cats.length];
                CmiKitManager.saveKits();
                showToast("✏ Categoría cambiada a: " + kd.category);
                this.rebuildWidgets();
                return;
            }
        }
        kd.category = "Espadas";
        CmiKitManager.saveKits();
        this.rebuildWidgets();
    }

    private void saveInventoryToCurrentKit(String kitId) {
        if (this.minecraft == null || this.minecraft.player == null) return;
        var inv = this.minecraft.player.getInventory();
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            items.add(inv.getItem(i).copy());
        }
        CmiKitManager.saveFullKit(kitId, items);
        showToast("💾 Inventario guardado en kit '" + kitId + "'");
        this.rebuildWidgets();
    }

    private void createNewKitFromInventory() {
        if (this.minecraft == null || this.minecraft.player == null) return;
        String newId = "kit_custom_" + (CmiKitManager.getAllKits().size() + 1);
        String newName = "Kit Personal " + (CmiKitManager.getAllKits().size() + 1);
        CmiKitManager.setActiveKitName(newName);
        saveInventoryToCurrentKit(newId);
        selectedKitId = newId;
        showToast("➕ Creado nuevo kit: " + newName);
        this.rebuildWidgets();
    }

    private void showToast(String msg) {
        this.statusToast = msg;
        this.statusToastTime = System.currentTimeMillis();
    }

    // =========================================================================
    // ENTRADA DE RATÓN: DRAG & DROP Y SHIFT+CLIC
    // =========================================================================

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.hasControlDown()) {
            if (event.key() == GLFW.GLFW_KEY_Z) {
                if (event.hasShiftDown()) {
                    handleRedo();
                } else {
                    handleUndo();
                }
                return true;
            } else if (event.key() == GLFW.GLFW_KEY_Y) {
                handleRedo();
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDown) {
        double mx = event.x();
        double my = event.y();

        KitData kd = CmiKitManager.getKit(selectedKitId);
        if (kd == null) kd = CmiKitManager.getActiveKit();

        int topY = 32;
        int col1W = Math.max(140, (int) (this.width * 0.25f));
        int col2W = Math.max(220, (int) (this.width * 0.45f));
        int col2X = 8 + col1W + 6;
        int col3X = col2X + col2W + 6;
        int col3W = this.width - col1W - col2W - 24;

        if (isDown) {
            boolean isShift = (this.minecraft != null && this.minecraft.getWindow() != null)
                    && (InputConstants.isKeyDown(this.minecraft.getWindow(), InputConstants.KEY_LSHIFT)
                    || InputConstants.isKeyDown(this.minecraft.getWindow(), InputConstants.KEY_RSHIFT));

            // 1. Clic en Bandeja Central Inferior (Kit items 0..35)
            int trayX = col2X + 8;
            int trayY = topY + 74;
            int slotS = 18;
            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 9; c++) {
                    int idx = r * 9 + c;
                    int sx = trayX + c * 20;
                    int sy = trayY + r * 20;

                    if (mx >= sx && mx <= sx + slotS && my >= sy && my <= sy + slotS) {
                        if (kd != null && idx < kd.items.size()) {
                            ItemStack clicked = kd.items.get(idx);
                            if (isShift) {
                                // Shift+Clic: transferir al primer slot libre de la Shulker Box
                                for (int s = 0; s < 27; s++) {
                                    if (shulkerSlots.get(s).isEmpty()) {
                                        shulkerSlots.set(s, clicked.copy());
                                        showToast("📦 Transferido a ranura Shulker #" + (s + 1));
                                        break;
                                    }
                                }
                            } else {
                                // Drag & Drop: recoger ítem
                                if (!clicked.isEmpty()) {
                                    this.draggedStack = clicked.copy();
                                    this.draggedSourceIndex = idx;
                                    this.draggedFromShulker = false;
                                }
                            }
                            return true;
                        }
                    }
                }
            }

            // 2. Clic en Shulker Box Slots (si Columna 3 está en modo Shulker)
            if (col3Tab == 1) {
                int sGridX = col3X + 12;
                int sGridY = topY + 44;
                for (int r = 0; r < 3; r++) {
                    for (int c = 0; c < 9; c++) {
                        int sIdx = r * 9 + c;
                        int sx = sGridX + c * 18;
                        int sy = sGridY + r * 18;

                        if (mx >= sx && mx <= sx + 16 && my >= sy && my <= sy + 16) {
                            ItemStack sItem = shulkerSlots.get(sIdx);
                            if (isShift) {
                                // Shift+Clic: vaciar ranura de Shulker
                                shulkerSlots.set(sIdx, ItemStack.EMPTY);
                                showToast("✕ Ranura Shulker #" + (sIdx + 1) + " vaciada");
                            } else {
                                if (!sItem.isEmpty()) {
                                    this.draggedStack = sItem.copy();
                                    this.draggedSourceIndex = sIdx;
                                    this.draggedFromShulker = true;
                                }
                            }
                            return true;
                        }
                    }
                }
            }
        } else {
            // Mouse Released: Soltar ítem arrastrado
            if (!this.draggedStack.isEmpty()) {
                // Verificar si se soltó sobre una ranura de Shulker Box
                if (col3Tab == 1) {
                    int sGridX = col3X + 12;
                    int sGridY = topY + 44;
                    for (int r = 0; r < 3; r++) {
                        for (int c = 0; c < 9; c++) {
                            int sIdx = r * 9 + c;
                            int sx = sGridX + c * 18;
                            int sy = sGridY + r * 18;

                            if (mx >= sx && mx <= sx + 16 && my >= sy && my <= sy + 16) {
                                shulkerSlots.set(sIdx, this.draggedStack.copy());
                                showToast("✔ Ítem colocado en Shulker #" + (sIdx + 1));
                                this.draggedStack = ItemStack.EMPTY;
                                this.draggedSourceIndex = -1;
                                return true;
                            }
                        }
                    }
                }

                // Si se soltó sobre otra casilla del kit, intercambiar
                int trayX = col2X + 8;
                int trayY = topY + 74;
                for (int r = 0; r < 4; r++) {
                    for (int c = 0; c < 9; c++) {
                        int idx = r * 9 + c;
                        int sx = trayX + c * 20;
                        int sy = trayY + r * 20;

                        if (mx >= sx && mx <= sx + 18 && my >= sy && my <= sy + 18) {
                            if (kd != null && idx < kd.items.size()) {
                                if (!draggedFromShulker && draggedSourceIndex >= 0 && draggedSourceIndex < kd.items.size()) {
                                    ItemStack targetItem = kd.items.get(idx);
                                    kd.items.set(idx, draggedStack);
                                    kd.items.set(draggedSourceIndex, targetItem);
                                    CmiKitManager.saveKits();
                                    showToast("✔ Ítems intercambiados");
                                }
                            }
                            this.draggedStack = ItemStack.EMPTY;
                            this.draggedSourceIndex = -1;
                            return true;
                        }
                    }
                }

                this.draggedStack = ItemStack.EMPTY;
                this.draggedSourceIndex = -1;
            }
        }

        return super.mouseClicked(event, isDown);
    }

    // =========================================================================
    // RENDERIZADO VISUAL PRINCIPAL
    // =========================================================================

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int topY = 32;
        int footerH = 34;
        int bodyH = this.height - topY - footerH - 4;

        int col1W = Math.max(140, (int) (this.width * 0.25f));
        int col2W = Math.max(220, (int) (this.width * 0.45f));
        int col3W = this.width - col1W - col2W - 24;

        int col1X = 8;
        int col2X = col1X + col1W + 6;
        int col3X = col2X + col2W + 6;

        KitData kd = CmiKitManager.getKit(selectedKitId);
        if (kd == null) kd = CmiKitManager.getActiveKit();

        // Logo y título
        CmiUiTheme.drawLogoIcon(guiGraphics, 10, 6, 36, 17);
        guiGraphics.text(this.font, Component.literal("§b§lCMI §7· Gestor de Kits Personales"), 52, 9, CmiUiTheme.COLOR_WHITE);

        // =========================================================================
        // COLUMNA 1: HERO SHOWCASE LENS 3D
        // =========================================================================
        CmiUiTheme.drawGlassPanel(guiGraphics, col1X, topY, col1W, bodyH, CmiUiTheme.GLASS_PANEL_BORDER);

        guiGraphics.fill(col1X + 4, topY + 4, col1X + col1W - 4, topY + 20, CmiUiTheme.PANEL_HEADER_BG);
        guiGraphics.centeredText(this.font, Component.literal("§6✦ HERO SHOWCASE ✦"), col1X + col1W / 2, topY + 7, CmiUiTheme.COLOR_AMBER);

        int lensCX = col1X + col1W / 2;
        int lensCY = topY + 54;
        CmiUiTheme.drawHeroShowcaseLens(guiGraphics, col1X + 10, topY + 24, col1W - 20, 60, false);

        if (kd != null && !kd.items.isEmpty()) {
            ItemStack heroItem = kd.items.get(0);
            guiGraphics.item(heroItem, lensCX - 8, lensCY - 8);
            guiGraphics.itemDecorations(this.font, heroItem, lensCX - 8, lensCY - 8);

            // Tooltip flotante nativo en hover
            if (mouseX >= lensCX - 12 && mouseX <= lensCX + 12 && mouseY >= lensCY - 12 && mouseY <= lensCY + 12) {
                guiGraphics.setTooltipForNextFrame(this.font, heroItem, mouseX, mouseY);
            }
        }

        // Título y categoría del kit
        if (kd != null) {
            guiGraphics.centeredText(this.font, Component.literal("§e§l" + kd.name), col1X + col1W / 2, topY + 92, CmiUiTheme.COLOR_AMBER);
            CmiUiTheme.ToolCategory tCat = CmiUiTheme.ToolCategory.fromString(kd.category);
            CmiUiTheme.drawToolBadgePill(guiGraphics, this.font, col1X + 8, topY + 108, tCat);
            guiGraphics.text(this.font, Component.literal("§8Contenido: " + kd.items.size() + " ítems"), col1X + 8, topY + 128, CmiUiTheme.COLOR_MUTED);
        }

        // =========================================================================
        // COLUMNA 2: MATRIZ DE KITS Y BANDEJA CENTRAL INFERIOR
        // =========================================================================
        CmiUiTheme.drawGlassPanel(guiGraphics, col2X, topY, col2W, bodyH, CmiUiTheme.GLASS_PANEL_BORDER);

        guiGraphics.fill(col2X + 4, topY + 4, col2X + col2W - 4, topY + 20, CmiUiTheme.PANEL_HEADER_BG);
        guiGraphics.centeredText(this.font, Component.literal("§b✦ BANDEJA DEL KIT (36 SLOTS) ✦"), col2X + col2W / 2, topY + 7, CmiUiTheme.COLOR_CYAN_NEON);

        // Renderizado de las 36 casillas del kit (4 filas de 9)
        int trayX = col2X + 8;
        int trayY = topY + 74;
        int slotS = 18;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 9; c++) {
                int idx = r * 9 + c;
                int sx = trayX + c * 20;
                int sy = trayY + r * 20;

                boolean hov = mouseX >= sx && mouseX <= sx + slotS && mouseY >= sy && mouseY <= sy + slotS;
                CmiUiTheme.drawSlot(guiGraphics, sx, sy, slotS, hov);

                if (kd != null && idx < kd.items.size()) {
                    ItemStack st = kd.items.get(idx);
                    if (st != null && !st.isEmpty()) {
                        guiGraphics.item(st, sx + 1, sy + 1);
                        guiGraphics.itemDecorations(this.font, st, sx + 1, sy + 1);

                        if (hov && draggedStack.isEmpty()) {
                            guiGraphics.setTooltipForNextFrame(this.font, st, mouseX, mouseY);
                        }
                    }
                }
            }
        }

        guiGraphics.text(this.font, Component.literal("§8Shift+Clic para transferir ítems a la Shulker Box"), trayX, trayY + 84, CmiUiTheme.COLOR_MUTED);

        // =========================================================================
        // COLUMNA 3: DROPDOWN MULTI-AUTOLORE O DROPDOWN SHULKER BOX
        // =========================================================================
        CmiUiTheme.drawGlassPanel(guiGraphics, col3X, topY, col3W, bodyH, CmiUiTheme.GLASS_PANEL_BORDER);

        if (col3Tab == 1) {
            // Renderizado de la Shulker Box (3x9 = 27 Slots)
            int sGridX = col3X + 12;
            int sGridY = topY + 44;

            guiGraphics.text(this.font, Component.literal("§6✦ CONTENIDO SHULKER (27 SLOTS):"), col3X + 10, sGridY - 12, CmiUiTheme.COLOR_AMBER);

            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 9; c++) {
                    int sIdx = r * 9 + c;
                    int sx = sGridX + c * 18;
                    int sy = sGridY + r * 18;

                    boolean sHov = mouseX >= sx && mouseX <= sx + 16 && mouseY >= sy && mouseY <= sy + 16;
                    CmiUiTheme.drawSlot(guiGraphics, sx, sy, 16, sHov);

                    if (sIdx < shulkerSlots.size()) {
                        ItemStack st = shulkerSlots.get(sIdx);
                        if (st != null && !st.isEmpty()) {
                            guiGraphics.item(st, sx, sy);
                            guiGraphics.itemDecorations(this.font, st, sx, sy);

                            if (sHov && draggedStack.isEmpty()) {
                                guiGraphics.setTooltipForNextFrame(this.font, st, mouseX, mouseY);
                            }
                        }
                    }
                }
            }

            guiGraphics.text(this.font, Component.literal("§7Color activo: §b" + selectedShulkerColor.toUpperCase()), col3X + 12, sGridY + 60, CmiUiTheme.COLOR_CYAN_NEON);
            guiGraphics.text(this.font, Component.literal("§8Arrastra ítems o haz Shift+Clic para guardar"), col3X + 12, sGridY + 72, CmiUiTheme.COLOR_MUTED);
        }

        // Renderizado del ítem arrastrado con el cursor (Drag & Drop)
        if (!this.draggedStack.isEmpty()) {
            guiGraphics.item(this.draggedStack, mouseX - 8, mouseY - 8);
            guiGraphics.itemDecorations(this.font, this.draggedStack, mouseX - 8, mouseY - 8);
        }

        // Estado toast
        if (System.currentTimeMillis() - this.statusToastTime < 3500) {
            guiGraphics.centeredText(this.font, Component.literal("§e" + this.statusToast), this.width / 2, this.height - 12, CmiUiTheme.COLOR_AMBER);
        }
    }
}
