/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq.module.impl.visual;

import dev.thoq.RyeClient;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.Render2DEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.impl.player.NukerModule;
import dev.thoq.module.impl.player.ReachModule;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.RenderUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

@SuppressWarnings({"FieldCanBeLocal", "SameParameterValue", "unused"})
public class HUDModule extends Module {

    private long animationStartTime = 0;
    private boolean isAnimatingIn = false;
    private boolean isAnimatingOut = false;
    private boolean wasVisible = false;
    private static final long ANIMATION_DURATION = 200;
    private static final int END_Y_POSITION = 30;
    private static String lastDynamicText = "";

    public HUDModule() {
        super("HUD", "Shows Heads Up Display", ModuleCategory.VISUAL);

        this.setEnabled(true);
    }

    private enum Mode {
        NORMAL,
        SCAFFOLD,
        SPEED,
        FLIGHT,
        KILLAURA,
        NUKER,
        REACH,
    }

    private final IEventListener<Render2DEvent> renderEvent = event -> {
        if(mc.player == null) return;

        boolean killauraEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Killaura").isEnabled();
        boolean scaffoldEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Scaffold").isEnabled();
        boolean speedEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Speed").isEnabled();
        boolean flightEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Flight").isEnabled();
        boolean nukerEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Nuker").isEnabled();
        boolean reachEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Reach").isEnabled();

        Mode mode = Mode.NORMAL;

        if(flightEnabled) mode = Mode.FLIGHT;
        if(speedEnabled) mode = Mode.SPEED;
        if(scaffoldEnabled) mode = Mode.SCAFFOLD;
        if(killauraEnabled) mode = Mode.KILLAURA;
        if(nukerEnabled) mode = Mode.NUKER;
        if(reachEnabled) mode = Mode.REACH;

        String time = RyeClient.getTime();
        String fps = RyeClient.getFps();
        String bps = RyeClient.getBps();
        String name = mc.player.getName().getString();
        String clientName = "§l" + "§d" + RyeClient.getName().charAt(0) + "§r§l" + RyeClient.getName().substring(1) + "§r";

        boolean shouldShowDynamic = mode != Mode.NORMAL;

        if(shouldShowDynamic && !wasVisible) {
            startShowAnimation();
        } else if(!shouldShowDynamic && wasVisible) {
            startHideAnimation();
        }

        wasVisible = shouldShowDynamic;

        if(shouldShowDynamic || isAnimatingOut) {
            String dynamicText = getString(mode, bps);
            drawAnimatedDynamicRect(
                    dynamicText,
                    event
            );
        }

        String displayText = String.format(
                " %s | %s f/s | %s | %s",
                clientName,
                fps,
                name,
                time
        );

        final int padding = 15;
        final int textWidth = TextRendererUtility.getTextWidth(displayText);
        final int textHeight = mc.textRenderer.fontHeight;
        final int xPosition = 2;
        final int yPosition = 2;
        final int backgroundColor = ColorUtility.getColor(ColorUtility.Colors.PANEL);

        RenderUtility.drawRect(
                event.getContext(),
                xPosition,
                yPosition,
                textWidth + padding,
                textHeight + padding,
                backgroundColor
        );

        TextRendererUtility.renderText(
                event.getContext(),
                displayText,
                ColorUtility.Colors.WHITE,
                xPosition + padding / 2,
                yPosition + padding / 2,
                true
        );
    };

    private void startShowAnimation() {
        animationStartTime = System.currentTimeMillis();
        isAnimatingIn = true;
        isAnimatingOut = false;
    }

    private void startHideAnimation() {
        animationStartTime = System.currentTimeMillis();
        isAnimatingIn = false;
        isAnimatingOut = true;
    }

    private float getAnimationProgress() {
        long elapsed = System.currentTimeMillis() - animationStartTime;
        float progress = Math.min(elapsed / (float) ANIMATION_DURATION, 1.0f);

        return 1.0f - (float) Math.pow(1.0f - progress, 3);
    }

