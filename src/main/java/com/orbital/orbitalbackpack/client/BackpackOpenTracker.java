package com.orbital.orbitalbackpack.client;

import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BackpackOpenTracker {

    private static final Set<UUID> openBackpacks = new HashSet<>();

    public static void setOpen(UUID player) {
        openBackpacks.add(player);
    }

    public static void setClosed(UUID player) {
        openBackpacks.remove(player);
    }

    public static boolean isOpen(UUID player) {
        return openBackpacks.contains(player);
    }
}