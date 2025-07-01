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

package dev.thoq.utilities.render;

import dev.thoq.config.setting.Setting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.types.Pair;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public enum Theme {
    RYE("Rye", new Color(92, 58, 220), new Color(100, 88, 147)),
    PINK_LEMONADE("Pink Lemonade", new Color(255, 105, 180), new Color(255, 182, 193)),
    SORBET("Sorbet", new Color(250, 171, 189), new Color(173, 81, 102));

    private static final Map<String, Theme> themeMap = new HashMap<>();
    private static Theme currentTheme = RYE;

    private final String name;
    private final Pair<Color, Color> colors;

    Theme(String name, Color color, Color colorAlternative) {
        this.name = name;
        System.out.println("Registering theme: " + name);
        colors = Pair.of(color, colorAlternative);
    }

    public static void init() {
        Arrays.stream(values()).forEach(theme -> themeMap.put(theme.name, theme));
    }

    public Pair<Color, Color> getColors() {
        return colors;
    }

    public String getName() {
        return name;
    }

    public Color getPrimaryColor() {
        return colors.getFirst();
    }

    public Color getSecondaryColor() {
        return colors.getSecond();
    }

    public int getPrimaryColorInt() {
        return ColorUtility.getIntFromColor(getPrimaryColor());
    }

    public int getSecondaryColorInt() {
        return ColorUtility.getIntFromColor(getSecondaryColor());
    }

    public static Pair<Color, Color> getThemeColors(String name) {
        return get(name).getColors();
    }

    public static Theme get(String name) {
        return themeMap.get(name);
    }

    public static Theme getCurrentTheme() {
        try {
            ModuleRepository repository = ModuleRepository.getInstance();
            Module hudModule = repository.getModuleByName("HUD");
            if(hudModule == null)
                throw new IllegalStateException("HUD module either found or not loaded");

            for(Setting<?> setting : hudModule.getSettings()) {
                if(setting.getName().equals("Theme") && setting instanceof ModeSetting themeSetting) {
                    String currentThemeName = themeSetting.getValue();
                    if(currentThemeName == null) {
                        currentThemeName = RYE.getName();
                        currentTheme = RYE;
                    }

                    Theme theme = get(currentThemeName);
                    if(theme == null) {
                        theme = RYE;
                    }

                    currentTheme = theme;
                    return currentTheme;
                }
            }
        } catch(Exception ignored) {
        }

        return currentTheme;
    }

    public static void setCurrentTheme(Theme theme) {
        currentTheme = theme;
    }

    public static void setCurrentTheme(String themeName) {
        Theme theme = get(themeName);
        if(theme != null) {
            currentTheme = theme;
        }
    }

    public static Color interpolateColor(Color color1, Color color2, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));

        int r = (int) (color1.getRed() + factor * (color2.getRed() - color1.getRed()));
        int g = (int) (color1.getGreen() + factor * (color2.getGreen() - color1.getGreen()));
        int b = (int) (color1.getBlue() + factor * (color2.getBlue() - color1.getBlue()));
        int a = (int) (color1.getAlpha() + factor * (color2.getAlpha() - color1.getAlpha()));

        return new Color(r, g, b, a);
    }

    public static int interpolateColorInt(Color color1, Color color2, float factor) {
        return ColorUtility.getIntFromColor(interpolateColor(color1, color2, factor));
    }

    public static Color getInterpolatedThemeColor(float factor) {
        Theme theme = getCurrentTheme();
        return interpolateColor(theme.getPrimaryColor(), theme.getSecondaryColor(), factor);
    }

    public static int getInterpolatedThemeColorInt(float factor) {
        return ColorUtility.getIntFromColor(getInterpolatedThemeColor(factor));
    }

    public static String[] getThemeNames() {
        return Arrays.stream(values()).map(Theme::getName).toArray(String[]::new);
    }
}