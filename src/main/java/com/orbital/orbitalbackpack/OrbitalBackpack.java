package com.orbital.orbitalbackpack;

import com.mojang.logging.LogUtils;
import com.orbital.orbitalbackpack.compat.CuriosCompat;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.registries.*;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.MinecraftForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.eventbus.api.IEventBus;
import net.neoforged.neoforge.eventbus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.ModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(OrbitalBackpack.MODID)
public class OrbitalBackpack {
    public static final String MODID = "orbitalbackpack";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OrbitalBackpack(ModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModMenus.MENU_TYPES.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModNetwork.register();
            if (ModList.get().isLoaded("curios")) {
                List<Item> backpacks = new ArrayList<>(ModItems.BACKPACKS.values()
                        .stream()
                        .map(ro -> ro.get())
                        .toList());
                CuriosCompat.registerCurioItems(backpacks);
            }
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
}