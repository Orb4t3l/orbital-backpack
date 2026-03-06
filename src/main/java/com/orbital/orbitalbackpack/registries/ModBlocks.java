package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.blocks.BackpackBlock;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.Map;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);

    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

    public static final Map<BackpackTier, DeferredHolder<Block, Block>> BLOCKS_MAP = new EnumMap<>(BackpackTier.class);

    static {
        for (BackpackTier tier : BackpackTier.values()) {
            String name = tier.name().toLowerCase() + "_backpack_block";
            DeferredHolder<Block, Block> block = BLOCKS.register(name, () -> new BackpackBlock(tier));
            BLOCKS_MAP.put(tier, block);
            BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }
    }

    public static DeferredHolder<Block, Block> get(BackpackTier tier) {
        return BLOCKS_MAP.get(tier);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }
}