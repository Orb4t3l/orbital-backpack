package com.orbital.orbitalbackpack.crafting;

import com.mojang.serialization.Codec;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.items.MagnetUpgrade;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BackpackUpgradeRecipe extends CustomRecipe {

    // Constructor no longer takes ResourceLocation in 1.20.4
    public BackpackUpgradeRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        boolean hasBackpack = false;
        boolean hasMagnetUpgrade = false;
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof Backpack) {
                if (stack.hasTag() && stack.getTag().getBoolean("magnet_unlocked")) return false;
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
    public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof Backpack) {
                ItemStack result = stack.copy();
                result.getOrCreateTag().putBoolean("magnet_unlocked", true);
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

        // 1.20.4: codec() returns Codec<T>, not MapCodec
        @Override
        public Codec<BackpackUpgradeRecipe> codec() {
            return CraftingBookCategory.CODEC
                    .optionalFieldOf("category", CraftingBookCategory.MISC)
                    .xmap(BackpackUpgradeRecipe::new, BackpackUpgradeRecipe::category)
                    .codec();
        }

        // 1.20.4: fromNetwork has no ResourceLocation parameter
        @Override
        public BackpackUpgradeRecipe fromNetwork(FriendlyByteBuf buf) {
            return new BackpackUpgradeRecipe(CraftingBookCategory.MISC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BackpackUpgradeRecipe recipe) {}
    }
}