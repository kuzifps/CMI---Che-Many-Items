package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.client.animated.AnimatedTagRegistry;
import com.fxfps.cmi.client.fonts.UnicodeFontHelper;
import com.fxfps.cmi.client.lores.ArgNetLoreManager;
import com.fxfps.cmi.client.lores.AutoLorePreset;
import com.fxfps.cmi.client.lores.AutoLoreRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * Pantalla interactiva del Gestor de Auto-Lore de CheManyItems (CMI) 🇦🇷.
 * Permite seleccionar entre múltiples estilos de Auto-Lore (Limpio, Rainbow, Fuego, etc.)
 * y crear presets personalizados con tags animados, secciones a medida e iconos.
 */
public class CmiAutoLoreScreen extends CmiScreen {


    private final ItemStack targetStack;
    private final Runnable onApplyCallback;

    private String selectedPresetId;
    private EditBox nameBox;
    private EditBox enchantHeaderBox;
    private EditBox attrHeaderBox;
    private EditBox headerBox;

    private static final String[] AVAILABLE_TAGS = {
            "gold", "silver", "rainbow", "celestial", "fire", "ocean",
            "toxic", "sakura", "void", "frost", "lava", "matrix",
            "aurora", "sunset", "neon", "candy", "galaxy", "glitch",
            "blood", "emerald", "diamond"
    };

    private int enchantTagIdx = 2; // "rainbow" por defecto para nuevos
    private int attrTagIdx = 15;   // "candy" por defecto para nuevos
    private boolean useIcons = true;
    private boolean bold = true;
    private boolean smallCaps = true; // SmallCaps activo por defecto

    private int presetPage = 0;
    private static final int PRESETS_PER_PAGE = 7;

    public CmiAutoLoreScreen(Screen parentScreen) {
        this(parentScreen, null, null);
    }

    public CmiAutoLoreScreen(Screen parentScreen, ItemStack targetStack, Runnable onApplyCallback) {
        super(Component.literal("Gestor de Auto-Lore - CMI 🇦🇷"), parentScreen);

        this.targetStack = targetStack;
        this.onApplyCallback = onApplyCallback;
        this.selectedPresetId = AutoLoreRegistry.getActivePreset().id;
    }

