/*
 * Copyright (c) Rye 2025-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric Injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * This project (and subsequently, its files) are all licensed under the MIT License.
 * This project should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 */

package dev.thoq.module.impl.visual.clickgui.Window;

import dev.thoq.RyeClient;
import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.config.setting.Setting;
import dev.thoq.config.setting.impl.SliderSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@SuppressWarnings({"SameParameterValue", "FieldCanBeLocal", "rawtypes"})
public class WindowClickGUI {
    private boolean visible = false;
    private final Map<ModuleCategory, List<Module>> categorizedModules = new EnumMap<>(ModuleCategory.class);
    private final Map<Module, Boolean> expandedModules = new HashMap<>();
    private ModuleCategory selectedCategory = ModuleCategory.COMBAT;

    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;
    private static final int CATEGORY_BUTTON_HEIGHT = 30;
    private static final int MODULE_HEIGHT = 24;
    private static final int SETTING_HEIGHT = 20;
    private static final int PADDING = 10;
    private static final int DESCRIPTION_HEIGHT = 16;
    private static final int WINDOW_COLOR = 0xDD1A1A1A;
    private static final int CATEGORY_COLOR = 0xFF2D2D30;
    private static final int SELECTED_CATEGORY_COLOR = 0xFF404040;
    private static final int MODULE_COLOR = 0xFF2D2D30;
    private static final int ENABLED_MODULE_COLOR = 0xFF3C7F3C;
    private static final int SETTING_COLOR = 0xFF252526;
    private static final int HOVER_COLOR = 0x20FFFFFF;
    private static final int BORDER_COLOR = 0xFF404040;
    private int mouseX;
    private int mouseY;
    private boolean mouseDown;
    private boolean wasMouseDown;
    private boolean rightMouseDown;
    private boolean wasRightMouseDown;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public WindowClickGUI() {
        for(ModuleCategory category : ModuleCategory.values()) {
            categorizedModules.put(category, new ArrayList<>());
        }
    }

    public void show() {
        for(List<Module> modules : categorizedModules.values())
            modules.clear();

        ModuleRepository repository = ModuleRepository.getInstance();

        for(Module module : repository.getModules()) {
            ModuleCategory category = module.getCategory();
            categorizedModules.get(category).add(module);
        }

        visible = true;
    }

    public void hide() {
        visible = false;
    }

    public void tick() {
        if(!visible) return;

        updateMouseInput();

        if(mouseDown && !wasMouseDown)
            handleLeftClick();

        if(rightMouseDown && !wasRightMouseDown)
            handleRightClick();
    }

    private void updateMouseInput() {
        long handle = mc.getWindow().getHandle();
        double[] xPos = new double[1];
        double[] yPos = new double[1];

        GLFW.glfwGetCursorPos(handle, xPos, yPos);

        mouseX = (int) xPos[0];
        mouseY = (int) yPos[0];

        wasMouseDown = mouseDown;
        mouseDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        wasRightMouseDown = rightMouseDown;
        rightMouseDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
    }

