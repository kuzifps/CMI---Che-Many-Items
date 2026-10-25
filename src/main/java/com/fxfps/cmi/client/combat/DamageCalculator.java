package com.fxfps.cmi.client.combat;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * Calculadora de Daño real para armas con atributos personalizados y encantamientos ilegales (Sharpness 255 / 32k).
 * Permite telemetría en tiempo real de Daño por Golpe, Corazones, Velocidad de Ataque y DPS.
 */
public class DamageCalculator {

    public static class DamageInfo {
        public double baseDamage = 1.0;
        public double extraDamage = 0.0;
        public double totalDamage = 1.0;
        public double hearts = 0.5;
        public double attackSpeed = 4.0;
        public double dps = 4.0;
        public double dpsHearts = 2.0;
        public boolean isOneShot = false;
        public boolean isUltraKill = false;
        public double smiteBonus = 0.0;
        public double baneBonus = 0.0;
        public double fireBonus = 0.0;

        public String getFormattedHearts() {
            if (hearts >= 1000.0) return String.format("%.0f ❤", hearts);
            return String.format("%.1f ❤", hearts);
        }

        public String getFormattedDps() {
            if (dpsHearts >= 1000.0) return String.format("%.0f DPS", dpsHearts);
            return String.format("%.1f DPS", dpsHearts);
        }

        public String getSummaryBadge() {
            if (isUltraKill) {
                return "§4⚡ 32K LETHAL (" + getFormattedHearts() + ")";
            } else if (isOneShot) {
                return "§c⚔ ONE-SHOT (" + getFormattedHearts() + ")";
            } else {
                return "§6⚔ " + getFormattedHearts() + " §7| §e⚡ " + getFormattedDps();
            }
        }
    }

    /**
     * Calcula el daño en corazones aproximado según daño base y nivel de afilado.
     */
    public static double calculateDamage(double baseDamage, int sharpnessLevel) {
        if (sharpnessLevel <= 0) return baseDamage / 2.0;
        double extraDamage = 0.5 * sharpnessLevel + 0.5;
        double totalDamage = baseDamage + extraDamage;
        return totalDamage / 2.0;
    }

    /**
     * Devuelve una cadena formateada para el tooltip
     */
    public static String getDamageTooltip(double baseDamage, int sharpnessLevel) {
        double hearts = calculateDamage(baseDamage, sharpnessLevel);
        if (hearts >= 100.0) {
            return String.format("§8[ ⚔ Daño Real: §c~%.1f ❤ §4(ONE-SHOT) §8]", hearts);
        }
        return String.format("§8[ ⚔ Daño Real: §c~%.1f ❤ §8]", hearts);
    }

    /**
     * Calcula la telemetría completa de daño físico, velocidad y DPS para cualquier ItemStack.
     */
    public static DamageInfo calculate(ItemStack stack) {
        DamageInfo info = new DamageInfo();
        if (stack == null || stack.isEmpty()) return info;

        // 1. Daño y velocidad base según el ítem
        double baseDmg = 1.0;
        double baseSpd = 4.0;

        if (stack.is(Items.NETHERITE_SWORD)) { baseDmg = 8.0; baseSpd = 1.6; }
        else if (stack.is(Items.DIAMOND_SWORD)) { baseDmg = 7.0; baseSpd = 1.6; }
        else if (stack.is(Items.IRON_SWORD)) { baseDmg = 6.0; baseSpd = 1.6; }
        else if (stack.is(Items.STONE_SWORD)) { baseDmg = 5.0; baseSpd = 1.6; }
        else if (stack.is(Items.GOLDEN_SWORD) || stack.is(Items.WOODEN_SWORD)) { baseDmg = 4.0; baseSpd = 1.6; }
        else if (stack.is(Items.NETHERITE_AXE)) { baseDmg = 10.0; baseSpd = 1.0; }
        else if (stack.is(Items.DIAMOND_AXE)) { baseDmg = 9.0; baseSpd = 1.0; }
        else if (stack.is(Items.IRON_AXE)) { baseDmg = 9.0; baseSpd = 0.9; }
        else if (stack.is(Items.STONE_AXE)) { baseDmg = 9.0; baseSpd = 0.8; }
        else if (stack.is(Items.WOODEN_AXE)) { baseDmg = 7.0; baseSpd = 0.8; }
        else if (stack.is(Items.GOLDEN_AXE)) { baseDmg = 7.0; baseSpd = 1.0; }
        else if (stack.is(Items.TRIDENT)) { baseDmg = 9.0; baseSpd = 1.1; }
        else if (stack.is(Items.MACE)) { baseDmg = 6.0; baseSpd = 0.6; }

        // 2. Modificadores de atributos (generic.attack_damage y generic.attack_speed)
        double modDmg = baseDmg;
        double modSpd = baseSpd;
        if (stack.has(DataComponents.ATTRIBUTE_MODIFIERS)) {
            ItemAttributeModifiers mods = stack.get(DataComponents.ATTRIBUTE_MODIFIERS);
            if (mods != null) {
                modDmg = mods.compute(Attributes.ATTACK_DAMAGE, baseDmg, EquipmentSlot.MAINHAND);
                modSpd = mods.compute(Attributes.ATTACK_SPEED, baseSpd, EquipmentSlot.MAINHAND);
            }
        }
        info.baseDamage = modDmg;
        info.attackSpeed = Math.max(0.1, modSpd);

        // 3. Encantamientos de daño
        var enchs = stack.getOrDefault(
                stack.is(Items.ENCHANTED_BOOK) ? DataComponents.STORED_ENCHANTMENTS : DataComponents.ENCHANTMENTS,
                ItemEnchantments.EMPTY
        );

        double extraDmg = 0.0;
        for (var entry : enchs.entrySet()) {
            Holder<Enchantment> holder = entry.getKey();
            int lvl = entry.getIntValue();
            if (lvl <= 0) continue;

            String path = holder.unwrapKey().map(k -> k.identifier().getPath().toLowerCase()).orElse("");
            if (path.contains("sharpness")) {
                extraDmg += (0.5 * lvl + 0.5);
            } else if (path.contains("smite")) {
                info.smiteBonus += (2.5 * lvl);
            } else if (path.contains("bane_of_arthropods")) {
                info.baneBonus += (2.5 * lvl);
            } else if (path.contains("fire_aspect")) {
                info.fireBonus += (lvl * 4.0);
            }
        }

        info.extraDamage = extraDmg;
        info.totalDamage = Math.max(0.0, info.baseDamage + info.extraDamage);
        info.hearts = info.totalDamage / 2.0;
        info.dps = info.totalDamage * info.attackSpeed;
        info.dpsHearts = info.dps / 2.0;
        info.isOneShot = info.hearts >= 10.0;
        info.isUltraKill = info.hearts >= 100.0;

        return info;
    }