    @Override
    protected void init() {
        super.init();

        int leftX = 14;
        int listWidth = 155;
        int rightX = leftX + listWidth + 10;
        int rightWidth = this.width - rightX - 14;

        // 1. LISTA DE PRESETS DISPONIBLES (Columna Izquierda)
        List<AutoLorePreset> allPresets = AutoLoreRegistry.getAllPresets();
        int totalPages = Math.max(1, (int) Math.ceil((double) allPresets.size() / PRESETS_PER_PAGE));
        if (presetPage >= totalPages) presetPage = totalPages - 1;
        if (presetPage < 0) presetPage = 0;

        int startIdx = presetPage * PRESETS_PER_PAGE;
        int endIdx = Math.min(startIdx + PRESETS_PER_PAGE, allPresets.size());

        int py = 46;
        for (int i = startIdx; i < endIdx; i++) {
            AutoLorePreset p = allPresets.get(i);
            boolean isSel = p.id.equalsIgnoreCase(selectedPresetId);
            boolean isActive = p.id.equalsIgnoreCase(AutoLoreRegistry.getActivePreset().id);

            String label = (isActive ? "✔ " : "") + p.displayName;
            CmiButton pBtn = new CmiButton(
                    leftX + 4, py, listWidth - 30, 18,
                    Component.literal(label),
                    btn -> {
                        this.selectedPresetId = p.id;
                        this.rebuildWidgets();
                    },
                    isSel ? CmiButton.Variant.GOLD : (isActive ? CmiButton.Variant.PRIMARY : CmiButton.Variant.DEFAULT)
            );
            pBtn.setSelected(isSel);
            this.addRenderableWidget(pBtn);

            // Botón eliminar si es personalizado
            if (p.custom) {
                this.addRenderableWidget(new CmiButton(
                        leftX + listWidth - 24, py, 18, 18,
                        Component.literal(CmiUiTheme.ICON_CLOSE),
                        btn -> {
                            AutoLoreRegistry.deletePreset(p.id);
                            if (selectedPresetId.equalsIgnoreCase(p.id)) {
                                selectedPresetId = "clean";
                            }
                            this.rebuildWidgets();
                        },
                        CmiButton.Variant.DANGER
                ));
            }

            py += 20;
        }

        // Paginación Izquierda
        int navY = py + 4;
        if (totalPages > 1) {
            this.addRenderableWidget(new CmiButton(
                    leftX + 4, navY, 20, 16,
                    Component.literal(CmiUiTheme.ICON_ARROW_LEFT),
                    btn -> {
                        if (presetPage > 0) {
                            presetPage--;
                            rebuildWidgets();
                        }
                    },
                    CmiButton.Variant.CHIP
            ));
            this.addRenderableWidget(new CmiButton(
                    leftX + listWidth - 24, navY, 20, 16,
                    Component.literal(CmiUiTheme.ICON_ARROW_RIGHT),
                    btn -> {
                        if (presetPage < totalPages - 1) {
                            presetPage++;
                            rebuildWidgets();
                        }
                    },
                    CmiButton.Variant.CHIP
            ));
            navY += 22;
        }

        // Botón Activar Preset Seleccionado
        this.addRenderableWidget(new CmiButton(
                leftX + 4, navY, listWidth - 8, 20,
                Component.literal("⚡ Activar Estilo"),
                btn -> {
                    AutoLoreRegistry.setActivePreset(selectedPresetId);
                    if (targetStack != null && !targetStack.isEmpty()) {
                        ArgNetLoreManager.restructureItemStack(targetStack);
                    }
                    if (onApplyCallback != null) {
                        onApplyCallback.run();
                    }
                    this.rebuildWidgets();
                },
                CmiButton.Variant.SUCCESS
        ));

        // 2. PANEL DERECHO: CREAR / PERSONALIZAR NUEVO PRESET
        int formY = 46;
        int inputW = Math.min(180, rightWidth - 8);

        // Nombre del preset
        nameBox = new EditBox(this.font, rightX + 6, formY + 12, inputW, 15, Component.literal("Nombre"));
        nameBox.setHint(Component.literal("§8Ej: Mi Estilo PvP"));
        this.addRenderableWidget(nameBox);
        formY += 32;

        // Título Sección Encantos
        enchantHeaderBox = new EditBox(this.font, rightX + 6, formY + 12, inputW, 15, Component.literal("Encantos"));
        enchantHeaderBox.setValue("&7&lENCANTAMIENTOS");
        this.addRenderableWidget(enchantHeaderBox);
        formY += 32;

        // Tag Animado de Encantamientos (Selector con 21 tags)
        String curEnchTag = AVAILABLE_TAGS[enchantTagIdx];
        this.addRenderableWidget(new CmiButton(
                rightX + 6, formY, inputW, 16,
                Component.literal("Tag Encantos: <" + curEnchTag + ">"),
                btn -> {
                    enchantTagIdx = (enchantTagIdx + 1) % AVAILABLE_TAGS.length;
                    rebuildWidgets();
                },
                CmiButton.Variant.PRIMARY
        ));
        formY += 20;

        // Título Sección Atributos
        attrHeaderBox = new EditBox(this.font, rightX + 6, formY + 12, inputW, 15, Component.literal("Atributos"));
        attrHeaderBox.setValue("&7&lATRIBUTOS");
        this.addRenderableWidget(attrHeaderBox);
        formY += 32;

        // Tag Animado de Atributos
        String curAttrTag = AVAILABLE_TAGS[attrTagIdx];
        this.addRenderableWidget(new CmiButton(
                rightX + 6, formY, inputW, 16,
                Component.literal("Tag Atributos: <" + curAttrTag + ">"),
                btn -> {
                    attrTagIdx = (attrTagIdx + 1) % AVAILABLE_TAGS.length;
                    rebuildWidgets();
                },
                CmiButton.Variant.PRIMARY
        ));
        formY += 20;

        // Cabecera opcional (banner)
        headerBox = new EditBox(this.font, rightX + 6, formY + 12, inputW, 15, Component.literal("Cabecera"));
        headerBox.setHint(Component.literal("§8(Vacío para lore limpio)"));
        this.addRenderableWidget(headerBox);
        formY += 32;

        // Toggles: Iconos y Negrita
        this.addRenderableWidget(new CmiButton(
                rightX + 6, formY, (inputW - 4) / 2, 16,
                Component.literal("Iconos: " + (useIcons ? "§aSÍ" : "§cNO")),
                btn -> {
                    useIcons = !useIcons;
                    rebuildWidgets();
                },
                CmiButton.Variant.CHIP
        ));
        this.addRenderableWidget(new CmiButton(
                rightX + 6 + (inputW - 4) / 2 + 4, formY, (inputW - 4) / 2, 16,
                Component.literal("Negrita &l: " + (bold ? "§aB" : "§7b")),
                btn -> {
                    bold = !bold;
                    rebuildWidgets();
                },
                CmiButton.Variant.CHIP
        ));
        formY += 19;

        // Toggle SmallCaps
        this.addRenderableWidget(new CmiButton(
                rightX + 6, formY, inputW, 16,
                Component.literal("SmallCaps: " + (smallCaps ? "§aSÍ (ᴍɪɴᴜ́sᴄᴜʟᴀs)" : "§cNO (MAYÚSCULAS)")),
                btn -> {
                    smallCaps = !smallCaps;
                    rebuildWidgets();
                },
                CmiButton.Variant.CHIP
        ));
        formY += 21;

        // Botón Guardar Preset
        this.addRenderableWidget(new CmiButton(
                rightX + 6, formY, inputW, 18,
                Component.literal("💾 Guardar y Aplicar"),
                btn -> {
                    String dName = (nameBox != null && !nameBox.getValue().trim().isEmpty())
                            ? nameBox.getValue().trim()
                            : ("Personalizado " + (AutoLoreRegistry.getAllPresets().size() + 1));
                    String id = dName.toLowerCase().replaceAll("[^a-z0-9_]", "_");

                    String eH = (enchantHeaderBox != null && !enchantHeaderBox.getValue().trim().isEmpty())
                            ? enchantHeaderBox.getValue().trim()
                            : "&7&lENCANTAMIENTOS";
                    String aH = (attrHeaderBox != null && !attrHeaderBox.getValue().trim().isEmpty())
                            ? attrHeaderBox.getValue().trim()
                            : "&7&lATRIBUTOS";
                    String h = (headerBox != null) ? headerBox.getValue().trim() : "";

                    AutoLorePreset customPreset = new AutoLorePreset(
                            id, dName, h, eH, AVAILABLE_TAGS[enchantTagIdx], aH, AVAILABLE_TAGS[attrTagIdx],
                            Collections.emptyList(), useIcons, bold, true, smallCaps, true
                    );

                    AutoLoreRegistry.savePreset(customPreset);
                    AutoLoreRegistry.setActivePreset(id);
                    this.selectedPresetId = id;

                    if (targetStack != null && !targetStack.isEmpty()) {
                        ArgNetLoreManager.restructureItemStack(targetStack);
                    }
                    if (onApplyCallback != null) {
                        onApplyCallback.run();
                    }
                    this.rebuildWidgets();
                },
                CmiButton.Variant.SUCCESS
        ));

        // 3. BOTÓN INFERIOR VOLVER
        this.addRenderableWidget(new CmiButton(
                this.width / 2 - 70, this.height - 26, 140, 18,
                Component.literal("↵ Volver al Editor"),
                btn -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(parentScreen);
                    }
                },
                CmiButton.Variant.PRIMARY
        ));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Fondo inmersivo oscuro degradado




        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // Título de la pantalla
        guiGraphics.text(this.font, Component.literal("🎨 §6§lGESTOR DE AUTO-LORE §8- CMI"), 16, 12, 0xFFFFAA00);
        guiGraphics.text(this.font, Component.literal("§7Personaliza o crea tus propios formatos automáticos para encantamientos y atributos"), 16, 23, 0xFF888888);

        int leftX = 14;
        int listWidth = 155;
        int rightX = leftX + listWidth + 10;
        int rightWidth = this.width - rightX - 14;

        // Títulos de columnas
        guiGraphics.text(this.font, Component.literal("§b📋 Presets Disponibles:"), leftX + 4, 37, 0xFF00E5FF);
        guiGraphics.text(this.font, Component.literal("§e➕ Crear Nuevo Auto-Lore:"), rightX + 6, 37, 0xFFFFAA00);

        // Previsualización en vivo del preset seleccionado (a la derecha de los campos o abajo)
        AutoLorePreset sel = AutoLoreRegistry.getPreset(selectedPresetId);
        int previewX = rightX + Math.min(180, rightWidth - 8) + 12;
        int previewW = rightWidth - Math.min(180, rightWidth - 8) - 16;
        int previewY = 46;
        int previewH = this.height - previewY - 36;

        if (previewW >= 120) {
            CmiUiTheme.drawGlassPanel(guiGraphics, previewX, previewY, previewW, previewH, CmiUiTheme.COLOR_CYAN_NEON);

            guiGraphics.text(this.font, Component.literal("§a👁 Vista Previa:"), previewX + 6, previewY + 6, 0xFF55FF55);

            int drawY = previewY + 22;
            if (sel != null) {
                if (sel.header != null && !sel.header.trim().isEmpty()) {
                    guiGraphics.text(this.font, UnicodeFontHelper.parseFormattedComponent(sel.header), previewX + 6, drawY, 0xFFFFFFFF);
                    drawY += 11;
                }

                // Encantamientos
                guiGraphics.text(this.font, UnicodeFontHelper.parseFormattedComponent(sel.enchantHeader), previewX + 6, drawY, 0xFFFFAA00);
                drawY += 11;

                String eIc = sel.useIcons ? "\uE4F7" : "";
                String eBold = sel.bold ? "&l" : "";
                String p1 = sel.smallCaps ? UnicodeFontHelper.toSmallCaps("PROTECCIÓN") : "PROTECCIÓN";
                String line1 = eIc + eBold + "<" + sel.enchantTag + ">" + p1 + " 20</" + sel.enchantTag + ">";
                guiGraphics.text(this.font, UnicodeFontHelper.parseFormattedComponent(line1), previewX + 6, drawY, 0xFFFFFFFF);
                drawY += 11;

                String p2 = sel.smallCaps ? UnicodeFontHelper.toSmallCaps("IRROMPIBILIDAD") : "IRROMPIBILIDAD";
                String line2 = (sel.useIcons ? "\uE4EB" : "") + eBold + "<" + sel.enchantTag + ">" + p2 + " 20</" + sel.enchantTag + ">";
                guiGraphics.text(this.font, UnicodeFontHelper.parseFormattedComponent(line2), previewX + 6, drawY, 0xFFFFFFFF);
                drawY += 15;

                // Atributos
                guiGraphics.text(this.font, UnicodeFontHelper.parseFormattedComponent(sel.attrHeader), previewX + 6, drawY, 0xFFC0C0C0);
                drawY += 11;

                String aIc = sel.useIcons ? "\uE4EA" : "";
                String a1 = sel.smallCaps ? UnicodeFontHelper.toSmallCaps("REGENERACIÓN") : "REGENERACIÓN";
                String lineA1 = aIc + eBold + "<" + sel.attrTag + ">" + a1 + " 2</" + sel.attrTag + ">";
                guiGraphics.text(this.font, UnicodeFontHelper.parseFormattedComponent(lineA1), previewX + 6, drawY, 0xFFFFFFFF);
                drawY += 11;

                String a2 = sel.smallCaps ? UnicodeFontHelper.toSmallCaps("EXTRA CORAZONES") : "EXTRA CORAZONES";
                String lineA2 = (sel.useIcons ? "\uE419" : "") + eBold + "<" + sel.attrTag + ">" + a2 + " +2</" + sel.attrTag + ">";
                guiGraphics.text(this.font, UnicodeFontHelper.parseFormattedComponent(lineA2), previewX + 6, drawY, 0xFFFFFFFF);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Se omite el fondo oscuro por defecto para mantener ultra-transparencia y renderizado del mundo en tiempo real
    }

}
