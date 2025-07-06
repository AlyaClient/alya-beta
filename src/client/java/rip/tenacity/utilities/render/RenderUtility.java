/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity.utilities.render;

import me.x150.renderer.render.ExtendedDrawContext;
import me.x150.renderer.util.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;

@SuppressWarnings("unused")
public class RenderUtility {

    private static final MinecraftClient MC = MinecraftClient.getInstance();

    /**
     * Represents the four corners of a rectangle.
     * <p>
     * This enum is used to specify which corner of a rectangle is being targeted
     * for rendering or calculation tasks in graphical operations. The enumeration
     * includes:
     * - TOP_LEFT: The top-left corner of a rectangle.
     * - TOP_RIGHT: The top-right corner of a rectangle.
     * - BOTTOM_RIGHT: The bottom-right corner of a rectangle.
     * - BOTTOM_LEFT: The bottom-left corner of a rectangle.
     * <p>
     * It is typically utilized in graphical rendering methods to define or modify
     * specific corners of a rounded rectangle, such as drawing filled or outlined
     * corners, calculating pixel coverage, or rendering individual corner details.
     */
    private enum CornerType {
        TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT
    }

    /**
     * Draws a filled rounded rectangle with the specified position, dimensions, corner radius, and color.
     *
     * @param context The {@code DrawContext} used for rendering the rounded rectangle.
     * @param x       The x-coordinate of the top-left corner of the rectangle.
     * @param y       The y-coordinate of the top-left corner of the rectangle.
     * @param width   The width of the rectangle.
     * @param height  The height of the rectangle.
     * @param radius  The radius of the rounded corners. The value is clamped to half the smaller of the rectangle's width or height.
     * @param color   The fill color of the rounded rectangle, specified as an ARGB integer.
     */
    public static void drawRoundedRect(
            DrawContext context,
            int x,
            int y,
            int width,
            int height,
            Vector4f radius,
            int color
    ) {

        ExtendedDrawContext.drawRoundedRect(
                context,
                x,
                y,
                width,
                height,
                radius,
                new Color(ColorUtility.getColorFromInt(color))
        );
    }

    /**
     * Draws the outline of a rounded rectangle with the specified dimensions, border thickness, and color.
     *
     * @param context   The {@code DrawContext} used for rendering the rounded rectangle outline.
     * @param x         The x-coordinate of the top-left corner of the rectangle.
     * @param y         The y-coordinate of the top-left corner of the rectangle.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     * @param radius    The radius of the rounded corners. The value is clamped to half the smaller of the rectangle's width or height.
     * @param lineWidth The thickness of the outline.
     * @param color     The color of the outline, specified as an ARGB integer.
     */
    public static void drawRoundedRectOutline(DrawContext context, float x, float y, float width, float height, float radius, float lineWidth, int color) {
        int ix = (int) x;
        int iy = (int) y;
        int iWidth = (int) width;
        int iHeight = (int) height;
        int iRadius = Math.min((int) radius, Math.min(iWidth, iHeight) / 2);
        int iLineWidth = Math.max(1, (int) lineWidth);

        if(iRadius <= 0) {
            drawRectOutline(context, ix, iy, iWidth, iHeight, iLineWidth, color);
            return;
        }

        context.fill(ix + iRadius, iy, ix + iWidth - iRadius, iy + iLineWidth, color);
        context.fill(ix + iRadius, iy + iHeight - iLineWidth, ix + iWidth - iRadius, iy + iHeight, color);
        context.fill(ix, iy + iRadius, ix + iLineWidth, iy + iHeight - iRadius, color);
        context.fill(ix + iWidth - iLineWidth, iy + iRadius, ix + iWidth, iy + iHeight - iRadius, color);

        drawCornerOutline(context, ix, iy, iRadius, iLineWidth, color, CornerType.TOP_LEFT);
        drawCornerOutline(context, ix + iWidth - iRadius, iy, iRadius, iLineWidth, color, CornerType.TOP_RIGHT);
        drawCornerOutline(context, ix + iWidth - iRadius, iy + iHeight - iRadius, iRadius, iLineWidth, color, CornerType.BOTTOM_RIGHT);
        drawCornerOutline(context, ix, iy + iHeight - iRadius, iRadius, iLineWidth, color, CornerType.BOTTOM_LEFT);
    }

