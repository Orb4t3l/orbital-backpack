package com.orbital.orbitalbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.items.Backpack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CurioBackpackRenderer implements ICurioRenderer {

    private ModelPart body;
    private ModelPart lid;
    private boolean initialized = false;

    private void init() {
        if (initialized) return;
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 8)
                        .addBox(-4f, -8f, -4f, 8f, 8f, 8f),
                PartPose.ZERO);

        root.addOrReplaceChild("lid",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4f, -3f, -4f, 8f, 3f, 8f),
                PartPose.offset(0f, -8f, 4f));

        LayerDefinition layer = LayerDefinition.create(mesh, 64, 32);
        ModelPart baked = layer.bakeRoot();
        this.body = baked.getChild("body");
        this.lid = baked.getChild("lid");
        this.initialized = true;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource renderTypeBuffer,
            int light,
            float limbSwing,
            float limbSwingAmount,
            float partialTicks,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        init();

        if (!(stack.getItem() instanceof Backpack backpack)) return;

        BackpackTier tier = backpack.getTier();
        ResourceLocation texture = new ResourceLocation("orbitalbackpack",
                "textures/block/" + tier.name().toLowerCase() + "_backpack_block.png");

        poseStack.pushPose();

        poseStack.translate(0.0, 0.0, 0.2);
        poseStack.mulPose(Axis.XP.rotationDegrees(180f));
        poseStack.scale(0.55f, 0.55f, 0.55f);
        poseStack.translate(0.0, -0.5, -0.85);

        var buffer = renderTypeBuffer.getBuffer(RenderType.entityCutout(texture));
        body.render(poseStack, buffer, light, OverlayTexture.NO_OVERLAY);
        lid.render(poseStack, buffer, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}