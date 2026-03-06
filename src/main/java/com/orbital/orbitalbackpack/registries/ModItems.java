package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.items.MagnetUpgrade;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final Map<BackpackTier, RegistryObject<Item>> BACKPACKS = new EnumMap<>(BackpackTier.class);

    public static final RegistryObject<Item> MAGNET_UPGRADE = ITEMS.register("magnet_upgrade", MagnetUpgrade::new);

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