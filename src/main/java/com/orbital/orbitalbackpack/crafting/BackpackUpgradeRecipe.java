package com.orbital.orbitalbackpack.crafting;

import com.google.gson.JsonObject;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.items.MagnetUpgrade;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BackpackUpgradeRecipe extends CustomRecipe {

    public BackpackUpgradeRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
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
        ItemStack backpack = ItemStack.EMPTY;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof Backpack) {
                backpack = stack.copy();
                break;
            }
        }

        if (backpack.isEmpty()) return ItemStack.EMPTY;

        backpack.getOrCreateTag().putBoolean("magnet_unlocked", true);
        return backpack;
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

        @Override
        public BackpackUpgradeRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new BackpackUpgradeRecipe(id, CraftingBookCategory.MISC);
        }

        @Override
        public BackpackUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new BackpackUpgradeRecipe(id, CraftingBookCategory.MISC);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BackpackUpgradeRecipe recipe) {}
    }
}