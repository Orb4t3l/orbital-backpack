package com.orbital.orbitalbackpack.blocks;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackBlockEntity extends BlockEntity {

    private ItemStackHandler handler;
    private boolean claimed = false;
    private boolean open = false;
    private int openTick = 0;

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BACKPACK_BE.get(), pos, state);
        BackpackTier tier = state.getBlock() instanceof BackpackBlock bb ? bb.getTier() : BackpackTier.LEATHER;
        this.handler = new ItemStackHandler(tier.getSlots());
    }

    public static void tick(net.minecraft.world.level.Level level, BlockPos pos,
                            BlockState state, BackpackBlockEntity entity) {
        if (entity.open && entity.openTick < 10) {
            entity.openTick++;
        } else if (!entity.open && entity.openTick > 0) {
            entity.openTick--;
        }
    }

    public ItemStackHandler getHandler() { return handler; }
    public boolean isClaimed() { return claimed; }
    public void setClaimed() { this.claimed = true; }
    public boolean isOpen() { return open; }
    public int getOpenTick() { return openTick; }

    public void setOpen(boolean open) {
        this.open = open;
        setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("inventory")) {
            handler.deserializeNBT(tag.getCompound("inventory"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", handler.serializeNBT());
    }
}