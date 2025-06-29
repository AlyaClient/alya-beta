/*
 * Copyright (c) Rye Client 2025-2025.
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
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import me.x150.renderer.fontng.FTLibrary;
import me.x150.renderer.fontng.FontScalingRegistry;
import me.x150.renderer.fontng.GlyphBuffer;
import me.x150.renderer.fontng.Font;
import me.x150.renderer.render.ExtendedDrawContext;
import me.x150.renderer.util.Color;
import me.x150.renderer.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector4f;

import java.util.Date;

@SuppressWarnings("FieldCanBeLocal")
public class HUDModule extends Module {
    private static boolean inited = false;
    private static Font font, emojiFont;
    private static GlyphBuffer gb;

    public HUDModule() {
        super("HUD", "Shows Heads Up Display", ModuleCategory.VISUAL);

        this.setEnabled(true);
    }

    private enum Mode {
        NORMAL,
        SCAFFOLD,
        SPEED,
        FLIGHT,
        KILLAURA
    }

    private final IEventListener<Render2DEvent> renderEvent = event -> {
        boolean killauraEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Killaura").isEnabled();
        boolean scaffoldEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Scaffold").isEnabled();
        boolean speedEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Speed").isEnabled();
        boolean flightEnabled = RyeClient.INSTANCE.getModuleRepository().getModuleByName("Flight").isEnabled();

        Mode mode = Mode.NORMAL;

        if(killauraEnabled) mode = Mode.KILLAURA;
        if(scaffoldEnabled) mode = Mode.SCAFFOLD;
        if(speedEnabled) mode = Mode.SPEED;
        if(flightEnabled) mode = Mode.FLIGHT;

        String displayText = getString(mode);

        final int padding = 15;
        final int radius = 12;
        final int screenWidth = event.getContext().getScaledWindowWidth();
        final int xPosition = (screenWidth / 2) - (TextRendererUtility.getTextWidth(displayText) / 2);
        final int yPosition = 2;
        final int backgroundColor = ColorUtility.getColor(ColorUtility.Colors.PANEL);
        final int textWidth = TextRendererUtility.getTextWidth(displayText);
        final int textHeight = mc.textRenderer.fontHeight;

        ExtendedDrawContext.drawRoundedRect(
                event.getContext(),
                xPosition,
                yPosition,
                textWidth + padding,
                textHeight + padding,
                new Vector4f(radius, radius, radius, radius),
                new Color(backgroundColor)
        );

        if (!inited) {
            inited = true;
            FTLibrary ftl = new FTLibrary();

            font = new Font(ftl, "C:\\Users\\trist\\IdeaProjects\\rye-v1\\src\\main\\resources\\assets\\rye\\fonts\\Figtree-Regular.ttf", 0, 20);

            emojiFont = new Font(ftl, "C:\\Users\\trist\\IdeaProjects\\rye-v1\\src\\main\\resources\\assets\\rye\\fonts\\Figtree-Regular.ttf", 0, 20);

            FontScalingRegistry.register(font, emojiFont);

            gb = new GlyphBuffer();
        }

        gb.clear();

        gb.addString(font, "penis", xPosition, yPosition);

        gb.offsetToTopLeft();

        gb.draw(event.getContext(), xPosition, yPosition);
    };

    private static @NotNull String getString(Mode hudMode) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null) throw new IllegalStateException("Minecraft player is null!");

        String name = "§l" + "§d" + RyeClient.getName().charAt(0) + "§r§l" + RyeClient.getName().substring(1) + "§r";
        String time = new java.text.SimpleDateFormat("hh:mm a").format(new Date());

        String bps = RyeClient.getBps();
        String fps = RyeClient.getFps();

        int scaffoldSlot = mc.player.getInventory().getSelectedSlot();
        int blocksRemaining = mc.player.getInventory().getStack(scaffoldSlot).getCount();

        switch(hudMode) {
            case NORMAL -> {
                String userName = mc.player.getName().getString();
                return String.format(
                        " %s | %s f/s | %s | %s",
                        name,
                        fps,
                        userName,
                        time
                );
            }

            case SPEED, FLIGHT -> {
                return String.format(
                        " %s | %s f/s | %s b/s | %s",
                        name,
                        fps,
                        bps,
                        time
                );
            }

            case SCAFFOLD -> {
                return String.format(
                        " %s | %s f/s | %s Remaining | %s b/s",
                        name,
                        fps,
                        blocksRemaining,
                        bps
                );
            }

            case KILLAURA -> {
                return String.format(
                        " %s | %s f/s | %s | %s b/s",
                        name,
                        fps,
                        String.format("%s/%s ♡", mc.player.getHealth() / 2, mc.player.getMaxHealth() / 2),
                        bps
                );
            }
        }

        return "error";
    }
}
