package com.fxfps.cmi.client;



import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import net.minecraft.world.item.component.ItemContainerContents;

import net.minecraft.world.item.enchantment.ItemEnchantments;

import com.fxfps.cmi.client.fonts.UnicodeFontHelper;

import com.fxfps.cmi.client.lores.ArgNetLoreManager;

import com.fxfps.cmi.client.kits.CmiKitManager;



import com.fxfps.cmi.CmiMod;

import com.fxfps.cmi.client.commands.CmiCommands;

import com.fxfps.cmi.client.config.CmiConfig;

import com.fxfps.cmi.client.config.CmiConfigManager;

import com.fxfps.cmi.client.gui.CmiButton;
import com.fxfps.cmi.client.gui.CmiEditorScreen;
import com.fxfps.cmi.client.gui.CmiTestScreen;
import com.fxfps.cmi.client.gui.CmiUiLayoutManager;

import com.fxfps.cmi.client.gui.CmiSettingsScreen;

import com.fxfps.cmi.client.gui.CmiUiTheme;

import com.fxfps.cmi.client.gui.HoveredSlotHelper;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;

import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;

import net.fabricmc.fabric.api.client.screen.v1.Screens;

import net.minecraft.core.component.DataComponents;

import java.util.List;

import net.minecraft.client.KeyMapping;

import net.minecraft.client.Minecraft;

import net.minecraft.client.gui.components.Button;

import net.minecraft.client.gui.components.EditBox;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import net.minecraft.network.chat.Component;

import net.minecraft.resources.Identifier;

import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;

import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.Items;

import org.lwjgl.glfw.GLFW;



public class CmiClientMod implements ClientModInitializer {



    public static KeyMapping.Category CMI_CATEGORY;
    public static KeyMapping openMenuKeyBinding;
    public static KeyMapping openSettingsKeyBinding;
    public static KeyMapping quickKitKeyBinding;
    private static boolean wasMenuKeyDownInWorld = false;



    @Override

    public void onInitializeClient() {

        CmiMod.LOGGER.info("[CMI] Inicializando cliente CheManyItems 🇦🇷");



        // 1. Cargar configuraciones de CMI y motor de UI Hot-Reload
        CmiConfigManager.init();
        CmiUiLayoutManager.init();

        CmiConfig config = CmiConfigManager.getConfig();



        // 2. Registrar categoría exclusiva de CMI (para que no quede perdida en 'Varios')

        CMI_CATEGORY = KeyMapping.Category.register(

                Identifier.fromNamespaceAndPath("cmi", "main")

        );



        // 3. Registrar teclas de acceso directo bajo la categoría CheManyItems

        openMenuKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(

                "key.cmi.open_editor",

                InputConstants.Type.KEYSYM,

                config.menuKeyCode,

                CMI_CATEGORY

        ));



        openSettingsKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(

                "key.cmi.open_settings",

                InputConstants.Type.KEYSYM,

                config.settingsKeyCode > 0 ? config.settingsKeyCode : InputConstants.UNKNOWN.getValue(),

                CMI_CATEGORY

        ));



        quickKitKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(

                "key.cmi.quick_kit",

                InputConstants.Type.KEYSYM,

                config.quickKitKeyCode,

                CMI_CATEGORY

        ));



        // 4. Registrar comandos de cliente (/cmi, /ie, /kit)

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {

            CmiCommands.register(dispatcher);

        });



        // 5. Escuchar pulsaciones de teclas en el mundo (sin pantalla abierta)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            if (client.level != null) {
                CmiKitManager.resolvePendingEnchantments(client);
            }

            boolean worldMenuTriggered = false;
            while (openMenuKeyBinding.consumeClick()) {
                worldMenuTriggered = true;
            }

            // Fallback de teclado para cuando se presiona la tecla configurada, 'U' o 'X' en el mundo
            if (client.getWindow() != null && client.screen == null) {
                int configured = config.menuKeyCode > 0 ? config.menuKeyCode : GLFW.GLFW_KEY_U;
                boolean isDown = InputConstants.isKeyDown(client.getWindow(), configured)
                        || InputConstants.isKeyDown(client.getWindow(), GLFW.GLFW_KEY_U)
                        || InputConstants.isKeyDown(client.getWindow(), GLFW.GLFW_KEY_X);
                if (isDown && !wasMenuKeyDownInWorld) {
                    worldMenuTriggered = true;
                }
                wasMenuKeyDownInWorld = isDown;
            } else {
                wasMenuKeyDownInWorld = false;
            }

            if (worldMenuTriggered) {
                if (client.player != null) {
                    try {
                        ItemStack held = client.player.getMainHandItem();
                        int slot = client.player.getInventory().getSelectedSlot();
                        client.setScreen(new CmiEditorScreen(null, held.isEmpty() ? new ItemStack(Items.NETHERITE_SWORD) : held, slot));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }

            while (openSettingsKeyBinding.consumeClick()) {
                client.setScreen(new CmiSettingsScreen(null));
            }

            while (quickKitKeyBinding.consumeClick()) {
                CmiKitManager.equipKitToInventory(client, config.defaultKitName);
            }
        });

        // 6. Escuchar pulsaciones dentro de inventarios, cofres y pantallas con Hover
        ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> {
            // Solo permitir interactuar dentro de pantallas de inventario y contenedores
            if (!(screen instanceof AbstractContainerScreen<?>)) {
                return;
            }

            ScreenKeyboardEvents.allowKeyPress(screen).register((scr, event) -> {
                // Si el jugador está escribiendo en una caja de texto (búsqueda, chat), no interceptar
                if (scr.getFocused() instanceof EditBox editBox && editBox.isFocused()) {
                    return true;
                }

                int k = event.key();
                int configured = config.menuKeyCode > 0 ? config.menuKeyCode : GLFW.GLFW_KEY_U;
                boolean matchesMenu = openMenuKeyBinding.matches(event)
                        || (k > 0 && (k == configured || k == GLFW.GLFW_KEY_U || k == GLFW.GLFW_KEY_X));

                if (matchesMenu) {
                    var slot = HoveredSlotHelper.getHoveredSlot(scr);
                    ItemStack item = ItemStack.EMPTY;
                    int containerSlot = -1;
                    if (slot != null) {
                        if (slot.hasItem()) {
                            item = slot.getItem().copy();
                        } else {
                            item = new ItemStack(Items.NETHERITE_SWORD);
                        }
                        containerSlot = slot.getContainerSlot();
                    } else if (client.player != null && !client.player.getMainHandItem().isEmpty()) {
                        item = client.player.getMainHandItem().copy();
                        containerSlot = client.player.getInventory().getSelectedSlot();
                    } else {
                        containerSlot = (client.player != null) ? client.player.getInventory().getSelectedSlot() : 0;
                        item = new ItemStack(Items.NETHERITE_SWORD);
                    }

                    try {
                        client.setScreen(new CmiEditorScreen(scr, item, slot, containerSlot));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    return false;
                }

                int settingsConfigured = config.settingsKeyCode > 0 ? config.settingsKeyCode : -1;
                if (openSettingsKeyBinding.matches(event) || (settingsConfigured > 0 && k == settingsConfigured)) {
                    client.setScreen(new CmiSettingsScreen(scr));
                    return false;
                }

                return true;
            });
        });

        // 7. Inyectar botón flotante CMI [ 🇦🇷 CMI ] en todas las pantallas de inventario y cofres
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            if (screen instanceof AbstractContainerScreen<?> containerScreen) {
                int btnX = screen.width - 64;
                int btnY = 6;
                CmiButton cmiFloatingBtn = new CmiButton(
                        btnX, btnY, 56, 18,
                        Component.literal("§b" + CmiUiTheme.LOGO_CMI + " §6§lCMI"),
                        btn -> {
                            var slot = HoveredSlotHelper.getHoveredSlot(containerScreen);
                            ItemStack item = ItemStack.EMPTY;
                            int containerSlot = -1;
                            if (slot != null) {
                                if (slot.hasItem()) {
                                    item = slot.getItem().copy();
                                } else {
                                    item = new ItemStack(Items.NETHERITE_SWORD);
                                }
                                containerSlot = slot.getContainerSlot();
                            } else if (client.player != null && !client.player.getMainHandItem().isEmpty()) {
                                item = client.player.getMainHandItem().copy();
                                containerSlot = client.player.getInventory().getSelectedSlot();
                            } else {
                                containerSlot = (client.player != null) ? client.player.getInventory().getSelectedSlot() : 0;
                                item = new ItemStack(Items.NETHERITE_SWORD);
                            }

                            try {
                                client.setScreen(new CmiEditorScreen(containerScreen, item, slot, containerSlot));
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                        },
                        CmiButton.Variant.TAB
                );
                Screens.getWidgets(screen).add(cmiFloatingBtn);
            }
        });



        // 8. Interceptar tooltips para Shulker Spy y formateo de lore/tags

        ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
            try {
                if (stack == null || stack.isEmpty() || lines.isEmpty()) return;

                // Módulo Shulker Spy (Hover Preview de Shulker Box, Bundles y Contenedores)
                if (CmiConfigManager.getConfig().shulkerSpyEnabled && stack.has(DataComponents.CONTAINER)) {
                    ItemContainerContents container = stack.get(DataComponents.CONTAINER);
                    if (container != null) {
                        List<ItemStack> items = container.nonEmptyItemCopyStream().toList();
                        if (!items.isEmpty()) {
                            lines.removeIf(comp -> {
                                String s = comp.getString().toLowerCase();
                                return (s.contains(" y ") && s.contains(" más")) || (s.contains(" and ") && s.contains(" more"));
                            });

                            lines.add(Component.literal("§b┌── 📦 §lShulker Spy §7(" + items.size() + "/27 slots) §b──┐"));
                            int maxDisplay = Math.min(items.size(), 10);
                            for (int i = 0; i < maxDisplay; i++) {
                                ItemStack sub = items.get(i);
                                String name = sub.getHoverName().getString();
                                int count = sub.getCount();
                                String enchBadge = "";
                                var enchs = sub.getOrDefault(sub.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
                                if (!enchs.isEmpty()) {
                                    int maxLvl = 0;
                                    for (var entry : enchs.entrySet()) {
                                        int l = entry.getIntValue();
                                        if (l > maxLvl) maxLvl = l;
                                    }
                                    if (maxLvl >= 100) {
                                        enchBadge = " §6⚡32k";
                                    } else if (maxLvl > 0) {
                                        enchBadge = " §b⚡" + CmiUiTheme.toRoman(maxLvl);
                                    }
                                }
                                lines.add(Component.literal(" §7• §f" + count + "x " + name + enchBadge));
                            }
                            if (items.size() > maxDisplay) {
                                lines.add(Component.literal(" §8... y " + (items.size() - maxDisplay) + " ítems más"));
                            }
                            lines.add(Component.literal("§b└──────────────────────────────┘"));
                        }
                    }
                }

                // Formatear títulos con tags animados o códigos de formato si existen EXCLUSIVAMENTE en ítems con nombre personalizado.
                // Si el ítem no fue renombrado (es un ítem vanilla como Espada de diamante), se preserva 100% su nombre y estilo nativo.
                if (stack.has(DataComponents.CUSTOM_NAME)) {
                    Component title = lines.get(0);
                    String raw = title.getString();
                    if (UnicodeFontHelper.hasCustomTagsOrShortcodes(raw)) {
                        lines.set(0, UnicodeFontHelper.parseFormattedComponent(raw));
                    }
                }

                // Detección y formateo de lore personalizado (preservando intactos atributos naturales y durabilidad)
                if (CmiConfigManager.getConfig().autoArgNetLore) {
                    ArgNetLoreManager.processTooltipLines(stack, lines);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

    }



    /**

     * Actualiza la tecla del Editor en caliente y sincroniza con Minecraft.

     */

    public static void updateMenuKeyBinding(int newKeyCode) {

        if (openMenuKeyBinding != null) {

            var key = newKeyCode > 0 ? InputConstants.Type.KEYSYM.getOrCreate(newKeyCode) : InputConstants.UNKNOWN;

            openMenuKeyBinding.setKey(key);

            KeyMapping.resetMapping();

            saveMinecraftOptions();

        }

    }



    /**

     * Actualiza la tecla de Ajustes en caliente y sincroniza con Minecraft.

     */

    public static void updateSettingsKeyBinding(int newKeyCode) {

        if (openSettingsKeyBinding != null) {

            var key = newKeyCode > 0 ? InputConstants.Type.KEYSYM.getOrCreate(newKeyCode) : InputConstants.UNKNOWN;

            openSettingsKeyBinding.setKey(key);

            KeyMapping.resetMapping();

            saveMinecraftOptions();

        }

    }



    /**

     * Actualiza la tecla de Kit Rápido en caliente y sincroniza con Minecraft.

     */

    public static void updateQuickKitKeyBinding(int newKeyCode) {

        if (quickKitKeyBinding != null) {

            var key = newKeyCode > 0 ? InputConstants.Type.KEYSYM.getOrCreate(newKeyCode) : InputConstants.UNKNOWN;

            quickKitKeyBinding.setKey(key);

            KeyMapping.resetMapping();

            saveMinecraftOptions();

        }

    }



    private static void saveMinecraftOptions() {

        try {

            var mc = Minecraft.getInstance();

            if (mc != null && mc.options != null) {

                mc.options.save();

            }

        } catch (Exception ignored) {}

    }

}

