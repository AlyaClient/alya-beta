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

package works.alya.utilities.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import works.alya.AlyaClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

import static org.lwjgl.system.MemoryUtil.memAlloc;

@Environment(EnvType.CLIENT)
public class IconLoader {

    /**
     * Sets a custom icon for a GLFW window using specified image resources.
     *
     * @param windowHandle The handle of the GLFW window to which the custom icon will be applied.
     */
    public static void setWindowIcon(long windowHandle) {
        try(InputStream icon16 = Objects.requireNonNull(IconLoader.class.getResourceAsStream("/assets/alya/icons/icon16.png"));
            InputStream icon32 = Objects.requireNonNull(IconLoader.class.getResourceAsStream("/assets/alya/icons/icon32.png"))) {

            BufferedImage image16 = ImageIO.read(icon16);
            BufferedImage image32 = ImageIO.read(icon32);

            ByteBuffer icon16Buffer = convertImageToByteBuffer(image16);
            ByteBuffer icon32Buffer = convertImageToByteBuffer(image32);

            GLFWImage.Buffer iconImages = GLFWImage.malloc(2);
            iconImages.position(0).width(16).height(16).pixels(icon16Buffer);
            iconImages.position(1).width(32).height(32).pixels(icon32Buffer);
            iconImages.position(0);

            GLFW.glfwSetWindowIcon(windowHandle, iconImages);
            iconImages.free();
        } catch(IOException e) {
            AlyaClient.LOGGER.error("Failed to load custom icon: {}", e.getMessage());
        } catch(NullPointerException e) {
            AlyaClient.LOGGER.error("Icon not found or path incorrect: {}", e.getMessage());
        }
    }

    /**
     * Converts a BufferedImage into a ByteBuffer, encoding its pixel data in RGBA format.
     *
     * @param image The BufferedImage to be converted into a ByteBuffer. The image's width, height,
     *              and pixel data are used to perform the conversion.
     * @return A ByteBuffer containing the RGBA representation of the image's pixel data.
     */
    private static ByteBuffer convertImageToByteBuffer(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        ByteBuffer buffer = memAlloc(width * height * 4);
        for(int pixel : pixels) {
            buffer.put((byte) ((pixel >> 16) & 0xFF)); // Red
            buffer.put((byte) ((pixel >> 8) & 0xFF));  // Green
            buffer.put((byte) (pixel & 0xFF));         // Blue
            buffer.put((byte) ((pixel >> 24) & 0xFF)); // Alpha
        }
        buffer.flip();
        return buffer;
    }
}
