package com.orbital.orbitalbackpack.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class ItemData {

    private ItemData() {}

    private static @Nullable CompoundTag readRaw(ItemStack stack) {
        final CompoundTag[] read = new CompoundTag[1];
        // Consumer will be called with the current tag if it exists.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> read[0] = tag.copy());
        return read[0];
    }

    // Get a tag guaranteed to exist (creates + sets if necessary).
    private static CompoundTag getOrCreateRaw(ItemStack stack) {
        CompoundTag existing = readRaw(stack);
        if (existing != null) return existing;

        CompoundTag created = new CompoundTag();
        CustomData.set(DataComponents.CUSTOM_DATA, stack, created);
        return created;
    }

    // and saves changes after editor runs.
    public static void edit(ItemStack stack, Consumer<CompoundTag> editor) {
        // Use update which should apply the editor to the stacks component.
        CustomData.update(DataComponents.CUSTOM_DATA, stack, editor);
    }

    // Shortcut set with a compound value.
    public static void set(ItemStack stack, String key, CompoundTag value) {
        edit(stack, tag -> tag.put(key, value));
    }

    public static boolean has(ItemStack stack, String key) {
        CompoundTag t = readRaw(stack);
        return t != null && t.contains(key);
    }

    public static CompoundTag getCompound(ItemStack stack, String key) {
        CompoundTag t = readRaw(stack);
        return (t != null && t.contains(key)) ? t.getCompound(key) : new CompoundTag();
    }

    public static void setCompound(ItemStack stack, String key, CompoundTag value) {
        edit(stack, tag -> tag.put(key, value));
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        CompoundTag t = readRaw(stack);
        return t != null && t.getBoolean(key);
    }

    public static void setBoolean(ItemStack stack, String key, boolean value) {
        edit(stack, tag -> tag.putBoolean(key, value));
    }

    public static int getInt(ItemStack stack, String key) {
        CompoundTag t = readRaw(stack);
        return t != null ? t.getInt(key) : 0;
    }

    public static void setInt(ItemStack stack, String key, int value) {
        edit(stack, tag -> tag.putInt(key, value));
    }

    public static void remove(ItemStack stack, String key) {
        edit(stack, tag -> tag.remove(key));
    }
    public static @Nullable CompoundTag getRawTagCopy(ItemStack stack) {
        CompoundTag t = readRaw(stack);
        return t != null ? t.copy() : null;
    }

    public static void setRawTag(ItemStack stack, CompoundTag tag) {
        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }
}