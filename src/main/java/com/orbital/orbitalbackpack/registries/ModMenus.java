package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final Map<BackpackTier, RegistryObject<MenuType<BackpackMenu>>> MENUS =
            new EnumMap<>(BackpackTier.class);

    static {
        for (BackpackTier tier : BackpackTier.values()) {
            String name = tier.name().toLowerCase() + "_backpack_menu";
            MENUS.put(tier, MENU_TYPES.register(name,
                    () -> IForgeMenuType.create((id, inv, buf) ->
                            BackpackMenu.create(tier, id, inv, buf))));
        }
    }

    public static void register(IEventBus eventBus) {
        MENU_TYPES.register(eventBus);
    }
}