/*
 * Copyright (c) Alya Client 2024-2025.
 *
 * This file belongs to Alya Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/AlyaClient/alya-beta.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Alya (and subsequently, its files) are all licensed under the MIT License.
 * Alya should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package works.alya.module.impl.visual.esp.BoundingBox;

import works.alya.utilities.render.ProjectionUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class BoundingBoxESP {
    private static final ProjectionUtility projectUtil = new ProjectionUtility();

    public static void render(DrawContext context, MinecraftClient mc) {
        if(mc.world == null || mc.player == null) return;

        float tickDelta = mc.getRenderTickCounter().getFixedDeltaTicks();

        for(Entity entity : mc.world.getEntities()) {
            if(!(entity instanceof PlayerEntity)) continue;
            if(entity == mc.player) continue;

            Vec3d[] corners = getInterpolatedVec3ds((PlayerEntity) entity, tickDelta);
            Vector3f[] screenCorners = new Vector3f[8];
            boolean allBehindCamera = true;

            for(int i = 0; i < 8; i++) {
                screenCorners[i] = projectUtil.worldToScreen(corners[i], mc);
                if(screenCorners[i] != null && screenCorners[i].z > 0) {
                    allBehindCamera = false;
                }
            }

            if(allBehindCamera) continue;

            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

            for(Vector3f corner : screenCorners) {
                if(corner != null && corner.z > 0) {
                    minX = Math.min(minX, corner.x);
                    minY = Math.min(minY, corner.y);
                    maxX = Math.max(maxX, corner.x);
                    maxY = Math.max(maxY, corner.y);
                }
            }

            if(minX != Float.MAX_VALUE && minY != Float.MAX_VALUE &&
                    maxX != Float.MIN_VALUE && maxY != Float.MIN_VALUE &&
                    minX >= 0 && minY >= 0 && 
                    maxX <= mc.getWindow().getScaledWidth() && maxY <= mc.getWindow().getScaledHeight()) {

                int color = 0x80FFFFFF;
                drawRect(context, (int)minX, (int)minY, (int)maxX, (int)maxY, color);
            }
        }
    }

    private static Vec3d @NotNull [] getInterpolatedVec3ds(PlayerEntity entity, float tickDelta) {
        // Get interpolated position
        double x = MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX());
        double y = MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY());
        double z = MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ());
        
        Box boundingBox = entity.getBoundingBox().offset(
            x - entity.getX(),
            y - entity.getY(), 
            z - entity.getZ()
        );

        return new Vec3d[]{
                new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ),
                new Vec3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ),
                new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ),
                new Vec3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ),
                new Vec3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ)
        };
    }

    private static void drawRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y1 + 1, color);
        context.fill(x1, y2 - 1, x2, y2, color);
        context.fill(x1, y1, x1 + 1, y2, color);
        context.fill(x2 - 1, y1, x2, y2, color);
    }
}