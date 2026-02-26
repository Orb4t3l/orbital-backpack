package com.orbital.orbitalbackpack.items;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.screen.BackpackScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Backpack extends Item {



    public Backpack() {
        super(new Properties().stacksTo(1));
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        OrbitalBackpack.LOGGER.info("test");
        if (level.isClientSide) {
            // Open preview GUI
            Minecraft.getInstance().setScreen(new BackpackScreen());
        }


        return super.use(level, player, hand);
    }
}
