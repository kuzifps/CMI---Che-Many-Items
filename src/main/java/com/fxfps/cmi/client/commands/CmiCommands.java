package com.fxfps.cmi.client.commands;

import com.fxfps.cmi.client.config.CmiConfigManager;
import com.fxfps.cmi.client.fonts.GradientHelper;
import com.fxfps.cmi.client.fonts.ServerEmojiRegistry;
import com.fxfps.cmi.client.fonts.UnicodeFontHelper;
import com.fxfps.cmi.client.gui.CmiAutoLoreScreen;
import com.fxfps.cmi.client.gui.CmiEditorScreen;
import com.fxfps.cmi.client.gui.CmiEmojiPickerScreen;
import com.fxfps.cmi.client.gui.CmiKitsScreen;
import com.fxfps.cmi.client.gui.CmiShulkerPackerScreen;
import com.fxfps.cmi.client.gui.CmiSettingsScreen;
import com.fxfps.cmi.client.gui.CmiTestScreen;
import com.fxfps.cmi.client.gui.CmiUiTheme;
import com.fxfps.cmi.client.gui.HoveredSlotHelper;
import com.fxfps.cmi.client.kits.CmiKitManager;
import com.fxfps.cmi.client.lores.ArgNetLoreManager;
import com.fxfps.cmi.client.lores.AutoLoreRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

import java.util.ArrayList;
import java.util.List;

public class CmiCommands {

    public static boolean renameSentFromMod = false;
    public static String lastRenamedValue = null;

    /**
     * Construye y envia automaticamente el comando de servidor:
     * /rename &l<animated> (nombre elegido)
     * Tambien aplica directamente al item en mano si el jugador esta en creativo.
     */
    public static boolean sendRenameCommand(Minecraft client, String rawInput) {
        if (client == null || client.player == null) {
            return false;
        }

        renameSentFromMod = true;
        lastRenamedValue = rawInput;

        if (rawInput == null || rawInput.trim().isEmpty()) {
            client.player.sendSystemMessage(
                    Component.literal("§c[CMI] Debes especificar un nombre para renombrar.")
            );
            return false;
        }

        String input = ServerEmojiRegistry.replaceShortcodes(rawInput.trim());

        // 1. Soporte para flag --smallcaps o --sc
        if (input.startsWith("--smallcaps ") || input.startsWith("--sc ")) {
            int prefixLen = input.startsWith("--smallcaps ") ? 12 : 5;
            String text = input.substring(prefixLen).trim();
            input = UnicodeFontHelper.toSmallCaps(text);
        }

        // 3. Limpieza de comandos /rename previos si fueron pasados
        if (input.startsWith("/rename ")) {
            input = input.substring(8).trim();
        } else if (input.startsWith("rename ")) {
            input = input.substring(7).trim();
        }

        // 4. Si contiene MiniMessage <gradient:...>, convertir a &#HEX universal por caracter
        input = GradientHelper.convertMiniMessageGradientsToHex(input);

        // 5. Proteger badges en el texto: asegurar que cualquier glifo de badge tenga &f
        StringBuilder cleanBuilder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (ServerEmojiRegistry.isBadgeUnicode(String.valueOf(c))) {
                boolean hasWhite = (cleanBuilder.length() >= 2 && 
                        (cleanBuilder.charAt(cleanBuilder.length() - 2) == '&' || cleanBuilder.charAt(cleanBuilder.length() - 2) == '§') &&
                        Character.toLowerCase(cleanBuilder.charAt(cleanBuilder.length() - 1)) == 'f');
                if (!hasWhite) {
                    cleanBuilder.append("&f");
                }
            }
            cleanBuilder.append(c);
        }
        String chosenName = cleanBuilder.toString();

        // 6. Si el texto comienza con un tag animado y no especifica negrita o color, asegurar &l
        if (chosenName.matches("(?i)^<(animated|rainbow|celestial|fire|ocean|golden|toxic|sakura|void|silver|frost|lava|matrix|aurora|sunset|neon|candy|galaxy|glitch|blood|emerald|diamond)>[^&§].*")) {
            int closeTag = chosenName.indexOf('>');
            chosenName = chosenName.substring(0, closeTag + 1) + "&l" + chosenName.substring(closeTag + 1);
        }

