package com.fxfps.cmi.client.lores;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo de datos para un perfil / preset de Auto-Lore en CMI.
 * Permite personalizar tags animados, títulos de sección, cabeceras, pies e iconos.
 */
public class AutoLorePreset {
    public String id;
    public String displayName;
    public String header = ""; // Vacío = Sin cabecera (Limpio)
    public String enchantHeader = "&7&lENCANTAMIENTOS"; // Vacío = Sin cabecera de encantamientos (<gold><enchant> <lvl>)
    public String enchantTag = "gold"; // gold, fire, rainbow, celestial, void, etc.
    public String customEnchantHeader = "&7🍁 CUSTOM ENCHANTS";
    public String customEnchantTag = "frost"; // frost, etc.
    public String attrHeader = "&7&lATRIBUTOS";
    public String attrTag = "silver"; // silver, gold, lava, etc.
    public List<String> footer = new ArrayList<>(); // Vacío = Sin pie
    public boolean useIcons = false;
    public boolean bold = false;
    public boolean uppercase = false;
    public boolean smallCaps = true; // Sin SmallCaps para respetar <gold><enchant> <lvl>
    public boolean custom = false; // true si fue creado por el usuario

    public AutoLorePreset() {}

    public AutoLorePreset(String id, String displayName, String header,
                          String enchantHeader, String enchantTag,
                          String attrHeader, String attrTag,
                          List<String> footer, boolean useIcons, boolean bold, boolean uppercase, boolean custom) {
        this(id, displayName, header, enchantHeader, enchantTag, "&7🍁 CUSTOM ENCHANTS", "frost", attrHeader, attrTag, footer, useIcons, bold, uppercase, false, custom);
    }

    public AutoLorePreset(String id, String displayName, String header,
                          String enchantHeader, String enchantTag,
                          String attrHeader, String attrTag,
                          List<String> footer, boolean useIcons, boolean bold, boolean uppercase, boolean smallCaps, boolean custom) {
        this(id, displayName, header, enchantHeader, enchantTag, "&7🍁 CUSTOM ENCHANTS", "frost", attrHeader, attrTag, footer, useIcons, bold, uppercase, smallCaps, custom);
    }

    public AutoLorePreset(String id, String displayName, String header,
                          String enchantHeader, String enchantTag,
                          String customEnchantHeader, String customEnchantTag,
                          String attrHeader, String attrTag,
                          List<String> footer, boolean useIcons, boolean bold, boolean uppercase, boolean smallCaps, boolean custom) {
        this.id = id;
        this.displayName = displayName;
        this.header = header != null ? header : "";
        this.enchantHeader = enchantHeader != null ? enchantHeader : "";
        this.enchantTag = enchantTag != null ? enchantTag : "gold";
        this.customEnchantHeader = customEnchantHeader != null ? customEnchantHeader : "&7🍁 CUSTOM ENCHANTS";
        this.customEnchantTag = customEnchantTag != null ? customEnchantTag : "frost";
        this.attrHeader = attrHeader != null ? attrHeader : "&7&lATRIBUTOS";
        this.attrTag = attrTag != null ? attrTag : "silver";
        this.footer = footer != null ? new ArrayList<>(footer) : new ArrayList<>();
        this.useIcons = useIcons;
        this.bold = bold;
        this.uppercase = uppercase;
        this.smallCaps = smallCaps;
        this.custom = custom;
    }
}
