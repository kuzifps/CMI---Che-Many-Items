package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.client.CmiClientMod;
import com.fxfps.cmi.client.config.CmiConfig;
import com.fxfps.cmi.client.config.CmiConfigManager;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/**
 * Menú de Ajustes dedicado de CheManyItems (CMI) 🇦🇷
 * Interfaz moderna con fondo inmersivo animado, bordes de vidrio oscuro y estilo PvP.
 */
public class CmiSettingsScreen extends CmiScreen {

    // parentScreen heredado de CmiScreen

    private enum ListeningTarget {
        NONE, MENU_KEY, SETTINGS_KEY, QUICK_KIT_KEY
    }

    private ListeningTarget listeningTarget = ListeningTarget.NONE;

    private CmiButton menuKeyButton;
    private CmiButton settingsKeyButton;
    private CmiButton quickKitKeyButton;

    private CmiButton safeModeButton;
    private CmiButton shulkerSpyButton;
    private CmiButton dpsCalcButton;
    private CmiButton animPreviewButton;
    private CmiButton autoArgNetLoreButton;

    public CmiSettingsScreen(Screen parentScreen) {
        super(Component.literal("Ajustes de CheManyItems (CMI) 🇦🇷"), parentScreen);

    }

    @Override
    protected void init() {
        super.init();
        CmiConfig config = CmiConfigManager.getConfig();

        int centerX = this.width / 2;
        int startY = 46;
        int btnWidth = 260;
        int btnHeight = 20;

        // 1. Tecla para Abrir Menú Editor
        menuKeyButton = new CmiButton(
                centerX - btnWidth / 2, startY, btnWidth, btnHeight,
                getMenuKeyButtonText(config),
                btn -> {
                    listeningTarget = ListeningTarget.MENU_KEY;
                    refreshButtonLabels();
                },
                CmiButton.Variant.PRIMARY
        );
        this.addRenderableWidget(menuKeyButton);

        // 2. Tecla para Menú de Ajustes
        settingsKeyButton = new CmiButton(
                centerX - btnWidth / 2, startY + 24, btnWidth, btnHeight,
                getSettingsKeyButtonText(config),
                btn -> {
                    listeningTarget = ListeningTarget.SETTINGS_KEY;
                    refreshButtonLabels();
                },
                CmiButton.Variant.PRIMARY
        );
        this.addRenderableWidget(settingsKeyButton);

        // 3. Tecla para Equipar Kit Rápido
        quickKitKeyButton = new CmiButton(
                centerX - btnWidth / 2, startY + 48, btnWidth, btnHeight,
                getQuickKitButtonText(config),
                btn -> {
                    listeningTarget = ListeningTarget.QUICK_KIT_KEY;
                    refreshButtonLabels();
                },
                CmiButton.Variant.PRIMARY
        );
        this.addRenderableWidget(quickKitKeyButton);

        // Separador de modulos
        int moduleStartY = startY + 76;

        // 4. Safe Mode Anti-Kick NBT (Toggle estilo CMI)
        safeModeButton = new CmiButton(
                centerX - btnWidth / 2, moduleStartY, btnWidth, btnHeight,
                Component.literal("🛡 Safe Mode Anti-Kick"),
                btn -> {
                    config.safeModeEnabled = !config.safeModeEnabled;
                    CmiConfigManager.save();
                    if (btn instanceof CmiButton cb) cb.setToggle(true, config.safeModeEnabled);
                },
                CmiButton.Variant.DEFAULT
        );
        safeModeButton.setToggle(true, config.safeModeEnabled);
        this.addRenderableWidget(safeModeButton);

        // 5. Shulker Spy (Hover Preview) (Toggle estilo CMI)
        shulkerSpyButton = new CmiButton(
                centerX - btnWidth / 2, moduleStartY + 24, btnWidth, btnHeight,
                Component.literal("📦 Shulker Spy (Preview)"),
                btn -> {
                    config.shulkerSpyEnabled = !config.shulkerSpyEnabled;
                    CmiConfigManager.save();
                    if (btn instanceof CmiButton cb) cb.setToggle(true, config.shulkerSpyEnabled);
                },
                CmiButton.Variant.DEFAULT
        );
        shulkerSpyButton.setToggle(true, config.shulkerSpyEnabled);
        this.addRenderableWidget(shulkerSpyButton);

        // 6. Calculadora DPS (Toggle estilo CMI)
        dpsCalcButton = new CmiButton(
                centerX - btnWidth / 2, moduleStartY + 48, btnWidth, btnHeight,
                Component.literal("⚔ Calculadora DPS"),
                btn -> {
                    config.dpsCalculatorEnabled = !config.dpsCalculatorEnabled;
                    CmiConfigManager.save();
                    if (btn instanceof CmiButton cb) cb.setToggle(true, config.dpsCalculatorEnabled);
                },
                CmiButton.Variant.DEFAULT
        );
        dpsCalcButton.setToggle(true, config.dpsCalculatorEnabled);
        this.addRenderableWidget(dpsCalcButton);

        // 7. Tags Animados en Chat (Toggle estilo CMI)
        animPreviewButton = new CmiButton(
                centerX - btnWidth / 2, moduleStartY + 72, btnWidth, btnHeight,
                Component.literal("🌈 Tags Animados en Chat"),
                btn -> {
                    config.liveAnimationPreview = !config.liveAnimationPreview;
                    CmiConfigManager.save();
                    if (btn instanceof CmiButton cb) cb.setToggle(true, config.liveAnimationPreview);
                },
                CmiButton.Variant.DEFAULT
        );
        animPreviewButton.setToggle(true, config.liveAnimationPreview);
        this.addRenderableWidget(animPreviewButton);

        // 8. Auto-Lore Argentina Network 🇦🇷 (Toggle estilo CMI)
        autoArgNetLoreButton = new CmiButton(
                centerX - btnWidth / 2, moduleStartY + 96, btnWidth, btnHeight,
                Component.literal("🇦🇷 Auto-Lore Argentina Network"),
                btn -> {
                    config.autoArgNetLore = !config.autoArgNetLore;
                    CmiConfigManager.save();
                    if (btn instanceof CmiButton cb) cb.setToggle(true, config.autoArgNetLore);
                },
                CmiButton.Variant.DEFAULT
        );
        autoArgNetLoreButton.setToggle(true, config.autoArgNetLore);
        this.addRenderableWidget(autoArgNetLoreButton);

        // 9. Botones Inferiores: Restaurar y Guardar
        int bottomY = moduleStartY + 126;
        int halfWidth = (btnWidth - 8) / 2;

        this.addRenderableWidget(new CmiButton(
                centerX - btnWidth / 2, bottomY, halfWidth, btnHeight,
                Component.literal(CmiUiTheme.ICON_REFRESH + " Restablecer"),
                btn -> {
                    config.menuKeyCode = 85; // 'U'
                    config.menuKeyName = "U";
                    config.quickKitKeyCode = 86; // 'V'
                    config.quickKitKeyName = "V";
                    config.settingsKeyCode = -1;
                    config.settingsKeyName = "SIN ASIGNAR";
                    config.autoArgNetLore = true;
                    CmiConfigManager.save();

                    CmiClientMod.updateMenuKeyBinding(config.menuKeyCode);
                    CmiClientMod.updateQuickKitKeyBinding(config.quickKitKeyCode);
                    CmiClientMod.updateSettingsKeyBinding(config.settingsKeyCode);

                    if (autoArgNetLoreButton != null) autoArgNetLoreButton.setToggle(true, true);
                    refreshButtonLabels();
                },
                CmiButton.Variant.DANGER
        ));

        this.addRenderableWidget(new CmiButton(
                centerX + 4, bottomY, halfWidth, btnHeight,
                Component.literal(CmiUiTheme.ICON_CHECK + " Guardar y Volver"),
                btn -> {
                    CmiConfigManager.save();
                    this.minecraft.setScreen(parentScreen);
                },
                CmiButton.Variant.SUCCESS
        ));
    }

