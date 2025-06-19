package dev.thoq.utilities.render;

import dev.thoq.RyeClient;
import dev.thoq.config.SettingsManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Custom font renderer that can use both Minecraft's default font and custom fonts.
 */
public class FontRenderer {
    private static final FontRenderer INSTANCE = new FontRenderer();

    private Font customFont;
    private final Map<Character, CustomGlyph> glyphCache = new HashMap<>();
    private int fontHeight = -1;
    private boolean initialized = false;
    private boolean fontLoaded = false;

    private FontRenderer() {

    }

    /**
     * Initializes the font renderer.
     * This should be called after Minecraft is fully initialized.
     */
    public void initialize() {
        if (initialized) return;

        try {

            if (!fontLoaded) {
                loadFont();
            }

            RyeClient.LOGGER.info("Initializing font renderer");
            RyeClient.LOGGER.info("Font renderer initialized with mode: " +
                    (SettingsManager.getInstance().getUseDefaultFont().getValue() ? "Default Minecraft" : "Custom Font"));

            initialized = true;
        } catch (Exception e) {
            RyeClient.LOGGER.error("Failed to initialize font renderer", e);

            SettingsManager.getInstance().getUseDefaultFont().setValue(true);
        }
    }

    /**
     * Loads the custom font.
     */
    private void loadFont() {
        if (fontLoaded) return;

        try {
            InputStream is = null;

            is = FontRenderer.class.getResourceAsStream("/rye/Fonts/SF-Pro-Rounded-Regular.ttf");
            if (is != null) {
                RyeClient.LOGGER.info("Found font at /rye/Fonts/SF-Pro-Rounded-Regular.ttf");
                customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 16f);
                is.close();
                fontLoaded = true;
                return;
            } else {
                RyeClient.LOGGER.error("Could not find custom font resource at /rye/Fonts/SF-Pro-Rounded-Regular.ttf");
            }

            is = getClass().getClassLoader().getResourceAsStream("rye/Fonts/SF-Pro-Rounded-Regular.ttf");
            if (is != null) {
                RyeClient.LOGGER.info("Found font at rye/Fonts/SF-Pro-Rounded-Regular.ttf (via ClassLoader)");
                customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 16f);
                is.close();
                fontLoaded = true;
                return;
            } else {
                RyeClient.LOGGER.error("Could not find custom font resource at rye/Fonts/SF-Pro-Rounded-Regular.ttf (via ClassLoader)");
            }

