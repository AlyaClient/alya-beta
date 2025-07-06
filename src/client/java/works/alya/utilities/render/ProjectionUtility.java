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

package works.alya.utilities.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class ProjectionUtility {
    public Vector3f worldToScreen(Vec3d worldPos, MinecraftClient mc) {
        Camera camera = mc.gameRenderer.getCamera();

        Matrix4f modelViewMatrix = new Matrix4f();
        Matrix4f projectionMatrix = new Matrix4f();

        Vec3d cameraPos = camera.getPos();

        Vector4f pos = new Vector4f(
                (float)(worldPos.x - cameraPos.x),
                (float)(worldPos.y - cameraPos.y),
                (float)(worldPos.z - cameraPos.z),
                1.0f
        );

        modelViewMatrix.identity();
        modelViewMatrix.rotateX((float)Math.toRadians(camera.getPitch()));
        modelViewMatrix.rotateY((float)Math.toRadians(camera.getYaw() + 180));

        pos = modelViewMatrix.transform(pos);

        if (pos.z >= 0) return null;

        float fov = (float)Math.toRadians(mc.options.getFov().getValue());
        float aspectRatio = (float)mc.getWindow().getScaledWidth() / (float)mc.getWindow().getScaledHeight();
        float near = 0.05f;
        float far = 1000.0f;

        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, near, far);

        pos = projectionMatrix.transform(pos);

        if (pos.w != 0) {
            pos.x /= pos.w;
            pos.y /= pos.w;
            pos.z /= pos.w;
        }

        float screenX = (pos.x + 1.0f) * 0.5f * mc.getWindow().getScaledWidth();
        float screenY = (1.0f - pos.y) * 0.5f * mc.getWindow().getScaledHeight();

        return new Vector3f(screenX, screenY, pos.z);
    }
}
