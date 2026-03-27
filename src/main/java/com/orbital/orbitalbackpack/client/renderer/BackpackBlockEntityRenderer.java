package com.orbital.orbitalbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.ClientSetup;
import com.orbital.orbitalbackpack.BackpackModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class BackpackBlockEntityRenderer implements BlockEntityRenderer<BackpackBlockEntity> {

    private final BackpackModel model;

    public BackpackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new BackpackModel(context.bakeLayer(ClientSetup.BACKPACK_LAYER));
    }

    @Override
    public void render(BackpackBlockEntity entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ResourceLocation texture = entity.getBlockState().getBlock() instanceof
                com.orbital.orbitalbackpack.blocks.BackpackBlock bb
                ? ResourceLocation.fromNamespaceAndPath("orbitalbackpack",
                "textures/entity/" + bb.getTier().name().toLowerCase() + "_backpack.png")
                : ResourceLocation.fromNamespaceAndPath("orbitalbackpack",
                "textures/entity/leather_backpack.png");

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.scale(0.9f, 0.9f, 0.9f);

        var buffer = bufferSource.getBuffer(RenderType.entityCutout(texture));
        model.renderToBuffer(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

    }
}