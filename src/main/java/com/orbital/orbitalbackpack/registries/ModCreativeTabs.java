package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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