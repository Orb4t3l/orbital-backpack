package com.orbital.orbitalbackpack.items;

import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.client.tooltip.BackpackTooltipComponent;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.common.ItemValueHelper;
import com.orbital.orbitalbackpack.registries.ModBlocks;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Backpack extends Item {

    private final BackpackTier tier;

    public Backpack(BackpackTier tier) {
        super(new Properties().stacksTo(1));
        this.tier = tier;
    }

    public BackpackTier getTier() { return tier; }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
        if (com.orbital.orbitalbackpack.compat.CuriosCompat.isLoaded()) {
            return new net.minecraftforge.common.capabilities.ICapabilityProvider() {
                private final top.theillusivec4.curios.api.type.capability.ICurioItem curio =
                        com.orbital.orbitalbackpack.compat.CuriosCompat.makeCurioItem(Backpack.this);

                @Override
                public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(
                        net.minecraftforge.common.capabilities.Capability<T> cap,
                        @Nullable net.minecraft.core.Direction side) {
                    try {
                        var curiosCap = top.theillusivec4.curios.api.CuriosCapability.ITEM;
                        if (curiosCap != null && cap == curiosCap) {
                            return net.minecraftforge.common.util.LazyOptional.of(() -> curio).cast();
                        }
                    } catch (Exception ignored) {}
                    return net.minecraftforge.common.util.LazyOptional.empty();
                }
            };
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        ItemStackHandler handler = new ItemStackHandler(tier.getSlots());
        if (stack.hasTag() && stack.getTag().contains("inventory")) {
            handler.deserializeNBT(stack.getTag().getCompound("inventory"));
        }
        int usedSlots = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) usedSlots++;
        }
        boolean expanded = Screen.hasShiftDown();
        return Optional.of(new BackpackTooltipComponent(
                ItemValueHelper.getTopItems(handler, expanded ? 27 : 5),
                usedSlots, tier.getSlots(), expanded
        ));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (player != null && player.isCrouching()) {
            BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
            if (level.getBlockState(pos).isAir()) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, ModBlocks.get(tier).get().defaultBlockState());
                    BlockEntity be = level.getBlockEntity(pos);
                    if (be instanceof BackpackBlockEntity backpackBE) {
                        ItemStack stack = context.getItemInHand();
                        if (stack.hasTag() && stack.getTag().contains("inventory")) {
                            backpackBE.getHandler().deserializeNBT(stack.getTag().getCompound("inventory"));
                        }
                        backpackBE.setChanged();
                    }
                    if (!player.getAbilities().instabuild) {
                        context.getItemInHand().shrink(1);
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            NetworkHooks.openScreen(
                    (ServerPlayer) player,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new BackpackMenu(
                                    ModMenus.MENUS.get(tier).get(), id, inv, hand, tier),
                            Component.translatable("item.orbitalbackpack."
                                    + tier.name().toLowerCase() + "_backpack")
                    ),
                    buf -> {
                        buf.writeBoolean(false);
                        buf.writeBoolean(false);
                        buf.writeBoolean(hand == InteractionHand.MAIN_HAND);
                    }
            );
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}