package com.orbital.orbitalbackpack.client.screen;

import com.orbital.orbitalbackpack.client.menu.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkHooks;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {



    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft","textures/gui/container/shulker_box.png");

    private EditBox searchBox;

    public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.searchBox = new EditBox(this.font, this.width / 2 - 100, 20, 200, 20, Component.literal("Search"));
        this.addRenderableWidget(this.searchBox);
        super.init();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        int x = (this.width - 176) / 2;
//        int y = (this.height - 166) / 2;


        this.renderBackground(guiGraphics);
//        guiGraphics.blit(TEXTURE, x, y, 0, 0, 176, 166);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.searchBox.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.searchBox.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null) {
            this.minecraft.player.playSound(
                    net.minecraft.sounds.SoundEvents.HORSE_SADDLE,
                    1.0F,
                    1.0F
            );
        }
        super.onClose();
    }


}
