package com.orbital.orbitalbackpack;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BackpackModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION =
			new ModelLayerLocation(
					ResourceLocation.fromNamespaceAndPath("modid", "backpack"),
					"main"
			);
	private final ModelPart bb_main;

	public BackpackModel(ModelPart root) {
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(14, 36).addBox(-2.0F, -7.0F, 3.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(26, 33).addBox(-5.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(36, 39).addBox(-5.5F, -7.0F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-4.0F, -7.0F, -3.0F, 8.0F, 7.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(14, 27).addBox(4.0F, -6.0F, -2.0F, 2.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(26, 21).addBox(-4.5F, -7.0F, -2.5F, 2.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(28, 7).addBox(-4.5F, -9.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 27).addBox(2.5F, -7.0F, -2.5F, 2.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(28, 14).addBox(2.5F, -9.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 21).addBox(-4.0F, -9.5F, -2.5F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(36, 33).addBox(-2.0F, -6.0F, 5.0F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 0).addBox(-3.0F, -6.0F, 3.0F, 6.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 39).addBox(-2.0F, -2.0F, 3.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(22, 40).addBox(2.0F, -7.0F, -3.1F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(40, 23).addBox(-3.0F, -7.0F, -3.1F, 1.0F, 2.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(20, 40).addBox(2.0F, -9.05F, -3.4F, 1.0F, 2.05F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(40, 21).addBox(-3.0F, -9.05F, -3.4F, 1.0F, 2.05F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(40, 26).addBox(2.0F, -9.625F, -2.6F, 1.0F, 0.575F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(40, 27).addBox(-3.0F, -9.625F, -2.6F, 1.0F, 0.575F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(40, 28).addBox(2.0F, -9.625F, 2.575F, 1.0F, 0.575F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(40, 29).addBox(-3.0F, -9.625F, 2.575F, 1.0F, 0.575F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(16, 40).addBox(2.0F, -9.05F, 3.1F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(18, 40).addBox(-3.0F, -9.05F, 3.1F, 1.0F, 3.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 13).addBox(-4.0F, -9.0F, -3.3F, 8.0F, 2.0F, 6.3F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 31).addBox(-1.0F, -1.825F, -1.0F, 1.0F, 0.525F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(40, 30).addBox(-6.0F, -1.825F, -1.0F, 1.0F, 0.525F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -8.05F, 1.275F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(14, 40).addBox(-1.0F, -6.475F, -1.0F, 1.0F, 5.175F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(12, 39).addBox(-6.0F, -6.475F, -1.0F, 1.0F, 5.175F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.0F, -8.625F, -3.9F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r3 = bb_main.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 25).addBox(-1.0F, -2.1F, -1.0F, 1.0F, 0.8F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(24, 40).addBox(4.0F, -2.1F, -1.0F, 1.0F, 0.8F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -8.05F, -4.7F, -1.5708F, 0.0F, 0.0F));

		PartDefinition cube_r4 = bb_main.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 0.3F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(4.0F, 0.0F, -1.0F, 1.0F, 0.3F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -6.0F, -3.1F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
							   int packedLight, int packedOverlay, int color) {
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}