            try {
                String runDir = MinecraftClient.getInstance().runDirectory.getAbsolutePath();
                String projectRoot = runDir.substring(0, runDir.lastIndexOf("/run"));
                String fontPath = projectRoot + "/client/src/client/resources/rye/Fonts/SF-Pro-Rounded-Regular.ttf";

                RyeClient.LOGGER.info("Trying direct file access at: " + fontPath);

                java.io.File fontFile = new java.io.File(fontPath);
                if (fontFile.exists()) {
                    is = new java.io.FileInputStream(fontFile);
                    RyeClient.LOGGER.info("Found font at " + fontPath + " (via direct file access)");
                    customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 16f);
                    is.close();
                    fontLoaded = true;
                    return;
                } else {
                    RyeClient.LOGGER.error("Could not find custom font resource at " + fontPath + " (via direct file access)");
                }
            } catch (Exception e) {
                RyeClient.LOGGER.error("Error trying direct file access", e);
            }

            RyeClient.LOGGER.error("Failed to load custom font from all attempted paths");

            SettingsManager.getInstance().getUseDefaultFont().setValue(true);

        } catch (Exception e) {
            RyeClient.LOGGER.error("Failed to load custom font", e);

            SettingsManager.getInstance().getUseDefaultFont().setValue(true);
        }
    }

    /**
     * Gets a glyph for a character, creating it if it doesn't exist.
     *
     * @param c The character
     * @return The glyph
     */
    private CustomGlyph getGlyph(char c) {

        if (!initialized) {
            initialize();
        }

        if (!fontLoaded) {
            loadFont();
        }

        if (glyphCache.containsKey(c)) {
            return glyphCache.get(c);
        }

        CustomGlyph glyph = new CustomGlyph(c, customFont);
        glyphCache.put(c, glyph);
        return glyph;
    }

    /**
     * Gets the instance of the font renderer.
     *
     * @return The font renderer instance
     */
    public static FontRenderer getInstance() {
        return INSTANCE;
    }

    /**
     * Draws text using either the default Minecraft font or the custom font.
     *
     * @param context The draw context
     * @param text The text to draw
     * @param x The x position
     * @param y The y position
     * @param color The color
     * @param shadow Whether to draw a shadow
     */
    public void drawText(DrawContext context, String text, float x, float y, int color, boolean shadow) {

        if (!initialized) {
            initialize();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) {
            return;
        }

        if (SettingsManager.getInstance().getUseDefaultFont().getValue()) {

            context.drawText(client.textRenderer, text, (int)x, (int)y, color, shadow);
        } else {

            if (shadow) {

                int shadowColor = (color & 0x00FFFFFF) | ((int)((color >> 24 & 0xFF) * 0.25f) << 24);
                context.drawText(client.textRenderer, text, (int)x + 1, (int)y + 1, shadowColor, false);
            }

            context.drawText(client.textRenderer, text, (int)x, (int)y, color, false);
        }
    }

    /**
     * Draws text using either the default Minecraft font or the custom font.
     *
     * @param context The draw context
     * @param text The text to draw
     * @param x The x position
     * @param y The y position
     * @param color The color
     * @param shadow Whether to draw a shadow
     */
    public void drawText(DrawContext context, Text text, float x, float y, int color, boolean shadow) {
        drawText(context, text.getString(), x, y, color, shadow);
    }

    /**
     * Gets the width of text.
     *
     * @param text The text
     * @return The width
     */
    public int getWidth(String text) {

        if (!initialized) {
            initialize();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) {
            return 0;
        }

        if (SettingsManager.getInstance().getUseDefaultFont().getValue()) {
            return client.textRenderer.getWidth(text);
        } else {

            return client.textRenderer.getWidth(text) + 1;
        }
    }

    /**
     * Gets the height of the font.
     *
     * @return The height
     */
    public int getHeight() {

        if (!initialized) {
            initialize();
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.textRenderer == null) {
            return 9;
        }

        if (SettingsManager.getInstance().getUseDefaultFont().getValue()) {
            return client.textRenderer.fontHeight;
        } else {
            if (fontHeight == -1) {

                fontHeight = customFont != null ? (int)(customFont.getSize() * 1.1f) : client.textRenderer.fontHeight;
            }
            return fontHeight;
        }
    }

    /**
     * Represents a glyph in the custom font.
     */
    private static class CustomGlyph {
        public final char character;
        public final int width;
        public final int height;
        public int textureId = -1;
        private boolean textureCreated = false;

        public CustomGlyph(char character, Font font) {
            this.character = character;

            int tempWidth;
            int tempHeight;
            try {

                Canvas canvas = new Canvas();
                FontMetrics metrics = canvas.getFontMetrics(font);
                tempWidth = metrics.charWidth(character);
                tempHeight = metrics.getHeight();

                tempWidth = Math.max(tempWidth, 1);
                tempHeight = Math.max(tempHeight, 1);
            } catch (Exception e) {

                tempWidth = 8;
                tempHeight = 9;
            }

            this.width = tempWidth;
            this.height = tempHeight;

        }

        /**
         * Creates the texture for this glyph if it hasn't been created yet.
         * This should only be called when OpenGL is ready.
         */
        public void createTexture() {
            if (textureCreated) return;

            try {

                textureId = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

                textureCreated = true;

            } catch (Exception e) {
                RyeClient.LOGGER.error("Failed to create texture for glyph: " + character, e);
            }
        }
    }
}
