package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.client.animated.AnimatedTagRegistry;
import com.fxfps.cmi.client.commands.CmiCommands;
import com.fxfps.cmi.client.config.CmiConfigManager;
import com.fxfps.cmi.client.fonts.UnicodeFontHelper;
import com.fxfps.cmi.client.kits.CmiKitManager;
import com.fxfps.cmi.client.lores.ArgNetLoreManager;
import com.fxfps.cmi.client.lores.ItemModifierColorManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.lwjgl.glfw.GLFW;

import java.util.*;

/**
 * PANTALLA PRINCIPAL DE CHEMANYITEMS (CMI) 💎
 * Refactor Oficial Neo-Capsule Liquid Glass:
 * - Ventana central flotante (Modal) responsiva y centrada en cualquier escala GUI.
 * - Juego en tiempo real a 60+ FPS sin pausa con shaders visibles.
 * - Cabecera con selector de ítem, navegación de pestañas y controles de esquina.
 * - 3 Columnas perfectamente balanceadas (Showcase 3D + Lore en vivo + Irrompible / Stepper,
 *   Nombre + Shaders 21 + Gradientes, Paleta Minecraft + Notepad Lore + Acciones y Badges).
 * - Barra inferior heroica con botón Esmeralda, copia de /give y selector de color activo.
 * - Sistema completo de Undo / Redo (Ctrl+Z / Ctrl+Y), Catálogo deslizable e Inspector F6/F7.
 */
public class CmiEditorScreen extends CmiScreen {

    // Contexto de ranura / kit destino
    private final Slot targetSlot;
    private final int targetContainerSlot;
    private ItemStack targetStack;
    private final String targetKitName;
    private final int targetKitItemIndex;

    // Sistema de Historial Deshacer / Rehacer
    private final ItemStackHistory history = new ItemStackHistory();
    private boolean isHistoryRestoring = false;

    // Estado del ítem en edición
    private String customNameText = "";
    private int itemCount = 1;
    private String rawLoreText = "";
    private boolean smallCapsActive = true;
    private int activeTab = 0; // 0 = Display, 1 = Enchants, 2 = Atributos, 3 = Visibilidad

    // Paleta de colores Minecraft
    private static final char[] COLOR_CODES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final int[] COLOR_RGB = {
            0xFF000000, 0xFF0000AA, 0xFF00AA00, 0xFF00AAAA,
            0xFFAA0000, 0xFFAA00AA, 0xFFFFAA00, 0xFFAAAAAA,
            0xFF555555, 0xFF5555FF, 0xFF55FF55, 0xFF55FFFF,
            0xFFFF5555, 0xFFFF55FF, 0xFFFFFF55, 0xFFFFFFFF
    };
    private static final String[] FORMAT_CODES = {"&l", "&o", "&n", "&m", "&k", "&r"};
    private static final String[] FORMAT_LABELS = {"B", "I", "U", "S", "K", "R"};

    private String activeColorCode = "&b";
    private int activeColorRgb = 0xFF55FFFF;

    // Widgets interactivos
    private EditBox nameInputBox;
    private EditBox countInputBox;
    private MultiLineEditBox loreTextBox;
    private CmiButton smallCapsBtn;

    // Sub-pestañas: Enchants & Atributos
    private EditBox enchantSearchBox;
    private int enchantScrollOffset = 0;
    private final List<Holder.Reference<Enchantment>> filteredEnchants = new ArrayList<>();
    private EditBox attrValueBox;
    private String selectedEyeCategory = "all";

    // Drawer lateral de Catálogo de ítems
    private boolean drawerOpen = false;
    private EditBox drawerSearchBox;
    private String selectedCatalogCategory = "all";
    private int catalogScrollOffset = 0;
    private final List<Item> allCatalogItems = new ArrayList<>();
    private final List<Item> filteredCatalogItems = new ArrayList<>();

    // Toast de notificación temporal
    private String statusToast = "Listo · CMI Suite 26.1.2";
    private long statusToastTime = 0;

    // =========================================================================
    // CONSTRUCTORES CON COMPATIBILIDAD TOTAL
    // =========================================================================

    public CmiEditorScreen() {
        this(null, null, null, -1, null, -1);
    }

    public CmiEditorScreen(Screen parentScreen, Slot slot) {
        this(parentScreen, slot != null ? slot.getItem() : null, slot, -1, null, -1);
    }

    public CmiEditorScreen(Screen parentScreen, ItemStack stack, Slot slot) {
        this(parentScreen, stack, slot, -1, null, -1);
    }

    public CmiEditorScreen(Screen parentScreen, ItemStack stack, int containerSlot) {
        this(parentScreen, stack, null, containerSlot, null, -1);
    }

    public CmiEditorScreen(Screen parentScreen, ItemStack stack, Slot slot, int containerSlot) {
        this(parentScreen, stack, slot, containerSlot, null, -1);
    }

    public CmiEditorScreen(Screen parentScreen, ItemStack stack, int containerSlot, String kitName, int kitItemIndex) {
        this(parentScreen, stack, null, containerSlot, kitName, kitItemIndex);
    }

