package com.orbital.orbitalbackpack;

import com.mojang.logging.LogUtils;
import com.orbital.orbitalbackpack.compat.CurioBackpackItem;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.registries.*;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.orbital.orbitalbackpack.registries.ModBlocks.BLOCKS;
import static com.orbital.orbitalbackpack.registries.ModBlocks.BLOCK_ITEMS;


@Mod(OrbitalBackpack.MODID)
public class OrbitalBackpack {
    public static final String MODID = "orbitalbackpack";
    public static final Logger LOGGER = LogUtils.getLogger();

    public OrbitalBackpack(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModMenus.MENU_TYPES.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded("curios")) {
                List<Item> backpacks = new ArrayList<>(ModItems.BACKPACKS.values()
                        .stream()
                        .map(ro -> ro.get())
                        .toList());
                for (Item item : backpacks) {
                    top.theillusivec4.curios.api.CuriosApi.registerCurio(item, CurioBackpackItem.INSTANCE);
                }}
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

}