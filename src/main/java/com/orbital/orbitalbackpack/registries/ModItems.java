package com.orbital.orbitalbackpack.registries;

import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.orbital.orbitalbackpack.OrbitalBackpack.MODID;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

//    public static final RegistryObject<Item> BACKPACK = ITEMS.register("backpack",
//            () -> new Item(new Item.Properties().stacksTo(1)));


    public static final RegistryObject<Item> BACKPACK = ITEMS.register("backpack",
            Backpack::new);
}
