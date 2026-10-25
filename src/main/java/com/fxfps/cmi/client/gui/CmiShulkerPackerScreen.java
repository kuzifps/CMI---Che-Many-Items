package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.client.commands.CmiCommands;
import com.fxfps.cmi.client.fonts.UnicodeFontHelper;
import com.fxfps.cmi.client.kits.CmiKitManager;
import com.fxfps.cmi.client.kits.CmiKitManager.KitData;
import com.fxfps.cmi.client.shulker.ShulkerPackerManager;
import com.fxfps.cmi.client.shulker.ShulkerPackerManager.ShulkerColorInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * PANTALLA OFICIAL DEL EMPAQUETADOR SHULKER BOX (16 TINTES) Y GENERADOR /give 📦
 *
 * Cumple al 100% con la Fase 4 y la Sección 8 de CMI_SISTEMA_MAESTRO_COMPLETO.md:
 * - Ultra-transparencia Liquid Glass in-game sin pausa (shouldPause -> false).
 * - Columna 1 (25%): Showcase Pedestal 3D con aura cálida, nombre personalizado y badges.
 * - Columna 2 (45%): Matriz 4x4 interactiva de los 16 colores oficiales de Shulker Box.
 * - Columna 3 (30%): Live Contents Inspector (3x9 = 27 casillas) y visor del comando /give.
 * - Acciones primarias: [ ✔ EMPAQUETAR Y ENTREGAR EN MANO ], [ 📋 COPIAR /give ], [ ⚡ EJECUTAR ].
 */
public class CmiShulkerPackerScreen extends CmiScreen {

    private String selectedColor = "cyan";
    private String targetKitId;
    private boolean packFromInventory = false;
    private String customTitleText = "";

    private EditBox titleInputBox;
    private final List<ItemStack> itemsToPack = new ArrayList<>();

    private String statusToast = "Listo · Empaquetador Shulker 60 FPS";
    private long statusToastTime = 0;

    public CmiShulkerPackerScreen(Screen parentScreen) {
        this(parentScreen, CmiKitManager.getActiveKitId(), false);
    }

    public CmiShulkerPackerScreen(Screen parentScreen, String kitId) {
        this(parentScreen, kitId, false);
    }

    public CmiShulkerPackerScreen(Screen parentScreen, String kitId, boolean fromInventory) {
        super(Component.literal("CMI · Empaquetador Shulker Box"), parentScreen);
        this.targetKitId = kitId;
        this.packFromInventory = fromInventory;
        this.selectedColor = CmiKitManager.getSelectedShulkerColor();
        loadItemsToPack();
    }

    private void loadItemsToPack() {
        this.itemsToPack.clear();
        if (packFromInventory) {
            if (this.minecraft != null && this.minecraft.player != null) {
                var inv = this.minecraft.player.getInventory();
                for (int i = 0; i < 27; i++) {
                    this.itemsToPack.add(inv.getItem(i).copy());
                }
            }
            this.customTitleText = "§6§lShulker de Inventario Personal";
        } else {
            KitData kd = CmiKitManager.getKit(targetKitId);
            if (kd != null) {
                int max = Math.min(27, kd.items.size());
                for (int i = 0; i < max; i++) {
                    this.itemsToPack.add(kd.items.get(i).copy());
                }
                this.customTitleText = "§6§l" + kd.name;
            } else {
                this.customTitleText = "§6§lShulker Box CheManyItems";
            }
        }
    }