    /**
     * Tooltip completo de telemetría de combate para un ItemStack.
     */
    public static String getDamageTooltip(ItemStack stack) {
        DamageInfo info = calculate(stack);
        if (info.isUltraKill) {
            return String.format("§8[ ⚔ Daño Real: §4%.1f ❤ §c(32K ONE-SHOT) §7| §e%.1f DPS §8]", info.hearts, info.dpsHearts);
        } else if (info.isOneShot) {
            return String.format("§8[ ⚔ Daño Real: §c%.1f ❤ §4(ONE-SHOT) §7| §e%.1f DPS §8]", info.hearts, info.dpsHearts);
        }
        return String.format("§8[ ⚔ Daño Real: §e%.1f ❤ §7| §6%.1f DPS §8]", info.hearts, info.dpsHearts);
    }

    // =========================================================================
    // SIMULADOR DE COMBATE CONTRA RIVAL BLINDADO (REGLA NÚCLEO 5 · TAB 4)
    // =========================================================================

    public static class RivalSimulation {
        public final double rawDamage;
        public final double afterArmor;
        public final double finalDamage;
        public final double finalHearts;
        public final int hitsToKill;
        public final double effectiveDps;
        public final String impactBadge;
        public final String armorName;

        public RivalSimulation(double rawDamage, double afterArmor, double finalDamage, double finalHearts,
                               int hitsToKill, double effectiveDps, String impactBadge, String armorName) {
            this.rawDamage = rawDamage;
            this.afterArmor = afterArmor;
            this.finalDamage = finalDamage;
            this.finalHearts = finalHearts;
            this.hitsToKill = hitsToKill;
            this.effectiveDps = effectiveDps;
            this.impactBadge = impactBadge;
            this.armorName = armorName;
        }
    }

    /**
     * Simula el daño recibido por un rival según tipo de armadura y nivel de Protección (0 a V).
     * @param stack Ítem atacante
     * @param armorMode 3 = Full Netherite, 2 = Full Diamante, 1 = Full Hierro, 0 = Sin Armadura
     * @param protLevel Nivel de Protección en las 4 piezas (0 a 5)
     * @param critical Si el golpe es crítico (1.5x de daño)
     */
    public static RivalSimulation simulateCombatAgainstRival(ItemStack stack, int armorMode, int protLevel, boolean critical) {
        DamageInfo dInfo = calculate(stack);
        double rawDmg = critical ? (dInfo.totalDamage * 1.5) : dInfo.totalDamage;

        double armorPoints = switch (armorMode) {
            case 3 -> 20.0; // Netherite
            case 2 -> 20.0; // Diamante
            case 1 -> 15.0; // Hierro
            default -> 0.0;
        };
        double toughness = (armorMode == 3) ? 12.0 : ((armorMode == 2) ? 8.0 : 0.0);
        String armorName = switch (armorMode) {
            case 3 -> "Full Netherite";
            case 2 -> "Full Diamante";
            case 1 -> "Full Hierro";
            default -> "Sin Armadura";
        };

        // Reducción por armadura física (fórmula oficial Minecraft)
        double defense = Math.max(armorPoints / 5.0, armorPoints - rawDmg / (2.0 + toughness / 4.0));
        double afterArmor = rawDmg * (1.0 - Math.min(20.0, defense) / 25.0);

        // Reducción por encantamiento de Protección (EPF: 4% por punto hasta 80% máximo)
        int clampedProt = Math.max(0, Math.min(5, protLevel));
        double epf = clampedProt * 4.0; // 4 piezas equipadas
        double protReduction = Math.min(20.0, epf) * 0.04;
        double finalDmg = Math.max(0.1, afterArmor * (1.0 - protReduction));
        double finalHearts = finalDmg / 2.0;

        int hitsToKill = (finalDmg <= 0) ? Integer.MAX_VALUE : (int) Math.ceil(20.0 / finalDmg);
        double effectiveDps = finalHearts * dInfo.attackSpeed;

        String impactBadge;
        if (hitsToKill == 1) {
            impactBadge = "§4⚡ 32K ONE-SHOT LETHAL";
        } else if (hitsToKill <= 3) {
            impactBadge = "§c⚔ ALTA LETALIDAD (" + hitsToKill + " HITS)";
        } else if (hitsToKill <= 7) {
            impactBadge = "§e⚔ COMBATE BALANCEADO (" + hitsToKill + " HITS)";
        } else {
            impactBadge = "§b🛡 DEFENSA ALTA (" + hitsToKill + " HITS)";
        }

        return new RivalSimulation(rawDmg, afterArmor, finalDmg, finalHearts, hitsToKill, effectiveDps, impactBadge, armorName);
    }
}
