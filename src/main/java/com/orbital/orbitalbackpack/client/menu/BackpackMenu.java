package com.orbital.orbitalbackpack.client.menu;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackMenu extends AbstractContainerMenu {

    private final ItemStackHandler handler;
    private final InteractionHand hand;
    private final Player player;
    private final BackpackTier tier;

    public BackpackMenu(MenuType<?> menuType, int id, Inventory inv, InteractionHand hand, BackpackTier tier) {
        super(menuType, id);
        this.hand = hand;
        this.player = inv.player;
        this.tier = tier;
        this.handler = new ItemStackHandler(tier.getSlots());

        ItemStack stack = player.getItemInHand(hand);
        if (stack != null && stack.hasTag() && stack.getTag().contains("inventory")) {
            this.handler.deserializeNBT(stack.getTag().getCompound("inventory"));
        }

        int startX = 8;
        int startY = 18;
        for (int row = 0; row < tier.getRows(); row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new SlotItemHandler(handler, col + row * 9, startX + col * 18, startY + row * 18));
            }
        }

        int playerStartY = startY + tier.getRows() * 18 + 14;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, playerStartY + row * 18));
            }
        }

        int hotbarY = playerStartY + 58;
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, hotbarY));
        }
    }

    public static BackpackMenu create(BackpackTier tier, int id, Inventory inv, FriendlyByteBuf buf) {
        InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new BackpackMenu(ModMenus.MENUS.get(tier).get(), id, inv, hand, tier);
    }

    public BackpackTier getTier() {
        return tier;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack != null) {
                stack.getOrCreateTag().put("inventory", handler.serializeNBT());
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();

            int containerSlots = tier.getSlots();
            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }
}