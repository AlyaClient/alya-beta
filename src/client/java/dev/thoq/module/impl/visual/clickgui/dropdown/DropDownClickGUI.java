/*
 * Copyright (c) Rye Client 2025-2025.
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

package dev.thoq.module.impl.visual.clickgui.dropdown;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SameParameterValue", "rawtypes", "FieldCanBeLocal"})
public class DropDownClickGUI {
    private boolean visible = false;
    private final Map<ModuleCategory, List<Module>> categorizedModules = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, Boolean> expandedCategories = new EnumMap<>(ModuleCategory.class);
    private final Map<Module, Boolean> expandedModules = new HashMap<>();
    private static final int SETTING_HEIGHT = 16;
    private static final int SETTING_INDENT = 15;
    private static final int CATEGORY_HEIGHT = 20;
    private static final int MODULE_HEIGHT = 16;
    private static final int PANEL_WIDTH = 140;
    private static final int PANEL_X = 70;
    private static final int PANEL_Y = 20;
    private static final int PANEL_SPACING = 10;
    private static final int PADDING = 2;
    private static final int BACKGROUND_COLOR = ColorUtility.getColor(ColorUtility.Colors.PANEL);
    private static final int CATEGORY_COLOR = 0xDD000000;
    private static final int HOVER_COLOR = 0x10FFFFFF;
    private int mouseX;
    private int mouseY;
    private boolean mouseDown;
    private boolean wasMouseDown;
    private boolean rightMouseDown;
    private boolean wasRightMouseDown;
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public DropDownClickGUI() {
        for(ModuleCategory category : ModuleCategory.values()) {
            categorizedModules.put(category, new ArrayList<>());
            expandedCategories.put(category, true);
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
        int scaleFactor = mc.getWindow().getScaleFactor();

        GLFW.glfwGetCursorPos(handle, xPos, yPos);

        mouseX = (int) xPos[0] / scaleFactor;
        mouseY = (int) yPos[0] / scaleFactor;

        wasMouseDown = mouseDown;
        mouseDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        wasRightMouseDown = rightMouseDown;
        rightMouseDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
    }

    public void render(DrawContext context) {
        if(!visible) return;

        int categoryIndex = 0;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            boolean expanded = expandedCategories.get(category);
            int categoryX = PANEL_X + (PANEL_WIDTH + PANEL_SPACING) * categoryIndex;
            int y = PANEL_Y;
            boolean hoverCategory = isMouseOver(categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT);

            renderRect(context, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT, CATEGORY_COLOR);

            if(hoverCategory)
                renderRect(context, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT, HOVER_COLOR);

            TextRendererUtility.renderText(
                    context,
                    category.getDisplayName() + (expanded ? " ▼" : " ▶"),
                    ColorUtility.Colors.WHITE,
                    categoryX + PADDING,
                    y + PADDING + 2,
                    false
            );

            y += CATEGORY_HEIGHT;

            if(expanded) {
                for(Module module : modules) {
                    boolean hoverModule = isMouseOver(categoryX, y, PANEL_WIDTH, MODULE_HEIGHT);

                    renderRect(context, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT, BACKGROUND_COLOR);

                    if(hoverModule)
                        renderRect(context, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT, HOVER_COLOR);

                    TextRendererUtility.renderText(
                            context,
                            module.getName(),
                            module.isEnabled() ? ColorUtility.Colors.WHITE : ColorUtility.Colors.GRAY,
                            categoryX + PADDING * 3,
                            y + PADDING + 2,
                            false
                    );

                    y += MODULE_HEIGHT;

                    if(expandedModules.getOrDefault(module, false)) {
                        for(Setting<?> setting : module.getSettings()) {
                            if(!setting.isVisible()) continue;

                            renderRect(context, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT, BACKGROUND_COLOR);
                            TextRendererUtility.renderText(
                                    context,
                                    setting.getName() + ": ",
                                    ColorUtility.Colors.GRAY,
                                    categoryX + PADDING * 3 + SETTING_INDENT,
                                    y + PADDING + 2,
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
                                    categoryX + PANEL_WIDTH - TextRendererUtility.getTextWidth(valueText) - PADDING * 3,
                                    y + PADDING + 2,
                                    false
                            );
                            y += SETTING_HEIGHT;
                        }
                    }
                }
            }
            categoryIndex++;
        }
    }

    private void handleLeftClick() {
        int categoryIndex = 0;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            int categoryX = PANEL_X + (PANEL_WIDTH + PANEL_SPACING) * categoryIndex;
            int y = PANEL_Y;

            if(isMouseOver(categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT)) {
                expandedCategories.put(category, !expandedCategories.get(category));
                return;
            }

            y += CATEGORY_HEIGHT;

            if(expandedCategories.get(category)) {
                for(Module module : modules) {
                    if(isMouseOver(categoryX, y, PANEL_WIDTH, MODULE_HEIGHT)) {
                        module.toggle();
                        return;
                    }

                    y += MODULE_HEIGHT;

                    if(expandedModules.getOrDefault(module, false)) {
                        for(Setting<?> setting : module.getSettings()) {
                            if(!setting.isVisible()) continue;
                            if(isMouseOver(categoryX, y, PANEL_WIDTH, SETTING_HEIGHT)) {
                                switch(setting) {
                                    case BooleanSetting booleanSetting -> booleanSetting.toggle();
                                    case ModeSetting modeSetting -> modeSetting.cycle();

                                    case NumberSetting numberSetting -> {
                                        boolean fastIncrement = mouseDown && wasMouseDown;
                                        numberSetting.increment(fastIncrement);
                                    }

                                    case SliderSetting sliderSetting -> {
                                        double normalizedPos = Math.max(0.0, Math.min(1.0,
                                                (double) (mouseX - categoryX) / PANEL_WIDTH));
                                        sliderSetting.setFromNormalizedValue(normalizedPos);
                                    }

                                    default -> {
                                    }
                                }

                                return;
                            }

                            y += SETTING_HEIGHT;
                        }
                    }
                }
            }

            categoryIndex++;
        }
    }

    private void handleRightClick() {
        int categoryIndex = 0;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            int categoryX = PANEL_X + (PANEL_WIDTH + PANEL_SPACING) * categoryIndex;
            int y = PANEL_Y;

            if(isMouseOver(categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT)) {
                expandedCategories.put(category, !expandedCategories.get(category));
                return;
            }

            y += CATEGORY_HEIGHT;

            if(expandedCategories.get(category)) {
                for(Module module : modules) {
                    if(isMouseOver(categoryX, y, PANEL_WIDTH, MODULE_HEIGHT)) {
                        expandedModules.put(module, !expandedModules.getOrDefault(module, false));
                        return;
                    }

                    y += MODULE_HEIGHT;

                    if(expandedModules.getOrDefault(module, false)) {
                        for(Setting<?> setting : module.getSettings()) {
                            if(!setting.isVisible()) continue;

                            if(isMouseOver(categoryX, y, PANEL_WIDTH, SETTING_HEIGHT)) {
                                if(setting instanceof NumberSetting numberSetting) {
                                    boolean fastDecrement = rightMouseDown && wasRightMouseDown;
                                    numberSetting.decrement(fastDecrement);
                                    return;
                                }
                            }

                            y += SETTING_HEIGHT;
                        }
                    }
                }
            }
            categoryIndex++;
        }
    }

    private boolean isMouseOver(int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    private void renderRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }
}