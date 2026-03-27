package com.orbital.orbitalbackpack.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.orbital.orbitalbackpack.BackpackModel;
import com.orbital.orbitalbackpack.client.ClientSetup;
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

    private BackpackModel model;

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource buffer,
            int light, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {

        if (model == null) {
            var mc = net.minecraft.client.Minecraft.getInstance();
            model = new BackpackModel(mc.getEntityModels().bakeLayer(ClientSetup.BACKPACK_LAYER));
        }

        if (!(stack.getItem() instanceof Backpack backpack)) return;

        ResourceLocation texture = ResourceLocation.fromNamespaceAndPath("orbitalbackpack",
                "textures/entity/" + backpack.getTier().name().toLowerCase() + "_backpack.png");

        poseStack.pushPose();
        poseStack.translate(0.0, 0.0, 0.2);
        poseStack.mulPose(Axis.YP.rotationDegrees(180f));
        poseStack.scale(0.55f, 0.55f, 0.55f);
        poseStack.translate(0.0, -1.5, 0.0);

        var vertexConsumer = buffer.getBuffer(RenderType.entityCutout(texture));
        model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}
