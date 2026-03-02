package com.orbital.orbitalbackpack.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class BackpackSmithingRecipe implements SmithingRecipe {

    // No id field - getId() is gone in 1.20.4
    public BackpackSmithingRecipe() {}

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack template = container.getItem(0);
        ItemStack base = container.getItem(1);
        ItemStack addition = container.getItem(2);
        return template.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
                && base.getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get()
                && addition.is(Items.NETHERITE_INGOT);
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess access) {
        ItemStack base = container.getItem(1);
        ItemStack result = new ItemStack(ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get());
        if (base.hasTag()) {
            result.setTag(base.getTag().copy());
        }
        return result;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return new ItemStack(ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get());
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return stack.getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get();
    }

    @Override
    public boolean isAdditionIngredient(ItemStack stack) {
        return stack.is(Items.NETHERITE_INGOT);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BACKPACK_SMITHING.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackSmithingRecipe> {

        // 1.20.4: Codec<T> not MapCodec
        @Override
        public Codec<BackpackSmithingRecipe> codec() {
            return RecordCodecBuilder.create(inst -> inst.point(new BackpackSmithingRecipe()));
        }

        // 1.20.4: no ResourceLocation parameter
        @Override
        public BackpackSmithingRecipe fromNetwork(FriendlyByteBuf buf) {
            return new BackpackSmithingRecipe();
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BackpackSmithingRecipe recipe) {}
    }
}