package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IForgeMenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, OrbitalBackpack.MODID);

    public static final Map<BackpackTier, RegistryObject<MenuType<BackpackMenu>>> MENUS = new EnumMap<>(BackpackTier.class);

    static {
        for (BackpackTier tier : BackpackTier.values()) {
            String name = tier.name().toLowerCase() + "_backpack_menu";
            MENUS.put(tier, MENU_TYPES.register(name,
                    () -> IForgeMenuType.create((id, inv, buf) -> BackpackMenu.create(tier, id, inv, buf))));

        }
    }
}