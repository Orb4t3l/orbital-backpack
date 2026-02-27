package com.orbital.orbitalbackpack.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemSortHelper {

    private enum SortCategory {
        WEAPON,
        TOOL,
        ARMOR,
        GEM,
        ORE,
        INGOT,
        FOOD,
        BLOCK,
        MISC
    }

    public static void sortAndPull(ItemStackHandler handler, Player player) {
        pullMatchingFromPlayer(handler, player);
        sortInternal(handler);
    }

    public static void sortOnly(ItemStackHandler handler) {
        sortInternal(handler);
    }

    private static void pullMatchingFromPlayer(ItemStackHandler handler, Player player) {
        for (int playerSlot = 0; playerSlot < player.getInventory().getContainerSize(); playerSlot++) {
            ItemStack playerStack = player.getInventory().getItem(playerSlot);
            if (playerStack.isEmpty()) continue;

            for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
                ItemStack backpackStack = handler.getStackInSlot(backpackSlot);
                if (backpackStack.isEmpty()) continue;

                if (ItemStack.isSameItemSameTags(playerStack, backpackStack)) {
                    int space = backpackStack.getMaxStackSize() - backpackStack.getCount();
                    if (space <= 0) continue;

                    int toMove = Math.min(space, playerStack.getCount());
                    backpackStack.grow(toMove);
                    playerStack.shrink(toMove);

                    if (playerStack.isEmpty()) {
                        player.getInventory().setItem(playerSlot, ItemStack.EMPTY);
                        break;
                    }
                }
            }
        }
    }

    private static void sortInternal(ItemStackHandler handler) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i).copy();
            if (!stack.isEmpty()) items.add(stack);
        }

        items = mergeStacks(items);

        items.sort(Comparator
                .comparingInt((ItemStack s) -> getCategory(s).ordinal())
                .thenComparing(s -> s.getItem().getClass().getSimpleName())
                .thenComparing(s -> s.getHoverName().getString()));

        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
    }

    private static List<ItemStack> mergeStacks(List<ItemStack> items) {
        List<ItemStack> merged = new ArrayList<>();

        for (ItemStack incoming : items) {
            ItemStack remaining = incoming.copy();
            for (ItemStack existing : merged) {
                if (!ItemStack.isSameItemSameTags(existing, remaining)) continue;
                int space = existing.getMaxStackSize() - existing.getCount();
                int toAdd = Math.min(space, remaining.getCount());
                existing.grow(toAdd);
                remaining.shrink(toAdd);
                if (remaining.isEmpty()) break;
            }
            if (!remaining.isEmpty()) merged.add(remaining);
        }

        return merged;
    }

    private static SortCategory getCategory(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof SwordItem || item instanceof BowItem
                || item instanceof CrossbowItem || item instanceof TridentItem) {
            return SortCategory.WEAPON;
        }

        if (item instanceof PickaxeItem || item instanceof AxeItem || item instanceof ShovelItem
                || item instanceof HoeItem || item instanceof ShearsItem || item instanceof FlintAndSteelItem) {
            return SortCategory.TOOL;
        }

        if (item instanceof ArmorItem) {
            return SortCategory.ARMOR;
        }

        String id = item.builtInRegistryHolder().key().location().getPath();

        if (id.contains("diamond") || id.contains("emerald") || id.contains("amethyst")
                || id.contains("quartz") || id.contains("pearl") || id.contains("crystal")) {
            return SortCategory.GEM;
        }

        if (id.contains("_ore") || id.contains("raw_")) {
            return SortCategory.ORE;
        }

        if (id.contains("_ingot") || id.contains("_nugget") || id.contains("_scrap") || id.contains("_shard")) {
            return SortCategory.INGOT;
        }

        if (item instanceof BlockItem) {
            return SortCategory.BLOCK;
        }

        if (item.isEdible()) {
            return SortCategory.FOOD;
        }

        return SortCategory.MISC;
    }
}