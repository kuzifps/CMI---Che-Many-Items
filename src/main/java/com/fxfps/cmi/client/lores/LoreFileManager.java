package com.fxfps.cmi.client.lores;

import com.fxfps.cmi.CmiMod;
import com.fxfps.cmi.client.config.CmiConfigManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class LoreFileManager {

    public static List<String> loadLoreFile(String fileName) {
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }

        Path path = CmiConfigManager.getLoresDir().resolve(fileName);
        if (!Files.exists(path)) {
            return Collections.emptyList();
        }

        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            CmiMod.LOGGER.error("Error al leer archivo de lore: " + fileName, e);
            return Collections.emptyList();
        }
    }

    public static boolean saveLoreFile(String fileName, List<String> lines) {
        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }

        Path path = CmiConfigManager.getLoresDir().resolve(fileName);
        try {
            Files.write(path, lines);
            return true;
        } catch (IOException e) {
            CmiMod.LOGGER.error("Error al guardar archivo de lore: " + fileName, e);
            return false;
        }
    }

    public static File[] listLoreFiles() {
        File dir = CmiConfigManager.getLoresDir().toFile();
        return dir.listFiles((d, name) -> name.endsWith(".txt"));
    }
}
