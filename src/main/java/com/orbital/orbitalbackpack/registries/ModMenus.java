package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, OrbitalBackpack.MODID);

    public static final RegistryObject<MenuType<BackpackMenu>> BACKPACK =
            MENU_TYPES.register("backpack_menu",
                    () -> IForgeMenuType.create(BackpackMenu::new));
}
