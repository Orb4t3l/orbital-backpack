package com.orbital.orbitalbackpack.items;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class MagnetUpgrade extends Item {

    public MagnetUpgrade() {
        super(new Properties().stacksTo(16).rarity(Rarity.UNCOMMON));
    }
}