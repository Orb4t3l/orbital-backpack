package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB, OrbitalBackpack.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> BACKPACK_TAB =
            CREATIVE_TABS.register("backpack_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.orbitalbackpack.backpack_tab"))
                    .icon(() -> ModItems.BACKPACKS.get(BackpackTier.LEATHER).get().getDefaultInstance())
                    .displayItems((params, output) -> {
                        for (BackpackTier tier : BackpackTier.values()) {
                            output.accept(ModItems.BACKPACKS.get(tier).get());
                        }
                        output.accept(ModItems.MAGNET_UPGRADE.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_TABS.register(eventBus);
    }
}