    @Override
    protected void init() {
        super.init();

        int topY = 32;
        int bottomH = 34;
        int mainH = this.height - topY - bottomH - 4;

        int col1W = Math.max(140, (int) (this.width * 0.25f));
        int col2W = Math.max(220, (int) (this.width * 0.45f));
        int col3W = this.width - col1W - col2W - 24;

        int col1X = 8;
        int col2X = col1X + col1W + 6;
        int col3X = col2X + col2W + 6;

        // =========================================================================
        // BARRA SUPERIOR: TOGGLE ORIGEN Y BOTÓN VOLVER
        // =========================================================================
        int pillY = 5;
        int pillH = 20;

        // Toggle: [ 🎒 Kit Seleccionado ] vs [ 👤 Mi Inventario ]
        int toggleW = 130;
        CmiButton kitSourceBtn = new CmiButton(col2X, pillY, toggleW, pillH, Component.literal("🎒 Kit: " + (targetKitId != null ? targetKitId : "Activo")), btn -> {
            packFromInventory = false;
            loadItemsToPack();
            if (titleInputBox != null) titleInputBox.setValue(customTitleText);
            rebuildWidgets();
        }, CmiButton.Variant.TAB);
        kitSourceBtn.setSelected(!packFromInventory);
        this.addRenderableWidget(kitSourceBtn);

        CmiButton invSourceBtn = new CmiButton(col2X + toggleW + 6, pillY, 130, pillH, Component.literal("👤 Mi Inventario"), btn -> {
            packFromInventory = true;
            loadItemsToPack();
            if (titleInputBox != null) titleInputBox.setValue(customTitleText);
            rebuildWidgets();
        }, CmiButton.Variant.TAB);
        invSourceBtn.setSelected(packFromInventory);
        this.addRenderableWidget(invSourceBtn);

        // Botón [ ✕ Volver ]
        this.addRenderableWidget(new CmiButton(this.width - 70, pillY, 60, pillH, Component.literal("✕ Volver"), btn -> {
            this.closeScreen();
        }, CmiButton.Variant.DANGER));

        // =========================================================================
        // COLUMNA 1: CONTROLES DEL ÍTEM SHULKER
        // =========================================================================
        int titleInputY = topY + 110;
        this.titleInputBox = new EditBox(this.font, col1X + 8, titleInputY, col1W - 16, 18, Component.literal("Título"));
        this.titleInputBox.setValue(this.customTitleText);
        this.titleInputBox.setResponder(val -> this.customTitleText = val);
        this.addRenderableWidget(this.titleInputBox);

        // =========================================================================
        // BARRA INFERIOR DE ACCIÓN GLOBAL
        // =========================================================================
        int botY = this.height - 28;
        int botHeroW = Math.max(170, (int) (this.width * 0.32f));

        // 1. [ ✔ EMPAQUETAR Y ENTREGAR EN MANO ]
        this.addRenderableWidget(new CmiButton(
                col1X, botY, botHeroW, 22,
                Component.literal("✔ EMPAQUETAR EN MANO"),
                btn -> packAndDeliver(),
                CmiButton.Variant.HERO_EMERALD
        ));

        // 2. [ 📋 COPIAR /give ]
        this.addRenderableWidget(new CmiButton(
                col1X + botHeroW + 8, botY, 140, 22,
                Component.literal("📋 Copiar /give"),
                btn -> copyGiveCommand(),
                CmiButton.Variant.ACTION_AMBER
        ));

        // 3. [ ⚡ EJECUTAR EN SERVIDOR ]
        this.addRenderableWidget(new CmiButton(
                col1X + botHeroW + 154, botY, 140, 22,
                Component.literal("⚡ Ejecutar Comando"),
                btn -> executeGiveCommand(),
                CmiButton.Variant.TAB
        ));
    }

    private void packAndDeliver() {
        if (this.minecraft != null) {
            boolean success = ShulkerPackerManager.giveShulkerToPlayer(this.minecraft, this.itemsToPack, this.customTitleText, this.selectedColor);
            if (success) {
                showToast("✔ ¡Shulker " + selectedColor.toUpperCase() + " entregada en tu mano!");
            }
        }
    }

    private void copyGiveCommand() {
        if (this.minecraft != null) {
            String cmd = ShulkerPackerManager.generateGiveCommand(this.itemsToPack, this.customTitleText, this.selectedColor);
            this.minecraft.keyboardHandler.setClipboard(cmd);
            showToast("✔ Comando /give copiado al portapapeles (" + cmd.length() + " caracteres)");
        }
    }

