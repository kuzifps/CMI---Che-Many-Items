package com.fxfps.cmi.client.gui;

import com.fxfps.cmi.CmiMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Gestor resiliente de Hot-Reload para UI de CheManyItems (CMI) 🇦🇷.
 *
 * Características principales:
 * - Carga y guarda coordenadas (x, y), dimensiones (width, height), variantes y márgenes.
 * - Archivo de configuración central: config/cmi/ui_layout.json.
 * - Background File Watcher en hilo demonio ("CMI-UI-Watcher-Daemon") con debounce de 300 ms.
 * - Prevención de bucles infinitos con flag isInternalSave para atajos in-game.
 * - Despacho seguro al hilo principal de Minecraft sin crasheos por sintaxis.
 */
public class CmiUiLayoutManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path configDir;
    private static Path layoutFilePath;
    private static File layoutFile;

    private static LayoutData layoutData = new LayoutData();
    private static final AtomicBoolean isInternalSave = new AtomicBoolean(false);
    private static Thread watcherThread;
    private static boolean initialized = false;

    public static class ComponentLayout {
        public int x;
        public int y;
        public int width;
        public int height;
        public String variant = "DEFAULT";
        public Integer marginTop;
        public Integer marginBottom;
        public Integer marginLeft;
        public Integer marginRight;

        public ComponentLayout() {}

        public ComponentLayout(int x, int y, int width, int height, String variant) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.variant = (variant != null && !variant.isEmpty()) ? variant : "DEFAULT";
        }
    }

    public static class LayoutData {
        public String _description = "CheManyItems (CMI) - Hot-Reload UI Layout Configuration 🇦🇷";
        public int version = 1;
        public Map<String, Map<String, ComponentLayout>> screens = new LinkedHashMap<>();
    }

    public static synchronized void init() {
        if (initialized) return;
        initialized = true;

        try {
            configDir = FabricLoader.getInstance().getConfigDir().resolve("cmi");
            Files.createDirectories(configDir);
            layoutFilePath = configDir.resolve("ui_layout.json");
            layoutFile = layoutFilePath.toFile();

            load();
            startFileWatcher(configDir);
            CmiMod.LOGGER.info("[CMI-LiveUI] Sistema de Hot-Reload e Inspector inicializado correctamente.");
        } catch (Exception e) {
            CmiMod.LOGGER.error("[CMI-LiveUI] Error al inicializar CmiUiLayoutManager", e);
        }
    }

    public static synchronized void load() {
        if (layoutFile == null || !layoutFile.exists()) {
            CmiMod.LOGGER.info("[CMI-LiveUI] ui_layout.json no existe. Creando configuración por defecto...");
            populateDefaults();
            saveToFile();
            return;
        }

        try (FileReader reader = new FileReader(layoutFile)) {
            LayoutData loaded = GSON.fromJson(reader, LayoutData.class);
            if (loaded != null && loaded.screens != null) {
                layoutData = loaded;
                CmiMod.LOGGER.info("[CMI-LiveUI] ui_layout.json cargado exitosamente. (" + layoutData.screens.size() + " pantallas)");
            } else {
                CmiMod.LOGGER.warn("[CMI-LiveUI] ui_layout.json estaba vacío o corrupto. Preservando estado en memoria.");
            }
        } catch (Exception e) {
            CmiMod.LOGGER.error("[CMI-LiveUI] Error de sintaxis en ui_layout.json. Se preserva el layout actual sin crashear.", e);
            notifyErrorInGame("§c[CMI LiveUI] Error de sintaxis en ui_layout.json. Revisa la consola o el archivo.");
        }
    }

    public static synchronized void saveToFile() {
        if (layoutFile == null) return;

        isInternalSave.set(true);
        try (FileWriter writer = new FileWriter(layoutFile)) {
            GSON.toJson(layoutData, writer);
            CmiMod.LOGGER.info("[CMI-LiveUI] ui_layout.json guardado en disco.");
        } catch (IOException e) {
            CmiMod.LOGGER.error("[CMI-LiveUI] Error al guardar ui_layout.json", e);
            notifyErrorInGame("§c[CMI LiveUI] Error al guardar ui_layout.json.");
        }
    }

    public static synchronized ComponentLayout getComponent(String screenName, String componentId) {
        if (layoutData.screens == null) {
            layoutData.screens = new LinkedHashMap<>();
        }
        Map<String, ComponentLayout> screenComponents = layoutData.screens.get(screenName);
        if (screenComponents != null) {
            return screenComponents.get(componentId);
        }
        return null;
    }

    public static synchronized void registerDefaultComponent(String screenName, String componentId, int x, int y, int width, int height, String variant) {
        if (layoutData.screens == null) {
            layoutData.screens = new LinkedHashMap<>();
        }
        Map<String, ComponentLayout> screenMap = layoutData.screens.computeIfAbsent(screenName, k -> new LinkedHashMap<>());
        if (!screenMap.containsKey(componentId)) {
            screenMap.put(componentId, new ComponentLayout(x, y, width, height, variant));
        }
    }

    public static synchronized void updateComponentLayout(String screenName, String componentId, int x, int y, int width, int height, String variant) {
        if (layoutData.screens == null) {
            layoutData.screens = new LinkedHashMap<>();
        }
        Map<String, ComponentLayout> screenMap = layoutData.screens.computeIfAbsent(screenName, k -> new LinkedHashMap<>());
        ComponentLayout cl = screenMap.get(componentId);
        if (cl == null) {
            cl = new ComponentLayout(x, y, width, height, variant);
            screenMap.put(componentId, cl);
        } else {
            cl.x = x;
            cl.y = y;
            cl.width = width;
            cl.height = height;
            if (variant != null && !variant.isEmpty()) {
                cl.variant = variant;
            }
        }
    }

    public static synchronized Map<String, ComponentLayout> getScreenComponents(String screenName) {
        if (layoutData.screens != null && layoutData.screens.containsKey(screenName)) {
            return layoutData.screens.get(screenName);
        }
        return Collections.emptyMap();
    }

    private static void populateDefaults() {
        layoutData = new LayoutData();
        layoutData.screens = new LinkedHashMap<>();

        // Defaults para CmiTestScreen
        Map<String, ComponentLayout> testScreen = new LinkedHashMap<>();
        testScreen.put("tab_display", new ComponentLayout(230, 6, 65, 18, "TAB"));
        testScreen.put("tab_enchants", new ComponentLayout(298, 6, 78, 18, "TAB"));
        testScreen.put("tab_attributes", new ComponentLayout(379, 6, 78, 18, "TAB"));
        testScreen.put("tab_kits", new ComponentLayout(460, 6, 60, 18, "TAB"));
        testScreen.put("btn_close", new ComponentLayout(532, 6, 20, 18, "DANGER"));
        testScreen.put("btn_hero_apply", new ComponentLayout(180, 44, 160, 20, "HERO_EMERALD"));
        testScreen.put("btn_action_load", new ComponentLayout(180, 70, 160, 20, "ACTION_AMBER"));
        testScreen.put("btn_danger_clear", new ComponentLayout(180, 96, 160, 20, "DANGER"));
        layoutData.screens.put("CmiTestScreen", testScreen);

        // Defaults para CmiEditorScreen
        Map<String, ComponentLayout> editorScreen = new LinkedHashMap<>();
        editorScreen.put("undo_btn", new ComponentLayout(180, 5, 78, 20, "TAB"));
        editorScreen.put("redo_btn", new ComponentLayout(262, 5, 74, 20, "TAB"));
        editorScreen.put("tab_display", new ComponentLayout(344, 5, 58, 20, "TAB"));
        editorScreen.put("tab_enchants", new ComponentLayout(404, 5, 78, 20, "TAB"));
        editorScreen.put("btn_apply", new ComponentLayout(8, 250, 180, 22, "HERO_EMERALD"));
        layoutData.screens.put("CmiEditorScreen", editorScreen);
    }

    private static void startFileWatcher(Path dir) {
        if (watcherThread != null && watcherThread.isAlive()) return;

        watcherThread = new Thread(() -> {
            try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
                dir.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
                long lastReloadTime = 0;

                while (!Thread.currentThread().isInterrupted()) {
                    WatchKey key;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException e) {
                        break;
                    }

                    boolean changed = false;
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path changedPath = (Path) event.context();
                        if (changedPath != null && changedPath.toString().endsWith("ui_layout.json")) {
                            changed = true;
                            break;
                        }
                    }

                    if (changed) {
                        long now = System.currentTimeMillis();
                        if (now - lastReloadTime > 300) {
                            lastReloadTime = now;

                            if (isInternalSave.compareAndSet(true, false)) {
                                key.reset();
                                continue;
                            }

                            Minecraft client = Minecraft.getInstance();
                            if (client != null) {
                                client.execute(() -> {
                                    load();
                                    if (client.screen instanceof CmiScreen cmiScreen) {
                                        cmiScreen.rebuildScreenPreservingState();
                                    }
                                    if (client.player != null) {
                                        client.player.sendSystemMessage(
                                            Component.literal("§b[CMI LiveUI] §a⚡ Layout recargado en caliente (Hot-Reload) exitosamente.")
                                        );
                                    }
                                });
                            }
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) break;
                }
            } catch (Exception e) {
                CmiMod.LOGGER.error("[CMI-LiveUI] Error en el daemon de vigilancia de layout", e);
            }
        }, "CMI-UI-Watcher-Daemon");

        watcherThread.setDaemon(true);
        watcherThread.start();
    }

    private static void notifyErrorInGame(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client != null && client.player != null) {
            client.execute(() -> client.player.sendSystemMessage(Component.literal(message)));
        }
    }
}