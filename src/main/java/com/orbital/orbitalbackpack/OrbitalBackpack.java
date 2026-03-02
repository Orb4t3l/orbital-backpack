package com.orbital.orbitalbackpack;

import com.mojang.logging.LogUtils;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.registries.*;
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

        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModMenus.MENU_TYPES.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.register();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
}