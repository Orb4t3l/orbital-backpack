package com.orbital.orbitalbackpack.items;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class Backpack extends Item {

    private final BackpackTier tier;

    public Backpack(BackpackTier tier) {
        super(new Properties().stacksTo(1));
        this.tier = tier;
    }

    public BackpackTier getTier() {
        return tier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            NetworkHooks.openScreen(
                    (ServerPlayer) player,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new BackpackMenu(ModMenus.MENUS.get(tier).get(), id, inv, hand, tier),
                            Component.translatable("item.orbitalbackpack." + tier.name().toLowerCase() + "_backpack")
                    ),
                    buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND)
            );
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}