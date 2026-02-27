package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> BACKPACK_TAB = CREATIVE_MODE_TABS.register("backpack_tab", () ->
            CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.BACKPACKS.get(BackpackTier.NETHERITE).get().getDefaultInstance())
                    .title(Component.translatable("creativetab.orbitalbackpack.backpack_tab"))
                    .displayItems((parameters, output) -> {
                        for (BackpackTier tier : BackpackTier.values()) {
                            output.accept(ModItems.BACKPACKS.get(tier).get());
                        }
                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}