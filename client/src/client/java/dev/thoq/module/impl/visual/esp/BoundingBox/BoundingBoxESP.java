package dev.thoq.module.impl.visual.esp.BoundingBox;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class BoundingBoxESP {
    public static void render(DrawContext context, MinecraftClient mc) {
        if(mc.world == null || mc.player == null) return;

        for(Entity entity : mc.world.getEntities()) {
            if(!(entity instanceof PlayerEntity)) continue;
            if(entity == mc.player) continue;

            Vec3d[] corners = getVec3ds((PlayerEntity) entity);
            Vector4f[] screenCorners = new Vector4f[8];
            boolean allBehindCamera = true;

            for(int i = 0; i < 8; i++) {
                screenCorners[i] = worldToScreen(corners[i], mc);
                if(screenCorners[i] != null) {
                    allBehindCamera = false;
                }
            }

            if(allBehindCamera) continue;

            float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

            for(Vector4f corner : screenCorners) {
                if(corner != null) {
                    minX = Math.min(minX, corner.x);
                    minY = Math.min(minY, corner.y);
                    maxX = Math.max(maxX, corner.x);
                    maxY = Math.max(maxY, corner.y);
                }
            }

            if(minX != Float.MAX_VALUE && minY != Float.MAX_VALUE &&
                    maxX != Float.MIN_VALUE && maxY != Float.MIN_VALUE) {

                int color = 0x80FFFFFF;
                drawRect(context, (int)minX, (int)minY, (int)maxX, (int)maxY, color);
            }
        }
    }

    private static Vec3d @NotNull [] getVec3ds(PlayerEntity entity) {
        Box boundingBox = entity.getBoundingBox();

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

    private static Vector4f worldToScreen(Vec3d worldPos, MinecraftClient mc) {
        var camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        float yaw = (float) Math.toRadians(camera.getYaw());
        float pitch = (float) Math.toRadians(camera.getPitch());

        double dx = worldPos.x - cameraPos.x;
        double dy = worldPos.y - cameraPos.y;
        double dz = worldPos.z - cameraPos.z;

        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);

        double x = dx * cosYaw + dz * sinYaw;
        double y = dy * cosPitch - (dx * sinYaw - dz * cosYaw) * sinPitch;
        double z = dy * sinPitch + (dx * sinYaw - dz * cosYaw) * cosPitch;

        if (z <= 0) return null;

        double fov = Math.toRadians(mc.options.getFov().getValue());
        double aspectRatio = (double) mc.getWindow().getScaledWidth() / mc.getWindow().getScaledHeight();
        double tanHalfFov = Math.tan(fov / 2.0);

        double screenX = x / (z * tanHalfFov * aspectRatio);
        double screenY = y / (z * tanHalfFov);

        screenX = (screenX + 1.0) * 0.5 * mc.getWindow().getScaledWidth();
        screenY = (1.0 - screenY) * 0.5 * mc.getWindow().getScaledHeight();

        return new Vector4f((float) screenX, (float) screenY, (float) z, 1.0f);
    }

    private static void drawRect(DrawContext context, int x1, int y1, int x2, int y2, int color) {
        context.fill(x1, y1, x2, y1 + 1, color);
        context.fill(x1, y2 - 1, x2, y2, color);
        context.fill(x1, y1, x1 + 1, y2, color);
        context.fill(x2 - 1, y1, x2, y2, color);
    }
}