    private void executeGiveCommand() {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.player.connection != null) {
            String cmd = ShulkerPackerManager.generateGiveCommand(this.itemsToPack, this.customTitleText, this.selectedColor);
            String rawCmd = cmd.startsWith("/") ? cmd.substring(1) : cmd;
            this.minecraft.player.connection.sendCommand(rawCmd);
            showToast("⚡ Comando /give enviado al servidor");
        }
    }

    private void showToast(String msg) {
        this.statusToast = msg;
        this.statusToastTime = System.currentTimeMillis();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDown) {
        if (isDown) {
            double mx = event.x();
            double my = event.y();

            int topY = 32;
            int col1W = Math.max(140, (int) (this.width * 0.25f));
            int col2W = Math.max(220, (int) (this.width * 0.45f));
            int col1X = 8;
            int col2X = col1X + col1W + 6;

            // Clic en la matriz 4x4 de 16 colores de Shulker Box
            int gridStartX = col2X + 10;
            int gridStartY = topY + 34;
            int cardW = (col2W - 20 - 12) / 4;
            int cardH = 46;

            for (int r = 0; r < 4; r++) {
                for (int c = 0; c < 4; c++) {
                    int idx = r * 4 + c;
                    if (idx < ShulkerPackerManager.ALL_COLORS.size()) {
                        int bx = gridStartX + c * (cardW + 4);
                        int by = gridStartY + r * (cardH + 4);

                        if (mx >= bx && mx <= bx + cardW && my >= by && my <= by + cardH) {
                            ShulkerColorInfo info = ShulkerPackerManager.ALL_COLORS.get(idx);
                            this.selectedColor = info.id();
                            CmiKitManager.setSelectedShulkerColor(info.id());
                            showToast("🎨 Tinte: " + info.displayName().toUpperCase());
                            return true;
                        }
                    }
                }
            }
        }

        return super.mouseClicked(event, isDown);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Fondo difuminado ultra-transparente sobre Minecraft en tiempo real
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        int topY = 32;
        int bottomH = 34;
        int mainH = this.height - topY - bottomH - 4;

        int col1W = Math.max(140, (int) (this.width * 0.25f));
        int col2W = Math.max(220, (int) (this.width * 0.45f));
        int col3W = this.width - col1W - col2W - 24;

        int col1X = 8;
        int col2X = col1X + col1W + 6;
        int col3X = col2X + col2W + 6;

        // 2. Cabecera y Logo Hero
        CmiUiTheme.drawLogoIcon(guiGraphics, 10, 6, 36, 17);
        guiGraphics.text(this.font, Component.literal("§b§lCMI §7· Empaquetador Shulker Box (16 Tintes)"), 52, 9, CmiUiTheme.COLOR_WHITE);

        // =========================================================================
        // COLUMNA 1 (25%): SHOWCASE PEDESTAL 3D DE SHULKER BOX
        // =========================================================================
        CmiUiTheme.drawGlassPanel(guiGraphics, col1X, topY, col1W, mainH, CmiUiTheme.GLASS_PANEL_BORDER);

        guiGraphics.fill(col1X + 4, topY + 4, col1X + col1W - 4, topY + 20, CmiUiTheme.PANEL_HEADER_BG);
        guiGraphics.centeredText(this.font, Component.literal("§6✦ SHULKER 3D ✦"), col1X + col1W / 2, topY + 7, CmiUiTheme.COLOR_AMBER);

        // Pedestal 3D con aura cálida concéntrica
        int pedCX = col1X + col1W / 2;
        int pedCY = topY + 54;
        CmiUiTheme.drawShowcasePedestal(guiGraphics, pedCX, pedCY, 58);

        // Renderizado del ítem Shulker Box del color elegido
        ItemStack shulkerPreview = ShulkerPackerManager.createShulkerBox(this.selectedColor);
        guiGraphics.item(shulkerPreview, pedCX - 8, pedCY - 8);
        guiGraphics.itemDecorations(this.font, shulkerPreview, pedCX - 8, pedCY - 8);

        // Micro-badge de Bloques
        int badgeY = topY + 86;
        CmiUiTheme.drawToolBadgePill(guiGraphics, this.font, col1X + 8, badgeY, CmiUiTheme.ToolCategory.BLOQUES);

        guiGraphics.text(this.font, Component.literal("§7Nombre de la Shulker:"), col1X + 8, topY + 98, CmiUiTheme.COLOR_MUTED);

        // Estadísticas de empaquetado
        int countY = topY + 134;
        guiGraphics.text(this.font, Component.literal("§7Capacidad de Caja:"), col1X + 8, countY, CmiUiTheme.COLOR_MUTED);
        guiGraphics.text(this.font, Component.literal("§a🎒 " + this.itemsToPack.size() + " / 27 slots llenos"), col1X + 8, countY + 10, CmiUiTheme.COLOR_EMERALD);
        guiGraphics.text(this.font, Component.literal("§8Color activo: §b" + this.selectedColor.toUpperCase()), col1X + 8, countY + 22, CmiUiTheme.COLOR_CYAN_NEON);

        // =========================================================================
        // COLUMNA 2 (45%): MATRIZ 4x4 DE LOS 16 TINTES OFICIALES
        // =========================================================================
        CmiUiTheme.drawGlassPanel(guiGraphics, col2X, topY, col2W, mainH, CmiUiTheme.GLASS_PANEL_BORDER);

        guiGraphics.fill(col2X + 4, topY + 4, col2X + col2W - 4, topY + 20, CmiUiTheme.PANEL_HEADER_BG);
        guiGraphics.centeredText(this.font, Component.literal("§b✦ SELECTOR DE 16 TINTES OFICIALES ✦"), col2X + col2W / 2, topY + 7, CmiUiTheme.COLOR_CYAN_NEON);

        int gridStartX = col2X + 10;
        int gridStartY = topY + 34;
        int cardW = (col2W - 20 - 12) / 4;
        int cardH = 46;

        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                int idx = r * 4 + c;
                if (idx < ShulkerPackerManager.ALL_COLORS.size()) {
                    ShulkerColorInfo info = ShulkerPackerManager.ALL_COLORS.get(idx);
                    int bx = gridStartX + c * (cardW + 4);
                    int by = gridStartY + r * (cardH + 4);

                    boolean isSelected = info.id().equalsIgnoreCase(this.selectedColor);
                    boolean isHover = mouseX >= bx && mouseX <= bx + cardW && mouseY >= by && mouseY <= by + cardH;

                    // Fondo de tarjeta con esquinas redondeadas
                    int cardBg = isSelected ? 0x850E2238 : (isHover ? 0x60121C2E : 0x400A1424);
                    int border = isSelected ? 0xFF00E5FF : (isHover ? 0x8038BDF8 : 0x3538BDF8);

                    CmiUiTheme.fillRoundedRect(guiGraphics, bx, by, cardW, cardH, 6, cardBg);
                    CmiUiTheme.drawRoundedBorder(guiGraphics, bx, by, cardW, cardH, 6, border);

                    // Ítem Shulker Box
                    ItemStack boxStack = new ItemStack(info.item());
                    guiGraphics.item(boxStack, bx + (cardW - 16) / 2, by + 5);

                    // Micro-punto de color
                    guiGraphics.fill(bx + 4, by + 4, bx + 8, by + 8, info.colorHex());

                    // Nombre del tinte
                    guiGraphics.centeredText(this.font, Component.literal(info.displayName()), bx + cardW / 2, by + 26, isSelected ? 0xFFFFD700 : 0xFFFFFFFF);
                }
            }
        }

        // =========================================================================
        // COLUMNA 3 (30%): LIVE CONTENTS INSPECTOR & VISOR /give
        // =========================================================================
        CmiUiTheme.drawGlassPanel(guiGraphics, col3X, topY, col3W, mainH, CmiUiTheme.GLASS_PANEL_BORDER);

        guiGraphics.fill(col3X + 4, topY + 4, col3X + col3W - 4, topY + 20, CmiUiTheme.PANEL_HEADER_BG);
        guiGraphics.centeredText(this.font, Component.literal("§a👁 CONTENIDO (27 SLOTS)"), col3X + col3W / 2, topY + 7, CmiUiTheme.COLOR_EMERALD);

        // Rejilla de 27 casillas (3x9) de la Shulker
        int slotSize = 16;
        int gridX = col3X + (col3W - (9 * slotSize)) / 2;
        int gridY = topY + 28;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 9; c++) {
                int slotIdx = r * 9 + c;
                int sx = gridX + c * slotSize;
                int sy = gridY + r * slotSize;
                boolean hovered = mouseX >= sx && mouseX < sx + slotSize && mouseY >= sy && mouseY < sy + slotSize;

                CmiUiTheme.drawSlot(guiGraphics, sx, sy, slotSize, hovered);
                if (slotIdx < this.itemsToPack.size()) {
                    ItemStack st = this.itemsToPack.get(slotIdx);
                    if (!st.isEmpty()) {
                        guiGraphics.item(st, sx, sy);
                        guiGraphics.itemDecorations(this.font, st, sx, sy);
                        if (hovered) {
                            guiGraphics.setTooltipForNextFrame(this.font, st, mouseX, mouseY);
                        }
                    }
                }
            }
        }

        // Visor del comando /give generado
        int cmdBoxY = gridY + 3 * slotSize + 8;
        int cmdBoxH = mainH - (cmdBoxY - topY) - 10;
        int cmdBoxW = col3W - 16;
        int cmdBoxX = col3X + 8;

        CmiUiTheme.fillRoundedRect(guiGraphics, cmdBoxX, cmdBoxY, cmdBoxW, cmdBoxH, 6, 0x450A1424);
        CmiUiTheme.drawRoundedBorder(guiGraphics, cmdBoxX, cmdBoxY, cmdBoxW, cmdBoxH, 6, 0x5038BDF8);

        guiGraphics.text(this.font, Component.literal("§e📄 Comando /give generado:"), cmdBoxX + 6, cmdBoxY + 6, CmiUiTheme.COLOR_AMBER);

        String cmd = ShulkerPackerManager.generateGiveCommand(this.itemsToPack, this.customTitleText, this.selectedColor);
        List<String> wrapped = wrapText(cmd, cmdBoxW - 12);
        int maxLines = (cmdBoxH - 22) / 10;
        for (int i = 0; i < Math.min(wrapped.size(), maxLines); i++) {
            guiGraphics.text(this.font, Component.literal("§8" + wrapped.get(i)), cmdBoxX + 6, cmdBoxY + 20 + i * 10, CmiUiTheme.COLOR_MUTED);
        }

        // Estado y Toast inferior
        if (System.currentTimeMillis() - this.statusToastTime < 3500) {
            guiGraphics.centeredText(this.font, Component.literal("§e" + this.statusToast), this.width / 2, this.height - 12, CmiUiTheme.COLOR_AMBER);
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        if (text == null) return lines;
        int len = text.length();
        int charsPerLine = Math.max(15, maxWidth / 6);
        for (int i = 0; i < len; i += charsPerLine) {
            lines.add(text.substring(i, Math.min(len, i + charsPerLine)));
        }
        return lines;
    }
}
