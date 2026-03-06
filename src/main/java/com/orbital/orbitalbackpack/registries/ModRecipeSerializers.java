package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.crafting.BackpackSmithingRecipe;
import com.orbital.orbitalbackpack.crafting.BackpackUpgradeRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BackpackUpgradeRecipe>> BACKPACK_UPGRADE =
            RECIPE_SERIALIZERS.register("backpack_upgrade", BackpackUpgradeRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BackpackSmithingRecipe>> BACKPACK_SMITHING =
            RECIPE_SERIALIZERS.register("backpack_smithing", BackpackSmithingRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}