    private void refreshButtonLabels() {
        CmiConfig config = CmiConfigManager.getConfig();
        if (menuKeyButton != null) menuKeyButton.setMessage(getMenuKeyButtonText(config));
        if (settingsKeyButton != null) settingsKeyButton.setMessage(getSettingsKeyButtonText(config));
        if (quickKitKeyButton != null) quickKitKeyButton.setMessage(getQuickKitButtonText(config));
    }

    private Component getMenuKeyButtonText(CmiConfig config) {
        if (listeningTarget == ListeningTarget.MENU_KEY) {
            return Component.literal("§e> Presiona una tecla para el Menú... <");
        }
        return Component.literal("⌨ Tecla Editor CMI: [ §6" + config.menuKeyName + "§r ]");
    }

    private Component getSettingsKeyButtonText(CmiConfig config) {
        if (listeningTarget == ListeningTarget.SETTINGS_KEY) {
            return Component.literal("§e> Presiona una tecla para Ajustes... <");
        }
        return Component.literal(CmiUiTheme.ICON_SETTINGS + " Tecla Ajustes CMI: [ §6" + config.settingsKeyName + "§r ]");
    }

    private Component getQuickKitButtonText(CmiConfig config) {
        if (listeningTarget == ListeningTarget.QUICK_KIT_KEY) {
            return Component.literal("§e> Presiona una tecla para Kit Rápido... <");
        }
        return Component.literal("⚡ Tecla Kit Rápido: [ §6" + config.quickKitKeyName + "§r ]");
    }

