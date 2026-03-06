package com.orbital.orbitalbackpack.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ItemValueHelper {

    private static final Map<String, Integer> KNOWN_VALUES = Map.ofEntries(
            Map.entry("minecraft:netherite_ingot", 1000),
            Map.entry("minecraft:netherite_scrap", 800),
            Map.entry("minecraft:ancient_debris", 900),
            Map.entry("minecraft:diamond", 500),
            Map.entry("minecraft:diamond_block", 500),
            Map.entry("minecraft:emerald", 400),
            Map.entry("minecraft:emerald_block", 400),
            Map.entry("minecraft:amethyst_shard", 200),
            Map.entry("minecraft:gold_ingot", 150),
            Map.entry("minecraft:gold_block", 150),
            Map.entry("minecraft:gold_nugget", 100),
            Map.entry("minecraft:iron_ingot", 100),
            Map.entry("minecraft:iron_block", 100),
            Map.entry("minecraft:iron_nugget", 60),
            Map.entry("minecraft:redstone", 80),
            Map.entry("minecraft:redstone_block", 80),
            Map.entry("minecraft:lapis_lazuli", 70),
            Map.entry("minecraft:lapis_block", 70),
            Map.entry("minecraft:coal", 30),
            Map.entry("minecraft:coal_block", 30),
            Map.entry("minecraft:copper_ingot", 40),
            Map.entry("minecraft:raw_iron", 60),
            Map.entry("minecraft:raw_gold", 100),
            Map.entry("minecraft:raw_copper", 30),
            Map.entry("minecraft:quartz", 50),
            Map.entry("minecraft:ender_pearl", 180),
            Map.entry("minecraft:blaze_rod", 160),
            Map.entry("minecraft:ghast_tear", 220),
            Map.entry("minecraft:nether_star", 950),
            Map.entry("minecraft:dragon_egg", 980),
            Map.entry("minecraft:elytra", 870),
            Map.entry("minecraft:totem_of_undying", 750),
            Map.entry("minecraft:heart_of_the_sea", 700),
            Map.entry("minecraft:trident", 650),
            Map.entry("minecraft:shulker_shell", 300),
            Map.entry("minecraft:beacon", 900)
    );

    public static List<ItemStack> getTopItems(ItemStackHandler handler, int count) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }

        items.sort(Comparator.comparingInt(ItemValueHelper::scoreItem).reversed());
        return items.subList(0, Math.min(count, items.size()));
    }

    private static int scoreItem(ItemStack stack) {
        int score = 0;

        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id != null) {
            score += KNOWN_VALUES.getOrDefault(id.toString(), 0);
        }

        Rarity rarity = stack.getRarity();
        score += switch (rarity) {
            case UNCOMMON -> 20;
            case RARE -> 50;
            case EPIC -> 100;
            default -> 0;
        };

        if (stack.isEnchanted()) {
            var enchants = stack.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS,
                    net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);

            int total = 0;
            for (var entry : enchants.entrySet()) {
                total += entry.getIntValue(); // level
            }

            score += total * 15;
        }

        if (stack.isDamageableItem()) {
            score += 10;
        }

        return score;
    }
}