    /**
     * Draws a corner of a rounded rectangle with specified dimensions, position, and color.
     *
     * @param context The {@code DrawContext} used for rendering the corner.
     * @param x       The x-coordinate of the top-left corner of the corner's bounding box.
     * @param y       The y-coordinate of the top-left corner of the corner's bounding box.
     * @param radius  The radius of the circular boundary defining the corner.
     * @param color   The ARGB color of the corner, specified as an integer.
     * @param corner  The {@code CornerType} specifying which corner is being drawn (e.g., TOP_LEFT, TOP_RIGHT).
     */
    private static void drawCorner(DrawContext context, int x, int y, int radius, int color, CornerType corner) {
        int baseAlpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        for(int py = 0; py < radius; py++) {
            for(int px = 0; px < radius; px++) {
                float coverage = getPixelCoverage(px, py, radius, corner);

                if(coverage > 0) {
                    int alpha = (int) (baseAlpha * coverage);
                    if(alpha > 0) {
                        int pixelColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

                        int drawX = x + (corner == CornerType.TOP_RIGHT || corner == CornerType.BOTTOM_RIGHT ? radius - px - 1 : px);
                        int drawY = y + (corner == CornerType.BOTTOM_LEFT || corner == CornerType.BOTTOM_RIGHT ? radius - py - 1 : py);

                        context.fill(drawX, drawY, drawX + 1, drawY + 1, pixelColor);
                    }
                }
            }
        }
    }

    /**
     * Draws the outline of a specified corner of a rounded rectangle with the given radius,
     * border thickness, and color.
     *
     * @param context   The {@code DrawContext} used for rendering the corner outline.
     * @param x         The x-coordinate of the top-left corner of the bounding box for the corner.
     * @param y         The y-coordinate of the top-left corner of the bounding box for the corner.
     * @param radius    The radius of the circular boundary defining the corner.
     * @param lineWidth The thickness of the outline, specified as an integer.
     * @param color     The ARGB color of the outline, represented as an integer.
     * @param corner    The {@code CornerType} representing the specific corner (e.g., TOP_LEFT, TOP_RIGHT, etc.).
     */
    private static void drawCornerOutline(DrawContext context, int x, int y, int radius, int lineWidth, int color, CornerType corner) {
        int baseAlpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        for(int py = 0; py < radius; py++) {
            for(int px = 0; px < radius; px++) {
                float outerCoverage = getPixelCoverage(px, py, radius, corner);
                float innerCoverage = getPixelCoverage(px, py, radius - lineWidth, corner);
                float coverage = Math.max(0, outerCoverage - innerCoverage);

                if(coverage > 0) {
                    int alpha = (int) (baseAlpha * coverage);
                    if(alpha > 0) {
                        int pixelColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

                        int drawX = x + (corner == CornerType.TOP_RIGHT || corner == CornerType.BOTTOM_RIGHT ? radius - px - 1 : px);
                        int drawY = y + (corner == CornerType.BOTTOM_LEFT || corner == CornerType.BOTTOM_RIGHT ? radius - py - 1 : py);

                        context.fill(drawX, drawY, drawX + 1, drawY + 1, pixelColor);
                    }
                }
            }
        }
    }

