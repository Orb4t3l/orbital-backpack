package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.items.MagnetUpgrade;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.EnumMap;
import java.util.Map;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(BuiltInRegistries.ITEM, MODID);

    public static final Map<BackpackTier, DeferredHolder<Item, Item>> BACKPACKS = new EnumMap<>(BackpackTier.class);

    public static final DeferredHolder<Item, Item> MAGNET_UPGRADE = ITEMS.register("magnet_upgrade", MagnetUpgrade::new);

    static {
        for (BackpackTier tier : BackpackTier.values()) {
            String name = tier.name().toLowerCase() + "_backpack";
            BACKPACKS.put(tier, ITEMS.register(name, () -> new Backpack(tier)));
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}