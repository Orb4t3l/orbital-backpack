package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Arrays;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);

    public static final RegistryObject<BlockEntityType<BackpackBlockEntity>> BACKPACK_BE =
            BLOCK_ENTITIES.register("backpack_be", () -> BlockEntityType.Builder.of(
                    BackpackBlockEntity::new,
                    Arrays.stream(BackpackTier.values())
                            .map(tier -> ModBlocks.get(tier).get())
                            .toArray(Block[]::new)
            ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}