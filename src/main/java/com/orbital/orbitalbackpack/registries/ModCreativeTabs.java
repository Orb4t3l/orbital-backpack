package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModCreativeTabs {

    // No DeferredRegister for creative tabs — avoids broken reobf mappings in Forge 1.20.6

    public static void register(IEventBus eventBus) {
        eventBus.addListener(ModCreativeTabs::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            for (BackpackTier tier : BackpackTier.values()) {
                event.accept(ModItems.BACKPACKS.get(tier).get());
            }
            event.accept(ModItems.MAGNET_UPGRADE.get());
        }
    }
}