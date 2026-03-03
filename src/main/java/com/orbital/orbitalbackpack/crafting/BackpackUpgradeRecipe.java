package com.orbital.orbitalbackpack.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.items.MagnetUpgrade;
import com.orbital.orbitalbackpack.util.ItemData;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Logic for the "combine a backpack + magnet upgrade item => same backpack with magnet_unlocked = true"
 *
 * This class focuses on the runtime behaviour (matches + assemble) and uses ItemData for NBT access
 * so it is compatible with Forge 1.20.6's data-component model.
 *
 * Serializer wiring is intentionally left out here so you can pick the serializer approach you prefer
 * (see options below).
 */
public class BackpackUpgradeRecipe extends CustomRecipe {

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
                // use ItemData helper to check magnet flag safely
                if (ItemData.getBoolean(stack, "magnet_unlocked")) return false;
                hasBackpack = true;
            } else if (stack.getItem() instanceof MagnetUpgrade) {
                hasMagnetUpgrade = true;
            } else {
                return false; // unknown extra ingredient → not a match
            }
        }
        return hasBackpack && hasMagnetUpgrade;
    }

    /**
     * assemble signature changed in modern API — implement the Provider variant.
     * We copy the backpack ItemStack and set the magnet_unlocked flag via ItemData.
     */
    @Override
    public ItemStack assemble(CraftingContainer container, HolderLookup.Provider access) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof Backpack) {
                ItemStack result = stack.copy();
                // write magnet_unlocked = true via ItemData helper
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

        private static final MapCodec<BackpackUpgradeRecipe> CODEC =
                MapCodec.unit(new BackpackUpgradeRecipe(CraftingBookCategory.MISC));

        @Override
        public MapCodec<BackpackUpgradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BackpackUpgradeRecipe> streamCodec() {
            return StreamCodec.unit(new BackpackUpgradeRecipe(CraftingBookCategory.MISC));
        }
    }

}