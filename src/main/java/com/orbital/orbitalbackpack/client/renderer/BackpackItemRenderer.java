package com.orbital.orbitalbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.orbital.orbitalbackpack.BackpackModel;
import com.orbital.orbitalbackpack.client.ClientSetup;
import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class BackpackItemRenderer extends BlockEntityWithoutLevelRenderer {

    private BackpackModel model;

    public BackpackItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    private BackpackModel getModel() {
        if (model == null) {
            model = new BackpackModel(
                    Minecraft.getInstance().getEntityModels().bakeLayer(ClientSetup.BACKPACK_LAYER));
        }
        return model;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext,
                             PoseStack poseStack, MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {
        if (!(stack.getItem() instanceof Backpack backpack)) return;

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("orbitalbackpack",
                "textures/entity/" + backpack.getTier().name().toLowerCase() + "_backpack.png");

        poseStack.pushPose();
        try {
            poseStack.translate(0.45, -0.9, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(75));
//            poseStack.mulPose(Axis.XP.rotationDegrees(-25f));  // tilt to show top
            poseStack.scale(1.15f, 1.15f, 1.15f);

            var vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));
            getModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        } finally {
            poseStack.popPose();
        }
    }
}