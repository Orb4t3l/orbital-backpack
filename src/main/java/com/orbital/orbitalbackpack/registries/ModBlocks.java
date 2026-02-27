package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.blocks.BackpackBlock;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);

    public static final DeferredRegister<Item> BLOCK_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final Map<BackpackTier, RegistryObject<Block>> BLOCKS_MAP = new EnumMap<>(BackpackTier.class);

    static {
        for (BackpackTier tier : BackpackTier.values()) {
            String name = tier.name().toLowerCase() + "_backpack_block";
            RegistryObject<Block> block = BLOCKS.register(name, () -> new BackpackBlock(tier));
            BLOCKS_MAP.put(tier, block);
            BLOCK_ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        }
    }

    public static RegistryObject<Block> get(BackpackTier tier) {
        return BLOCKS_MAP.get(tier);
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ITEMS.register(eventBus);
    }
}