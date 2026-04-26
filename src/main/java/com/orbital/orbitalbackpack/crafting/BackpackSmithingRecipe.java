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
import net.minecraft.world.item.crafting.SmithingRecipeInput;
import net.minecraft.world.level.Level;

public class BackpackSmithingRecipe implements SmithingRecipe {

    public BackpackSmithingRecipe() {}

    @Override
    public boolean matches(SmithingRecipeInput input, Level level) {
        return input.template().is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                && input.base().getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get()
                && input.addition().is(Items.NETHERITE_INGOT);
    }

    @Override
    public ItemStack assemble(SmithingRecipeInput input, HolderLookup.Provider access) {
        ItemStack base = input.base();
        ItemStack result = new ItemStack(ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get());
        var customData = base.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (customData != null) {
            net.minecraft.nbt.CompoundTag tag = customData.copyTag();
            if (tag.contains("inventory")) {
                net.minecraft.nbt.CompoundTag inv = tag.getCompound("inventory");
                inv.putInt("Size", BackpackTier.NETHERITE.getSlots());
                tag.put("inventory", inv);
            }
            result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));
        }
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