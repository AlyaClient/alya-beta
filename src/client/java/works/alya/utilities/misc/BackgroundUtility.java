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

import works.alya.utilities.render.ColorUtility;
import works.alya.utilities.render.RenderUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

import java.util.Random;

public class BackgroundUtility {
    private static final int MAX_STARS = 200;
    private static final Star[] stars = new Star[MAX_STARS];
    private static final Random random = new Random();
    private static boolean initialized = false;
    private static int lastWidth = 0;
    private static int lastHeight = 0;

    private static final Identifier KITTY_TEXTURE = Identifier.of("alya", "images/kitty.png");
    private static final FloatingKitty kitty = new FloatingKitty();

    private static class Star {
        float x, y;
        float alpha;
        float fadeSpeed;
        boolean fadingIn;
        int size;

        Star(float x, float y) {
            this.x = x;
            this.y = y;
            this.alpha = 0.0f;
            this.fadeSpeed = 0.01f + random.nextFloat() * 0.02f;
            this.fadingIn = true;
            this.size = 1;
        }

        void update() {
            if(fadingIn) {
                alpha += fadeSpeed;
                if(alpha >= 1.0f) {
                    alpha = 1.0f;
                    fadingIn = false;
                }
            } else {
                alpha -= fadeSpeed;
                if(alpha <= 0.0f) {
                    alpha = 0.0f;
                    fadingIn = true;
                }
            }
        }

        void render(DrawContext context) {
            if(alpha > 0.0f) {
                int whiteColor = 0xFFFFFF;
                int alphaValue = (int) (alpha * 255);
                int colorWithAlpha = (alphaValue << 24) | whiteColor;
                RenderUtility.drawRect(context, x, y, size, size, colorWithAlpha);
            }
        }

        void resetPosition(int screenWidth, int screenHeight) {
            this.x = random.nextFloat() * screenWidth;
            this.y = random.nextFloat() * screenHeight;
            this.alpha = 0.0f;
            this.fadingIn = true;
            this.fadeSpeed = 0.01f + random.nextFloat() * 0.02f;
        }
    }

    private static class FloatingKitty {
        double x, y;
        double velocityX, velocityY;
        int width = 128;
        int height = 128;
        boolean initialized = false;

        void initialize(int screenWidth, int screenHeight) {
            this.x = random.nextDouble() * (screenWidth - width);
            this.y = random.nextDouble() * (screenHeight - height);
            this.velocityX = (random.nextDouble() - 0.5) * 0.8;
            this.velocityY = (random.nextDouble() - 0.5) * 0.8;
            this.initialized = true;
        }

        void update(int screenWidth, int screenHeight) {
            if(!initialized) {
                initialize(screenWidth, screenHeight);
                return;
            }

            x += velocityX;
            y += velocityY;

            if(x <= 0 || x >= screenWidth - width) {
                velocityX = -velocityX;
                x = Math.max(0, Math.min(screenWidth - width, x));
            }

            if(y <= 0 || y >= screenHeight - height) {
                velocityY = -velocityY;
                y = Math.max(0, Math.min(screenHeight - height, y));
            }

            if(random.nextDouble() < 0.003) {
                velocityX += (random.nextDouble() - 0.5) * 0.1;
                velocityY += (random.nextDouble() - 0.5) * 0.1;

                velocityX = Math.max(-1.2, Math.min(1.2, velocityX));
                velocityY = Math.max(-1.2, Math.min(1.2, velocityY));
            }
        }

        void render(DrawContext context) {
            if(initialized) {
                RenderUtility.drawImage(
                        KITTY_TEXTURE,
                        (int) Math.round(x),
                        (int) Math.round(y),
                        width,
                        height,
                        width,
                        height,
                        context
                );
            }
        }

        void resetPosition(int screenWidth, int screenHeight) {
            initialize(screenWidth, screenHeight);
        }
    }

    public static void initializeStars(int screenWidth, int screenHeight) {
        for(int i = 0; i < MAX_STARS; i++) {
            float x = random.nextFloat() * screenWidth;
            float y = random.nextFloat() * screenHeight;
            stars[i] = new Star(x, y);

            if(random.nextBoolean()) {
                stars[i].alpha = random.nextFloat();
                stars[i].fadingIn = random.nextBoolean();
            }
        }
        initialized = true;
        lastWidth = screenWidth;
        lastHeight = screenHeight;
        kitty.resetPosition(screenWidth, screenHeight);
    }

    public static void updateStars(int screenWidth, int screenHeight) {
        if(!initialized || screenWidth != lastWidth || screenHeight != lastHeight) {
            initializeStars(screenWidth, screenHeight);
            return;
        }

        for(Star star : stars) {
            star.update();

            if(random.nextFloat() < 0.001f) {
                star.resetPosition(screenWidth, screenHeight);
            }
        }

        kitty.update(screenWidth, screenHeight);
    }

    public static void renderStars(DrawContext context) {
        if(!initialized) return;

        for(Star star : stars) {
            star.render(context);
        }
    }

    public static void drawStarField(DrawContext context) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        RenderUtility.drawRect(
                context,
                0,
                0,
                width,
                height,
                ColorUtility.getColor(ColorUtility.Colors.BLACK)
        );

        updateStars(width, height);
        renderStars(context);
        kitty.render(context);
    }
}