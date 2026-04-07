package com.orbital.orbitalbackpack.blocks;

import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

public class BackpackBlockEntity extends BlockEntity {

    private final ItemStackHandler handler;
    private boolean claimed = false;
    private boolean open = false;
    private int openTick = 0;

    public BackpackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BACKPACK_BE.get(), pos, state);
        BackpackTier tier = state.getBlock() instanceof BackpackBlock bb ? bb.getTier() : BackpackTier.LEATHER;
        this.handler = new ItemStackHandler(tier.getSlots());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BackpackBlockEntity entity) {
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
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", handler.serializeNBT(registries));
        tag.putBoolean("claimed", this.claimed);
        tag.putBoolean("open", this.open);
        tag.putInt("openTick", this.openTick);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("inventory")) {
            handler.deserializeNBT(registries, tag.getCompound("inventory"));
        }
        this.claimed = tag.getBoolean("claimed");
        this.open = tag.getBoolean("open");
        this.openTick = tag.getInt("openTick");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}