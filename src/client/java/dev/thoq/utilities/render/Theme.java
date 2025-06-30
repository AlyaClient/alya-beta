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

import dev.thoq.utilities.types.Pair;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public enum Theme {
    RYE("Rye", new Color(92, 58, 220), new Color(100, 88, 147), true),
    CHRISTMAS("Christmas", new Color(255, 0, 0), new Color(255, 255, 255)),
    GUFF("Guff", new Color(255, 0, 255), new Color(12, 0, 255)),
    SKEET("Skeet", new Color(0, 255, 0), new Color(0, 0, 0)),
    SNOWY_SKY("Snowy Sky", new Color(0, 190, 255), new Color(255, 255, 255)),
    WINTER("Winter", new Color(200, 200, 200), new Color(255, 255, 255)),
    ORANGE_JUICE("Orange Juice", new Color(255, 150, 0), new Color(255, 185, 0)),
    JAVA("Java", new Color(111, 78, 55), new Color(130, 90, 70)),
    WATER("Water", new Color(0, 190, 255), new Color(0, 110, 255)),
    THOQ("Thoq", new Color(236, 133, 209), new Color(80, 69, 206)),
    CORAL_PINK("Coral Pink", new Color(248, 131, 121), new Color(120, 0, 110)),
    SUNSET_ORANGE("Sunset Orange", new Color(255, 69, 0), new Color(255, 140, 0)),
    BEACH_BLUE("Beach Blue", new Color(226, 202, 118), new Color(65, 184, 243)),
    SUMMER("Summer", new Color(255, 0, 0), new Color(255, 255, 0)),
    BATMAN("Batman", new Color(0, 0, 0), new Color(255, 255, 0)),
    STEEL("Steel", new Color(65, 131, 247), new Color(56, 70, 96)),
    PINK_LEMONADE("Pink Lemonade", new Color(255, 105, 180), new Color(255, 182, 193)),
    SORBET("Sorbet", new Color(250, 171, 189), new Color(173, 81, 102));

    private static final Map<String, Theme> themeMap = new HashMap<>();
    private static Theme currentTheme = RYE; // default theme

    private final String name;
    private final Pair<Color, Color> colors;
    private final boolean gradient;

    Theme(String name, Color color, Color colorAlternative) {
        this(name, color, colorAlternative, false);
    }

    Theme(String name, Color color, Color colorAlternative, boolean gradient) {
        this.name = name;
        colors = Pair.of(color, colorAlternative);
        this.gradient = gradient;
    }

    public static void init() {
        Arrays.stream(values()).forEach(theme -> themeMap.put(theme.name, theme));
    }

    public Pair<Color, Color> getColors() {
        return colors;
    }

    public boolean hasGradient() {
        return gradient;
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