    /**
     * Calculates the pixel coverage of a circular region for a given position and radius.
     * The method uses supersampling with a 4x4 grid to determine the proportion of the pixel
     * that is covered by the circle.
     *
     * @param px     The x-coordinate of the pixel.
     * @param py     The y-coordinate of the pixel.
     * @param radius The radius of the circular region.
     * @param corner The {@code CornerType} specifying which corner of the rounded rectangle is being considered.
     * @return The fractional coverage of the pixel by the circle as a float value between 0.0 and 1.0.
     */
    private static float getPixelCoverage(int px, int py, float radius, CornerType corner) {
        if(radius <= 0) return 0;

        float centerX = radius - 0.5f;
        float centerY = radius - 0.5f;

        int samples = 0;
        int inside = 0;

        for(int sx = 0; sx < 4; sx++) {
            for(int sy = 0; sy < 4; sy++) {
                float sampleX = px + (sx + 0.5f) / 4.0f;
                float sampleY = py + (sy + 0.5f) / 4.0f;

                float dx = sampleX - centerX;
                float dy = sampleY - centerY;
                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if(dist <= radius) {
                    inside++;
                }
                samples++;
            }
        }

        return (float) inside / samples;
    }

    /**
     * Draws the outline of a rectangle at the specified position with the given dimensions,
     * line thickness, and color.
     *
     * @param context   The {@code DrawContext} used for rendering the rectangle outline.
     * @param x         The x-coordinate of the top-left corner of the rectangle.
     * @param y         The y-coordinate of the top-left corner of the rectangle.
     * @param width     The width of the rectangle.
     * @param height    The height of the rectangle.
     * @param lineWidth The thickness of the rectangle's outline, specified as an integer.
     * @param color     The color of the outline, specified as an ARGB integer.
     */
    private static void drawRectOutline(DrawContext context, int x, int y, int width, int height, int lineWidth, int color) {
        context.fill(x, y, x + width, y + lineWidth, color);
        context.fill(x, y + height - lineWidth, x + width, y + height, color);
        context.fill(x, y, x + lineWidth, y + height, color);
        context.fill(x + width - lineWidth, y, x + width, y + height, color);
    }

    /**
     * Draws an image onto the screen with specified position, dimensions, and scaling.
     *
     * @param texture      The {@code Identifier} representing the texture to draw.
     * @param posX         The x-coordinate of the top-left corner where the image will be rendered.
     * @param posY         The y-coordinate of the top-left corner where the image will be rendered.
     * @param renderWidth  The width of the area where the image will be rendered on screen.
     * @param renderHeight The height of the area where the image will be rendered on screen.
     * @param imageWidth   The actual width of the source image or texture.
     * @param imageHeight  The actual height of the source image or texture.
     * @param context      The {@code DrawContext} used for rendering the image.
     */
    public static void drawImage(
            Identifier texture,
            int posX,
            int posY,
            int renderWidth,
            int renderHeight,
            int imageWidth,
            int imageHeight,
            DrawContext context
    ) {
        context.drawTexture(
                RenderPipelines.GUI_TEXTURED, // Should've looked harder. shut up rare
                texture,
                posX, posY,
                0, 0,
                renderWidth, renderHeight,
                imageWidth, imageHeight
        );
    }

    public static void drawRoundedRect(DrawContext context, int x, int y, int width, int height, Vector4f radius) {
        drawRoundedRect(context, x, y, width, height, radius, 0xFFFFFFFF);
    }

    public static void drawRoundedRectOutline(DrawContext context, float x, float y, float width, float height, float radius, float lineWidth) {
        drawRoundedRectOutline(context, x, y, width, height, radius, lineWidth, 0xFFFFFFFF);
    }

