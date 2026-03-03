package com.orbital.orbitalbackpack.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;

public class ItemData {

    // ===== INTERNAL =====

    private static @Nullable CompoundTag getRaw(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null ? data.copyTag() : null;
    }

    private static CompoundTag getOrCreateRaw(ItemStack stack) {
        CompoundTag tag = getRaw(stack);
        return tag != null ? tag : new CompoundTag();
    }

    private static void save(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    // ===== PUBLIC API =====

    public static boolean has(ItemStack stack, String key) {
        CompoundTag tag = getRaw(stack);
        return tag != null && tag.contains(key);
    }

    public static CompoundTag getCompound(ItemStack stack, String key) {
        CompoundTag tag = getRaw(stack);
        return tag != null ? tag.getCompound(key) : new CompoundTag();
    }

    public static void setCompound(ItemStack stack, String key, CompoundTag value) {
        CompoundTag tag = getOrCreateRaw(stack);
        tag.put(key, value);
        save(stack, tag);
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        CompoundTag tag = getRaw(stack);
        return tag != null && tag.getBoolean(key);
    }

    public static void setBoolean(ItemStack stack, String key, boolean value) {
        CompoundTag tag = getOrCreateRaw(stack);
        tag.putBoolean(key, value);
        save(stack, tag);
    }

    public static int getInt(ItemStack stack, String key) {
        CompoundTag tag = getRaw(stack);
        return tag != null ? tag.getInt(key) : 0;
    }

    public static void setInt(ItemStack stack, String key, int value) {
        CompoundTag tag = getOrCreateRaw(stack);
        tag.putInt(key, value);
        save(stack, tag);
    }

    public static void remove(ItemStack stack, String key) {
        CompoundTag tag = getRaw(stack);
        if (tag != null) {
            tag.remove(key);
            save(stack, tag);
        }
    }

    public static void edit(ItemStack stack, java.util.function.Consumer<CompoundTag> editor) {
        CompoundTag tag = getOrCreateRaw(stack);
        editor.accept(tag);
        save(stack, tag);
    }
    public static void set(ItemStack stack, String key, CompoundTag value) {
        edit(stack, tag -> tag.put(key, value));
    }
}