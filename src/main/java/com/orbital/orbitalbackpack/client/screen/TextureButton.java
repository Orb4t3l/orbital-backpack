package com.orbital.orbitalbackpack.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextureButton extends AbstractButton {

    private final ResourceLocation texture;
    private final int texU;
    private final int texV;
    private final int texWidth;
    private final int texHeight;
    private final OnPress onPress;

    public TextureButton(int x, int y, int width, int height,
                         int texU, int texV, int texWidth, int texHeight,
                         int textureFileWidth, int textureFileHeight,
                         ResourceLocation texture, OnPress onPress) {
        super(x, y, width, height, Component.empty());
        this.texture = texture;
        this.texU = texU;
        this.texV = texV;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        onPress.onPress(this);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        guiGraphics.blit(texture, getX(), getY(), texU, texV, texWidth, texHeight, 16, 16);

        if (this.isHoveredOrFocused()) {
            guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, 0x33FFFFFF);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {}

    @FunctionalInterface
    public interface OnPress {
        void onPress(TextureButton button);
    }
}