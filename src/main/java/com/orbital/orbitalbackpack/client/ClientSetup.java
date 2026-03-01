package com.orbital.orbitalbackpack.client;

import com.orbital.orbitalbackpack.OrbitalBackpack;
import com.orbital.orbitalbackpack.blocks.BackpackBlockEntity;
import com.orbital.orbitalbackpack.client.renderer.BackpackBlockEntityRenderer;
import com.orbital.orbitalbackpack.client.screen.BackpackScreen;
import com.orbital.orbitalbackpack.common.BackpackTier;
import com.orbital.orbitalbackpack.items.Backpack;
import com.orbital.orbitalbackpack.registries.ModBlockEntities;
import com.orbital.orbitalbackpack.registries.ModItems;
import com.orbital.orbitalbackpack.registries.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import com.orbital.orbitalbackpack.client.tooltip.BackpackClientTooltipComponent;
import com.orbital.orbitalbackpack.client.tooltip.BackpackTooltipComponent;

@Mod.EventBusSubscriber(modid = OrbitalBackpack.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientSetup {

    private ClientSetup() {}

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (BackpackTier tier : BackpackTier.values()) {
                MenuScreens.register(ModMenus.MENUS.get(tier).get(), BackpackScreen::new);

                ItemProperties.register(
                        ModItems.BACKPACKS.get(tier).get(),
                        new ResourceLocation(OrbitalBackpack.MODID, "open"),
                        (stack, level, entity, seed) -> {
                            if (entity == null) return 0f;
                            Minecraft mc = Minecraft.getInstance();
                            if (mc.player == null) return 0f;
                            return BackpackOpenTracker.isOpen(mc.player.getUUID()) ? 1f : 0f;
                        }
                );
            }

            BlockEntityRenderers.register(ModBlockEntities.BACKPACK_BE.get(), BackpackBlockEntityRenderer::new);
        });
    }

    @SubscribeEvent
    public static void onRegisterTooltips(final RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(BackpackTooltipComponent.class, BackpackClientTooltipComponent::new);
    }
}