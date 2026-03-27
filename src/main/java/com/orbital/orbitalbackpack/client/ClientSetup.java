package com.orbital.orbitalbackpack.client;

import com.orbital.orbitalbackpack.BackpackModel;
import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.client.renderer.BackpackBlockEntityRenderer;
import com.orbital.orbitalbackpack.client.renderer.CurioBackpackRenderer;
import com.orbital.orbitalbackpack.client.screen.BackpackScreen;
import com.orbital.orbitalbackpack.client.tooltip.BackpackClientTooltipComponent;
import com.orbital.orbitalbackpack.client.tooltip.BackpackTooltipComponent;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.registries.ModBlockEntities;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

@EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    public static final KeyMapping OPEN_BACK_BACKPACK = new KeyMapping(
            "key.orbitalbackpack.open_back_backpack",
            GLFW.GLFW_KEY_B,
            "key.categories.orbitalbackpack"
    );

    public static final ModelLayerLocation BACKPACK_LAYER = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath("orbitalbackpack", "backpack"), "main");

    private ClientSetup() {}

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (BackpackTier tier : BackpackTier.values()) {
                ItemProperties.register(
                        ModItems.BACKPACKS.get(tier).get(),
                        ResourceLocation.fromNamespaceAndPath(OrbitalBackpack.MODID, "open"),
                        (stack, level, entity, seed) -> {
                            if (entity == null) return 0f;
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.player == null) return 0f;
                            return BackpackOpenTracker.isOpen(mc.player.getUUID()) ? 1f : 0f;
                        }
                );

                if (ModList.get().isLoaded("curios")) {
                    CuriosRendererRegistry.register(
                            ModItems.BACKPACKS.get(tier).get(),
                            CurioBackpackRenderer::new
                    );
                }
            }

            BlockEntityRenderers.register(ModBlockEntities.BACKPACK_BE.get(),
                    BackpackBlockEntityRenderer::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        for (BackpackTier tier : BackpackTier.values()) {
            event.register(ModMenus.MENUS.get(tier).get(), BackpackScreen::new);
        }
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BACK_BACKPACK);
    }

    @SubscribeEvent
    public static void onRegisterTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BackpackTooltipComponent.class, BackpackClientTooltipComponent::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BACKPACK_LAYER, BackpackModel::createBodyLayer);
    }
}