    private Component getSafeModeButtonText(CmiConfig config) {
        return Component.literal("🛡 Safe Mode Anti-Kick: " + (config.safeModeEnabled ? "§aACTIVADO" : "§cDESACTIVADO"));
    }

    private Component getShulkerSpyButtonText(CmiConfig config) {
        return Component.literal("📦 Shulker Spy (Preview): " + (config.shulkerSpyEnabled ? "§aACTIVADO" : "§cDESACTIVADO"));
    }

    private Component getDpsCalcButtonText(CmiConfig config) {
        return Component.literal("⚔ Calculadora DPS: " + (config.dpsCalculatorEnabled ? "§aACTIVADO" : "§cDESACTIVADO"));
    }

    private Component getAnimPreviewButtonText(CmiConfig config) {
        return Component.literal("🌈 Tags Animados en Chat: " + (config.liveAnimationPreview ? "§aACTIVADO" : "§cDESACTIVADO"));
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        int keyCode = event.key();

        if (listeningTarget != ListeningTarget.NONE) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                listeningTarget = ListeningTarget.NONE;
                refreshButtonLabels();
                return true;
            }

            CmiConfig config = CmiConfigManager.getConfig();

            if (keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
                applyKeyBinding(listeningTarget, -1, "SIN ASIGNAR", config);
                listeningTarget = ListeningTarget.NONE;
                refreshButtonLabels();
                return true;
            }

            String keyName = getKeyName(keyCode);
            applyKeyBinding(listeningTarget, keyCode, keyName, config);
            listeningTarget = ListeningTarget.NONE;
            refreshButtonLabels();
            return true;
        }

        return super.keyPressed(event);
    }

    private void applyKeyBinding(ListeningTarget target, int keyCode, String keyName, CmiConfig config) {
        switch (target) {
            case MENU_KEY -> {
                config.menuKeyCode = keyCode;
                config.menuKeyName = keyName;
                CmiClientMod.updateMenuKeyBinding(keyCode);
            }
            case SETTINGS_KEY -> {
                config.settingsKeyCode = keyCode;
                config.settingsKeyName = keyName;
                CmiClientMod.updateSettingsKeyBinding(keyCode);
            }
            case QUICK_KIT_KEY -> {
                config.quickKitKeyCode = keyCode;
                config.quickKitKeyName = keyName;
                CmiClientMod.updateQuickKitKeyBinding(keyCode);
            }
            default -> {}
        }
        CmiConfigManager.save();
    }

    private String getKeyName(int keyCode) {
        if (keyCode <= 0) return "SIN ASIGNAR";
        try {
            return com.mojang.blaze3d.platform.InputConstants.Type.KEYSYM.getOrCreate(keyCode).getDisplayName().getString().toUpperCase();
        } catch (Throwable t) {
            String name = GLFW.glfwGetKeyName(keyCode, 0);
            if (name != null && !name.trim().isEmpty()) {
                return name.toUpperCase();
            }
            return "KEY_" + keyCode;
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 1. Fondo inmersivo completo con partículas estelares
        CmiUiTheme.drawScreenBackdrop(guiGraphics, this.width, this.height);

        // 2. Tarjeta central de ajustes
        int centerX = this.width / 2;
        int cardW = 284;
        int cardH = 224;
        int cardX = centerX - cardW / 2;
        int cardY = 32;

        CmiUiTheme.drawModernCard(guiGraphics, cardX, cardY, cardW, cardH, null, 0, true);

        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);

        // 3. Título principal con logo del mod ampliado
        String titleStr = "CheManyItems (CMI) - Ajustes";
        int titleW = this.font.width(titleStr);
        int logoSize = 20;
        int totalTitleW = logoSize + 6 + titleW;
        int startX = centerX - totalTitleW / 2;
        CmiUiTheme.drawLogo(guiGraphics, startX, 10, logoSize);
        guiGraphics.text(this.font, Component.literal("§b§lCheManyItems (CMI) §7- §fAjustes"), startX + logoSize + 6, 16, 0xFF00D2FF);

        // Texto informativo al pie
        guiGraphics.centeredText(this.font, Component.literal("§8Las teclas se sincronizan automáticamente con Minecraft"), centerX, this.height - 16, 0xFF666666);
    }

    @Override
    public void onClose() {
        CmiConfigManager.save();
        this.minecraft.setScreen(parentScreen);
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
