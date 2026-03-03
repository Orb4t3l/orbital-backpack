//package com.orbital.orbitalbackpack.crafting;
//
//import com.mojang.serialization.MapCodec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import com.orbital.orbitalbackpack.common.BackpackTier;
//import com.orbital.orbitalbackpack.registries.ModItems;
//import com.orbital.orbitalbackpack.registries.ModRecipeSerializers;
//import com.orbital.orbitalbackpack.util.ItemData;
//import net.minecraft.core.HolderLookup;
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraft.world.Container;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.item.crafting.RecipeSerializer;
//import net.minecraft.world.item.crafting.SmithingRecipe;
//import net.minecraft.world.level.Level;
//import org.jetbrains.annotations.Nullable;
//
//public class BackpackSmithingRecipe implements SmithingRecipe {
//
//    // constructor stays simple - recipe has no custom data
//    public BackpackSmithingRecipe() {}
//
//    @Override
//    public boolean matches(Container container, Level level) {
//        ItemStack template = container.getItem(0);
//        ItemStack base = container.getItem(1);
//        ItemStack addition = container.getItem(2);
//        return template.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE)
//                && base.getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get()
//                && addition.is(Items.NETHERITE_INGOT);
//    }
//
//    /**
//     * NOTE: Recipe interface changed: assemble now takes a HolderLookup.Provider-like provider
//     * in modern mappings (RegistryAccess implements HolderLookup.Provider). Implement that signature.
//     */
//    @Override
//    public ItemStack assemble(Container container, HolderLookup.Provider provider) {
//        ItemStack base = container.getItem(1);
//        ItemStack result = new ItemStack(ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get());
//
//        // copy full tag (old: result.setTag(base.getTag().copy());)
//        var baseTag = ItemData.getRawTagCopy(base); // returns null or a copy
//        if (baseTag != null) {
//            ItemData.setRawTag(result, baseTag); // sets the whole custom_data component
//        }
//
//        return result;
//    }
//
//    /**
//     * getResultItem now receives the provider object as well in recent versions.
//     * Return a "preview" of the result (no NBT required here, but we return the item).
//     */
//    @Override
//    public ItemStack getResultItem(HolderLookup.Provider provider) {
//        return new ItemStack(ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get());
//    }
//
//    @Override
//    public boolean isTemplateIngredient(ItemStack stack) {
//        return stack.is(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE);
//    }
//
//    @Override
//    public boolean isBaseIngredient(ItemStack stack) {
//        return stack.getItem() == ModItems.BACKPACKS.get(BackpackTier.DIAMOND).get();
//    }
//
//    @Override
//    public boolean isAdditionIngredient(ItemStack stack) {
//        return stack.is(Items.NETHERITE_INGOT);
//    }
//
//    @Override
//    public RecipeSerializer<?> getSerializer() {
//        return ModRecipeSerializers.BACKPACK_SMITHING.get();
//    }
//
//    // -------------------
//    // Serializer: see note below
//    // -------------------
//    public static class Serializer implements RecipeSerializer<BackpackSmithingRecipe> {
//        // NOTE: recipe serializers now need a MapCodec and a StreamCodec.
//        // For a stateless recipe (no JSON fields) we can use a simple point codec.
//
//        // MapCodec: returns an instance when reading from JSON (no fields here).
//        public static final MapCodec<BackpackSmithingRecipe> CODEC =
//                RecordCodecBuilder.mapCodec(inst -> inst.group().apply(inst, ignored -> new BackpackSmithingRecipe()));
//
//        // StreamCodec: the network codec (these utility classes may be in a helper package in your mappings).
//        // NeoForge/vanilla examples use StreamCodec.composite/PacketCodec helpers.
//        // We'll provide a minimal stream codec that writes nothing (stateless).
//        public static final net.minecraft.network.codec.PacketCodec<BackpackSmithingRecipe> PACKET_CODEC =
//                net.minecraft.network.codec.PacketCodec.of(buf -> new BackpackSmithingRecipe(), (buf, r) -> { /* nothing */ });
//
//        @Override
//        public MapCodec<BackpackSmithingRecipe> codec() {
//            return CODEC;
//        }
//
//        @Override
//        public net.minecraft.network.codec.PacketCodec<BackpackSmithingRecipe> packetCodec() {
//            return PACKET_CODEC;
//        }
//
//        // These two methods are still required by the interface (vanilla calls them for
//        // old-style network serialization). We'll implement the simple versions:
//
//        @Override
//        public BackpackSmithingRecipe fromNetwork(FriendlyByteBuf buf) {
//            return new BackpackSmithingRecipe();
//        }
//
//        @Override
//        public void toNetwork(FriendlyByteBuf buf, BackpackSmithingRecipe recipe) {
//            // nothing to write - recipe is stateless
//        }
//    }
//}