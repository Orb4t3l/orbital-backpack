package com.orbital.orbitalbackpack.client.screen;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.network.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private static final ResourceLocation PICKUP_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/pickup_button.png");
    private static final ResourceLocation SORT_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/sort_button.png");
    private static final ResourceLocation DEPOSIT_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/deposit_button.png");
    private static final ResourceLocation WITHDRAW_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/withdraw_button.png");
    private static final ResourceLocation MAGNET_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/magnet_button.png");

    private final BackpackTier tier;
    private EditBox searchBox;
    private String searchText = "";

    private static final int SLOT_HIDDEN_COLOR = 0xFF3D3D3D;
    private static final int SEARCH_BOX_WIDTH = 60;
    private static final int SEARCH_BOX_HEIGHT = 12;
    private static final int BUTTON_SIZE = 12;
    private static final int BUTTON_GAP = 3;
    private static final int MAGNET_ACTIVE_COLOR = 0x5500AAFF;

    public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.tier = menu.getTier();
        this.imageWidth = 176;
        this.imageHeight = 114 + tier.getRows() * 18;
    }

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelY = tier.getRows() * 18 + 32 - 10;

        int sideX = this.leftPos + this.imageWidth + 6;
        int currentY = this.topPos;

        this.searchBox = new EditBox(this.font, sideX, currentY, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT, Component.literal(""));
        this.searchBox.setMaxLength(32);
        this.searchBox.setHint(Component.translatable("gui.orbitalbackpack.search"));
        this.searchBox.setResponder(text -> this.searchText = text.toLowerCase());
        this.addRenderableWidget(this.searchBox);
        currentY += SEARCH_BOX_HEIGHT + BUTTON_GAP;

        if (this.menu.isBlockBased()) {
            ImageButton pickupButton = new ImageButton(
                    sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                    0, 0, BUTTON_SIZE,
                    PICKUP_BUTTON_TEXTURE, BUTTON_SIZE, BUTTON_SIZE * 2,
                    btn -> onPickupClicked()
            );
            pickupButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.pickup")));
            this.addRenderableWidget(pickupButton);
            currentY += BUTTON_SIZE + BUTTON_GAP;
        }

        ImageButton sortButton = new ImageButton(
                sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                0, 0, BUTTON_SIZE,
                SORT_BUTTON_TEXTURE, BUTTON_SIZE, BUTTON_SIZE * 2,
                btn -> onSortClicked()
        );
        sortButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.sort")));
        this.addRenderableWidget(sortButton);
        currentY += BUTTON_SIZE + BUTTON_GAP;

        ImageButton depositButton = new ImageButton(
                sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                0, 0, BUTTON_SIZE,
                DEPOSIT_BUTTON_TEXTURE, BUTTON_SIZE, BUTTON_SIZE * 2,
                btn -> onDepositClicked()
        );
        depositButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.deposit")));
        this.addRenderableWidget(depositButton);
        currentY += BUTTON_SIZE + BUTTON_GAP;

        ImageButton withdrawButton = new ImageButton(
                sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                0, 0, BUTTON_SIZE,
                WITHDRAW_BUTTON_TEXTURE, BUTTON_SIZE, BUTTON_SIZE * 2,
                btn -> onWithdrawClicked()
        );
        withdrawButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.withdraw")));
        this.addRenderableWidget(withdrawButton);
        currentY += BUTTON_SIZE + BUTTON_GAP;

        if (!this.menu.isBlockBased()) {
            ImageButton magnetButton = new ImageButton(
                    sideX, currentY, BUTTON_SIZE, BUTTON_SIZE,
                    0, 0, BUTTON_SIZE,
                    MAGNET_BUTTON_TEXTURE, BUTTON_SIZE, BUTTON_SIZE * 2,
                    btn -> onMagnetClicked()
            );
            magnetButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.magnet")));
            this.addRenderableWidget(magnetButton);
        }
    }

    private void onPickupClicked() {
        if (this.menu.isBlockBased() && this.menu.getBlockPos() != null) {
            ModNetwork.CHANNEL.sendToServer(new PickupBackpackPacket(this.menu.getBlockPos(), tier));
        }
    }

    private void onSortClicked() {
        boolean isBlock = this.menu.isBlockBased();
        ModNetwork.CHANNEL.sendToServer(new SortBackpackPacket(
                isBlock,
                isBlock ? this.menu.getBlockPos() : null,
                !isBlock && this.menu.getHand() == InteractionHand.MAIN_HAND
        ));
    }

    private void onDepositClicked() {
        boolean isBlock = this.menu.isBlockBased();
        ModNetwork.CHANNEL.sendToServer(new DepositAllPacket(
                isBlock,
                isBlock ? this.menu.getBlockPos() : null,
                !isBlock && this.menu.getHand() == InteractionHand.MAIN_HAND
        ));
    }

    private void onWithdrawClicked() {
        boolean isBlock = this.menu.isBlockBased();
        ModNetwork.CHANNEL.sendToServer(new WithdrawAllPacket(
                isBlock,
                isBlock ? this.menu.getBlockPos() : null,
                !isBlock && this.menu.getHand() == InteractionHand.MAIN_HAND
        ));
    }

    private void onMagnetClicked() {
        ModNetwork.CHANNEL.sendToServer(new MagnetTogglePacket(
                this.menu.getHand() == InteractionHand.MAIN_HAND
        ));
    }

    private boolean isMagnetActive() {
        if (this.minecraft == null || this.minecraft.player == null) return false;
        for (int i = 0; i < this.minecraft.player.getInventory().getContainerSize(); i++) {
            ItemStack stack = this.minecraft.player.getInventory().getItem(i);
            if (stack.getItem() == this.menu.getTier().equals(tier)
                    && stack.hasTag()
                    && stack.getTag().getBoolean("magnet")) {
                return true;
            }
        }
        InteractionHand hand = this.menu.getHand();
        if (hand != null) {
            ItemStack held = this.minecraft.player.getItemInHand(hand);
            return held.hasTag() && held.getTag().getBoolean("magnet");
        }
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        if (!searchText.isEmpty()) {
            for (Slot slot : this.menu.slots) {
                if (slot instanceof SlotItemHandler) {
                    if (!matchesSearch(slot)) {
                        int slotX = this.leftPos + slot.x;
                        int slotY = this.topPos + slot.y;
                        guiGraphics.fill(slotX, slotY, slotX + 16, slotY + 16, SLOT_HIDDEN_COLOR);
                    }
                }
            }
        }

        if (isMagnetActive()) {
            guiGraphics.fill(
                    this.leftPos - 2, this.topPos - 2,
                    this.leftPos + this.imageWidth + 2,
                    this.topPos + this.imageHeight + 2,
                    MAGNET_ACTIVE_COLOR
            );
        }

        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(this.font, "Search",
                this.leftPos + this.imageWidth + 6, this.topPos - 9, 0xFFFFFF, true);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(tier.getTexture(), this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    private boolean matchesSearch(Slot slot) {
        if (!slot.hasItem()) return false;
        String itemName = slot.getItem().getHoverName().getString().toLowerCase();
        String itemId = slot.getItem().getItem().builtInRegistryHolder().key().location().toString().toLowerCase();
        return itemName.contains(searchText) || itemId.contains(searchText);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox.isFocused() && this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char c, int modifiers) {
        if (this.searchBox.isFocused() && this.searchBox.charTyped(c, modifiers)) {
            return true;
        }
        return super.charTyped(c, modifiers);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(net.minecraft.sounds.SoundEvents.HORSE_SADDLE, 1.0F, 1.0F);
        }
        super.onClose();
    }
}