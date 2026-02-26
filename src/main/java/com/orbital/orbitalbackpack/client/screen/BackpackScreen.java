package com.orbital.orbitalbackpack.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BackpackScreen extends Screen {


    private static final ResourceLocation TEXTURE = new ResourceLocation("orbitalbackpack","textures/gui/backpack_screen.png");

    public BackpackScreen(Component component) {
        super(component);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    public boolean keyPressed(int p_96552_, int p_96553_, int p_96554_) {
        return true;
    }

    public void onClose() {
        this.minecraft.popGuiLayer();
        this.minecraft.player.playSound(
                net.minecraft.sounds.SoundEvents.HORSE_SADDLE,
                1.0F,
                1.0F
        );
    }


}
