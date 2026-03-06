package com.orbital.orbitalbackpack.crafting;

import com.mojang.serialization.MapCodec;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.items.MagnetUpgrade;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BackpackUpgradeRecipe extends CustomRecipe {

    public BackpackUpgradeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasBackpack = false;
        boolean hasMagnetUpgrade = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof Backpack) {
                if (ItemData.getBoolean(stack, "magnet_unlocked")) return false;
                hasBackpack = true;
            } else if (stack.getItem() instanceof MagnetUpgrade) {
                hasMagnetUpgrade = true;
            } else {
                return false;
            }
        }
        return hasBackpack && hasMagnetUpgrade;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider access) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof Backpack) {
                ItemStack result = stack.copy();
                ItemData.setBoolean(result, "magnet_unlocked", true);
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BACKPACK_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackUpgradeRecipe> {

        public static final MapCodec<BackpackUpgradeRecipe> CODEC =
                MapCodec.unit(() -> new BackpackUpgradeRecipe(CraftingBookCategory.MISC));

        public static final StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public BackpackUpgradeRecipe decode(RegistryFriendlyByteBuf buf) {
                        return new BackpackUpgradeRecipe(CraftingBookCategory.MISC);
                    }
                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, BackpackUpgradeRecipe value) {}
                };

        @Override
        public MapCodec<BackpackUpgradeRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> streamCodec() { return STREAM_CODEC; }
    }
}