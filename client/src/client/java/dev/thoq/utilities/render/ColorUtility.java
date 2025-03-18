package dev.thoq.utilities.render;

public class ColorUtility {
    public enum Colors {
        WHITE,
        RED,
        GREEN,
        BLUE,
        YELLOW,
        CYAN,
        MAGENTA,
        PURPLE,
        LAVENDER,
        DARK_PURPLE,
        BLACK,
        GRAY,
        LIGHT_GRAY,
    }

    public static int getColor(Colors color) {
        return switch(color) {
            case WHITE -> 0xFFFFFFFF;
            case RED -> 0xFFFF0000;
            case GREEN -> 0xFF00FF00;
            case BLUE -> 0xFF0000FF;
            case YELLOW -> 0xFFFFFF00;
            case CYAN -> 0xFF00FFFF;
            case MAGENTA -> 0xFFFF00FF;
            case PURPLE -> 0x9C27F5;
            case LAVENDER -> 0xE5B8FF;
            case DARK_PURPLE -> 0x7B1FA2;
            case BLACK -> 0xFF000000;
            case GRAY -> 0xFF808080;
            case LIGHT_GRAY -> 0xFFD3D3D3;
        };
    }
}
