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

package dev.thoq.module.impl.visual.clickgui.dropdown;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.config.setting.impl.MultipleBooleanSetting;
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.config.setting.Setting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.render.*;
import dev.thoq.utilities.render.DragUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"SameParameterValue", "rawtypes", "FieldCanBeLocal"})
public class DropDownClickGUI extends Screen {
    private final Map<ModuleCategory, List<Module>> categorizedModules = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, Boolean> expandedCategories = new EnumMap<>(ModuleCategory.class);
    private final Map<Module, Boolean> expandedModules = new HashMap<>();
    private final Map<ModuleCategory, DragUtility> categoryDragUtils = new EnumMap<>(ModuleCategory.class);

    private static final int SETTING_HEIGHT = 25;
    private static final int SETTING_INDENT = 2;
    private static final int CATEGORY_HEIGHT = 24;
    private static final int MODULE_HEIGHT = 25;
    private static final int PANEL_WIDTH = 160;
    private static final int DEFAULT_PANEL_X = 20;
    private static final int DEFAULT_PANEL_Y = 20;
    private static final int PANEL_SPACING = 15;
    private static final int PADDING = 8;
    private static final int BACKGROUND_COLOR = ColorUtility.getColor(ColorUtility.Colors.GRAY);
    private static final int CATEGORY_COLOR = 0xFF222222;
    private static final int HOVER_COLOR = 0x10FFFFFF;
    private static final float CORNER_RADIUS = 4f;
    private static final int TOOLTIP_BACKGROUND = 0xFF000000;
    private static final int TOOLTIP_BORDER = 0xFF212121;
    private static final int TOOLTIP_MAX_WIDTH = 200;
    private static final int TOOLTIP_PADDING = 2;
    private static final int TOOLTIP_OFFSET = 10;
    private String hoveredTooltip = null;
    private int tooltipX = 0;
    private int tooltipY = 0;
    private final boolean showTooltips;
    private int scrollOffset = 0;
    private ModuleCategory draggingCategory = null;
    private boolean draggingNumberSetting = false;
    private NumberSetting<?> currentDraggedNumberSetting = null;
    private int sliderStartX = 0;
    private int sliderWidth = 0;

    public DropDownClickGUI(boolean showTooltips) {
        super(Text.literal("Click GUI"));
        this.showTooltips = showTooltips;

        int categoryX = DEFAULT_PANEL_X;
        for(ModuleCategory category : ModuleCategory.values()) {
            categorizedModules.put(category, new ArrayList<>());
            expandedCategories.put(category, true);
            categoryDragUtils.put(category, new DragUtility(categoryX, DEFAULT_PANEL_Y));
            categoryX += PANEL_WIDTH + PANEL_SPACING;
        }

        initializeModules();
    }

