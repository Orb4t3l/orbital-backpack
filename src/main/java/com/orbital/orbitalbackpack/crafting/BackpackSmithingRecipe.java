package com.orbital.orbitalbackpack.crafting;

import com.mojang.serialization.MapCodec;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class BackpackSmithingRecipe implements SmithingRecipe {

    public BackpackSmithingRecipe() {}

    @Override
    public boolean matches(Container container, Level level) {
        return container.getItem(0).is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                && container.getItem(1).getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get()
                && container.getItem(2).is(Items.NETHERITE_INGOT);
    }

    @Override
    public ItemStack assemble(Container container, HolderLookup.Provider access) {
        ItemStack base = container.getItem(1);
        ItemStack result = new ItemStack(ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get());
        // Copy custom data component if present
        var customData = base.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData != null) result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, customData);
        return result;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider access) {
        return new ItemStack(ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get());
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) { return stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE); }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return stack.getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get();
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) { return stack.is(Items.NETHERITE_INGOT); }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipeSerializers.BACKPACK_SMITHING.get(); }

    public static class Serializer implements RecipeSerializer<BackpackSmithingRecipe> {

        public static final MapCodec<BackpackSmithingRecipe> CODEC =
                MapCodec.unit(BackpackSmithingRecipe::new);

        public static final StreamCodec<RegistryFriendlyByteBuf, BackpackSmithingRecipe> STREAM_CODEC =
                new StreamCodec<>() {
                    @Override
                    public BackpackSmithingRecipe decode(RegistryFriendlyByteBuf buf) {
                        return new BackpackSmithingRecipe();
                    }
                    @Override
                    public void encode(RegistryFriendlyByteBuf buf, BackpackSmithingRecipe value) {}
                };

        @Override
        public MapCodec<BackpackSmithingRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BackpackSmithingRecipe> streamCodec() { return STREAM_CODEC; }
    }

}