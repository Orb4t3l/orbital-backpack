package com.orbital.orbitalbackpack.client.menu;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModMenus;
import com.orbital.orbitalbackpack.util.ItemData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.items.ItemStackHandler;
import top.theillusivec4.curios.api.CuriosApi;

public class BackpackMenu extends AbstractContainerMenu {

    private final ItemStackHandler handler;
    private final BackpackTier tier;
    private final Player player;
    private final InteractionHand hand;
    private final BlockPos blockPos;
    private final boolean isBlockBased;
    private final boolean isCurioSlot;

    public BackpackMenu(MenuType<?> menuType, int id, Inventory inv, InteractionHand hand, BackpackTier tier) {
        super(menuType, id);
        this.hand = hand;
        this.blockPos = null;
        this.isBlockBased = false;
        this.isCurioSlot = false;
        this.player = inv.player;
        this.tier = tier;
        this.handler = new ItemStackHandler(tier.getSlots());

        ItemStack stack = player.getItemInHand(hand);
        if (ItemData.has(stack, "inventory")) {
            this.handler.deserializeNBT(inv.player.registryAccess(), ItemData.getCompound(stack, "inventory"));
        }
        buildSlots(inv);
    }

    public BackpackMenu(MenuType<?> menuType, int id, Inventory inv, BlockPos blockPos,
                        BackpackTier tier, ItemStackHandler existingHandler) {
        super(menuType, id);
        this.hand = null;
        this.blockPos = blockPos;
        this.isBlockBased = true;
        this.isCurioSlot = false;
        this.player = inv.player;
        this.tier = tier;
        this.handler = existingHandler;
        buildSlots(inv);
    }

    public BackpackMenu(MenuType<?> menuType, int id, Inventory inv, BackpackTier tier, ItemStack curioStack) {
        super(menuType, id);
        this.hand = null;
        this.blockPos = null;
        this.isBlockBased = false;
        this.isCurioSlot = true;
        this.player = inv.player;
        this.tier = tier;
        this.handler = new ItemStackHandler(tier.getSlots());

        if (ItemData.has(curioStack, "inventory")) {
            this.handler.deserializeNBT(inv.player.registryAccess(), ItemData.getCompound(curioStack, "inventory"));
        }
        buildSlots(inv);
    }

    public static BackpackMenu create(BackpackTier tier, int id, Inventory inv, FriendlyByteBuf buf) {
        boolean isBlock = buf.readBoolean();
        MenuType<?> menuType = ModMenus.MENUS.get(tier).get();

        if (isBlock) {
            BlockPos pos = buf.readBlockPos();
            BlockEntity be = inv.player.level().getBlockEntity(pos);
            if (be instanceof BackpackBlockEntity backpackBE) {
                return new BackpackMenu(menuType, id, inv, pos, tier, backpackBE.getHandler());
            }
        }

        boolean isCurio = buf.readBoolean();
        if (isCurio && ModList.get().isLoaded("curios")) {
            ItemStack curioStack = ItemStack.EMPTY;
            try {
                curioStack = CuriosApi.getCuriosHelper()
                        .getCuriosHandler(inv.player)
                        .map(h -> h.getStacksHandler("back")
                                .map(s -> s.getStacks().getStackInSlot(0))
                                .orElse(ItemStack.EMPTY))
                        .orElse(ItemStack.EMPTY);
            } catch (Exception ignored) {}
            return new BackpackMenu(menuType, id, inv, tier, curioStack);
        }

        InteractionHand hand = buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        return new BackpackMenu(menuType, id, inv, hand, tier);
    }

    private void buildSlots(Inventory inv) {
        int startX = 8, startY = 18;
        for (int row = 0; row < tier.getRows(); row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new RestrictedBackpackSlot(handler, col + row * 9,
                        startX + col * 18, startY + row * 18));
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

    public BackpackTier getTier() { return tier; }
    public boolean isBlockBased() { return isBlockBased; }
    public boolean isCurioSlot() { return isCurioSlot; }
    public BlockPos getBlockPos() { return blockPos; }
    public InteractionHand getHand() { return hand; }
    public ItemStackHandler getHandler() { return handler; }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player.level().isClientSide) return;

        if (isBlockBased) {
            BlockEntity be = player.level().getBlockEntity(blockPos);
            if (be instanceof BackpackBlockEntity backpackBE) {
                backpackBE.setOpen(false);
                backpackBE.setChanged();
            }
        } else if (isCurioSlot) {
            if (ModList.get().isLoaded("curios")) {
                try {
                    CuriosApi.getCuriosHelper()
                            .getCuriosHandler(player)
                            .ifPresent(h -> h.getStacksHandler("back").ifPresent(stacks -> {
                                ItemStack slotStack = stacks.getStacks().getStackInSlot(0);
                                if (!slotStack.isEmpty()) {
                                    ItemData.edit(slotStack, tag -> tag.put("inventory", handler.serializeNBT(player.registryAccess())));
                                }
                            }));
                } catch (Exception ignored) {}
            }
        } else {
            ItemStack stack = player.getItemInHand(hand);
            if (!stack.isEmpty()) {
                ItemData.edit(stack, tag -> tag.put("inventory", handler.serializeNBT(player.registryAccess())));
            }
        }
    }

    @Override
    public boolean stillValid(Player player) { return true; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();
            int containerSlots = tier.getSlots();
            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false))
                    return ItemStack.EMPTY;
            }
            if (stackInSlot.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return stack;
    }
}