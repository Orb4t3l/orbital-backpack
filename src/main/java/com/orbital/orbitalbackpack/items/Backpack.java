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
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import java.util.Optional;

public class Backpack extends Item {

    private final BackpackTier tier;

    public Backpack(BackpackTier tier) {
        super(new Properties().stacksTo(1));
        this.tier = tier;
    }

    public BackpackTier getTier() { return tier; }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (ModList.get().isLoaded("curios")) {
            return new CurioCapabilityProvider();
        }
        return super.initCapabilities(stack, nbt);
    }

    private static class CurioCapabilityProvider implements ICapabilityProvider {

        private final ICurio curio = new ICurio() {
            @Override
            public ItemStack getStack() {
                return ItemStack.EMPTY;
            }

            @Override
            public void curioTick(SlotContext slotContext) {}

            @Override
            public boolean canEquip(SlotContext slotContext) {
                return slotContext.identifier().equals("back");
            }

            @Override
            public boolean canUnequip(SlotContext slotContext) {
                return true;
            }

            @Override
            public ICurio.SoundInfo getEquipSound(SlotContext slotContext) {
                return new ICurio.SoundInfo(
                        net.minecraft.sounds.SoundEvents.ARMOR_EQUIP_LEATHER, 1.0f, 1.0f);
            }
        };

        private final LazyOptional<ICurio> lazyOptional = LazyOptional.of(() -> curio);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(
                @NotNull Capability<T> cap, @Nullable Direction side) {
            if (cap == CuriosCapability.ITEM) {
                return lazyOptional.cast();
            }
            return LazyOptional.empty();
        }
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
                        ItemStack held = context.getItemInHand();
                        if (held.hasTag() && held.getTag().contains("inventory")) {
                            backpackBE.getHandler().deserializeNBT(held.getTag().getCompound("inventory"));
                        }
                        backpackBE.setChanged();
                    }
                    if (!player.getAbilities().instabuild) context.getItemInHand().shrink(1);
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
                        buf.writeBoolean(hand == InteractionHand.MAIN_HAND);
                    }
            );
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}