    private void initializeModules() {
        for(List<Module> modules : categorizedModules.values())
            modules.clear();

        ModuleRepository repository = ModuleRepository.getInstance();

        for(Module module : repository.getModules()) {
            ModuleCategory category = module.getCategory();
            categorizedModules.get(category).add(module);
        }

        for(List<Module> modules : categorizedModules.values()) {
            modules.sort((m1, m2) -> m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName()));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        hoveredTooltip = null;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            boolean expanded = expandedCategories.get(category);
            DragUtility dragUtil = categoryDragUtils.get(category);
            int categoryX = dragUtil.getX();
            int y = dragUtil.getY() - scrollOffset;

            Vector4f categoryRadius = new Vector4f(CORNER_RADIUS, 0, CORNER_RADIUS, 0);
            RenderUtility.drawRoundedRect(context, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT, categoryRadius, CATEGORY_COLOR);

            Identifier img = Identifier.of("rye", "icons/category/" + category.toString().toLowerCase() + ".png");

            TextRendererUtility.renderText(
                    context,
                    category.getDisplayName(),
                    ColorUtility.Colors.WHITE,
                    categoryX + PADDING,
                    y + PADDING + 2,
                    false
            );

            RenderUtility.drawImage(
                    img,
                    categoryX + PANEL_WIDTH - 12 - PADDING,
                    y + PADDING + 2,
                    10,
                    10,
                    10,
                    10,
                    context
            );

            y += CATEGORY_HEIGHT;

            if(expanded) {
                for(Module module : modules) {
                    boolean hoverModule = isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT);
                    boolean isLastModule = (modules.indexOf(module) == modules.size() - 1);
                    boolean isModuleExpanded = expandedModules.getOrDefault(module, false);

                    if(showTooltips && hoverModule && module.getDescription() != null && !module.getDescription().isEmpty()) {
                        hoveredTooltip = module.getDescription();
                        tooltipX = mouseX;
                        tooltipY = y;
                    }

                    Vector4f moduleRadius = new Vector4f(0, 0, 0, 0);

                    if(isLastModule && !isModuleExpanded) {
                        moduleRadius = new Vector4f(0, CORNER_RADIUS, 0, CORNER_RADIUS);
                    }

                    if(module.isEnabled()) {
                        int primaryColor = Theme.COLOR$1;
                        int secondaryColor = Theme.COLOR$2;

                        primaryColor = primaryColor | 0xFF000000;
                        secondaryColor = secondaryColor | 0xFF000000;

                        renderGradientRoundedRect(context, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT,
                                primaryColor, secondaryColor, moduleRadius);
                    } else {
                        RenderUtility.drawRoundedRect(context, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT, moduleRadius, BACKGROUND_COLOR);
                    }

                    if(hoverModule) {
                        RenderUtility.drawRoundedRect(context, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT, moduleRadius, HOVER_COLOR);
                    }

                    int textColor;
                    if(module.isEnabled()) {
                        textColor = ColorUtility.getColor(ColorUtility.Colors.WHITE);
                    } else {
                        textColor = ColorUtility.getColor(ColorUtility.Colors.LIGHT_GRAY);
                    }

                    TextRendererUtility.renderText(
                            context,
                            module.getDisplayName(),
                            textColor,
                            categoryX + PADDING * 3,
                            y + PADDING + 2,
                            false
                    );

                    y += MODULE_HEIGHT;

                    if(isModuleExpanded) {
                        List<Setting<?>> visibleSettings = new ArrayList<>();
                        for(Setting<?> setting : module.getSettings()) {
                            if(setting.isVisible()) {
                                visibleSettings.add(setting);
                            }
                        }

                        for(int settingIndex = 0; settingIndex < visibleSettings.size(); settingIndex++) {
                            Setting<?> setting = visibleSettings.get(settingIndex);
                            boolean hoverSetting = isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT);
                            boolean isLastSetting = (settingIndex == visibleSettings.size() - 1);
                            boolean shouldRoundSetting = isLastModule && isLastSetting;

                            if(showTooltips && hoverSetting && setting.getDescription() != null && !setting.getDescription().isEmpty()) {
                                hoveredTooltip = setting.getDescription();
                                tooltipX = mouseX;
                                tooltipY = y;
                            }

                            Vector4f settingRadius = new Vector4f(0, 0, 0, 0);
                            if(shouldRoundSetting) {
                                settingRadius = new Vector4f(0, CORNER_RADIUS, 0, CORNER_RADIUS);
                            }

                            RenderUtility.drawRoundedRect(context, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT, settingRadius, BACKGROUND_COLOR);

                            int settingTextColor = ColorUtility.getColor(ColorUtility.Colors.LIGHT_GRAY);

                            TextRendererUtility.renderText(
                                    context,
                                    setting.getName() + ": ",
                                    settingTextColor,
                                    categoryX + PADDING * 3 + SETTING_INDENT,
                                    y + PADDING + 4,
                                    false
                            );

                            switch(setting) {
                                case BooleanSetting booleanSetting -> {
                                    boolean isOn = booleanSetting.getValue();
                                    int switchWidth = 30;
                                    int switchHeight = 14;
                                    int switchX = categoryX + PANEL_WIDTH - switchWidth - PADDING * 3;
                                    int switchY = y + PADDING + 2;

                                    int bgColor = isOn ? 0xFF777777 : 0xFF555555;
                                    RenderUtility.drawRoundedRect(context, switchX, switchY, switchWidth, switchHeight, new Vector4f(7, 7, 7, 7), bgColor);

                                    int knobSize = 10;
                                    int knobX = isOn ? switchX + switchWidth - knobSize - 2 : switchX + 2;
                                    int knobY = switchY + (switchHeight - knobSize) / 2;
                                    RenderUtility.drawRoundedRect(context, knobX, knobY, knobSize, knobSize, new Vector4f(5, 5, 5, 5), 0xFFFFFFFF);

                                }
                                case ModeSetting modeSetting -> {
                                    String currentMode = modeSetting.getValue();
                                    int dropdownWidth = PANEL_WIDTH - TextRendererUtility.getTextWidth(setting.getName() + ": ") - PADDING * 6 - SETTING_INDENT;
                                    int dropdownX = categoryX + PANEL_WIDTH - dropdownWidth - PADDING * 3;
                                    int dropdownY = y + PADDING + 2;
                                    int dropdownHeight = 14;

                                    RenderUtility.drawRoundedRect(context, dropdownX, dropdownY, dropdownWidth, dropdownHeight, new Vector4f(4, 4, 4, 4), 0xFF333333);

                                    TextRendererUtility.renderText(
                                            context,
                                            currentMode,
                                            ColorUtility.getColor(ColorUtility.Colors.WHITE),
                                            dropdownX + 5,
                                            dropdownY + 3,
                                            false
                                    );

                                    RenderUtility.drawRoundedRect(context,
                                            dropdownX + dropdownWidth - 12,
                                            dropdownY + 5,
                                            8,
                                            5,
                                            new Vector4f(1, 1, 1, 1),
                                            ColorUtility.getColor(ColorUtility.Colors.WHITE));
                                }
                                case NumberSetting<?> numberSetting -> {
                                    Number value = numberSetting.getValue();
                                    Number min = numberSetting.getMinValue();
                                    Number max = numberSetting.getMaxValue();

                                    int sliderWidth = PANEL_WIDTH - TextRendererUtility.getTextWidth(setting.getName() + ": ") - PADDING * 6 - SETTING_INDENT;
                                    int sliderX = categoryX + PANEL_WIDTH - sliderWidth - PADDING * 3;
                                    int sliderY = y + PADDING + 8;
                                    int sliderHeight = 4;

                                    double percentage = getPercentage(value, min, max);

                                    RenderUtility.drawRoundedRect(context, sliderX, sliderY, sliderWidth, sliderHeight, new Vector4f(2, 2, 2, 2), 0xFF555555);

                                    int filledWidth = (int) (sliderWidth * percentage);
                                    if(filledWidth > 0) {
                                        RenderUtility.drawRoundedRect(context, sliderX, sliderY, filledWidth, sliderHeight, new Vector4f(2, 2, 2, 2), Theme.COLOR$1);
                                    }

                                    int knobSize = 10;
                                    int knobX = sliderX + filledWidth - knobSize / 2;
                                    int knobY = sliderY + sliderHeight / 2 - knobSize / 2;
                                    RenderUtility.drawRoundedRect(context, knobX, knobY, knobSize, knobSize, new Vector4f(5, 5, 5, 5), 0xFFFFFFFF);

                                    String valueText = value.toString();
                                    TextRendererUtility.renderText(
                                            context,
                                            valueText,
                                            ColorUtility.getColor(ColorUtility.Colors.WHITE),
                                            sliderX + sliderWidth / 2 - TextRendererUtility.getTextWidth(valueText) / 2,
                                            y + 2,
                                            false
                                    );
                                }
                                case MultipleBooleanSetting multipleBooleanSetting -> {
                                    int dropdownWidth = PANEL_WIDTH - TextRendererUtility.getTextWidth(setting.getName() + ": ") - PADDING * 6 - SETTING_INDENT;
                                    int dropdownX = categoryX + PANEL_WIDTH - dropdownWidth - PADDING * 3;
                                    int dropdownY = y + PADDING + 2;
                                    int dropdownHeight = 14;

                                    RenderUtility.drawRoundedRect(context, dropdownX, dropdownY, dropdownWidth, dropdownHeight, new Vector4f(4, 4, 4, 4), BACKGROUND_COLOR);

                                    List<String> enabledOptions = multipleBooleanSetting.getEnabledOptions();
                                    String displayText = enabledOptions.isEmpty() ? "None" : String.join(", ", enabledOptions);

                                    if(TextRendererUtility.getTextWidth(displayText) > dropdownWidth - 20) {
                                        displayText = "Mult";
                                    }

                                    TextRendererUtility.renderText(
                                            context,
                                            displayText,
                                            ColorUtility.getColor(ColorUtility.Colors.WHITE),
                                            dropdownX + 5,
                                            dropdownY + 3,
                                            false
                                    );

                                    RenderUtility.drawRoundedRect(context,
                                            dropdownX + dropdownWidth - 12,
                                            dropdownY + 5,
                                            8,
                                            5,
                                            new Vector4f(1, 1, 1, 1),
                                            ColorUtility.getColor(ColorUtility.Colors.WHITE));

                                    if(multipleBooleanSetting.isExpanded()) {
                                        List<String> options = multipleBooleanSetting.getOptions();
                                        int optionHeight = 14;
                                        int totalOptionsHeight = options.size() * optionHeight;
                                        int optionsY = dropdownY + dropdownHeight + 2;

                                        RenderUtility.drawRoundedRect(context,
                                                dropdownX,
                                                optionsY,
                                                dropdownWidth,
                                                totalOptionsHeight,
                                                new Vector4f(4, 4, 4, 4),
                                                BACKGROUND_COLOR);

                                        for(int i = 0; i < options.size(); i++) {
                                            String option = options.get(i);
                                            boolean isEnabled = multipleBooleanSetting.isEnabled(option);
                                            int optionY = optionsY + (i * optionHeight);

                                            if(isEnabled) {
                                                RenderUtility.drawRoundedRect(context,
                                                        dropdownX + 2,
                                                        optionY + 2,
                                                        dropdownWidth - 4,
                                                        optionHeight - 4,
                                                        new Vector4f(3, 3, 3, 3),
                                                        Theme.COLOR$1
                                                );

                                                int checkboxSize = 8;
                                                int checkboxX = dropdownX + dropdownWidth - checkboxSize - 5;
                                                int checkboxY = optionY + (optionHeight - checkboxSize) / 2;
                                                RenderUtility.drawRoundedRect(context,
                                                        checkboxX,
                                                        checkboxY,
                                                        checkboxSize,
                                                        checkboxSize,
                                                        new Vector4f(2, 2, 2, 2),
                                                        0xFFFFFFFF);
                                            }

                                            TextRendererUtility.renderText(
                                                    context,
                                                    option,
                                                    ColorUtility.getColor(isEnabled ? ColorUtility.Colors.WHITE : ColorUtility.Colors.LIGHT_GRAY),
                                                    dropdownX + 5,
                                                    optionY + 3,
                                                    false
                                            );
                                        }

                                        y += totalOptionsHeight + 2;
                                    }
                                }
                                default -> {
                                    String valueText = setting.getValue().toString();
                                    int valueColor = ColorUtility.getColor(ColorUtility.Colors.WHITE);

                                    TextRendererUtility.renderText(
                                            context,
                                            valueText,
                                            valueColor,
                                            categoryX + PANEL_WIDTH - TextRendererUtility.getTextWidth(valueText) - PADDING * 3,
                                            y + PADDING + 4,
                                            false
                                    );
                                }
                            }

                            y += SETTING_HEIGHT;
                        }
                    }
                }
            }
        }

        if(showTooltips && hoveredTooltip != null) {
            renderTooltip(context, hoveredTooltip, tooltipX, tooltipY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private static double getPercentage(Number value, Number min, Number max) {
        double percentage = 0;
        if(value instanceof Integer) {
            percentage = (double) ((Integer) value - (Integer) min) / ((Integer) max - (Integer) min);
        } else if(value instanceof Float) {
            percentage = (double) ((Float) value - (Float) min) / ((Float) max - (Float) min);
        } else if(value instanceof Double) {
            percentage = ((Double) value - (Double) min) / ((Double) max - (Double) min);
        }
        return percentage;
    }

    private void renderTooltip(DrawContext context, String text, int x, int y) {
        if(text == null || text.isEmpty()) return;

        List<String> lines = wrapText(text, TOOLTIP_MAX_WIDTH);
        if(lines.isEmpty()) return;

        int maxLineWidth = 0;
        for(String line : lines) {
            int lineWidth = TextRendererUtility.getTextWidth(line);
            if(lineWidth > maxLineWidth) {
                maxLineWidth = lineWidth;
            }
        }

        int tooltipWidth = maxLineWidth + TOOLTIP_PADDING * 2;
        int tooltipHeight = lines.size() * 12 + TOOLTIP_PADDING * 2;

        int tooltipX = x;
        int tooltipY = y - tooltipHeight - TOOLTIP_OFFSET;

        if(tooltipX + tooltipWidth > width) {
            tooltipX = width - tooltipWidth - 5;
        }

        if(tooltipX < 0) {
            tooltipX = 5;
        }

        if(tooltipY < 0) {
            tooltipY = y + MODULE_HEIGHT + TOOLTIP_OFFSET;
        }

        if(tooltipY + tooltipHeight > height) {
            tooltipY = height - tooltipHeight - 5;
        }

        Vector4f tooltipRadius = new Vector4f(4, 4, 4, 4);
        RenderUtility.drawRoundedRect(context, tooltipX, tooltipY, tooltipWidth, tooltipHeight, tooltipRadius, TOOLTIP_BACKGROUND);
        RenderUtility.drawRoundedRectOutline(context, tooltipX, tooltipY, tooltipWidth, tooltipHeight, tooltipRadius.x, 1, TOOLTIP_BORDER);

        for(int i = 0; i < lines.size(); i++) {
            TextRendererUtility.renderText(
                    context,
                    lines.get(i),
                    ColorUtility.Colors.WHITE,
                    tooltipX + TOOLTIP_PADDING,
                    tooltipY + TOOLTIP_PADDING + i * 12,
                    false
            );
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for(String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;

            if(TextRendererUtility.getTextWidth(testLine) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if(!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }

        if(!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= (int) (verticalAmount * 20);
        scrollOffset = Math.max(0, scrollOffset);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 0) {
            for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
                ModuleCategory category = entry.getKey();
                List<Module> modules = entry.getValue();

                DragUtility dragUtil = categoryDragUtils.get(category);
                int categoryX = dragUtil.getX();
                int y = dragUtil.getY() - scrollOffset + CATEGORY_HEIGHT;

                if(expandedCategories.get(category)) {
                    for(Module module : modules) {
                        if(isMouseOver((int) mouseX, (int) mouseY, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT)) {
                            module.toggle();
                            return true;
                        }

                        y += MODULE_HEIGHT;

                        if(expandedModules.getOrDefault(module, false)) {
                            for(Setting<?> setting : module.getSettings()) {
                                if(!setting.isVisible()) continue;

                                if(setting instanceof NumberSetting<?> numberSetting) {
                                    int sliderWidth = PANEL_WIDTH - TextRendererUtility.getTextWidth(setting.getName() + ": ") - PADDING * 6 - SETTING_INDENT;
                                    int sliderX = categoryX + PANEL_WIDTH - sliderWidth - PADDING * 3;
                                    int sliderY = y + PADDING + 8;
                                    int sliderHeight = 4;

                                    if(isMouseOver((int) mouseX, (int) mouseY, sliderX, sliderY - 5, sliderWidth, sliderHeight + 10)) {
                                        draggingNumberSetting = true;
                                        currentDraggedNumberSetting = numberSetting;
                                        sliderStartX = sliderX;
                                        this.sliderWidth = sliderWidth;

                                        updateNumberSettingFromMouse((int) mouseX);
                                        return true;
                                    }
                                } else if(setting instanceof MultipleBooleanSetting multipleBooleanSetting) {
                                    int dropdownWidth = PANEL_WIDTH - TextRendererUtility.getTextWidth(setting.getName() + ": ") - PADDING * 6 - SETTING_INDENT;
                                    int dropdownX = categoryX + PANEL_WIDTH - dropdownWidth - PADDING * 3;
                                    int dropdownY = y + PADDING + 2;
                                    int dropdownHeight = 14;

                                    if(isMouseOver((int) mouseX, (int) mouseY, dropdownX, dropdownY, dropdownWidth, dropdownHeight)) {
                                        multipleBooleanSetting.toggleExpanded();
                                        return true;
                                    }

                                    if(multipleBooleanSetting.isExpanded()) {
                                        List<String> options = multipleBooleanSetting.getOptions();
                                        int optionHeight = 14;
                                        int optionsY = dropdownY + dropdownHeight + 2;

                                        for(int i = 0; i < options.size(); i++) {
                                            String option = options.get(i);
                                            int optionY = optionsY + (i * optionHeight);

                                            if(isMouseOver((int) mouseX, (int) mouseY, dropdownX, optionY, dropdownWidth, optionHeight)) {
                                                multipleBooleanSetting.toggle(option);
                                                return true;
                                            }
                                        }
                                    }
                                }

                                y += SETTING_HEIGHT;
                            }
                        }
                    }
                }
            }

            for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
                ModuleCategory category = entry.getKey();
                DragUtility dragUtil = categoryDragUtils.get(category);
                int categoryX = dragUtil.getX();
                int y = dragUtil.getY() - scrollOffset;

                if(isMouseOver((int) mouseX, (int) mouseY, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT)) {
                    draggingCategory = category;
                    dragUtil.startDragging((int) mouseX, (int) mouseY);
                    return true;
                }
            }

        } else if(button == 1) {
            handleRightClick((int) mouseX, (int) mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(button == 0) {
            if(draggingNumberSetting && currentDraggedNumberSetting != null) {
                updateNumberSettingFromMouse((int) mouseX);
                return true;
            }

            if(draggingCategory != null) {
                DragUtility dragUtil = categoryDragUtils.get(draggingCategory);
                if(dragUtil.isDragging()) {
                    dragUtil.updateDragPosition((int) mouseX, (int) mouseY);
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if(button == 0) {
            if(draggingNumberSetting) {
                draggingNumberSetting = false;
                currentDraggedNumberSetting = null;
                return true;
            }

            if(draggingCategory != null) {
                DragUtility dragUtil = categoryDragUtils.get(draggingCategory);
                if(dragUtil.isDragging()) {
                    dragUtil.stopDragging();
                    draggingCategory = null;
                    return true;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @SuppressWarnings("unchecked")
    private void updateNumberSettingFromMouse(int mouseX) {
        if(currentDraggedNumberSetting == null) return;

        double clickPercentage = (double) (mouseX - sliderStartX) / sliderWidth;
        clickPercentage = Math.max(0, Math.min(1, clickPercentage));

        Number min = currentDraggedNumberSetting.getMinValue();
        Number max = currentDraggedNumberSetting.getMaxValue();
        Number currentValue = currentDraggedNumberSetting.getValue();

        if(currentValue instanceof Integer && min instanceof Integer && max instanceof Integer) {
            int range = (Integer) max - (Integer) min;
            int newValue = (Integer) min + (int) (range * clickPercentage);
            ((NumberSetting<Integer>) currentDraggedNumberSetting).setValue(newValue);
        } else if(currentValue instanceof Float && min instanceof Float && max instanceof Float) {
            float range = (Float) max - (Float) min;
            float newValue = (Float) min + (range * (float) clickPercentage);
            newValue = Math.round(newValue * 10) / 10.0f;
            ((NumberSetting<Float>) currentDraggedNumberSetting).setValue(newValue);
        } else if(currentValue instanceof Double && min instanceof Double && max instanceof Double) {
            double range = (Double) max - (Double) min;
            double newValue = (Double) min + (range * clickPercentage);
            newValue = Math.round(newValue * 10) / 10.0;
            ((NumberSetting<Double>) currentDraggedNumberSetting).setValue(newValue);
        }
    }

    private void handleRightClick(int mouseX, int mouseY) {
        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            DragUtility dragUtil = categoryDragUtils.get(category);
            int categoryX = dragUtil.getX();
            int y = dragUtil.getY() - scrollOffset;

            if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT)) {
                boolean newState = !expandedCategories.get(category);
                expandedCategories.put(category, newState);
                return;
            }

            y += CATEGORY_HEIGHT;

            if(expandedCategories.get(category)) {
                for(Module module : modules) {
                    if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT)) {
                        boolean newState = !expandedModules.getOrDefault(module, false);
                        expandedModules.put(module, newState);
                        return;
                    }

                    y += MODULE_HEIGHT;

                    if(expandedModules.getOrDefault(module, false)) {
                        for(Setting<?> setting : module.getSettings()) {
                            if(!setting.isVisible()) continue;

                            int controlWidth = PANEL_WIDTH - TextRendererUtility.getTextWidth(setting.getName() + ": ") - PADDING * 6 - SETTING_INDENT;
                            int controlX = categoryX + PANEL_WIDTH - controlWidth - PADDING * 3;
                            int controlY = y + PADDING + 2;
                            int controlHeight = 14;

                            if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT)) {
                                switch(setting) {
                                    case BooleanSetting booleanSetting -> {
                                        booleanSetting.toggle();
                                    }
                                    case ModeSetting modeSetting -> {
                                        modeSetting.cycle();
                                    }
                                    case NumberSetting numberSetting -> {
                                        numberSetting.decrement(false);
                                    }
                                    case MultipleBooleanSetting multipleBooleanSetting -> {
                                        multipleBooleanSetting.toggleExpanded();
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
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX <= x + width &&
                mouseY >= y && mouseY <= y + height;
    }

    private void renderGradientRoundedRect(DrawContext context, int x, int y, int width, int height, int color1, int color2, Vector4f radius) {
        RenderUtility.drawGradientRoundedRect(context, x, y, width, height, radius, color1, color2);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