        // 6.1 Aplicar directamente al item en mano si esta en creativo
        ItemStack held = client.player.getMainHandItem();
        if (!held.isEmpty() && client.player.isCreative() && client.gameMode != null) {
            held.set(DataComponents.CUSTOM_NAME, UnicodeFontHelper.parseFormattedComponent(chosenName));
            int selectedSlot = client.player.getInventory().getSelectedSlot();
            client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
        }

        // 7. Construir comando exacto para servidor:
        String serverCmd = "rename " + chosenName;

        // 8. Enviar comando de chat al servidor
        if (client.player.connection != null) {
            client.player.connection.sendCommand(serverCmd);
            client.player.sendSystemMessage(
                    Component.literal("§a[CMI] ✔ Nombre aplicado. Enviado al servidor: §e/" + serverCmd)
            );
            return true;
        } else {
            client.player.sendSystemMessage(
                    Component.literal("§c[CMI] Error: No hay conexion activa con el servidor.")
            );
            return false;
        }
    }

    public static void sendCustomEnchantCommand(Minecraft client, String enchantId, int level) {
        if (client == null || client.player == null) return;
        ItemStack held = client.player.getMainHandItem();
        if (!held.isEmpty()) {
            ArgNetLoreManager.addOrUpdateCustomEnchant(held, enchantId, level);
            if (client.player.isCreative() && client.gameMode != null) {
                int slot = client.player.getInventory().getSelectedSlot();
                client.gameMode.handleCreativeModeItemAdd(held, 36 + slot);
            }
        }
        if (client.player.connection != null) {
            client.player.connection.sendCommand("customenchant add " + enchantId + " " + level);
        }
        client.player.sendSystemMessage(Component.literal("§a[CMI] ✔ Encantamiento personalizado §e" + enchantId + " " + level + " §aaplicado."));
    }

    private static void openEditorWithContainer(Minecraft client) {
        if (client == null) return;
        try {
            if (client.screen instanceof AbstractContainerScreen<?> containerScreen) {
                var slot = HoveredSlotHelper.getHoveredSlot(containerScreen);
                ItemStack item = ItemStack.EMPTY;
                int containerSlot = -1;
                if (slot != null && slot.hasItem()) {
                    item = slot.getItem().copy();
                    containerSlot = slot.getContainerSlot();
                } else if (client.player != null && !client.player.getMainHandItem().isEmpty()) {
                    item = client.player.getMainHandItem().copy();
                    containerSlot = client.player.getInventory().getSelectedSlot();
                } else {
                    containerSlot = (client.player != null) ? client.player.getInventory().getSelectedSlot() : 0;
                    item = new ItemStack(Items.NETHERITE_SWORD);
                }
                client.setScreen(new CmiEditorScreen(containerScreen, item, slot, containerSlot));
            } else {
                ItemStack item = (client.player != null && !client.player.getMainHandItem().isEmpty())
                        ? client.player.getMainHandItem().copy()
                        : new ItemStack(Items.NETHERITE_SWORD);
                int slot = (client.player != null) ? client.player.getInventory().getSelectedSlot() : 0;
                client.setScreen(new CmiEditorScreen(null, item, slot));
            }
        } catch (Throwable t) {
            com.fxfps.cmi.CmiMod.LOGGER.error("Error al abrir editor CMI", t);
        }
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        // 1. Comando principal: /cmi
        var cmiRoot = ClientCommands.literal("cmi")
                .executes(ctx -> {
                    Minecraft mc = Minecraft.getInstance();
                    mc.execute(() -> openEditorWithContainer(mc));
                    return 1;
                })
                .then(ClientCommands.literal("menu")
                        .executes(ctx -> {
                            Minecraft mc = Minecraft.getInstance();
                            mc.execute(() -> openEditorWithContainer(mc));
                            return 1;
                        }))
                .then(ClientCommands.literal("editor")
                        .executes(ctx -> {
                            Minecraft mc = Minecraft.getInstance();
                            mc.execute(() -> openEditorWithContainer(mc));
                            return 1;
                        }))
                .then(ClientCommands.literal("test")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiTestScreen(null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("demo")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiTestScreen(null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("settings")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiSettingsScreen(null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("kits")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiKitsScreen(null)));
                            return 1;
                        })
                        .then(ClientCommands.literal("create")
                                .then(ClientCommands.argument("nombre", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "nombre");
                                            Minecraft mc = Minecraft.getInstance();
                                            if (mc.player != null) {
                                                List<ItemStack> inv = new ArrayList<>();
                                                for (int i = 0; i < 36; i++) {
                                                    inv.add(mc.player.getInventory().getItem(i).copy());
                                                }
                                                CmiKitManager.saveFullKit(name, inv);
                                                ctx.getSource().sendFeedback(Component.literal("§a[CMI] 🎒 Kit §e" + name + " §aguardado con tu inventario actual!"));
                                                return 1;
                                            }
                                            return 0;
                                        }))))
                .then(ClientCommands.literal("config")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiSettingsScreen(null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("emojis")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiEmojiPickerScreen(null, null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("shulker")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiShulkerPackerScreen(null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("pack")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiShulkerPackerScreen(null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("help")
                        .executes(ctx -> {
                            ctx.getSource().sendFeedback(Component.literal("§6§l=== CheManyItems (CMI) Comandos ==="));
                            ctx.getSource().sendFeedback(Component.literal("§e/cmi §7- Abre el Editor Visual de Items"));
                            ctx.getSource().sendFeedback(Component.literal("§e/cmi rename <nombre> §7- Renombra item con colores y formato"));
                            ctx.getSource().sendFeedback(Component.literal("§e/cmi enchant <ench> <lvl> §7- Encanta el item sostenido (1-32767)"));
                            ctx.getSource().sendFeedback(Component.literal("§e/cmi lore paste §7- Pega lore desde el portapapeles"));
                            ctx.getSource().sendFeedback(Component.literal("§e/cmi lore clear §7- Limpia el lore del item sostenido"));
                            ctx.getSource().sendFeedback(Component.literal("§e/fixlore §7- Formatea lore al estandar Argentina Network"));
                            ctx.getSource().sendFeedback(Component.literal("§e/autolore §7- Configura presets de Auto-Lore"));
                            ctx.getSource().sendFeedback(Component.literal("§e/kit §7- Gestor visual y rapido de Kits"));
                            ctx.getSource().sendFeedback(Component.literal("§e/kit create <nombre> §7- Guarda el inventario en un kit"));
                            ctx.getSource().sendFeedback(Component.literal("§e/cmi settings §7- Ajustes de CMI"));
                            ctx.getSource().sendFeedback(Component.literal("§6==================================="));
                            return 1;
                        }))
                .then(ClientCommands.literal("rename")
                        .then(ClientCommands.literal("--smallcaps")
                                .then(ClientCommands.argument("nombre_sc", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "nombre_sc");
                                            sendRenameCommand(Minecraft.getInstance(), UnicodeFontHelper.toSmallCaps(name));
                                            return 1;
                                        })))
                        .then(ClientCommands.literal("--sc")
                                .then(ClientCommands.argument("nombre_sc", StringArgumentType.greedyString())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "nombre_sc");
                                            sendRenameCommand(Minecraft.getInstance(), UnicodeFontHelper.toSmallCaps(name));
                                            return 1;
                                        })))
                        .then(ClientCommands.argument("nombre", StringArgumentType.greedyString())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "nombre");
                                    sendRenameCommand(Minecraft.getInstance(), name);
                                    return 1;
                                })))
                .then(ClientCommands.literal("enchant")
                        .then(ClientCommands.argument("enchant", StringArgumentType.word())
                                .then(ClientCommands.argument("nivel", IntegerArgumentType.integer(1, 32767))
                                        .executes(ctx -> {
                                            String enchName = StringArgumentType.getString(ctx, "enchant");
                                            int lvl = IntegerArgumentType.getInteger(ctx, "nivel");
                                            Minecraft client = Minecraft.getInstance();
                                            if (client.player == null || client.level == null) return 0;
                                            ItemStack held = client.player.getMainHandItem();
                                            if (held.isEmpty()) {
                                                ctx.getSource().sendFeedback(Component.literal("§c[CMI] Sosten un item en la mano principal para encantarlo."));
                                                return 0;
                                            }
                                            var reg = client.level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
                                            Holder<Enchantment> targetHolder = null;
                                            String search = enchName.toLowerCase().replace("minecraft:", "").replace("_", "");
                                            for (var holder : reg.listElements().toList()) {
                                                String key = holder.unwrapKey().map(k -> k.identifier().getPath().toLowerCase().replace("_", "")).orElse("");
                                                String fullKey = holder.unwrapKey().map(k -> k.identifier().toString().toLowerCase()).orElse("");
                                                if (key.equals(search) || fullKey.equals(enchName.toLowerCase()) || key.startsWith(search)) {
                                                    targetHolder = holder;
                                                    break;
                                                }
                                            }
                                            if (targetHolder == null) {
                                                ctx.getSource().sendFeedback(Component.literal("§c[CMI] No se encontro el encantamiento: §e" + enchName));
                                                return 0;
                                            }
                                            boolean isBook = held.is(Items.ENCHANTED_BOOK);
                                            var compType = isBook ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS;
                                            var current = held.getOrDefault(compType, ItemEnchantments.EMPTY);
                                            var mut = new ItemEnchantments.Mutable(current);
                                            CmiEditorScreen.setMutableEnchantLevel(mut, targetHolder, lvl);
                                            held.set(compType, mut.toImmutable());
                                            if (CmiConfigManager.getConfig().autoArgNetLore) {
                                                ArgNetLoreManager.syncEnchantmentsToLore(held);
                                            }
                                            int selectedSlot = client.player.getInventory().getSelectedSlot();
                                            if (client.player.isCreative() && client.gameMode != null) {
                                                client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
                                            }
                                            String pathName = targetHolder.unwrapKey().map(k -> k.identifier().getPath()).orElse("enchant");
                                            String displayLvl = lvl >= 100 ? "§6⚡" + lvl : "§b" + CmiUiTheme.toRoman(lvl);
                                            ctx.getSource().sendFeedback(Component.literal("§a[CMI] ✔ Encantamiento §e" + pathName + " " + displayLvl + " §aaplicado!"));
                                            return 1;
                                        }))))
                .then(ClientCommands.literal("fixlore")
                        .executes(ctx -> {
                            var client = Minecraft.getInstance();
                            if (client.player != null) {
                                var held = client.player.getMainHandItem();
                                if (!held.isEmpty()) {
                                    ArgNetLoreManager.restructureItemStack(held);
                                    int selectedSlot = client.player.getInventory().getSelectedSlot();
                                    if (client.player.isCreative() && client.gameMode != null) {
                                        client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
                                    }
                                    ctx.getSource().sendFeedback(Component.literal("§a[CMI] 🇦🇷 ¡Lore reestructurado al formato oficial de Argentina Network!"));
                                    return 1;
                                }
                            }
                            ctx.getSource().sendFeedback(Component.literal("§c[CMI] Sosten un item en la mano para reestructurar su lore."));
                            return 1;
                        }))
                .then(ClientCommands.literal("lore")
                        .then(ClientCommands.literal("format")
                                .executes(ctx -> {
                                    var client = Minecraft.getInstance();
                                    if (client.player != null) {
                                        var held = client.player.getMainHandItem();
                                        if (!held.isEmpty()) {
                                            ArgNetLoreManager.restructureItemStack(held);
                                            int selectedSlot = client.player.getInventory().getSelectedSlot();
                                            if (client.player.isCreative() && client.gameMode != null) {
                                                client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
                                            }
                                            ctx.getSource().sendFeedback(Component.literal("§a[CMI] 🇦🇷 ¡Lore reestructurado al formato oficial de Argentina Network!"));
                                            return 1;
                                        }
                                    }
                                    ctx.getSource().sendFeedback(Component.literal("§c[CMI] Sosten un item en la mano para reestructurar su lore."));
                                    return 1;
                                }))
                        .then(ClientCommands.literal("paste")
                                .executes(ctx -> {
                                    Minecraft client = Minecraft.getInstance();
                                    if (client.player == null) return 0;
                                    ItemStack held = client.player.getMainHandItem();
                                    if (held.isEmpty()) {
                                        ctx.getSource().sendFeedback(Component.literal("§c[CMI] Sosten un item en la mano para pegar lore."));
                                        return 0;
                                    }
                                    String clip = client.keyboardHandler.getClipboard();
                                    if (clip == null || clip.trim().isEmpty()) {
                                        ctx.getSource().sendFeedback(Component.literal("§c[CMI] El portapapeles esta vacio."));
                                        return 0;
                                    }
                                    String[] rawLines = clip.split("\r?\n");
                                    List<Component> compLines = new ArrayList<>();
                                    for (String line : rawLines) {
                                        if (!line.trim().isEmpty()) {
                                            compLines.add(UnicodeFontHelper.parseFormattedComponent(line));
                                        }
                                    }
                                    held.set(DataComponents.LORE, new ItemLore(compLines));
                                    int selectedSlot = client.player.getInventory().getSelectedSlot();
                                    if (client.player.isCreative() && client.gameMode != null) {
                                        client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
                                    }
                                    ctx.getSource().sendFeedback(Component.literal("§a[CMI] ✔ Se pegaron §e" + compLines.size() + " lineas §ade lore desde el portapapeles."));
                                    return 1;
                                }))
                        .then(ClientCommands.literal("clear")
                                .executes(ctx -> {
                                    Minecraft client = Minecraft.getInstance();
                                    if (client.player == null) return 0;
                                    ItemStack held = client.player.getMainHandItem();
                                    if (held.isEmpty()) {
                                        ctx.getSource().sendFeedback(Component.literal("§c[CMI] Sosten un item en la mano."));
                                        return 0;
                                    }
                                    held.remove(DataComponents.LORE);
                                    int selectedSlot = client.player.getInventory().getSelectedSlot();
                                    if (client.player.isCreative() && client.gameMode != null) {
                                        client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
                                    }
                                    ctx.getSource().sendFeedback(Component.literal("§e[CMI] 🗑 Lore eliminado del item en mano."));
                                    return 1;
                                })))
                .then(ClientCommands.literal("autolore")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiAutoLoreScreen(null)));
                            return 1;
                        })
                        .then(ClientCommands.literal("list")
                                .executes(ctx -> {
                                    ctx.getSource().sendFeedback(Component.literal("§6§l=== Presets de Auto-Lore Registrados ==="));
                                    for (var p : AutoLoreRegistry.getAllPresets()) {
                                        ctx.getSource().sendFeedback(Component.literal("§e• " + p.id + " §7- §f" + p.displayName + " §8[tag: " + p.enchantTag + "]"));
                                    }
                                    return 1;
                                }))
                        .then(ClientCommands.literal("set")
                                .then(ClientCommands.argument("preset_id", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String id = StringArgumentType.getString(ctx, "preset_id");
                                            var p = AutoLoreRegistry.getPreset(id);
                                            if (p == null) {
                                                ctx.getSource().sendFeedback(Component.literal("§c[CMI] No existe ningun preset con el id: " + id));
                                                return 0;
                                            }
                                            AutoLoreRegistry.setActivePreset(id);
                                            var client = Minecraft.getInstance();
                                            if (client.player != null) {
                                                var held = client.player.getMainHandItem();
                                                if (!held.isEmpty()) {
                                                    ArgNetLoreManager.restructureItemStack(held);
                                                    int selectedSlot = client.player.getInventory().getSelectedSlot();
                                                    if (client.player.isCreative() && client.gameMode != null) {
                                                        client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
                                                    }
                                                }
                                            }
                                            ctx.getSource().sendFeedback(Component.literal("§a[CMI] 🎨 Preset de Auto-Lore establecido a: §e" + p.displayName));
                                            return 1;
                                        }))));

        dispatcher.register(cmiRoot);

        // 2. Alias rapido: /ie (ItemEditor)
        dispatcher.register(ClientCommands.literal("ie")
                .executes(ctx -> {
                    Minecraft mc = Minecraft.getInstance();
                    mc.execute(() -> openEditorWithContainer(mc));
                    return 1;
                })
                .then(ClientCommands.literal("menu")
                        .executes(ctx -> {
                            Minecraft mc = Minecraft.getInstance();
                            mc.execute(() -> openEditorWithContainer(mc));
                            return 1;
                        }))
                .then(ClientCommands.literal("editor")
                        .executes(ctx -> {
                            Minecraft mc = Minecraft.getInstance();
                            mc.execute(() -> openEditorWithContainer(mc));
                            return 1;
                        })));

        // 2.1 Alias rapido: /fixlore
        dispatcher.register(ClientCommands.literal("fixlore")
                .executes(ctx -> {
                    var client = Minecraft.getInstance();
                    if (client.player != null) {
                        var held = client.player.getMainHandItem();
                        if (!held.isEmpty()) {
                            ArgNetLoreManager.restructureItemStack(held);
                            int selectedSlot = client.player.getInventory().getSelectedSlot();
                            if (client.player.isCreative() && client.gameMode != null) {
                                client.gameMode.handleCreativeModeItemAdd(held, 36 + selectedSlot);
                            }
                            ctx.getSource().sendFeedback(Component.literal("§a[CMI] 🇦🇷 ¡Lore reestructurado al formato oficial de Argentina Network!"));
                            return 1;
                        }
                    }
                    ctx.getSource().sendFeedback(Component.literal("§c[CMI] Sosten un item en la mano para reestructurar su lore."));
                    return 1;
                }));

        // 2.2 Alias rapido: /autolore
        dispatcher.register(ClientCommands.literal("autolore")
                .executes(ctx -> {
                    Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiAutoLoreScreen(null)));
                    return 1;
                }));

        // 3. Alias rapido: /kit
        dispatcher.register(ClientCommands.literal("kit")
                .executes(ctx -> {
                    Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiKitsScreen(null)));
                    return 1;
                })
                .then(ClientCommands.literal("menu")
                        .executes(ctx -> {
                            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiKitsScreen(null)));
                            return 1;
                        }))
                .then(ClientCommands.literal("create")
                        .then(ClientCommands.argument("nombre", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "nombre");
                                    Minecraft mc = Minecraft.getInstance();
                                    if (mc.player != null) {
                                        List<ItemStack> inv = new ArrayList<>();
                                        for (int i = 0; i < 36; i++) {
                                            inv.add(mc.player.getInventory().getItem(i).copy());
                                        }
                                        CmiKitManager.saveFullKit(name, inv);
                                        ctx.getSource().sendFeedback(Component.literal("§a[CMI] 🎒 Kit §e" + name + " §aguardado con tu inventario actual!"));
                                        return 1;
                                    }
                                    return 0;
                                })))
                .then(ClientCommands.literal("save")
                        .then(ClientCommands.argument("nombre", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "nombre");
                                    Minecraft mc = Minecraft.getInstance();
                                    if (mc.player != null) {
                                        List<ItemStack> inv = new ArrayList<>();
                                        for (int i = 0; i < 36; i++) {
                                            inv.add(mc.player.getInventory().getItem(i).copy());
                                        }
                                        CmiKitManager.saveFullKit(name, inv);
                                        ctx.getSource().sendFeedback(Component.literal("§a[CMI] 🎒 Kit §e" + name + " §aguardado con tu inventario actual!"));
                                        return 1;
                                    }
                                    return 0;
                                })))
                .then(ClientCommands.literal("delete")
                        .then(ClientCommands.argument("nombre", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "nombre");
                                    CmiKitManager.deleteKit(name);
                                    ctx.getSource().sendFeedback(Component.literal("§c[CMI] 🗑 Kit §e" + name + " §celiminado."));
                                    return 1;
                                })))
                .then(ClientCommands.literal("equip")
                        .then(ClientCommands.argument("nombre", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "nombre");
                                    CmiKitManager.equipKitToInventory(Minecraft.getInstance(), name);
                                    return 1;
                                })))
                .then(ClientCommands.literal("packshulker")
                        .executes(ctx -> {
                            CmiKitManager.giveKitShulkerBox(Minecraft.getInstance(), CmiKitManager.getActiveKitName());
                            return 1;
                        })
                        .then(ClientCommands.argument("nombre", StringArgumentType.word())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "nombre");
                                    CmiKitManager.giveKitShulkerBox(Minecraft.getInstance(), name);
                                    return 1;
                                }))));

        // 5. Comando directo /shulker
        dispatcher.register(ClientCommands.literal("shulker")
                .executes(ctx -> {
                    Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new CmiShulkerPackerScreen(null)));
                    return 1;
                }));
    }
}
