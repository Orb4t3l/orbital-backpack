package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(BuiltInRegistries.MENU, OrbitalBackpack.MODID);

    public static final Map<BackpackTier, DeferredHolder<MenuType<?>, MenuType<BackpackMenu>>> MENUS =
            new EnumMap<>(BackpackTier.class);

    static {
        for (BackpackTier tier : BackpackTier.values()) {
            String name = tier.name().toLowerCase() + "_backpack_menu";
            MENUS.put(tier, MENU_TYPES.register(name,
                    () -> IMenuTypeExtension.create((id, inv, buf) -> BackpackMenu.create(tier, id, inv, buf))));
        }
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}