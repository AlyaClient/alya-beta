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
 */

package dev.thoq.font;

import dev.thoq.Rye;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Manages loading and caching of TrueType fonts for rendering in the client.
 *
 * @author RareHyperIon
 * @since 01/07/2025
 */
public class FontManager {

    /**
     * Cache of loaded TextRenderer instanced keyed by "name_size".
     */
    private static final HashMap<String, TextRenderer> FONT_CACHE = new HashMap<>();

    private static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final ResourceManager RESOURCE_MANAGER = MC.getResourceManager();
    private static final TextureManager TEXTURE_MANAGER = MC.getTextureManager();

    /**
     * Preloads commonly used fonts to prevent hitches when rendering for the first time.
     * <p>This should be called once during the initialization phase of the game to warm up the cache.</p>
     */
    public static void cacheCommon() {
        // Example:
        // getFont("figtree", 11);
    }

    /**
     * Retrieves a TextRenderer configured for the specified TrueType font and size.
     * <p>
     *     If the renderer has been previously loaded with the same parameters, it will be returned from cache.
     *     Otherwise, it will be loaded, cached, and returned.
     * </p>
     * The loaded font is oversampled at 2x and uses bilinear filtering on its atlas to produce smooth, anti-aliases text.
     *
     * @param fontName The base name of the font file (without extension) located under <code>assets/&lt;modid&gt;/font/&lt;fontName&gt;.ttf</code>
     * @param size The font pixel height for rendering.
     * @return An instance of TextRenderer for drawing text with the specified font and size.
     */
    public static TextRenderer getFont(final String fontName, final int size) {
        final String key = (fontName + size).toLowerCase();

        if(FONT_CACHE.containsKey(key))
            return FONT_CACHE.get(key);

        final TrueTypeFontLoader loader = new TrueTypeFontLoader(
                Identifier.of(Rye.MOD_ID, fontName + ".ttf"),
                size, 2.0F, // Don't change oversampling, it will ruin the text rendering. (believe him)
                TrueTypeFontLoader.Shift.NONE,
                ""
        );

        try {
            final FontLoader.Loadable loadable = loader.build().orThrow();
            final Font font = loadable.load(RESOURCE_MANAGER);

            final Identifier storageId = Identifier.of(Rye.MOD_ID, String.format("%s_font", key));
            final FontStorage storage = new FontStorage(TEXTURE_MANAGER, storageId);

            storage.setFonts(
                    List.of(new Font.FontFilterPair(font, FontFilterType.FilterMap.NO_FILTER)),
                    Collections.emptySet()
            );

            final TextRenderer renderer = new TextRenderer(id -> storage, true);
            FONT_CACHE.put(key, renderer);
            return renderer;
        } catch (final IOException exception) {
            exception.printStackTrace(System.err);
            return MC.textRenderer; // Return vanilla renderer if it fails instead of crashing. (no balls approach)
        }
    }


}
