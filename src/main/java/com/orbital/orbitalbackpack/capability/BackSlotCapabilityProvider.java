package com.orbital.orbitalbackpack.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BackSlotCapabilityProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<BackSlotCapability> BACK_SLOT =
            CapabilityManager.get(new CapabilityToken<>() {});

    private final BackSlotCapability instance = new BackSlotCapability();
    private final LazyOptional<BackSlotCapability> optional = LazyOptional.of(() -> instance);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == BACK_SLOT ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        instance.deserializeNBT(tag);
    }

    public void invalidate() {
        optional.invalidate();
    }
}