    public void render(DrawContext context) {
        if(!visible) return;

        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int windowX = (screenWidth - WINDOW_WIDTH) / 2;
        int windowY = (screenHeight - WINDOW_HEIGHT) / 2;

        renderRect(context, windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_COLOR);
        renderBorder(context, windowX, windowY, WINDOW_WIDTH, WINDOW_HEIGHT, BORDER_COLOR);

        TextRendererUtility.renderText(
                context,
                String.format("%s Client", RyeClient.getName()),
                ColorUtility.Colors.WHITE,
                windowX + PADDING,
                windowY + PADDING,
                false
        );

        int categoryButtonY = windowY + PADDING + 20;
        int categoryButtonX = windowX + PADDING;
        int categoryButtonWidth = (WINDOW_WIDTH - PADDING * 2) / ModuleCategory.values().length;

        for(ModuleCategory category : ModuleCategory.values()) {
            boolean isSelected = category == selectedCategory;
            boolean isHovered = isMouseOver(categoryButtonX, categoryButtonY, categoryButtonWidth, CATEGORY_BUTTON_HEIGHT);

            int buttonColor = isSelected ? SELECTED_CATEGORY_COLOR : CATEGORY_COLOR;
            if(isHovered && !isSelected) {
                buttonColor = blendColors(buttonColor, HOVER_COLOR);
            }

            renderRect(context, categoryButtonX, categoryButtonY, categoryButtonWidth, CATEGORY_BUTTON_HEIGHT, buttonColor);

            TextRendererUtility.renderText(
                    context,
                    category.getDisplayName(),
                    ColorUtility.Colors.WHITE,
                    categoryButtonX + categoryButtonWidth / 2 - TextRendererUtility.getTextWidth(category.getDisplayName()) / 2,
                    categoryButtonY + (CATEGORY_BUTTON_HEIGHT - 8) / 2,
                    false
            );

            categoryButtonX += categoryButtonWidth;
        }

        List<Module> modules = categorizedModules.get(selectedCategory);
        if(modules != null) {
            int moduleY = categoryButtonY + CATEGORY_BUTTON_HEIGHT + PADDING;
            int moduleX = windowX + PADDING;
            int moduleWidth = WINDOW_WIDTH - PADDING * 2;

            for(Module module : modules) {
                int totalModuleHeight = MODULE_HEIGHT + DESCRIPTION_HEIGHT;
                
                if(expandedModules.getOrDefault(module, false)) {
                    for(Setting<?> setting : module.getSettings()) {
                        if(setting.isVisible()) {
                            totalModuleHeight += SETTING_HEIGHT;
                        }
                    }
                }

                if(moduleY + totalModuleHeight > windowY + WINDOW_HEIGHT - PADDING) {
                    break;
                }

                boolean isHovered = isMouseOver(moduleX, moduleY, moduleWidth, MODULE_HEIGHT + DESCRIPTION_HEIGHT);
                int moduleColor = module.isEnabled() ? ENABLED_MODULE_COLOR : MODULE_COLOR;

                if(isHovered) {
                    moduleColor = blendColors(moduleColor, HOVER_COLOR);
                }

                renderRect(context, moduleX, moduleY, moduleWidth, MODULE_HEIGHT + DESCRIPTION_HEIGHT, moduleColor);
                renderBorder(context, moduleX, moduleY, moduleWidth, MODULE_HEIGHT + DESCRIPTION_HEIGHT, BORDER_COLOR);

                TextRendererUtility.renderText(
                        context,
                        module.getName(),
                        module.isEnabled() ? ColorUtility.Colors.WHITE : ColorUtility.Colors.LIGHT_GRAY,
                        moduleX + PADDING,
                        moduleY + PADDING / 2,
                        false
                );

                TextRendererUtility.renderText(
                        context,
                        module.getDescription(),
                        ColorUtility.Colors.GRAY,
                        moduleX + PADDING,
                        moduleY + MODULE_HEIGHT,
                        false
                );

                String status = module.isEnabled() ? "ENABLED" : "DISABLED";
                ColorUtility.Colors statusColor = status.equals("ENABLED") ? ColorUtility.Colors.GREEN : ColorUtility.Colors.RED;
                TextRendererUtility.renderText(
                        context,
                        status,
                        statusColor,
                        moduleX + moduleWidth - TextRendererUtility.getTextWidth(status) - PADDING,
                        moduleY + PADDING / 2,
                        false
                );

                moduleY += MODULE_HEIGHT + DESCRIPTION_HEIGHT;

                if(expandedModules.getOrDefault(module, false)) {
                    for(Setting<?> setting : module.getSettings()) {
                        if(!setting.isVisible()) continue;

                        boolean settingHovered = isMouseOver(moduleX, moduleY, moduleWidth, SETTING_HEIGHT);
                        int settingColor = SETTING_COLOR;
                        if(settingHovered) {
                            settingColor = blendColors(settingColor, HOVER_COLOR);
                        }

                        renderRect(context, moduleX, moduleY, moduleWidth, SETTING_HEIGHT, settingColor);
                        
                        TextRendererUtility.renderText(
                                context,
                                setting.getName() + ":",
                                ColorUtility.Colors.LIGHT_GRAY,
                                moduleX + PADDING * 2,
                                moduleY + (SETTING_HEIGHT - 8) / 2,
                                false
                        );

                        String valueText;
                        if(setting instanceof BooleanSetting) {
                            valueText = ((Boolean) setting.getValue()) ? "ON" : "OFF";
                        } else if(setting instanceof ModeSetting) {
                            valueText = setting.getValue().toString();
                        } else {
                            valueText = setting.getValue().toString();
                        }

                        TextRendererUtility.renderText(
                                context,
                                valueText,
                                ColorUtility.Colors.WHITE,
                                moduleX + moduleWidth - TextRendererUtility.getTextWidth(valueText) - PADDING * 2,
                                moduleY + (SETTING_HEIGHT - 8) / 2,
                                false
                        );

                        moduleY += SETTING_HEIGHT;
                    }
                }

                moduleY += 2;
            }
        }
    }

