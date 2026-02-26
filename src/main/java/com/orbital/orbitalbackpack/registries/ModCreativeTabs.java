package com.orbital.orbitalbackpack.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> BACKPACK = CREATIVE_MODE_TABS.register("backpack_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.BACKPACK.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(ModItems.BACKPACK.get());
//                output.accept(ModItems.BACKPACK.get()); other items if i want
//                output.accept(ModItems.BACKPACK.get());
//                output.accept(ModItems.BACKPACK.get());
//                output.accept(ModItems.BACKPACK.get());
            }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