    public CmiEditorScreen(Screen parentScreen, ItemStack stack, Slot slot, int containerSlot, String kitName, int kitItemIndex) {
        super(Component.literal("CheManyItems (CMI) · Suite 26.1.2"), parentScreen);
        this.targetSlot = slot;
        this.targetContainerSlot = containerSlot;
        this.targetKitName = kitName;
        this.targetKitItemIndex = kitItemIndex;

        if (stack != null && !stack.isEmpty()) {
            this.targetStack = stack.copy();
        } else {
            this.targetStack = new ItemStack(Items.NETHERITE_SWORD);
        }

        this.itemCount = Math.max(1, this.targetStack.getCount());
        if (this.targetStack.has(DataComponents.CUSTOM_NAME)) {
            this.customNameText = this.targetStack.get(DataComponents.CUSTOM_NAME).getString();
        } else {
            this.customNameText = "";
        }

        this.rawLoreText = ArgNetLoreManager.getRawLore(this.targetStack);
        if (this.rawLoreText == null || this.rawLoreText.isEmpty()) {
            this.rawLoreText = ArgNetLoreManager.itemLoreToString(this.targetStack);
        }
        if (this.rawLoreText == null || this.rawLoreText.isEmpty()) {
            this.rawLoreText = "<enchants>\n<customenchants>\n<attributes>";
        }

        // Historial inicial
        history.record(this.targetStack, this.customNameText, this.rawLoreText, this.itemCount, this.smallCapsActive);

        // Catálogo de ítems
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR) {
                this.allCatalogItems.add(item);
            }
        }
        filterCatalogItems("");
    }

    // =========================================================================
    // INICIALIZACIÓN DE WIDGETS RESPONSIVOS Y CENTRADOS
    // =========================================================================

    @Override
    protected void init() {
        super.init();

        // 1. Cálculo del Modal Flotante Centrado
        int winW = Math.min(this.width - 60, 680);
        int winH = Math.min(this.height - 40, 390);
        int winX = (this.width - winW) / 2;
        int winY = (this.height - winH) / 2;

        registerInspectableContainer("modal_card", winX, winY, winW, winH, "Modal Central CMI");

        // Dimensiones del Header y Footer dentro del Card
        int headY = winY + 6;
        int headH = 20;
        int footerH = 22;
        int footerY = winY + winH - footerH - 4;

        // Distribución de 3 Columnas (~26% / ~34% / ~40%)
        int gap = 6;
        int bodyY = winY + 30;
        int bodyH = footerY - bodyY - 4;
        int availW = winW - 16 - (2 * gap);
        int col1W = (int) (availW * 0.26f);
        int col2W = (int) (availW * 0.34f);
        int col3W = availW - col1W - col2W;

        int col1X = winX + 8;
        int col2X = col1X + col1W + gap;
        int col3X = col2X + col2W + gap;

        registerInspectableContainer("col_left", col1X, bodyY, col1W, bodyH, "Columna 1 - Izquierda");
        registerInspectableContainer("col_center", col2X, bodyY, col2W, bodyH, "Columna 2 - Centro");
        registerInspectableContainer("col_right", col3X, bodyY, col3W, bodyH, "Columna 3 - Derecha");

        // =========================================================================
        // SECCIÓN A: BARRA SUPERIOR DEL CARD (HEADER)
        // =========================================================================

        // Selector de Ítem en Mano (Píldora con Dropdown / Catálogo)
        String itemPath = BuiltInRegistries.ITEM.getKey(this.targetStack.getItem()).getPath();
        if (itemPath.length() > 14) itemPath = itemPath.substring(0, 13) + "..";
        CmiButton itemSelectorBtn = new CmiButton(winX + 116, headY, 114, headH, Component.literal(itemPath + " ▼"), btn -> {
            this.drawerOpen = !this.drawerOpen;
            if (this.drawerSearchBox != null) {
                this.drawerSearchBox.setVisible(this.drawerOpen);
            }
        }, CmiButton.Variant.TAB);
        this.addRenderableWidget(applyAndRegisterLayout("btn_item_selector", itemSelectorBtn));

        // Píldoras de Navegación de Pestañas
        int navX = winX + 236;

        CmiButton tabDisplayBtn = new CmiButton(navX, headY, 52, headH, Component.literal("Display"), btn -> {
            this.activeTab = 0;
            this.rebuildWidgets();
        }, CmiButton.Variant.TAB);
        tabDisplayBtn.setSelected(this.activeTab == 0);
        this.addRenderableWidget(applyAndRegisterLayout("tab_display", tabDisplayBtn));

        CmiButton tabEnchantsBtn = new CmiButton(navX + 54, headY, 68, headH, Component.literal("⚡ Enchants"), btn -> {
            this.activeTab = 1;
            refreshFilteredEnchants();
            this.rebuildWidgets();
        }, CmiButton.Variant.TAB);
        tabEnchantsBtn.setSelected(this.activeTab == 1);
        this.addRenderableWidget(applyAndRegisterLayout("tab_enchants", tabEnchantsBtn));

        CmiButton tabAttrsBtn = new CmiButton(navX + 124, headY, 68, headH, Component.literal("⚔ Atributos"), btn -> {
            this.activeTab = 2;
            this.rebuildWidgets();
        }, CmiButton.Variant.TAB);
        tabAttrsBtn.setSelected(this.activeTab == 2);
        this.addRenderableWidget(applyAndRegisterLayout("tab_attributes", tabAttrsBtn));

        CmiButton tabVisibilityBtn = new CmiButton(navX + 194, headY, 74, headH, Component.literal("👁 Visibilidad"), btn -> {
            this.activeTab = 3;
            this.rebuildWidgets();
        }, CmiButton.Variant.TAB);
        tabVisibilityBtn.setSelected(this.activeTab == 3);
        this.addRenderableWidget(applyAndRegisterLayout("tab_visibility", tabVisibilityBtn));

        CmiButton tabKitsBtn = new CmiButton(navX + 270, headY, 54, headH, Component.literal("📦 Kits"), btn -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new CmiKitsScreen(this));
            }
        }, CmiButton.Variant.TAB);
        this.addRenderableWidget(applyAndRegisterLayout("tab_kits", tabKitsBtn));

        // Controles de Esquina: Ajustes [ ⚙ ] y Cerrar [ ✕ ]
        this.addRenderableWidget(applyAndRegisterLayout("btn_settings", new CmiButton(winX + winW - 46, headY, 18, headH, Component.literal("⚙"), btn -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new CmiSettingsScreen(this));
            }
        }, CmiButton.Variant.TAB)));

        this.addRenderableWidget(applyAndRegisterLayout("btn_close", new CmiButton(winX + winW - 24, headY, 18, headH, Component.literal("✕"), btn -> {
            this.closeScreen();
        }, CmiButton.Variant.DANGER)));

        // =========================================================================
        // SECCIÓN B: CUERPO CENTRAL (3 COLUMNAS)
        // =========================================================================

        if (this.activeTab == 0) {
            // ---------------------------------------------------------------------
            // COLUMNA 1 (IZQUIERDA ~26%): SHOWCASE, LORE EN VIVO & STEPPER
            // ---------------------------------------------------------------------
            int showcaseH = 118;
            int previewH = 106;
            int stepperH = 46;

            int stepY = bodyY + showcaseH + previewH + 8;
            int sBtnY = stepY + 20;

            // Fila Irrompible: Botón cápsula toggle
            boolean isUnbrk = this.targetStack.has(DataComponents.UNBREAKABLE);
            CmiButton unbreakBtn = new CmiButton(col1X + 6, stepY + 2, col1W - 12, 16, Component.literal(isUnbrk ? "🛡 Irrompible: ON" : "🛡 Irrompible: OFF"), btn -> {
                toggleUnbreakable();
                this.rebuildWidgets();
            }, isUnbrk ? CmiButton.Variant.HERO_EMERALD : CmiButton.Variant.TAB);
            unbreakBtn.setSelected(isUnbrk);
            this.addRenderableWidget(applyAndRegisterLayout("btn_unbreakable", unbreakBtn));

            // Stepper de Cantidad: [-] [ 1 ] [+] [64] [✕]
            this.addRenderableWidget(applyAndRegisterLayout("btn_step_dec", new CmiButton(col1X + 6, sBtnY, 18, 18, Component.literal("-"), btn -> setCount(this.itemCount - 1), CmiButton.Variant.TAB)));

            this.countInputBox = new EditBox(this.font, col1X + 26, sBtnY, 28, 18, Component.literal("Cant"));
            this.countInputBox.setValue(String.valueOf(this.itemCount));
            this.countInputBox.setResponder(val -> {
                if (isHistoryRestoring) return;
                try {
                    int c = Integer.parseInt(val.trim());
                    if (c > 0 && c <= 999) {
                        this.itemCount = c;
                        this.targetStack.setCount(Math.min(c, 64));
                        recordSnapshot();
                    }
                } catch (Exception ignored) {}
            });
            this.addRenderableWidget(this.countInputBox);

            this.addRenderableWidget(applyAndRegisterLayout("btn_step_inc", new CmiButton(col1X + 56, sBtnY, 18, 18, Component.literal("+"), btn -> setCount(this.itemCount + 1), CmiButton.Variant.TAB)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_step_64", new CmiButton(col1X + 76, sBtnY, 24, 18, Component.literal("64"), btn -> setCount(64), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_step_reset", new CmiButton(col1X + 102, sBtnY, 18, 18, Component.literal("✕"), btn -> setCount(1), CmiButton.Variant.DANGER)));

            // ---------------------------------------------------------------------
            // COLUMNA 2 (CENTRAL ~34%): NOMBRE, SHADERS 21 Y GRADIENTES
            // ---------------------------------------------------------------------
            int nameCardH = 46;

            // Fila 1: Input de Nombre editable + botón de acción ámbar ⚡ /rename
            this.nameInputBox = new EditBox(this.font, col2X + 6, bodyY + 4, col2W - 68, 18, Component.literal("Nombre"));
            this.nameInputBox.setValue(this.customNameText.isEmpty() ? this.targetStack.getHoverName().getString() : this.customNameText);
            this.nameInputBox.setResponder(val -> {
                if (isHistoryRestoring) return;
                this.customNameText = val;
                updateCustomName();
                recordSnapshot();
            });
            this.addRenderableWidget(this.nameInputBox);

            this.addRenderableWidget(applyAndRegisterLayout("btn_rename", new CmiButton(col2X + col2W - 58, bodyY + 4, 52, 18, Component.literal("⚡ /rename"), btn -> {
                if (this.minecraft != null) {
                    CmiCommands.sendRenameCommand(this.minecraft, this.customNameText);
                    showToast("✔ Comando /rename enviado al servidor");
                }
            }, CmiButton.Variant.ACTION_AMBER)));

            // Fila 2: Botones de utilidades SmallCaps y Emojis
            int halfUtilW = (col2W - 16) / 2;
            this.smallCapsBtn = new CmiButton(col2X + 6, bodyY + 24, halfUtilW, 16, Component.literal(this.smallCapsActive ? "🛡 SmallCaps: ON" : "🛡 SmallCaps: OFF"), btn -> toggleSmallCaps(), CmiButton.Variant.TAB);
            this.smallCapsBtn.setSelected(this.smallCapsActive);
            this.addRenderableWidget(applyAndRegisterLayout("btn_smallcaps", this.smallCapsBtn));

            this.addRenderableWidget(applyAndRegisterLayout("btn_emojis", new CmiButton(col2X + 8 + halfUtilW, bodyY + 24, halfUtilW, 16, Component.literal("😜 Emojis"), btn -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(new CmiEmojiPickerScreen(this, this::insertInline));
                }
            }, CmiButton.Variant.TAB)));

            // Panel de Shaders/Animaciones (<ANIMATED>) - Cuadrícula limpia de 21 píldoras
            int animCardY = bodyY + nameCardH + 4;
            int animGridY = animCardY + 18;
            int animCols = 3;
            int animRows = 7;
            int aBtnW = (col2W - 16) / animCols;
            int aBtnH = 14;

            for (int i = 0; i < Math.min(21, AnimatedTagRegistry.PRESETS.size()); i++) {
                var preset = AnimatedTagRegistry.PRESETS.get(i);
                int r = i / animCols;
                int c = i % animCols;
                int bx = col2X + 6 + c * (aBtnW + 2);
                int by = animGridY + r * (aBtnH + 2);

                CmiButton animBtn = new CmiButton(bx, by, aBtnW, aBtnH, Component.literal("<" + preset.tag() + ">"), btn -> {
                    insertTag("<" + preset.tag() + ">");
                    showToast("✔ Shader <" + preset.tag() + "> insertado");
                }, CmiButton.Variant.CHIP);
                animBtn.setAnimatedPresetId(preset.id());
                this.addRenderableWidget(applyAndRegisterLayout("anim_" + preset.tag(), animBtn));
            }

            // Selector Inferior: Colores & Gradientes (HEX / RGB)
            int gradY = animGridY + animRows * (aBtnH + 2) + 6;
            int gBtnW = (col2W - 16) / 2;
            this.addRenderableWidget(applyAndRegisterLayout("btn_grad_fuego", new CmiButton(col2X + 6, gradY + 16, gBtnW, 16, Component.literal("🔥 Fuego Épico"), btn -> insertTag("<gradient:#ff5555:#ffaa00>Texto</gradient>"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_grad_cosmos", new CmiButton(col2X + 8 + gBtnW, gradY + 16, gBtnW, 16, Component.literal("🌌 Cosmos Neón"), btn -> insertTag("<gradient:#00aaff:#aa00ff>Texto</gradient>"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_grad_esmeralda", new CmiButton(col2X + 6, gradY + 34, gBtnW, 16, Component.literal("❇ Esmeralda"), btn -> insertTag("<gradient:#00ff88:#00aaaa>Texto</gradient>"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_grad_oro", new CmiButton(col2X + 8 + gBtnW, gradY + 34, gBtnW, 16, Component.literal("👑 Oro Divino"), btn -> insertTag("<gradient:#ffd700:#ff8c00>Texto</gradient>"), CmiButton.Variant.CHIP)));

            // ---------------------------------------------------------------------
            // COLUMNA 3 (DERECHA ~40%): PALETA MINECRAFT, LORE EDITOR & ACCIONES
            // ---------------------------------------------------------------------
            int palCardH = 46;

            // Paleta de Colores de Minecraft (16 códigos 0-9, a-f)
            int cSize = (col3W - 16) / 16;
            for (int i = 0; i < 16; i++) {
                final int idx = i;
                final String code = "&" + COLOR_CODES[i];
                int bx = col3X + 6 + i * cSize;
                this.addRenderableWidget(applyAndRegisterLayout("pal_c_" + COLOR_CODES[i], new CmiButton(bx, bodyY + 4, cSize - 1, 15, Component.literal(String.valueOf(COLOR_CODES[i])), btn -> {
                    insertInline(code);
                    this.activeColorCode = code;
                    this.activeColorRgb = COLOR_RGB[idx];
                    showToast("✔ Color " + code + " activo");
                }, CmiButton.Variant.DEFAULT)));
            }

            // Fila de Formatos (B, I, U, S, K, R)
            int fSize = (col3W - 16) / 6;
            for (int i = 0; i < 6; i++) {
                final String fcode = FORMAT_CODES[i];
                int bx = col3X + 6 + i * fSize;
                this.addRenderableWidget(applyAndRegisterLayout("pal_fmt_" + FORMAT_LABELS[i], new CmiButton(bx, bodyY + 22, fSize - 2, 16, Component.literal(FORMAT_LABELS[i]), btn -> {
                    insertInline(fcode);
                    showToast("✔ Formato " + fcode + " insertado");
                }, CmiButton.Variant.CHIP)));
            }

            // Editor de Lore Principal (.txt) estilo Notepad
            int loreCardY = bodyY + palCardH + 4;
            int loreEditorH = 120;
            this.loreTextBox = MultiLineEditBox.builder()
                    .setX(col3X + 6)
                    .setY(loreCardY + 16)
                    .setShowBackground(false)
                    .setShowDecorations(false)
                    .setPlaceholder(Component.literal("§8item_lore.txt...\nUsa <enchants>, <customenchants>, <attributes>"))
                    .build(this.font, col3W - 12, loreEditorH - 20, Component.literal("item_lore.txt"));
            this.loreTextBox.setCharacterLimit(Short.MAX_VALUE);
            this.loreTextBox.setValue(this.rawLoreText);
            this.loreTextBox.setValueListener(val -> {
                if (isHistoryRestoring) return;
                this.rawLoreText = val;
                compileLore();
                recordSnapshot();
            });
            this.addRenderableWidget(this.loreTextBox);

            // Botonera de Acción Rápida: ✔ Sig., 📋 AutoLore, ✕ Limpiar, ✨ Limpio (Strip)
            int actY = loreCardY + loreEditorH + 4;
            int actBtnW = (col3W - 18) / 4;
            this.addRenderableWidget(applyAndRegisterLayout("btn_act_next", new CmiButton(col3X + 6, actY, actBtnW, 16, Component.literal("✔ Sig."), btn -> insertInline("\n"), CmiButton.Variant.TAB)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_act_autolore", new CmiButton(col3X + 8 + actBtnW, actY, actBtnW, 16, Component.literal("📋 AutoLore"), btn -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(new CmiAutoLoreScreen(this, this.targetStack, this::rebuildWidgets));
                }
            }, CmiButton.Variant.TAB)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_act_clear", new CmiButton(col3X + 10 + actBtnW * 2, actY, actBtnW, 16, Component.literal("✕ Limpiar"), btn -> clearLore(), CmiButton.Variant.DANGER)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_act_strip", new CmiButton(col3X + 12 + actBtnW * 3, actY, actBtnW, 16, Component.literal("✨ Strip"), btn -> stripLoreColors(), CmiButton.Variant.HERO_EMERALD)));

            // Badges y Atajos de Ubicación y Símbolos
            int badgeRowY = actY + 22;
            int tagW = (col3W - 16) / 3;
            this.addRenderableWidget(applyAndRegisterLayout("btn_tag_ench", new CmiButton(col3X + 6, badgeRowY, tagW, 15, Component.literal("<enchants>"), btn -> insertTag("<enchants>"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_tag_custom", new CmiButton(col3X + 8 + tagW, badgeRowY, tagW, 15, Component.literal("<custom..>"), btn -> insertTag("<customenchants>"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_tag_attrs", new CmiButton(col3X + 10 + tagW * 2, badgeRowY, tagW, 15, Component.literal("<attributes>"), btn -> insertTag("<attributes>"), CmiButton.Variant.CHIP)));

            int symRowY = badgeRowY + 18;
            this.addRenderableWidget(applyAndRegisterLayout("btn_sym_star", new CmiButton(col3X + 6, symRowY, 44, 15, Component.literal("★ Star"), btn -> insertInline("★"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_sym_bolt", new CmiButton(col3X + 52, symRowY, 20, 15, Component.literal("⚡"), btn -> insertInline("⚡"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_sym_crown", new CmiButton(col3X + 74, symRowY, 20, 15, Component.literal("👑"), btn -> insertInline("👑"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_sym_sword", new CmiButton(col3X + 96, symRowY, 20, 15, Component.literal("🗡"), btn -> insertInline("🗡"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_sym_shield", new CmiButton(col3X + 118, symRowY, 20, 15, Component.literal("🛡"), btn -> insertInline("🛡"), CmiButton.Variant.CHIP)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_sym_emojis", new CmiButton(col3X + 140, symRowY, col3W - 146, 15, Component.literal("+336 Emojis"), btn -> {
                if (this.minecraft != null) {
                    this.minecraft.setScreen(new CmiEmojiPickerScreen(this, this::insertInline));
                }
            }, CmiButton.Variant.TAB)));

        } else if (this.activeTab == 1) {
            // PESTAÑA 1: ENCANTAMIENTOS
            int enchInnerW = col2W + col3W + gap;
            this.enchantSearchBox = new EditBox(this.font, col2X, bodyY + 6, enchInnerW - 140, 18, Component.literal("Buscar Enchants"));
            this.enchantSearchBox.setHint(Component.literal("🔍 Buscar encantamiento vanilla..."));
            this.enchantSearchBox.setResponder(val -> {
                refreshFilteredEnchants();
                enchantScrollOffset = 0;
            });
            this.addRenderableWidget(this.enchantSearchBox);

            this.addRenderableWidget(applyAndRegisterLayout("btn_max_all_ench", new CmiButton(col2X + enchInnerW - 136, bodyY + 6, 66, 18, Component.literal("Max All"), btn -> maxAllEnchantments(), CmiButton.Variant.HERO_EMERALD)));
            this.addRenderableWidget(applyAndRegisterLayout("btn_clear_all_ench", new CmiButton(col2X + enchInnerW - 68, bodyY + 6, 68, 18, Component.literal("✕ Limpiar"), btn -> clearAllEnchantments(), CmiButton.Variant.DANGER)));

        } else if (this.activeTab == 2) {
            // PESTAÑA 2: ATRIBUTOS
            int attrInnerW = col2W + col3W + gap;
            int aBtnW = (attrInnerW - 8) / 3;

            this.addRenderableWidget(applyAndRegisterLayout("btn_attr_10k_dmg", new CmiButton(col2X, bodyY + 6, aBtnW, 18, Component.literal("➕ +10000 Daño"), btn -> {
                ArgNetLoreManager.addCustomAttribute(this.targetStack, "generic.attack_damage", "10000");
                compileLore();
                recordSnapshot();
                showToast("✔ +10000 Daño aplicado");
            }, CmiButton.Variant.CHIP)));

            this.addRenderableWidget(applyAndRegisterLayout("btn_attr_40_hp", new CmiButton(col2X + aBtnW + 4, bodyY + 6, aBtnW, 18, Component.literal("➕ +40 Vida (20❤)"), btn -> {
                ArgNetLoreManager.addCustomAttribute(this.targetStack, "generic.max_health", "40");
                compileLore();
                recordSnapshot();
                showToast("✔ +40 Vida aplicada");
            }, CmiButton.Variant.CHIP)));

            this.addRenderableWidget(applyAndRegisterLayout("btn_attr_speed", new CmiButton(col2X + (aBtnW + 4) * 2, bodyY + 6, aBtnW, 18, Component.literal("➕ +0.2 Velocidad"), btn -> {
                ArgNetLoreManager.addCustomAttribute(this.targetStack, "generic.movement_speed", "0.2");
                compileLore();
                recordSnapshot();
                showToast("✔ +0.2 Velocidad aplicada");
            }, CmiButton.Variant.CHIP)));

            this.addRenderableWidget(applyAndRegisterLayout("btn_attr_armor", new CmiButton(col2X, bodyY + 28, aBtnW, 18, Component.literal("➕ +30 Armadura"), btn -> {
                ArgNetLoreManager.addCustomAttribute(this.targetStack, "generic.armor", "30");
                compileLore();
                recordSnapshot();
                showToast("✔ +30 Armadura aplicada");
            }, CmiButton.Variant.CHIP)));

            this.addRenderableWidget(applyAndRegisterLayout("btn_attr_toughness", new CmiButton(col2X + aBtnW + 4, bodyY + 28, aBtnW, 18, Component.literal("➕ +8 Dureza"), btn -> {
                ArgNetLoreManager.addCustomAttribute(this.targetStack, "generic.armor_toughness", "8");
                compileLore();
                recordSnapshot();
                showToast("✔ +8 Dureza aplicada");
            }, CmiButton.Variant.CHIP)));

            this.addRenderableWidget(applyAndRegisterLayout("btn_attr_clear", new CmiButton(col2X + (aBtnW + 4) * 2, bodyY + 28, aBtnW, 18, Component.literal("✕ Limpiar Atributos"), btn -> {
                ArgNetLoreManager.clearCustomAttributes(this.targetStack);
                compileLore();
                recordSnapshot();
                showToast("✕ Atributos custom eliminados");
            }, CmiButton.Variant.DANGER)));

            this.attrValueBox = new EditBox(this.font, col2X, bodyY + 54, 76, 18, Component.literal("Valor Atributo"));
            this.attrValueBox.setValue("1000");
            this.addRenderableWidget(this.attrValueBox);

            this.addRenderableWidget(applyAndRegisterLayout("btn_attr_add_custom", new CmiButton(col2X + 82, bodyY + 54, attrInnerW - 82, 18, Component.literal("➕ Añadir generic.attack_damage"), btn -> {
                String val = (attrValueBox != null) ? attrValueBox.getValue().trim() : "100";
                ArgNetLoreManager.addCustomAttribute(this.targetStack, "generic.attack_damage", val);
                compileLore();
                recordSnapshot();
                showToast("✔ Atributo aplicado: generic.attack_damage +" + val);
            }, CmiButton.Variant.HERO_EMERALD)));

        } else if (this.activeTab == 3) {
            // PESTAÑA 3: VISIBILIDAD & OJOS 👁️
            int visInnerW = col2W + col3W + gap;
            String[] eyeFilterCats = {"all", "vanilla", "custom", "attrs"};
            String[] eyeFilterLabels = {"Todos", "Vanilla", "Custom", "Attrs"};
            int efW = (visInnerW - 6) / 4;
            for (int i = 0; i < 4; i++) {
                final String f = eyeFilterCats[i];
                boolean isF = selectedEyeCategory.equals(f);
                CmiButton fBtn = new CmiButton(col2X + i * (efW + 2), bodyY + 6, efW, 16, Component.literal(eyeFilterLabels[i]), b -> {
                    selectedEyeCategory = f;
                    this.rebuildWidgets();
                }, isF ? CmiButton.Variant.TAB : CmiButton.Variant.CHIP);
                fBtn.setSelected(isF);
                this.addRenderableWidget(applyAndRegisterLayout("btn_eye_filter_" + f, fBtn));
            }
        }

        // =========================================================================
        // SECCIÓN C: BARRA INFERIOR DEL CARD (FOOTER DE ACCIONES)
        // =========================================================================

        // Izquierda: 🎒 Kits & Shulker y 🎲 Gamble
        this.addRenderableWidget(applyAndRegisterLayout("btn_footer_kits", new CmiButton(winX + 8, footerY, 92, footerH, Component.literal("🎒 Kits & Shulker"), btn -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new CmiKitsScreen(this));
            }
        }, CmiButton.Variant.TAB)));

        this.addRenderableWidget(applyAndRegisterLayout("btn_footer_gamble", new CmiButton(winX + 104, footerY, 66, footerH, Component.literal("🎲 Gamble"), btn -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new CmiCustomEnchantsScreen(this));
            }
        }, CmiButton.Variant.TAB)));

        // Derecha: 🔄 Restablecer, 📋 Copiar /give y ✔ APLICAR EN SLOT
        int rightBtnsX = winX + winW - 326;
        this.addRenderableWidget(applyAndRegisterLayout("btn_footer_reset", new CmiButton(rightBtnsX, footerY, 74, footerH, Component.literal("🔄 Restablecer"), btn -> resetEditor(), CmiButton.Variant.TAB)));
        this.addRenderableWidget(applyAndRegisterLayout("btn_footer_copy", new CmiButton(rightBtnsX + 78, footerY, 92, footerH, Component.literal("📋 Copiar /give"), btn -> copyGiveCommand(), CmiButton.Variant.TAB)));
        this.addRenderableWidget(applyAndRegisterLayout("btn_footer_apply", new CmiButton(rightBtnsX + 174, footerY, 144, footerH, Component.literal("✔ APLICAR EN SLOT"), btn -> applyItemDirectly(), CmiButton.Variant.HERO_EMERALD)));

        // Drawer lateral de Catálogo de Ítems
        this.drawerSearchBox = new EditBox(this.font, 12, headY + 28, 296, 18, Component.literal("Buscar Catálogo"));
        this.drawerSearchBox.setHint(Component.literal("🔍 Filtrar ítems por nombre o ID..."));
        this.drawerSearchBox.setResponder(this::filterCatalogItems);
        this.drawerSearchBox.setVisible(this.drawerOpen);
        this.addRenderableWidget(this.drawerSearchBox);
    }

    // =========================================================================
    // LÓGICA DE CONTROL, EDICIÓN Y COMPILACIÓN
    // =========================================================================

    private void recordSnapshot() {
        if (isHistoryRestoring) return;
        history.record(this.targetStack, this.customNameText, this.rawLoreText, this.itemCount, this.smallCapsActive);
    }

    private void handleUndo() {
        if (!history.canUndo()) return;
        ItemStackHistory.Snapshot prev = history.undo();
        if (prev != null) {
            restoreSnapshot(prev);
            showToast("↩ Deshecho (" + history.getUndoCount() + " restantes)");
        }
    }

    private void handleRedo() {
        if (!history.canRedo()) return;
        ItemStackHistory.Snapshot next = history.redo();
        if (next != null) {
            restoreSnapshot(next);
            showToast("↪ Rehecho");
        }
    }

    private void restoreSnapshot(ItemStackHistory.Snapshot s) {
        this.isHistoryRestoring = true;
        try {
            this.targetStack = s.stack.copy();
            this.customNameText = s.customName;
            this.rawLoreText = s.rawLore;
            this.itemCount = s.count;
            this.smallCapsActive = s.smallCaps;

            if (this.nameInputBox != null) this.nameInputBox.setValue(this.customNameText);
            if (this.countInputBox != null) this.countInputBox.setValue(String.valueOf(this.itemCount));
            if (this.loreTextBox != null) this.loreTextBox.setValue(this.rawLoreText);

            compileLore();
        } finally {
            this.isHistoryRestoring = false;
        }
    }

    private void setCount(int c) {
        this.itemCount = Math.max(1, Math.min(999, c));
        this.targetStack.setCount(Math.min(this.itemCount, 64));
        if (this.countInputBox != null) {
            this.countInputBox.setValue(String.valueOf(this.itemCount));
        }
        recordSnapshot();
    }

    private void toggleUnbreakable() {
        recordSnapshot();
        if (this.targetStack.has(DataComponents.UNBREAKABLE)) {
            this.targetStack.remove(DataComponents.UNBREAKABLE);
            showToast("🛡 Irrompible: Desactivado");
        } else {
            this.targetStack.set(DataComponents.UNBREAKABLE, Unit.INSTANCE);
            showToast("🛡 Irrompible: Activado");
        }
        compileLore();
        recordSnapshot();
    }

    private void updateCustomName() {
        if (this.customNameText == null || this.customNameText.trim().isEmpty()) {
            this.targetStack.remove(DataComponents.CUSTOM_NAME);
        } else {
            this.targetStack.set(DataComponents.CUSTOM_NAME, UnicodeFontHelper.parseFormattedComponent(this.customNameText));
        }
    }

    private void insertTag(String tag) {
        if (tag == null || tag.isEmpty()) return;
        if (this.loreTextBox != null) {
            String curr = this.loreTextBox.getValue();
            if (curr == null || curr.isEmpty()) {
                this.loreTextBox.setValue(tag);
            } else {
                this.loreTextBox.setValue(curr + "\n" + tag);
            }
            this.rawLoreText = this.loreTextBox.getValue();
        } else {
            this.rawLoreText = (this.rawLoreText != null && !this.rawLoreText.isEmpty()) ? (this.rawLoreText + "\n" + tag) : tag;
        }
        compileLore();
        recordSnapshot();
    }

    private void insertInline(String text) {
        if (text == null || text.isEmpty()) return;
        if (this.loreTextBox != null) {
            String curr = this.loreTextBox.getValue();
            this.loreTextBox.setValue((curr != null ? curr : "") + text);
            this.rawLoreText = this.loreTextBox.getValue();
        } else {
            this.rawLoreText = (this.rawLoreText != null ? this.rawLoreText : "") + text;
        }
        compileLore();
        recordSnapshot();
    }

    private void toggleSmallCaps() {
        this.smallCapsActive = !this.smallCapsActive;
        if (this.smallCapsBtn != null) {
            this.smallCapsBtn.setMessage(Component.literal(this.smallCapsActive ? "🛡 SmallCaps: ON" : "🛡 SmallCaps: OFF"));
            this.smallCapsBtn.setSelected(this.smallCapsActive);
        }
        compileLore();
        recordSnapshot();
        showToast("🛡 SmallCaps: " + (this.smallCapsActive ? "Activado" : "Desactivado"));
    }

    private void clearLore() {
        this.rawLoreText = "";
        if (this.loreTextBox != null) {
            this.loreTextBox.setValue("");
        }
        compileLore();
        recordSnapshot();
        showToast("✕ Lore limpiado");
    }

    private void stripLoreColors() {
        if (this.rawLoreText != null) {
            String stripped = this.rawLoreText.replaceAll("&[0-9a-fk-orA-FK-OR]", "")
                    .replaceAll("§[0-9a-fk-orA-FK-OR]", "");
            this.rawLoreText = stripped;
            if (this.loreTextBox != null) {
                this.loreTextBox.setValue(stripped);
            }
            compileLore();
            recordSnapshot();
            showToast("✨ Lore limpio (colores removidos)");
        }
    }

    private void resetEditor() {
        this.customNameText = "";
        this.rawLoreText = "<enchants>\n<customenchants>\n<attributes>";
        this.itemCount = 1;
        this.targetStack.setCount(1);
        this.targetStack.remove(DataComponents.CUSTOM_NAME);
        if (this.nameInputBox != null) this.nameInputBox.setValue("");
        if (this.countInputBox != null) this.countInputBox.setValue("1");
        if (this.loreTextBox != null) this.loreTextBox.setValue(this.rawLoreText);
        compileLore();
        recordSnapshot();
        showToast("🔄 Editor restablecido");
    }

    private void compileLore() {
        if (this.targetStack != null && this.rawLoreText != null) {
            ArgNetLoreManager.compileAndApplyLore(this.targetStack, this.rawLoreText, this.smallCapsActive);
        }
    }

    private void selectItemFromCatalog(Item newItem) {
        recordSnapshot();
        this.targetStack = ArgNetLoreManager.transmuteItemPreservingComponents(this.targetStack, newItem, this.itemCount);
        if (this.customNameText == null || this.customNameText.isEmpty()) {
            this.customNameText = this.targetStack.getHoverName().getString();
        }
        if (this.nameInputBox != null) {
            this.nameInputBox.setValue(this.customNameText);
        }
        compileLore();
        recordSnapshot();

        this.drawerOpen = false;
        if (this.drawerSearchBox != null) {
            this.drawerSearchBox.setVisible(false);
        }
        this.rebuildWidgets();
        showToast("✔ Material cambiado a: " + newItem.getName(newItem.getDefaultInstance()).getString());
    }

    private void filterCatalogItems(String query) {
        String q = (query != null) ? query.trim().toLowerCase(Locale.ROOT) : "";
        this.filteredCatalogItems.clear();
        for (Item item : this.allCatalogItems) {
            String path = BuiltInRegistries.ITEM.getKey(item).getPath().toLowerCase(Locale.ROOT);
            String name = item.getName(item.getDefaultInstance()).getString().toLowerCase(Locale.ROOT);

            boolean matchesCategory = switch (this.selectedCatalogCategory) {
                case "weapons" -> path.contains("sword") || path.contains("axe") || path.contains("mace") || path.contains("trident");
                case "tools" -> path.contains("pickaxe") || path.contains("shovel") || path.contains("hoe") || path.contains("shears");
                case "armor" -> path.contains("helmet") || path.contains("chestplate") || path.contains("leggings") || path.contains("boots") || path.contains("shield");
                case "blocks" -> item instanceof BlockItem;
                case "potions" -> path.contains("potion") || path.contains("arrow");
                default -> true;
            };

            if (matchesCategory && (q.isEmpty() || path.contains(q) || name.contains(q))) {
                this.filteredCatalogItems.add(item);
            }
        }
    }

    private void refreshFilteredEnchants() {
        this.filteredEnchants.clear();
        if (this.minecraft != null && this.minecraft.level != null) {
            var registry = this.minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            String q = (this.enchantSearchBox != null) ? this.enchantSearchBox.getValue().trim().toLowerCase(Locale.ROOT) : "";
            for (Holder.Reference<Enchantment> holder : registry.listElements().toList()) {
                String path = holder.key().identifier().getPath().toLowerCase(Locale.ROOT);
                String name = holder.value().description().getString().toLowerCase(Locale.ROOT);
                if (q.isEmpty() || path.contains(q) || name.contains(q)) {
                    this.filteredEnchants.add(holder);
                }
            }
        }
    }

    private void setEnchantLevel(Holder<Enchantment> holder, int newLevel) {
        ItemEnchantments current = this.targetStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
        setMutableEnchantLevel(mutable, holder, Math.max(0, newLevel));
        this.targetStack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
        compileLore();
        recordSnapshot();
    }

    private void maxAllEnchantments() {
        if (this.minecraft != null && this.minecraft.level != null) {
            var registry = this.minecraft.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            ItemEnchantments current = this.targetStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
            ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(current);
            for (Holder.Reference<Enchantment> ench : registry.listElements().toList()) {
                if (ench.value().canEnchant(this.targetStack)) {
                    setMutableEnchantLevel(mutable, ench, ench.value().getMaxLevel());
                }
            }
            this.targetStack.set(DataComponents.ENCHANTMENTS, mutable.toImmutable());
            compileLore();
            recordSnapshot();
            showToast("✔ Encantamientos vanilla aplicados");
        }
    }

    private void clearAllEnchantments() {
        this.targetStack.set(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        compileLore();
        recordSnapshot();
        showToast("✕ Encantamientos eliminados");
    }

    private void copyGiveCommand() {
        if (this.minecraft == null) return;
        String itemId = BuiltInRegistries.ITEM.getKey(this.targetStack.getItem()).toString();
        String cmd = "/give @p " + itemId + " " + this.itemCount;
        this.minecraft.keyboardHandler.setClipboard(cmd);
        showToast("📋 Comando /give copiado al portapapeles");
    }

    private void applyItemDirectly() {
        compileLore();
        updateCustomName();

        if (this.minecraft != null && this.minecraft.player != null) {
            Inventory inv = this.minecraft.player.getInventory();

            if (this.targetSlot != null) {
                this.targetSlot.set(this.targetStack.copy());
                this.targetSlot.setChanged();
            } else if (this.targetContainerSlot >= 0 && this.targetContainerSlot < inv.getContainerSize()) {
                inv.setItem(this.targetContainerSlot, this.targetStack.copy());
                inv.setChanged();
            } else {
                int sel = inv.getSelectedSlot();
                inv.setItem(sel, this.targetStack.copy());
                this.minecraft.player.setItemInHand(InteractionHand.MAIN_HAND, this.targetStack.copy());
                inv.setChanged();
            }

            if (this.minecraft.player.hasInfiniteMaterials() && this.minecraft.gameMode != null) {
                int slotIndex = (this.targetContainerSlot >= 0) ? this.targetContainerSlot : (36 + inv.getSelectedSlot());
                this.minecraft.gameMode.handleCreativeModeItemAdd(this.targetStack.copy(), slotIndex);
            }

            if (this.targetKitName != null && this.targetKitItemIndex >= 0) {
                CmiKitManager.updateItemInKit(this.targetKitName, this.targetKitItemIndex, this.targetStack);
            }

            this.minecraft.player.sendSystemMessage(Component.literal("§a[CMI] ✔ Ítem aplicado con éxito al slot."));
        }
        this.closeScreen();
    }

    private void showToast(String msg) {
        this.statusToast = msg;
        this.statusToastTime = System.currentTimeMillis();
    }

    protected void rebuildWidgets() {
        this.rebuildScreenPreservingState();
    }

    // =========================================================================
    // RESPALDO Y RESTAURACIÓN DE ESTADO EN HOT-RELOAD
    // =========================================================================

    @Override
    protected void saveScreenState() {
        if (this.nameInputBox != null) this.customNameText = this.nameInputBox.getValue();
        if (this.loreTextBox != null) this.rawLoreText = this.loreTextBox.getValue();
    }

    @Override
    protected void restoreScreenState() {
        if (this.nameInputBox != null) this.nameInputBox.setValue(this.customNameText);
        if (this.loreTextBox != null) this.loreTextBox.setValue(this.rawLoreText);
    }

    // =========================================================================
    // ENTRADA DE TECLADO Y RATÓN
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
        if (isDown) {
            double mx = event.x();
            double my = event.y();

            // Clics en el Drawer lateral si está abierto
            if (this.drawerOpen && mx >= 0 && mx <= 320) {
                int catY = 46;
                String[] cats = {"all", "weapons", "tools", "armor", "blocks", "potions"};
                for (int i = 0; i < cats.length; i++) {
                    int bx = 12 + (i % 6) * 50;
                    if (mx >= bx && mx <= bx + 48 && my >= catY && my <= catY + 16) {
                        this.selectedCatalogCategory = cats[i];
                        filterCatalogItems(this.drawerSearchBox != null ? this.drawerSearchBox.getValue() : "");
                        return true;
                    }
                }

                // Cuadrícula de ítems en el catálogo
                int gridX = 14;
                int gridY = 72;
                int cols = 4;
                int slotS = 22;
                if (mx >= gridX && mx <= gridX + cols * (slotS + 50) && my >= gridY) {
                    int col = (int) (mx - gridX) / (slotS + 50);
                    int row = (int) (my - gridY) / slotS;
                    int idx = (catalogScrollOffset * cols) + (row * cols + col);
                    if (idx >= 0 && idx < this.filteredCatalogItems.size()) {
                        selectItemFromCatalog(this.filteredCatalogItems.get(idx));
                        return true;
                    }
                }
                return true;
            }

            // Clics interactivos en la lista de Sub-pestaña Enchants
            if (this.activeTab == 1) {
                int winW = Math.min(this.width - 60, 680);
                int winH = Math.min(this.height - 40, 390);
                int winX = (this.width - winW) / 2;
                int winY = (this.height - winH) / 2;
                int gap = 6;
                int availW = winW - 16 - (2 * gap);
                int col1W = (int) (availW * 0.26f);
                int col2X = winX + 8 + col1W + gap;
                int col2W = (int) (availW * 0.34f);
                int col3W = availW - col1W - col2W;
                int enchInnerW = col2W + col3W + gap;

                int listY = winY + 60;
                int rowH = 24;
                int listH = winH - 100;
                int visible = listH / rowH;

                if (mx >= col2X && mx <= col2X + enchInnerW && my >= listY && my < listY + visible * rowH) {
                    int rowIdx = (int) (my - listY) / rowH;
                    int actualIdx = enchantScrollOffset + rowIdx;
                    if (actualIdx >= 0 && actualIdx < this.filteredEnchants.size()) {
                        Holder.Reference<Enchantment> holder = this.filteredEnchants.get(actualIdx);
                        ItemEnchantments enchs = this.targetStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                        int curLvl = enchs.getLevel(holder);
                        int maxLvl = holder.value().getMaxLevel();

                        int rightX = col2X + enchInnerW - 8;
                        if (mx >= rightX - 16 && mx <= rightX) {
                            setEnchantLevel(holder, 0);
                            return true;
                        }
                        if (mx >= rightX - 44 && mx <= rightX - 18) {
                            setEnchantLevel(holder, maxLvl);
                            return true;
                        }
                        if (mx >= rightX - 66 && mx <= rightX - 46) {
                            setEnchantLevel(holder, curLvl + 1);
                            return true;
                        }
                        if (mx >= rightX - 88 && mx <= rightX - 68) {
                            setEnchantLevel(holder, Math.max(0, curLvl - 1));
                            return true;
                        }
                    }
                }
            } else if (this.activeTab == 3) {
                // Clic en ojos interactivos [ 👁 ON / OFF ]
                int winW = Math.min(this.width - 60, 680);
                int winH = Math.min(this.height - 40, 390);
                int winX = (this.width - winW) / 2;
                int winY = (this.height - winH) / 2;
                int gap = 6;
                int availW = winW - 16 - (2 * gap);
                int col1W = (int) (availW * 0.26f);
                int col2X = winX + 8 + col1W + gap;
                int col2W = (int) (availW * 0.34f);
                int col3W = availW - col1W - col2W;
                int visInnerW = col2W + col3W + gap;

                int listY = winY + 56;
                int rowH = 22;
                List<EyeEntry> entries = getEyeEntries();
                for (int i = 0; i < entries.size(); i++) {
                    int ry = listY + i * rowH;
                    int btnX = col2X + visInnerW - 54;
                    if (mx >= btnX && mx <= btnX + 48 && my >= ry + 2 && my <= ry + 18) {
                        EyeEntry entry = entries.get(i);
                        ArgNetLoreManager.toggleEnchantVisibility(this.targetStack, entry.id);
                        compileLore();
                        recordSnapshot();
                        showToast("👁 Visibilidad cambiada: " + entry.displayName);
                        return true;
                    }
                }
            }
        }

        return super.mouseClicked(event, isDown);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.drawerOpen && mouseX >= 0 && mouseX <= 320) {
            int cols = 4;
            int totalRows = Math.max(1, (int) Math.ceil((double) this.filteredCatalogItems.size() / cols));
            int visibleRows = (this.height - 100) / 22;
            int maxOffset = Math.max(0, totalRows - visibleRows);

            if (verticalAmount > 0) {
                this.catalogScrollOffset = Math.max(0, this.catalogScrollOffset - 2);
            } else if (verticalAmount < 0) {
                this.catalogScrollOffset = Math.min(maxOffset, this.catalogScrollOffset + 2);
            }
            return true;
        }

        if (this.activeTab == 1) {
            int visible = (this.height - 110) / 24;
            int maxOffset = Math.max(0, this.filteredEnchants.size() - visible);
            if (verticalAmount > 0) {
                enchantScrollOffset = Math.max(0, enchantScrollOffset - 1);
            } else if (verticalAmount < 0) {
                enchantScrollOffset = Math.min(maxOffset, enchantScrollOffset + 1);
            }
            return true;
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    // =========================================================================
    // RENDERIZADO VISUAL PRINCIPAL (LIQUID GLASS 100% REAL TIME)
    // =========================================================================

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Fondo 100% transparente en tiempo real intencional para ver el mundo y shaders a 60+ FPS.
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Cálculo responsivo y centrado
        int winW = Math.min(this.width - 60, 680);
        int winH = Math.min(this.height - 40, 390);
        int winX = (this.width - winW) / 2;
        int winY = (this.height - winH) / 2;

        int gap = 6;
        int bodyY = winY + 30;
        int footerH = 22;
        int footerY = winY + winH - footerH - 4;
        int bodyH = footerY - bodyY - 4;

        int availW = winW - 16 - (2 * gap);
        int col1W = (int) (availW * 0.26f);
        int col2W = (int) (availW * 0.34f);
        int col3W = availW - col1W - col2W;

        int col1X = winX + 8;
        int col2X = col1X + col1W + gap;
        int col3X = col2X + col2W + gap;

        // 2. Fondo difuminado sobre el mundo de Minecraft
        CmiUiTheme.drawScreenBackdrop(guiGraphics, this.width, this.height);

        // 3. Ventana Central Flotante (Card Principal Liquid Glass)
        CmiUiTheme.fillRoundedRect(guiGraphics, winX, winY, winW, winH, 12, 0xD00A1424);
        CmiUiTheme.drawRoundedBorder(guiGraphics, winX, winY, winW, winH, 12, 0x6038BDF8);
        guiGraphics.fill(winX + 12, winY + 1, winX + winW - 12, winY + 2, 0x30FFFFFF);
        guiGraphics.fill(winX + 8, winY + 28, winX + winW - 8, winY + 29, 0x3038BDF8);
        guiGraphics.fill(winX + 8, footerY - 3, winX + winW - 8, footerY - 2, 0x3038BDF8);

        // 4. Fondos y Cards Interiores
        if (this.activeTab == 0) {
            // Columna 1
            int showcaseH = 118;
            int previewH = 106;
            int stepperH = 46;
            CmiUiTheme.drawLiquidPanel(guiGraphics, col1X, bodyY, col1W, showcaseH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
            CmiUiTheme.drawLiquidPanel(guiGraphics, col1X, bodyY + showcaseH + 4, col1W, previewH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
            CmiUiTheme.drawLiquidPanel(guiGraphics, col1X, bodyY + showcaseH + previewH + 8, col1W, stepperH, 8, CmiUiTheme.GLASS_PANEL_BORDER);

            // Columna 2
            int nameCardH = 46;
            int animCardH = 142;
            int gradCardH = 58;
            CmiUiTheme.drawLiquidPanel(guiGraphics, col2X, bodyY, col2W, nameCardH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
            CmiUiTheme.drawLiquidPanel(guiGraphics, col2X, bodyY + nameCardH + 4, col2W, animCardH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
            CmiUiTheme.drawLiquidPanel(guiGraphics, col2X, bodyY + nameCardH + animCardH + 8, col2W, gradCardH, 8, CmiUiTheme.GLASS_PANEL_BORDER);

            // Columna 3
            int palCardH = 46;
            int loreCardH = 144;
            int badgesCardH = 42;
            CmiUiTheme.drawLiquidPanel(guiGraphics, col3X, bodyY, col3W, palCardH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
            CmiUiTheme.drawLiquidPanel(guiGraphics, col3X, bodyY + palCardH + 4, col3W, loreCardH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
            CmiUiTheme.drawLiquidPanel(guiGraphics, col3X, bodyY + palCardH + loreCardH + 28, col3W, badgesCardH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
        } else {
            // Paneles para otras pestañas
            CmiUiTheme.drawLiquidPanel(guiGraphics, col1X, bodyY, col1W, bodyH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
            CmiUiTheme.drawLiquidPanel(guiGraphics, col2X, bodyY, col2W + col3W + gap, bodyH, 8, CmiUiTheme.GLASS_PANEL_BORDER);
        }

        // 5. Render de los widgets hijos (botones, editboxes)
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // 6. Contenido Gráfico Foreground del Card
        // Header: Badge circular (C), CheManyItems y CMI Suite 26.1.2
        int badgeX = winX + 8;
        int badgeY = winY + 5;
        CmiUiTheme.fillRoundedRect(guiGraphics, badgeX, badgeY, 18, 18, 6, 0xFF0284C7);
        CmiUiTheme.drawRoundedBorder(guiGraphics, badgeX, badgeY, 18, 18, 6, CmiUiTheme.COLOR_CYAN_NEON);
        guiGraphics.centeredText(this.font, Component.literal("C"), badgeX + 9, badgeY + 5, 0xFFFFFFFF);

        guiGraphics.text(this.font, Component.literal("§b§lCheManyItems"), badgeX + 22, badgeY + 1, CmiUiTheme.COLOR_CYAN_NEON);
        guiGraphics.text(this.font, Component.literal("§7CMI Suite 26.1.2"), badgeX + 22, badgeY + 10, 0xFF7DD3FC);

        // Header: Slot micro-tag
        int slotNum = (this.targetSlot != null) ? (this.targetSlot.getContainerSlot() + 1) : ((this.targetContainerSlot >= 0) ? (this.targetContainerSlot + 1) : 1);
        int slotTagX = winX + winW - 96;
        CmiUiTheme.fillRoundedRect(guiGraphics, slotTagX, badgeY, 46, 18, 9, 0x3010B981);
        CmiUiTheme.drawRoundedBorder(guiGraphics, slotTagX, badgeY, 46, 18, 9, 0x6010B981);
        CmiUiTheme.drawFilledCircle(guiGraphics, slotTagX + 7, badgeY + 9, 2, 0xFF10B981);
        guiGraphics.text(this.font, Component.literal("§aSlot #" + slotNum), slotTagX + 13, badgeY + 5, 0xFF34D399);

        // Header: Pestaña activa resplandor neón cyan
        if (this.activeTab == 0) {
            guiGraphics.fill(winX + 236 + 6, winY + 26, winX + 236 + 46, winY + 28, 0xFF00E5FF);
        }

        // =====================================================================
        // CONTENIDO COLUMNA 1 (SHOWCASE, LORE COMPILADO, CANTIDAD)
        // =====================================================================
        int showcaseH = 118;
        int pedCX = col1X + col1W / 2;
        int pedCY = bodyY + 40;

        // Badge x1 / x64 en showcase
        int badgeCountW = this.font.width("x" + this.itemCount) + 8;
        CmiUiTheme.fillRoundedRect(guiGraphics, col1X + col1W - badgeCountW - 6, bodyY + 5, badgeCountW, 12, 4, 0x800F172A);
        CmiUiTheme.drawRoundedBorder(guiGraphics, col1X + col1W - badgeCountW - 6, bodyY + 5, badgeCountW, 12, 4, 0x6038BDF8);
        guiGraphics.text(this.font, Component.literal("§bx" + this.itemCount), col1X + col1W - badgeCountW - 2, bodyY + 7, CmiUiTheme.COLOR_CYAN_NEON);

        // Pedestal circular concéntrico
        CmiUiTheme.drawShowcasePedestal(guiGraphics, pedCX, pedCY, 42);

        // Ítem flotando en 3D con oscilación fluida
        float rotAngle = (float) Math.sin((System.currentTimeMillis() % 4000L) / 4000.0 * Math.PI * 2) * 0.12f;
        var pose = guiGraphics.pose();
        pose.pushMatrix();
        pose.translate((float) pedCX, (float) pedCY);
        pose.rotate(rotAngle);
        pose.translate((float) -pedCX, (float) -pedCY);
        guiGraphics.item(this.targetStack, pedCX - 8, pedCY - 8);
        guiGraphics.itemDecorations(this.font, this.targetStack, pedCX - 8, pedCY - 8);
        pose.popMatrix();

        // Tooltip flotante nativo al pasar el cursor
        if (mouseX >= pedCX - 12 && mouseX <= pedCX + 12 && mouseY >= pedCY - 12 && mouseY <= pedCY + 12) {
            guiGraphics.setTooltipForNextFrame(this.font, this.targetStack, mouseX, mouseY);
        }

        // Nombre e ID debajo del pedestal
        String dItemName = this.targetStack.getHoverName().getString();
        if (dItemName.length() > 16) dItemName = dItemName.substring(0, 15) + "..";
        guiGraphics.centeredText(this.font, Component.literal(dItemName), pedCX, bodyY + 68, 0xFFFFFFFF);

        String fullId = BuiltInRegistries.ITEM.getKey(this.targetStack.getItem()).toString();
        if (fullId.length() > 20) fullId = fullId.substring(0, 19) + "..";
        guiGraphics.centeredText(this.font, Component.literal("§b" + fullId), pedCX, bodyY + 80, CmiUiTheme.COLOR_CYAN_NEON);

        // Badge Irrompible si aplica
        if (this.targetStack.has(DataComponents.UNBREAKABLE)) {
            CmiUiTheme.fillRoundedRect(guiGraphics, pedCX - 28, bodyY + 94, 56, 12, 4, 0x4010B981);
            CmiUiTheme.drawRoundedBorder(guiGraphics, pedCX - 28, bodyY + 94, 56, 12, 4, 0x8010B981);
            guiGraphics.centeredText(this.font, Component.literal("§aIrrompible"), pedCX, bodyY + 96, 0xFF34D399);
        }

        // Caja de Lore Compilado (EN VIVO)
        int prevY = bodyY + showcaseH + 4;
        guiGraphics.text(this.font, Component.literal("§6📜 Lore Compilado"), col1X + 6, prevY + 5, CmiUiTheme.COLOR_AMBER);
        guiGraphics.text(this.font, Component.literal("§bEN VIVO"), col1X + col1W - 40, prevY + 5, CmiUiTheme.COLOR_CYAN_NEON);

        int loreBoxY = prevY + 16;
        int loreBoxH = 82;
        CmiUiTheme.fillRoundedRect(guiGraphics, col1X + 5, loreBoxY, col1W - 10, loreBoxH, 4, 0x90050A14);
        CmiUiTheme.drawRoundedBorder(guiGraphics, col1X + 5, loreBoxY, col1W - 10, loreBoxH, 4, 0x4038BDF8);

        ItemLore lore = this.targetStack.get(DataComponents.LORE);
        if (lore != null && !lore.lines().isEmpty()) {
            int ly = loreBoxY + 4;
            for (int i = 0; i < Math.min(6, lore.lines().size()); i++) {
                Component line = lore.lines().get(i);
                guiGraphics.text(this.font, line, col1X + 8, ly, 0xFFCBD5E1);
                ly += 12;
            }
        } else {
            guiGraphics.text(this.font, Component.literal("§8(Sin lore compilado)"), col1X + 8, loreBoxY + 8, 0xFF64748B);
        }

        // =====================================================================
        // CONTENIDO COLUMNA 2 (CENTRAL)
        // =====================================================================
        if (this.activeTab == 0) {
            int nameCardH = 46;
            int animCardY = bodyY + nameCardH + 4;

            // Header <ANIMATED> 21 Shaders
            CmiUiTheme.fillRoundedRect(guiGraphics, col2X + 6, animCardY + 4, 76, 11, 4, 0x30F59E0B);
            CmiUiTheme.drawRoundedBorder(guiGraphics, col2X + 6, animCardY + 4, 76, 11, 4, 0x60F59E0B);
            guiGraphics.text(this.font, Component.literal("§6<ANIMATED>"), col2X + 10, animCardY + 5, CmiUiTheme.COLOR_AMBER);
            guiGraphics.text(this.font, Component.literal("§821 SHADERS"), col2X + col2W - 64, animCardY + 5, 0xFF64748B);

            // Selector Inferior: Colores & Gradientes
            int animGridY = animCardY + 18;
            int gradY = animGridY + 7 * 16 + 6;
            guiGraphics.text(this.font, Component.literal("§e🎨 Colores & Gradientes"), col2X + 6, gradY + 4, CmiUiTheme.COLOR_AMBER);
            guiGraphics.text(this.font, Component.literal("§bHEX / RGB ▼"), col2X + col2W - 68, gradY + 4, CmiUiTheme.COLOR_CYAN_NEON);

            // =====================================================================
            // CONTENIDO COLUMNA 3 (DERECHA)
            // =====================================================================
            int palCardH = 46;
            int loreCardY = bodyY + palCardH + 4;
            int loreCardH = 144;

            guiGraphics.text(this.font, Component.literal("§8item_lore.txt · UTF-8"), col3X + 6, loreCardY + 4, 0xFF64748B);
            int charLen = (this.rawLoreText != null) ? this.rawLoreText.length() : 0;
            guiGraphics.text(this.font, Component.literal("§8" + charLen + "/32767"), col3X + col3W - 54, loreCardY + 4, 0xFF64748B);

        } else if (this.activeTab == 1) {
            // Lista interactiva de encantamientos vanilla
            int listY = bodyY + 30;
            int rowH = 24;
            int visible = (bodyH - 36) / rowH;
            ItemEnchantments currentEnchs = this.targetStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

            for (int i = enchantScrollOffset; i < Math.min(filteredEnchants.size(), enchantScrollOffset + visible); i++) {
                Holder.Reference<Enchantment> holder = filteredEnchants.get(i);
                int ry = listY + (i - enchantScrollOffset) * rowH;
                int rw = col2W + col3W + gap;
                int curLvl = currentEnchs.getLevel(holder);
                int maxLvl = holder.value().getMaxLevel();
                boolean isActive = curLvl > 0;

                CmiUiTheme.fillRoundedRect(guiGraphics, col2X, ry, rw, rowH - 2, 4, isActive ? 0x5006B6D4 : 0x250A1424);
                CmiUiTheme.drawRoundedBorder(guiGraphics, col2X, ry, rw, rowH - 2, 4, isActive ? CmiUiTheme.COLOR_CYAN_NEON : 0x3038BDF8);

                String name = holder.value().description().getString();
                guiGraphics.text(this.font, Component.literal((isActive ? "§6★ " : "§7") + name), col2X + 6, ry + 4, isActive ? 0xFFFFD700 : 0xFFCBD5E1);
                guiGraphics.text(this.font, Component.literal("§8Max: " + maxLvl + (isActive ? " | §bLvl: " + curLvl : "")), col2X + 6, ry + 13, CmiUiTheme.COLOR_MUTED);

                int rightX = col2X + rw - 6;
                CmiUiTheme.drawCapsulePill(guiGraphics, this.font, rightX - 14, ry + 4, 14, 15, Component.literal("✕"), false, false, CmiUiTheme.COLOR_DANGER, CmiUiTheme.COLOR_DANGER);
                CmiUiTheme.drawCapsulePill(guiGraphics, this.font, rightX - 42, ry + 4, 26, 15, Component.literal("Max"), false, false, CmiUiTheme.COLOR_CYAN_NEON, CmiUiTheme.COLOR_CYAN_ACTIVE);
                CmiUiTheme.drawCapsulePill(guiGraphics, this.font, rightX - 64, ry + 4, 20, 15, Component.literal("+"), false, false, CmiUiTheme.COLOR_AMBER, CmiUiTheme.COLOR_AMBER_DARK);
                CmiUiTheme.drawCapsulePill(guiGraphics, this.font, rightX - 86, ry + 4, 20, 15, Component.literal("-"), false, false, CmiUiTheme.COLOR_MUTED, CmiUiTheme.COLOR_MUTED);
            }

        } else if (this.activeTab == 2) {
            // Atributos actuales
            guiGraphics.text(this.font, Component.literal("§e✦ ATRIBUTOS ACTUALES EN EL ÍTEM ✦"), col2X + 6, bodyY + 80, CmiUiTheme.COLOR_AMBER);
            var attrs = ArgNetLoreManager.getCustomAttributes(this.targetStack);
            if (attrs.isEmpty()) {
                guiGraphics.text(this.font, Component.literal("§7(Sin atributos custom agregados)"), col2X + 6, bodyY + 96, 0xFF64748B);
            } else {
                int ay = bodyY + 96;
                for (var ca : attrs) {
                    guiGraphics.text(this.font, Component.literal("§a✔ " + ca.toLoreLine()), col2X + 6, ay, 0xFF34D399);
                    ay += 12;
                }
            }

        } else if (this.activeTab == 3) {
            // Visibilidad & Ojos 👁️
            guiGraphics.text(this.font, Component.literal("§b✦ VISIBILIDAD DE ENCHANTS Y ATRIBUTOS:"), col2X + 6, bodyY + 28, CmiUiTheme.COLOR_CYAN_NEON);
            List<EyeEntry> entries = getEyeEntries();
            int listY = bodyY + 42;
            int rowH = 22;
            int rw = col2W + col3W + gap;

            for (int i = 0; i < Math.min(10, entries.size()); i++) {
                EyeEntry e = entries.get(i);
                int ry = listY + i * rowH;
                boolean isHidden = ArgNetLoreManager.isEnchantHidden(this.targetStack, e.id);
                boolean isVisible = !isHidden;

                CmiUiTheme.fillRoundedRect(guiGraphics, col2X, ry, rw, 20, 4, 0x300B1422);
                CmiUiTheme.drawRoundedBorder(guiGraphics, col2X, ry, rw, 20, 4, 0x3038BDF8);
                CmiUiTheme.drawFilledCircle(guiGraphics, col2X + 8, ry + 10, 2, e.color);
                guiGraphics.text(this.font, Component.literal("§f" + e.displayName), col2X + 16, ry + 6, isVisible ? 0xFFFFFFFF : 0xFF64748B);

                int eyeBtnX = col2X + rw - 54;
                boolean eyeHovered = mouseX >= eyeBtnX && mouseX <= eyeBtnX + 48 && mouseY >= ry + 2 && mouseY <= ry + 18;
                CmiUiTheme.drawEyeToggle(guiGraphics, this.font, eyeBtnX, ry + 2, isVisible, eyeHovered);
            }
        }

        // =====================================================================
        // FOOTER: INDICADOR DE COLOR ACTIVO
        // =====================================================================
        int colIndX = winX + 180;
        int colIndW = 100;
        CmiUiTheme.fillRoundedRect(guiGraphics, colIndX, footerY, colIndW, footerH, footerH / 2, 0x800F172A);
        CmiUiTheme.drawRoundedBorder(guiGraphics, colIndX, footerY, colIndW, footerH, footerH / 2, 0x5038BDF8);
        CmiUiTheme.drawFilledCircle(guiGraphics, colIndX + 8, footerY + footerH / 2, 3, this.activeColorRgb);
        guiGraphics.text(this.font, Component.literal("Color: " + this.activeColorCode), colIndX + 16, footerY + 7, this.activeColorRgb);

        // Toast de notificación temporal
        if (System.currentTimeMillis() - this.statusToastTime < 3200) {
            guiGraphics.centeredText(this.font, Component.literal("§e" + this.statusToast), winX + winW / 2, winY + winH - 36, CmiUiTheme.COLOR_AMBER);
        }

        // =====================================================================
        // DRAWER LATERAL DE CATÁLOGO (SI ESTÁ ABIERTO)
        // =====================================================================
        if (this.drawerOpen) {
            CmiUiTheme.drawCatalogDrawerPanel(guiGraphics, 0, 0, 320, this.height);
            guiGraphics.text(this.font, Component.literal("§b§lCATÁLOGO DE ÍTEMS"), 14, 12, CmiUiTheme.COLOR_CYAN_NEON);
            guiGraphics.text(this.font, Component.literal("§7Preserva encantamientos, lore y NBT"), 14, 22, 0xFF64748B);

            String[] catNames = {"Todos", "Armas", "Herram.", "Armad.", "Bloq.", "Poc."};
            for (int i = 0; i < catNames.length; i++) {
                int bx = 12 + i * 50;
                CmiUiTheme.drawCapsulePill(guiGraphics, this.font, bx, 46, 48, 15,
                        Component.literal(catNames[i]),
                        mouseX >= bx && mouseX <= bx + 48 && mouseY >= 46 && mouseY <= 61,
                        false, CmiUiTheme.COLOR_CYAN_NEON, CmiUiTheme.COLOR_CYAN_ACTIVE);
            }

            int gridX = 14;
            int gridY = 72;
            int cols = 4;
            int slotS = 22;
            int visibleRows = (this.height - gridY - 24) / slotS;
            int startIdx = this.catalogScrollOffset * cols;

            for (int r = 0; r < visibleRows; r++) {
                for (int c = 0; c < cols; c++) {
                    int idx = startIdx + (r * cols + c);
                    if (idx < this.filteredCatalogItems.size()) {
                        Item item = this.filteredCatalogItems.get(idx);
                        int sx = gridX + c * (slotS + 50);
                        int sy = gridY + r * slotS;

                        boolean slotHover = mouseX >= sx && mouseX <= sx + slotS + 48 && mouseY >= sy && mouseY <= sy + slotS;
                        CmiUiTheme.drawSlot(guiGraphics, sx, sy, slotS, slotHover);

                        ItemStack previewStack = new ItemStack(item);
                        guiGraphics.item(previewStack, sx + 3, sy + 3);

                        String name = item.getName(item.getDefaultInstance()).getString();
                        if (name.length() > 7) name = name.substring(0, 6) + "..";
                        guiGraphics.text(this.font, Component.literal("§7" + name), sx + slotS + 4, sy + 6, slotHover ? 0xFFFFFFFF : 0xFF94A3B8);
                    }
                }
            }
        }
    }

    // =========================================================================
    // UTILIDADES DE MODELOS Y VISIBILIDAD (OJOS)
    // =========================================================================

    private static class EyeEntry {
        public final String id;
        public final String displayName;
        public final String category;
        public final int color;

        public EyeEntry(String id, String displayName, String category, int color) {
            this.id = id;
            this.displayName = displayName;
            this.category = category;
            this.color = color;
        }
    }

    private List<EyeEntry> getEyeEntries() {
        List<EyeEntry> list = new ArrayList<>();

        if (this.targetStack.has(DataComponents.ENCHANTMENTS)) {
            ItemEnchantments enchs = this.targetStack.get(DataComponents.ENCHANTMENTS);
            if (enchs != null) {
                for (var entry : enchs.entrySet()) {
                    String path = entry.getKey().unwrapKey().map(k -> k.identifier().getPath()).orElse("");
                    String name = entry.getKey().value().description().getString() + " " + entry.getIntValue();
                    if (selectedEyeCategory.equals("all") || selectedEyeCategory.equals("vanilla")) {
                        list.add(new EyeEntry(path, name, "vanilla", CmiUiTheme.COLOR_AMBER));
                    }
                }
            }
        }

        Map<String, Integer> ceMap = ArgNetLoreManager.getCustomEnchantsMap(this.targetStack);
        for (var e : ceMap.entrySet()) {
            if (selectedEyeCategory.equals("all") || selectedEyeCategory.equals("custom")) {
                list.add(new EyeEntry(e.getKey(), e.getKey().toUpperCase() + " " + e.getValue(), "custom", CmiUiTheme.COLOR_CYAN_NEON));
            }
        }

        var attrs = ArgNetLoreManager.getCustomAttributes(this.targetStack);
        for (var a : attrs) {
            if (selectedEyeCategory.equals("all") || selectedEyeCategory.equals("attrs")) {
                list.add(new EyeEntry(a.name(), a.name() + " " + a.level(), "attrs", CmiUiTheme.COLOR_EMERALD));
            }
        }

        return list;
    }

    public static void setMutableEnchantLevel(net.minecraft.world.item.enchantment.ItemEnchantments.Mutable mutable, Holder<net.minecraft.world.item.enchantment.Enchantment> holder, int n) {
        if (n <= 0) {
            mutable.set(holder, 0);
            return;
        }
        int level = Math.min(Short.MAX_VALUE, Math.max(1, n));
        try {
            java.lang.reflect.Field field = net.minecraft.world.item.enchantment.ItemEnchantments.Mutable.class.getDeclaredField("enchantments");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<Holder<net.minecraft.world.item.enchantment.Enchantment>> map =
                    (it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap<Holder<net.minecraft.world.item.enchantment.Enchantment>>) field.get(mutable);
            if (map != null) {
                map.put(holder, level);
                return;
            }
        } catch (Throwable ignored) {}
        mutable.set(holder, level);
    }
}