    private void handleLeftClick() {
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int windowX = (screenWidth - WINDOW_WIDTH) / 2;
        int windowY = (screenHeight - WINDOW_HEIGHT) / 2;

        int categoryButtonY = windowY + PADDING + 20;
        int categoryButtonX = windowX + PADDING;
        int categoryButtonWidth = (WINDOW_WIDTH - PADDING * 2) / ModuleCategory.values().length;

        for(ModuleCategory category : ModuleCategory.values()) {
            if(isMouseOver(categoryButtonX, categoryButtonY, categoryButtonWidth, CATEGORY_BUTTON_HEIGHT)) {
                selectedCategory = category;
                return;
            }
            categoryButtonX += categoryButtonWidth;
        }

        List<Module> modules = categorizedModules.get(selectedCategory);
        if(modules != null) {
            int moduleY = categoryButtonY + CATEGORY_BUTTON_HEIGHT + PADDING;
            int moduleX = windowX + PADDING;
            int moduleWidth = WINDOW_WIDTH - PADDING * 2;

            for(Module module : modules) {
                int totalModuleHeight = MODULE_HEIGHT + DESCRIPTION_HEIGHT;
                
                if(expandedModules.getOrDefault(module, false)) {
                    for(Setting<?> setting : module.getSettings()) {
                        if(setting.isVisible()) {
                            totalModuleHeight += SETTING_HEIGHT;
                        }
                    }
                }

                if(moduleY + totalModuleHeight > windowY + WINDOW_HEIGHT - PADDING) {
                    break;
                }

                if(isMouseOver(moduleX, moduleY, moduleWidth, MODULE_HEIGHT + DESCRIPTION_HEIGHT)) {
                    module.toggle();
                    return;
                }

                moduleY += MODULE_HEIGHT + DESCRIPTION_HEIGHT;

                if(expandedModules.getOrDefault(module, false)) {
                    for(Setting<?> setting : module.getSettings()) {
                        if(!setting.isVisible()) continue;

                        if(isMouseOver(moduleX, moduleY, moduleWidth, SETTING_HEIGHT)) {
                            switch(setting) {
                                case BooleanSetting booleanSetting -> booleanSetting.toggle();
                                case ModeSetting modeSetting -> modeSetting.cycle();
                                case NumberSetting numberSetting -> {
                                    boolean fastIncrement = mouseDown && wasMouseDown;
                                    numberSetting.increment(fastIncrement);
                                }
                                case SliderSetting sliderSetting -> {
                                    double normalizedPos = Math.max(0.0, Math.min(1.0,
                                            (double) (mouseX - moduleX) / moduleWidth));
                                    sliderSetting.setFromNormalizedValue(normalizedPos);
                                }
                                default -> {
                                }
                            }
                            return;
                        }

                        moduleY += SETTING_HEIGHT;
                    }
                }

                moduleY += 2;
            }
        }
    }

    private void handleRightClick() {
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();

        int windowX = (screenWidth - WINDOW_WIDTH) / 2;
        int windowY = (screenHeight - WINDOW_HEIGHT) / 2;

        int categoryButtonY = windowY + PADDING + 20;

        List<Module> modules = categorizedModules.get(selectedCategory);
        if(modules != null) {
            int moduleY = categoryButtonY + CATEGORY_BUTTON_HEIGHT + PADDING;
            int moduleX = windowX + PADDING;
            int moduleWidth = WINDOW_WIDTH - PADDING * 2;

            for(Module module : modules) {
                int totalModuleHeight = MODULE_HEIGHT + DESCRIPTION_HEIGHT;
                
                if(expandedModules.getOrDefault(module, false)) {
                    for(Setting<?> setting : module.getSettings()) {
                        if(setting.isVisible()) {
                            totalModuleHeight += SETTING_HEIGHT;
                        }
                    }
                }

                if(moduleY + totalModuleHeight > windowY + WINDOW_HEIGHT - PADDING) {
                    break;
                }

                if(isMouseOver(moduleX, moduleY, moduleWidth, MODULE_HEIGHT + DESCRIPTION_HEIGHT)) {
                    expandedModules.put(module, !expandedModules.getOrDefault(module, false));
                    return;
                }

                moduleY += MODULE_HEIGHT + DESCRIPTION_HEIGHT;

                if(expandedModules.getOrDefault(module, false)) {
                    for(Setting<?> setting : module.getSettings()) {
                        if(!setting.isVisible()) continue;

                        if(isMouseOver(moduleX, moduleY, moduleWidth, SETTING_HEIGHT)) {
                            if(setting instanceof NumberSetting numberSetting) {
                                boolean fastDecrement = rightMouseDown && wasRightMouseDown;
                                numberSetting.decrement(fastDecrement);
                                return;
                            }
                        }

                        moduleY += SETTING_HEIGHT;
                    }
                }

                moduleY += 2;
            }
        }
    }

    private boolean isMouseOver(int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    private void renderRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    private void renderBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y, x + 1, y + height, color);
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    private int blendColors(int color1, int color2) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        float alpha = a2 / 255.0f;
        int a = Math.max(a1, a2);
        int r = (int) (r1 * (1 - alpha) + r2 * alpha);
        int g = (int) (g1 * (1 - alpha) + g2 * alpha);
        int b = (int) (b1 * (1 - alpha) + b2 * alpha);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