    private void drawAnimatedDynamicRect(
            String displayText,
            Render2DEvent event
    ) {
        if(mc.player == null) return;

        float progress = getAnimationProgress();

        if(progress >= 1.0f) {
            if(isAnimatingOut) {
                isAnimatingOut = false;
                return;
            }
            if(isAnimatingIn) {
                isAnimatingIn = false;
            }
        }

        float animatedProgress;
        if(isAnimatingIn) {
            animatedProgress = progress;
        } else if(isAnimatingOut) {
            animatedProgress = 1.0f - progress;
        } else {
            animatedProgress = 1.0f;
        }

        final int hudPadding = 15;
        final int hudTextWidth = TextRendererUtility.getTextWidth(String.format(
                " %s | %s f/s | %s | %s",
                "§l" + "§d" + RyeClient.getName().charAt(0) + "§r§l" + RyeClient.getName().substring(1) + "§r",
                RyeClient.getFps(),
                mc.player.getName().getString(),
                new java.text.SimpleDateFormat("hh:mm a").format(new Date())
        ));

        final int hudWidth = hudTextWidth + hudPadding;
        final int dynamicPadding = 15;
        final int dynamicTextWidth = TextRendererUtility.getTextWidth(displayText);
        final int dynamicWidth = dynamicTextWidth + dynamicPadding;
        final int centeredX = 2 + (hudWidth - dynamicWidth) / 2;
        final int hudBottomY = 2 + mc.textRenderer.fontHeight + hudPadding;
        final int startY = hudBottomY - 10;
        final int animatedY = (int) (startY + (END_Y_POSITION - startY) * animatedProgress);

        int alpha = (int) (255 * animatedProgress);
        int baseBackgroundColor = ColorUtility.getColor(ColorUtility.Colors.PANEL);
        int animatedBackgroundColor = applyAlpha(baseBackgroundColor, alpha);

        drawDynamicRect(
                displayText,
                event,
                centeredX,
                animatedY,
                dynamicPadding,
                animatedBackgroundColor,
                alpha
        );
    }

    private int applyAlpha(int color, int alpha) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int originalAlpha = (color >> 24) & 0xFF;
        
        int combinedAlpha = (originalAlpha * alpha) / 255;
        
        return (combinedAlpha << 24) | (r << 16) | (g << 8) | b;
    }

    private static void drawDynamicRect(
            String displayText,
            Render2DEvent event,
            int xPosition,
            int yPosition,
            int padding,
            int backgroundColor,
            int textAlpha
    ) {
        int textWidth = TextRendererUtility.getTextWidth(displayText);
        int textHeight = TextRendererUtility.getTextHeight();

        RenderUtility.drawRect(
                event.getContext(),
                xPosition,
                yPosition,
                textWidth + padding,
                textHeight + padding,
                backgroundColor
        );

        int textColor = ColorUtility.getColor(ColorUtility.Colors.WHITE);
        int animatedTextColor = (textAlpha << 24) | (textColor & 0x00FFFFFF);

        TextRendererUtility.renderText(
                event.getContext(),
                displayText,
                animatedTextColor,
                xPosition + padding / 2,
                yPosition + padding / 2,
                true
        );
    }

    private static @NotNull String getString(
            Mode hudMode,
            String bps
    ) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null) throw new IllegalStateException("Minecraft player is null!");

        switch(hudMode) {
            case SPEED:
            case FLIGHT:
                String speedFlightMsg = String.format("Going %s b/s", RyeClient.getBps());
                lastDynamicText = speedFlightMsg;

                return speedFlightMsg;

            case SCAFFOLD:
                int scaffoldSlot = mc.player.getInventory().getSelectedSlot();
                int blocksRemaining = mc.player.getInventory().getStack(scaffoldSlot).getCount();
                String scaffoldMsg = String.format("%s Remaining | Going %s b/s", blocksRemaining, RyeClient.getBps());

                lastDynamicText = scaffoldMsg;

                return scaffoldMsg;

            case KILLAURA:
                String killauraMsg = String.format("%s | Going %s b/s",
                        String.format("%.1f/%.1f ♡", mc.player.getHealth() / 2, mc.player.getMaxHealth() / 2),
                        bps);

                lastDynamicText = killauraMsg;

                return killauraMsg;

            case NUKER:
                String nukerMsg = String.format("%s blocks destroyed", NukerModule.getBlocksDestroyed());

                lastDynamicText = nukerMsg;

                return nukerMsg;

            case REACH:
                String reachMsg = String.format("Reached %.1f blocks", ReachModule.getLastReach());

                lastDynamicText = reachMsg;

                return reachMsg;

            case NORMAL:
            default:
                if(!lastDynamicText.isEmpty()) return lastDynamicText;
                else return "Dynamic HUD Encountered an error!";
        }
    }
}