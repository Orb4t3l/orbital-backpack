package com.orbital.orbitalbackpack;

import com.mojang.logging.LogUtils;
import com.orbital.orbitalbackpack.capability.BackSlotCapability;
import com.orbital.orbitalbackpack.capability.BackSlotCapabilityProvider;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.registries.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
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
        modEventBus.addListener(this::registerCapabilities);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenus.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ModNetwork.register();
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(BackSlotCapability.class);
    }
}