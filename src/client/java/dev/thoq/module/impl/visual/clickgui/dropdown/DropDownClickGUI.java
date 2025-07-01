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
import dev.thoq.config.setting.impl.NumberSetting;
import dev.thoq.config.setting.Setting;
import dev.thoq.config.setting.impl.SliderSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.RenderUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import dev.thoq.utilities.render.Theme;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
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
    private static final int SETTING_HEIGHT = 20;
    private static final int SETTING_INDENT = 5;
    private static final int CATEGORY_HEIGHT = 24;
    private static final int MODULE_HEIGHT = 25;
    private static final int PANEL_WIDTH = 160;
    private static final int PANEL_X = 70;
    private static final int PANEL_Y = 20;
    private static final int PANEL_SPACING = 15;
    private static final int PADDING = 6;
    private static final int BACKGROUND_COLOR = ColorUtility.getColor(ColorUtility.Colors.GRAY);
    private static final int CATEGORY_COLOR = 0xFF222222;
    private static final int HOVER_COLOR = 0x10FFFFFF;
    private static final float CORNER_RADIUS = 5.0f;
    private static final int TOOLTIP_BACKGROUND = 0xE0000000;
    private static final int TOOLTIP_BORDER = 0xFF555555;
    private static final int TOOLTIP_MAX_WIDTH = 200;
    private static final int TOOLTIP_PADDING = 8;

    private String hoveredTooltip = null;
    private int tooltipX = 0;
    private int tooltipY = 0;

    public DropDownClickGUI() {
        super(Text.literal("Click GUI"));

        for(ModuleCategory category : ModuleCategory.values()) {
            categorizedModules.put(category, new ArrayList<>());
            expandedCategories.put(category, true);
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
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if(Theme.get("Rye") == null) {
            Theme.init();
        }

        hoveredTooltip = null;
        int categoryIndex = 0;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            boolean expanded = expandedCategories.get(category);
            int categoryX = PANEL_X + (PANEL_WIDTH + PANEL_SPACING) * categoryIndex;
            int y = PANEL_Y;

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
                int moduleIndex = 0;

                for(Module module : modules) {
                    boolean hoverModule = isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT);
                    boolean isLastModule = (moduleIndex == modules.size() - 1);
                    boolean isModuleExpanded = expandedModules.getOrDefault(module, false);

                    if(hoverModule && module.getDescription() != null && !module.getDescription().isEmpty()) {
                        hoveredTooltip = module.getDescription();
                        tooltipX = mouseX;
                        tooltipY = mouseY;
                    }

                    Vector4f moduleRadius = new Vector4f(0, 0, 0, 0);

                    if(isLastModule && !isModuleExpanded) {
                        moduleRadius = new Vector4f(0, CORNER_RADIUS, 0, CORNER_RADIUS);
                    }

                    if(module.isEnabled()) {
                        Theme currentTheme = Theme.getCurrentTheme();
                        int primaryColor = currentTheme.getPrimaryColorInt();
                        int secondaryColor = currentTheme.getSecondaryColorInt();

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
                    moduleIndex++;

                    if(isModuleExpanded) {
                        List<Setting<?>> visibleSettings = new ArrayList<>();
                        for(Setting<?> setting : module.getSettings()) {
                            if(setting.isVisible()) {
                                visibleSettings.add(setting);
                            }
                        }

                        int settingIndex = 0;
                        for(Setting<?> setting : visibleSettings) {
                            boolean hoverSetting = isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT);
                            boolean isLastSetting = (settingIndex == visibleSettings.size() - 1);
                            boolean shouldRoundSetting = isLastModule && isLastSetting;

                            if(hoverSetting && setting.getDescription() != null && !setting.getDescription().isEmpty()) {
                                hoveredTooltip = setting.getDescription();
                                tooltipX = mouseX;
                                tooltipY = mouseY;
                            }

                            Vector4f settingRadius = new Vector4f(0, 0, 0, 0);
                            if(shouldRoundSetting) {
                                settingRadius = new Vector4f(0, CORNER_RADIUS, 0, CORNER_RADIUS);
                            }

                            RenderUtility.drawRoundedRect(context, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT, settingRadius, BACKGROUND_COLOR);

                            TextRendererUtility.renderText(
                                    context,
                                    setting.getName() + ": ",
                                    ColorUtility.Colors.LIGHT_GRAY,
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

                            int valueColor = ColorUtility.getColor(ColorUtility.Colors.WHITE);

                            TextRendererUtility.renderText(
                                    context,
                                    valueText,
                                    valueColor,
                                    categoryX + PANEL_WIDTH - TextRendererUtility.getTextWidth(valueText) - PADDING * 3,
                                    y + PADDING + 2,
                                    false
                            );
                            y += SETTING_HEIGHT;
                            settingIndex++;
                        }
                    }
                }
            }
            categoryIndex++;
        }

        if(hoveredTooltip != null) {
            renderTooltip(context, hoveredTooltip, tooltipX, tooltipY);
        }

        super.render(context, mouseX, mouseY, delta);
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

        int tooltipX = x + 10;
        int tooltipY = y - 10;

        if(tooltipX + tooltipWidth > width) {
            tooltipX = x - tooltipWidth - 10;
        }
        if(tooltipY + tooltipHeight > height) {
            tooltipY = y - tooltipHeight + 10;
        }
        if(tooltipX < 0) {
            tooltipX = 5;
        }
        if(tooltipY < 0) {
            tooltipY = 5;
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
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;

            if(TextRendererUtility.getTextWidth(testLine) <= maxWidth) {
                currentLine = new StringBuilder(testLine);
            } else {
                if(currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }

        if(currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 0) {
            handleLeftClick((int) mouseX, (int) mouseY);
        } else if(button == 1) {
            handleRightClick((int) mouseX, (int) mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleLeftClick(int mouseX, int mouseY) {
        int categoryIndex = 0;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            int categoryX = PANEL_X + (PANEL_WIDTH + PANEL_SPACING) * categoryIndex;
            int y = PANEL_Y;

            if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT)) {
                expandedCategories.put(category, !expandedCategories.get(category));
                return;
            }

            y += CATEGORY_HEIGHT;

            if(expandedCategories.get(category)) {
                for(Module module : modules) {
                    if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT)) {
                        module.toggle();
                        return;
                    }

                    y += MODULE_HEIGHT;

                    if(expandedModules.getOrDefault(module, false)) {
                        for(Setting<?> setting : module.getSettings()) {
                            if(!setting.isVisible()) continue;
                            if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT)) {
                                switch(setting) {
                                    case BooleanSetting booleanSetting -> booleanSetting.toggle();
                                    case ModeSetting modeSetting -> modeSetting.cycle();
                                    case NumberSetting numberSetting -> numberSetting.increment(false);
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

    private void handleRightClick(int mouseX, int mouseY) {
        int categoryIndex = 0;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            int categoryX = PANEL_X + (PANEL_WIDTH + PANEL_SPACING) * categoryIndex;
            int y = PANEL_Y;

            if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT)) {
                expandedCategories.put(category, !expandedCategories.get(category));
                return;
            }

            y += CATEGORY_HEIGHT;

            if(expandedCategories.get(category)) {
                for(Module module : modules) {
                    if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT)) {
                        expandedModules.put(module, !expandedModules.getOrDefault(module, false));
                        return;
                    }

                    y += MODULE_HEIGHT;

                    if(expandedModules.getOrDefault(module, false)) {
                        for(Setting<?> setting : module.getSettings()) {
                            if(!setting.isVisible()) continue;

                            if(isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, SETTING_HEIGHT)) {
                                if(setting instanceof NumberSetting numberSetting) {
                                    numberSetting.decrement(false);
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