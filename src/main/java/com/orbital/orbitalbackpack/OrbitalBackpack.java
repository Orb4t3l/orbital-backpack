package com.orbital.orbitalbackpack;

import com.mojang.logging.LogUtils;
import com.orbital.orbitalbackpack.registries.ModBlocks;
import com.orbital.orbitalbackpack.registries.ModCreativeTabs;
import com.orbital.orbitalbackpack.registries.ModItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(OrbitalBackpack.MODID)
public class OrbitalBackpack {
    public static final String MODID = "orbitalbackpack";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OrbitalBackpack(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        // Register setup
        modEventBus.addListener(this::commonSetup);

        // Register registries
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        // Register forge events
        MinecraftForge.EVENT_BUS.register(this);

        // Config
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Orbital Backpack loaded!");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Server starting...");
    }
}