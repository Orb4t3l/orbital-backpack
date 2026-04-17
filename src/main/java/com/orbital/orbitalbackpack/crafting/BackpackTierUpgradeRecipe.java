package com.orbital.orbitalbackpack.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BackpackTierUpgradeRecipe extends CustomRecipe {

    private final BackpackTier fromTier;
    private final BackpackTier toTier;
    private final String material;

    public BackpackTierUpgradeRecipe(BackpackTier fromTier, BackpackTier toTier, String material) {
        super(CraftingBookCategory.MISC);
        this.fromTier = fromTier;
        this.toTier = toTier;
        this.material = material;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasBackpack = false;
        int materialCount = 0;
        int expectedMaterial = 8;

        net.minecraft.resources.ResourceLocation targetRL =
                net.minecraft.resources.ResourceLocation.parse(material);

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof Backpack backpack) {
                if (backpack.getTier() != fromTier) return false;
                hasBackpack = true;
            } else if (net.minecraft.core.registries.BuiltInRegistries.ITEM
                    .getKey(stack.getItem()).equals(targetRL)) {
                materialCount++;
            } else {
                return false;
            }
        }
        return hasBackpack && materialCount == expectedMaterial;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider access) {
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem() instanceof Backpack) {
                ItemStack result = new ItemStack(ModItems.BACKPACKS.get(toTier).get());
                // copy inventory NBT
                if (ItemData.has(stack, "inventory")) {
                    ItemData.set(result, "inventory", ItemData.getCompound(stack, "inventory"));
                }
                // copy magnet data
                if (ItemData.has(stack, "magnet_unlocked")) {
                    ItemData.setBoolean(result, "magnet_unlocked", ItemData.getBoolean(stack, "magnet_unlocked"));
                }
                if (ItemData.has(stack, "magnet")) {
                    ItemData.setBoolean(result, "magnet", ItemData.getBoolean(stack, "magnet"));
                }
                return result;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.BACKPACK_TIER_UPGRADE.get();
    }

    public static class Serializer implements RecipeSerializer<BackpackTierUpgradeRecipe> {

        public static final MapCodec<BackpackTierUpgradeRecipe> CODEC =
                RecordCodecBuilder.mapCodec(inst -> inst.group(
                        BackpackTier.CODEC.fieldOf("from").forGetter(r -> r.fromTier),
                        BackpackTier.CODEC.fieldOf("to").forGetter(r -> r.toTier),
                        Codec.STRING.fieldOf("material").forGetter(r -> r.material)
                ).apply(inst, BackpackTierUpgradeRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, BackpackTierUpgradeRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8, r -> r.fromTier.name(),
                        ByteBufCodecs.STRING_UTF8, r -> r.toTier.name(),
                        ByteBufCodecs.STRING_UTF8, r -> r.material,
                        (from, to, mat) -> new BackpackTierUpgradeRecipe(
                                BackpackTier.valueOf(from), BackpackTier.valueOf(to), mat)
                );

        @Override
        public MapCodec<BackpackTierUpgradeRecipe> codec() { return CODEC; }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BackpackTierUpgradeRecipe> streamCodec() { return STREAM_CODEC; }
    }
}