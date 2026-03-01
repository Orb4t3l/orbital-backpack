package com.orbital.orbitalbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class BackpackBlockEntityRenderer implements BlockEntityRenderer<BackpackBlockEntity> {

    private static final float[] FRAME_ANGLES = { 0f, 22.5f, 45f, 67.5f, 90f };
    private static final int OPEN_TICKS = 10;

    private final ModelPart body;
    private final ModelPart lid;

    public BackpackBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        LayerDefinition layer = createLayer();
        ModelPart root = layer.bakeRoot();
        this.body = root.getChild("body");
        this.lid = root.getChild("lid");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-4f, 0f, -4f, 8f, 8f, 8f),
                PartPose.offset(0f, 16f, 0f));

        root.addOrReplaceChild("lid",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4f, -3f, -4f, 8f, 3f, 8f),
                PartPose.offset(0f, 16f, 4f));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void render(BackpackBlockEntity entity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        ResourceLocation texture = entity.getBlockState().getBlock() instanceof
                com.orbital.orbitalbackpack.blocks.BackpackBlock bb
                ? getTexture(bb.getTier())
                : new ResourceLocation("orbitalbackpack", "textures/block/leather_backpack_block.png");

        float lidAngle = getLidAngle(entity, partialTick);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.scale(-1f, -1f, 1f);

        var buffer = bufferSource.getBuffer(RenderType.entityCutout(texture));

        body.render(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.pushPose();
        poseStack.translate(0f, -1f, -0.25f);
        poseStack.mulPose(Axis.XP.rotationDegrees(lidAngle));
        poseStack.translate(0f, 1f, 0.25f);
        lid.render(poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();

        poseStack.popPose();
    }

    private float getLidAngle(BackpackBlockEntity entity, float partialTick) {
        int openTick = entity.getOpenTick();
        boolean isOpen = entity.isOpen();

        float progress;
        if (isOpen) {
            progress = Math.min((openTick + partialTick) / OPEN_TICKS, 1f);
        } else {
            progress = Math.max(1f - (openTick + partialTick) / OPEN_TICKS, 0f);
        }

        int frameIndex = Math.min((int)(progress * (FRAME_ANGLES.length - 1)), FRAME_ANGLES.length - 2);
        float frameFraction = (progress * (FRAME_ANGLES.length - 1)) - frameIndex;
        return FRAME_ANGLES[frameIndex] + (FRAME_ANGLES[frameIndex + 1] - FRAME_ANGLES[frameIndex]) * frameFraction;
    }

    private ResourceLocation getTexture(com.orbital.orbitalbackpack.common.BackpackTier tier) {
        return new ResourceLocation("orbitalbackpack",
                "textures/block/" + tier.name().toLowerCase() + "_backpack_block.png");
    }
}