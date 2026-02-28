package com.orbital.orbitalbackpack.common;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemSortHelper {

    public static void pullMatchingFromPlayer(ItemStackHandler handler, Player player) {
        for (int playerSlot = 0; playerSlot < player.getInventory().getContainerSize(); playerSlot++) {
            ItemStack playerStack = player.getInventory().getItem(playerSlot);
            if (playerStack.isEmpty()) continue;

            Item playerItem = playerStack.getItem();
            if (!backpackContainsItem(handler, playerItem)) continue;

            for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
                ItemStack backpackStack = handler.getStackInSlot(backpackSlot);
                if (backpackStack.isEmpty()) continue;
                if (backpackStack.getItem() != playerItem) continue;

                int space = backpackStack.getMaxStackSize() - backpackStack.getCount();
                if (space <= 0) continue;

                int toMove = Math.min(space, playerStack.getCount());
                ItemStack updatedBackpack = backpackStack.copy();
                updatedBackpack.setCount(backpackStack.getCount() + toMove);
                handler.setStackInSlot(backpackSlot, updatedBackpack);

                playerStack.shrink(toMove);
                player.getInventory().setItem(playerSlot, playerStack.isEmpty() ? ItemStack.EMPTY : playerStack);
                if (playerStack.isEmpty()) break;
            }

            playerStack = player.getInventory().getItem(playerSlot);
            if (playerStack.isEmpty()) continue;

            for (int backpackSlot = 0; backpackSlot < handler.getSlots(); backpackSlot++) {
                if (!handler.getStackInSlot(backpackSlot).isEmpty()) continue;

                int toMove = Math.min(playerStack.getMaxStackSize(), playerStack.getCount());
                ItemStack newStack = playerStack.copy();
                newStack.setCount(toMove);
                handler.setStackInSlot(backpackSlot, newStack);

                playerStack.shrink(toMove);
                player.getInventory().setItem(playerSlot, playerStack.isEmpty() ? ItemStack.EMPTY : playerStack);
                if (playerStack.isEmpty()) break;
            }
        }
    }

    public static void sortInternal(ItemStackHandler handler) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i).copy();
            if (!stack.isEmpty()) items.add(stack);
        }

        items = mergeStacks(items);
        items.sort(Comparator
                .comparingInt(ItemSortHelper::getCategoryIndex)
                .thenComparing(stack -> {
                    var key = ForgeRegistries.ITEMS.getKey(stack.getItem());
                    return key != null ? key.getPath() : "";
                }));

        for (int i = 0; i < handler.getSlots(); i++) {
            handler.setStackInSlot(i, i < items.size() ? items.get(i) : ItemStack.EMPTY);
        }
    }

    private static boolean backpackContainsItem(ItemStackHandler handler, Item item) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).getItem() == item) return true;
        }
        return false;
    }

    private static List<ItemStack> mergeStacks(List<ItemStack> stacks) {
        List<ItemStack> merged = new ArrayList<>();

        for (ItemStack incoming : stacks) {
            ItemStack remaining = incoming.copy();
            for (ItemStack existing : merged) {
                if (existing.getItem() != remaining.getItem()) continue;
                int space = existing.getMaxStackSize() - existing.getCount();
                if (space <= 0) continue;
                int toAdd = Math.min(space, remaining.getCount());
                existing.grow(toAdd);
                remaining.shrink(toAdd);
                if (remaining.isEmpty()) break;
            }
            if (!remaining.isEmpty()) merged.add(remaining);
        }

        return merged;
    }

    private static int getCategoryIndex(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof SwordItem || item instanceof BowItem
                || item instanceof CrossbowItem || item instanceof TridentItem) return 0;
        if (item instanceof PickaxeItem || item instanceof AxeItem || item instanceof ShovelItem
                || item instanceof HoeItem || item instanceof ShearsItem
                || item instanceof FlintAndSteelItem) return 1;
        if (item instanceof ArmorItem) return 2;

        var key = ForgeRegistries.ITEMS.getKey(item);
        String id = key != null ? key.getPath() : "";

        if (id.contains("_ingot") || id.contains("_nugget") || id.contains("netherite_scrap")) return 3;
        if (id.contains("diamond") || id.contains("emerald")
                || id.contains("amethyst") || id.contains("quartz")) return 4;
        if (id.contains("_ore") || id.startsWith("raw_")) return 5;
        if (item instanceof BlockItem) return 6;
        if (item.isEdible()) return 7;

        return 8;
    }
}