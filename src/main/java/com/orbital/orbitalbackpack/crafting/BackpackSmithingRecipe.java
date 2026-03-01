package com.orbital.orbitalbackpack.crafting;

import com.google.gson.JsonObject;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

public class BackpackSmithingRecipe implements SmithingRecipe {

    private final ResourceLocation id;

    public BackpackSmithingRecipe(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean matches(Container container, Level level) {
        ItemStack template = container.getItem(0);
        ItemStack base = container.getItem(1);
        ItemStack addition = container.getItem(2);

        boolean isTemplate = template.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
        boolean isDiamondBackpack = base.getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get();
        boolean isNetheriteIngot = addition.is(Items.NETHERITE_INGOT);

        return isTemplate && isDiamondBackpack && isNetheriteIngot;
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BACKPACK_SMITHING.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackSmithingRecipe> {

        @Override
        public BackpackSmithingRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new BackpackSmithingRecipe(id);
        }

        @Override
        public BackpackSmithingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new BackpackSmithingRecipe(id);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BackpackSmithingRecipe recipe) {}
    }
}