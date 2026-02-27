package com.orbital.orbitalbackpack.client.screen;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.network.ModNetwork;
import com.orbital.orbitalbackpack.network.PickupBackpackPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private static final ResourceLocation PICKUP_BUTTON_TEXTURE =
            new ResourceLocation("orbitalbackpack", "textures/gui/pickup_button.png");

    private final BackpackTier tier;
    private EditBox searchBox;
    private ImageButton pickupButton;
    private String searchText = "";

    private static final int SLOT_HIDDEN_COLOR = 0xFF3D3D3D;
    private static final int SEARCH_BOX_WIDTH = 80;
    private static final int SEARCH_BOX_HEIGHT = 18;
    private static final int BUTTON_SIZE = 18;

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

        int sideX = this.leftPos + this.imageWidth + 4;

        this.searchBox = new EditBox(this.font, sideX, this.topPos, SEARCH_BOX_WIDTH, SEARCH_BOX_HEIGHT, Component.literal(""));
        this.searchBox.setMaxLength(32);
        this.searchBox.setHint(Component.translatable("gui.orbitalbackpack.search"));
        this.searchBox.setResponder(text -> this.searchText = text.toLowerCase());
        this.addRenderableWidget(this.searchBox);

        if (this.menu.isBlockBased()) {
            this.pickupButton = new ImageButton(
                    sideX,
                    this.topPos + SEARCH_BOX_HEIGHT + 4,
                    BUTTON_SIZE,
                    BUTTON_SIZE,
                    0, 0,
                    BUTTON_SIZE,
                    PICKUP_BUTTON_TEXTURE,
                    BUTTON_SIZE, BUTTON_SIZE * 2,
                    btn -> onPickupClicked()
            );
            this.pickupButton.setTooltip(Tooltip.create(Component.translatable("gui.orbitalbackpack.pickup")));
            this.addRenderableWidget(this.pickupButton);
        }
    }

    private void onPickupClicked() {
        if (this.menu.isBlockBased() && this.menu.getBlockPos() != null) {
            ModNetwork.CHANNEL.sendToServer(new PickupBackpackPacket(this.menu.getBlockPos(), tier));
        }
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

        this.renderTooltip(guiGraphics, mouseX, mouseY);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawString(this.font, "Search",
                this.leftPos + this.imageWidth + 4, this.topPos - 10, 0xFFFFFF, true);
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