    public static void drawGradientRoundedRect(DrawContext context, int x, int y, int width, int height, Vector4f radius, int colorLeft, int colorRight) {
        int cornerSize = (int) Math.max(Math.max(radius.x, radius.y), Math.max(radius.z, radius.w));
        int segments = Math.max(width / 2, 1);

        for(int i = 0; i < segments; i++) {
            int segmentStartX = x + (i * width) / segments;
            int segmentEndX = x + ((i + 1) * width) / segments;
            int segmentWidth = segmentEndX - segmentStartX;

            float factor = segments > 1 ? (float) i / (segments - 1) : 0.0f;
            int interpolatedColor = ColorUtility.interpolateColor(colorLeft, colorRight, factor);

            context.fill(segmentStartX, y + (int) Math.max(radius.x, radius.y),
                    segmentEndX, y + height - (int) Math.max(radius.z, radius.w), interpolatedColor);
        }

        for(int i = 0; i < segments; i++) {
            int segmentStartX = x + (i * width) / segments;
            int segmentEndX = x + ((i + 1) * width) / segments;

            float factor = segments > 1 ? (float) i / (segments - 1) : 0.0f;
            int interpolatedColor = ColorUtility.interpolateColor(colorLeft, colorRight, factor);

            int topStartX = Math.max(segmentStartX, x + (int) radius.x);
            int topEndX = Math.min(segmentEndX, x + width - (int) radius.y);
            if(topStartX < topEndX) {
                context.fill(topStartX, y, topEndX, y + (int) Math.max(radius.x, radius.y), interpolatedColor);
            }

            int bottomStartX = Math.max(segmentStartX, x + (int) radius.w);
            int bottomEndX = Math.min(segmentEndX, x + width - (int) radius.z);
            if(bottomStartX < bottomEndX) {
                context.fill(bottomStartX, y + height - (int) Math.max(radius.z, radius.w),
                        bottomEndX, y + height, interpolatedColor);
            }
        }

        drawGradientCorner(context, x, y, (int) radius.x, colorLeft, CornerType.TOP_LEFT);
        drawGradientCorner(context, x + width - (int) radius.y, y, (int) radius.y, colorRight, CornerType.TOP_RIGHT);
        drawGradientCorner(context, x + width - (int) radius.z, y + height - (int) radius.z, (int) radius.z, colorRight, CornerType.BOTTOM_RIGHT);
        drawGradientCorner(context, x, y + height - (int) radius.w, (int) radius.w, colorLeft, CornerType.BOTTOM_LEFT);
    }

    /**
     * Draws a single corner with anti-aliasing and the specified color.
     * This method uses the same anti-aliasing technique as your existing drawCorner method.
     */
    private static void drawGradientCorner(DrawContext context, int x, int y, int radius, int color, CornerType corner) {
        if(radius <= 0) return;

        int baseAlpha = (color >> 24) & 0xFF;
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;

        for(int py = 0; py < radius; py++) {
            for(int px = 0; px < radius; px++) {
                float coverage = getPixelCoverage(px, py, radius, corner);

                if(coverage > 0) {
                    int alpha = (int) (baseAlpha * coverage);
                    if(alpha > 0) {
                        int pixelColor = (alpha << 24) | (red << 16) | (green << 8) | blue;

                        int drawX = x + (corner == CornerType.TOP_RIGHT || corner == CornerType.BOTTOM_RIGHT ? radius - px - 1 : px);
                        int drawY = y + (corner == CornerType.BOTTOM_LEFT || corner == CornerType.BOTTOM_RIGHT ? radius - py - 1 : py);

                        context.fill(drawX, drawY, drawX + 1, drawY + 1, pixelColor);
                    }
                }
            }
        }
    }

    public static void drawRect(DrawContext context, float x, float y, float width, float height, int color) {
        context.fill((int) x, (int) y, (int) (x + width), (int) (y + height), color);
    }

    /**
     * Draws a line between two points with specified thickness and color.
     *
     * @param context   The {@code DrawContext} used for rendering the line.
     * @param x         The x-coordinate of the starting point of the line.
     * @param y         The y-coordinate of the starting point of the line.
     * @param toX       The*/
    public static void drawLine(
            DrawContext context,
            int x,
            int y,
            int toX,
            int toY,
            float thickness,
            int color
    ) {
        ExtendedDrawContext.drawLine(
                context,
                x,
                y,
                toX,
                toY,
                thickness,
                new Color(color)
        );
    }
}