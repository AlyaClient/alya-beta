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

package works.alya.module.impl.visual.clickgui.astolfo;

import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.config.setting.Setting;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.ModuleRepository;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import works.alya.utilities.render.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This ClickGUI was inspired by Astolfo Clients ClickGUI.
 * The file is based on/adapted from
 * <a href="https://github.com/perzeroo/Astolfo-Like-Clickgui">Astolfo-Like-ClickGUI</a>
 * and modified to work inside FabricMC
 */
@SuppressWarnings({"rawtypes", "MismatchedQueryAndUpdateOfCollection"})
public class AstolfoClickGUI extends Screen {
    private final Map<ModuleCategory, List<Module>> categorizedModules = new EnumMap<>(ModuleCategory.class);
    private final Map<ModuleCategory, Boolean> expandedCategories = new EnumMap<>(ModuleCategory.class);
    private final Map<Module, Boolean> expandedModules = new HashMap<>();
    private final Map<ModuleCategory, DragUtility> categoryDragUtils = new EnumMap<>(ModuleCategory.class);
    private final Map<NumberSetting<?>, Integer> numberSettingPositions = new HashMap<>();

    private static final int PANEL_WIDTH = 100;
    private static final int PANEL_HEIGHT = 18;
    private static final int MODULE_HEIGHT = 18;
    private static final int SETTING_HEIGHT = 9;
    private static final int PANEL_SPACING = 120;
    private static final int BACKGROUND_COLOR = 0xFF181A17;
    private static final int MODULE_BACKGROUND_COLOR = 0xFF232623;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int BORDER_WIDTH = 2;
    private static final int DEFAULT_CATEGORY_COLOR = 0xFF666666;

    private static final Map<ModuleCategory, Integer> CATEGORY_COLORS = new EnumMap<>(ModuleCategory.class);

    static {
        CATEGORY_COLORS.put(ModuleCategory.COMBAT, 0xFFE64D3A);
        CATEGORY_COLORS.put(ModuleCategory.MOVEMENT, 0xFF2ECD6F);
        CATEGORY_COLORS.put(ModuleCategory.WORLD, 0xFFE65F00);
        CATEGORY_COLORS.put(ModuleCategory.VISUAL, 0xFF3A9DE6);
        CATEGORY_COLORS.put(ModuleCategory.UTILITY, 0xffF29D11);
        CATEGORY_COLORS.put(ModuleCategory.SCRIPTS, 0xFFE67300);
    }

    private boolean dragging = false;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    private ModuleCategory draggingCategory = null;
    private NumberSetting<?> currentDraggedNumberSetting = null;
    private int currentDraggedSettingX = 0;

    public AstolfoClickGUI() {
        super(Text.literal("Astolfo Click GUI"));
        initializeModules();

        int x = 4;
        for(ModuleCategory category : ModuleCategory.values()) {
            int finalX = x;
            categoryDragUtils.computeIfAbsent(category, k -> new DragUtility(finalX, 4));
            x += PANEL_SPACING;
        }
    }

    private void initializeModules() {
        categorizedModules.clear();
        expandedCategories.clear();

        for(ModuleCategory category : ModuleCategory.values()) {
            categorizedModules.put(category, new ArrayList<>());
            expandedCategories.put(category, false);
        }

        ModuleRepository repository = works.alya.AlyaClient.INSTANCE.getModuleRepository();
        for(Module module : repository.getModules()) {
            List<Module> categoryModules = categorizedModules.get(module.getCategory());
            if(categoryModules != null) {
                categoryModules.add(module);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0x80000000);

        for(ModuleCategory category : ModuleCategory.values()) {
            renderCategoryPanel(context, category, mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private void renderCategoryPanel(DrawContext context, ModuleCategory category, int mouseX, int mouseY) {
        DragUtility dragUtil = categoryDragUtils.get(category);
        if(dragUtil == null) return;

        int panelX = dragUtil.getX();
        int panelY = dragUtil.getY();

        if(dragging && draggingCategory == category) {
            panelX = mouseX + dragOffsetX;
            panelY = mouseY + dragOffsetY;
            dragUtil.setX(panelX);
            dragUtil.setY(panelY);
        }

        List<Module> modules = categorizedModules.get(category);
        if(modules == null) return;

        int totalHeight = PANEL_HEIGHT;

        if(expandedCategories.get(category)) {
            for(Module module : modules) {
                totalHeight += MODULE_HEIGHT;
                if(expandedModules.getOrDefault(module, false)) {
                    totalHeight += module.getSettings().size() * SETTING_HEIGHT;
                }
            }
        }

        RenderUtility.drawRect(context, panelX, panelY, PANEL_WIDTH, PANEL_HEIGHT, BACKGROUND_COLOR);

        String categoryName = category.name().toLowerCase();
        TextRendererUtility.renderText(context, categoryName, TEXT_COLOR, panelX + 4, panelY + 5, false);

        if(expandedCategories.get(category)) {
            int currentY = panelY + PANEL_HEIGHT;

            for(Module module : modules) {
                renderModuleButton(context, module, panelX, currentY, category);
                currentY += MODULE_HEIGHT;

                if(expandedModules.getOrDefault(module, false)) {
                    for(Setting setting : module.getSettings()) {
                        renderSettingButton(context, setting, panelX, currentY, category);
                        currentY += SETTING_HEIGHT;
                    }
                }
            }
        }

        int categoryColor = getCategoryColor(category);
        RenderUtility.drawRoundedRectOutline(context, panelX, panelY, PANEL_WIDTH, totalHeight, 0, BORDER_WIDTH, categoryColor);

        RenderUtility.drawRect(context, panelX, panelY + totalHeight, PANEL_WIDTH, 2, BACKGROUND_COLOR);
    }

    private void renderModuleButton(DrawContext context, Module module, int x, int y, ModuleCategory category) {
        RenderUtility.drawRect(context, x, y, PANEL_WIDTH, MODULE_HEIGHT, BACKGROUND_COLOR);

        boolean extended = expandedModules.getOrDefault(module, false);
        if(!extended) {
            int bgColor = module.isEnabled() ? getCategoryColor(category) : MODULE_BACKGROUND_COLOR;
            RenderUtility.drawRect(context, x + 2, y, PANEL_WIDTH - 4, MODULE_HEIGHT, bgColor);
        } else {
            RenderUtility.drawRect(context, x + 2, y, PANEL_WIDTH - 4, MODULE_HEIGHT, BACKGROUND_COLOR);
        }

        String moduleName = module.getName().toLowerCase();
        int textColor = extended ? (module.isEnabled() ? getCategoryColor(category) : TEXT_COLOR) : TEXT_COLOR;
        int textX = x + PANEL_WIDTH - TextRendererUtility.getTextWidth(moduleName) - 3;
        TextRendererUtility.renderText(context, moduleName, textColor, textX, y + 5, false);
    }

    private void renderSettingButton(DrawContext context, Setting setting, int x, int y, ModuleCategory category) {
        RenderUtility.drawRect(context, x, y, PANEL_WIDTH, SETTING_HEIGHT, BACKGROUND_COLOR);

        if(setting instanceof BooleanSetting boolSetting) {
            if(boolSetting.getValue()) {
                RenderUtility.drawRect(context, x + 3, y, PANEL_WIDTH - 6, SETTING_HEIGHT, getCategoryColor(category));
            }
            TextRendererUtility.renderDynamicText(
                    context,
                    setting.getName(),
                    TEXT_COLOR,
                    x + 4,
                    y,
                    false,
                    "sf_pro_rounded_regular",
                    8
            );

        } else if(setting instanceof ModeSetting modeSetting) {
            String text = setting.getName() + " > " + modeSetting.getValue();
            TextRendererUtility.renderDynamicText(
                    context,
                    text,
                    TEXT_COLOR,
                    x + 4,
                    y + 1,
                    false,
                    "sf_pro_rounded_regular",
                    8
            );

        } else if(setting instanceof NumberSetting<?> numberSetting) {
            numberSettingPositions.put(numberSetting, x);

            RenderUtility.drawRect(context, x, y, PANEL_WIDTH, SETTING_HEIGHT, BACKGROUND_COLOR);

            double value = numberSetting.getValue().doubleValue();
            double min = numberSetting.getMinValue().doubleValue();
            double max = numberSetting.getMaxValue().doubleValue();
            double percentage = (value - min) / (max - min);

            int fillWidth = (int) (percentage * (PANEL_WIDTH - 6));
            RenderUtility.drawRect(context, x + 3, y, fillWidth, SETTING_HEIGHT, getCategoryColor(category));

            String text = setting.getName() + ": " + Math.round(value * 100.0) / 100.0;
            TextRendererUtility.renderDynamicText(
                    context,
                    text,
                    TEXT_COLOR,
                    x + 4,
                    y,
                    false,
                    "sf_pro_rounded_regular",
                    8
            );
        }
    }

    private int getCategoryColor(ModuleCategory category) {
        return CATEGORY_COLORS.getOrDefault(category, DEFAULT_CATEGORY_COLOR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int intMouseX = (int) mouseX;
        int intMouseY = (int) mouseY;

        for(ModuleCategory category : ModuleCategory.values()) {
            DragUtility dragUtil = categoryDragUtils.get(category);
            if(dragUtil == null) continue;

            int panelX = dragUtil.getX();
            int panelY = dragUtil.getY();

            if(isMouseOver(intMouseX, intMouseY, panelX, panelY, PANEL_HEIGHT)) {
                if(button == 0) {
                    dragging = true;
                    draggingCategory = category;
                    dragOffsetX = panelX - intMouseX;
                    dragOffsetY = panelY - intMouseY;
                } else if(button == 1) {
                    expandedCategories.put(category, !expandedCategories.get(category));
                }
                return true;
            }

            if(expandedCategories.get(category)) {
                List<Module> modules = categorizedModules.get(category);
                if(modules != null) {
                    int currentY = panelY + PANEL_HEIGHT;

                    for(Module module : modules) {
                        if(isMouseOver(intMouseX, intMouseY, panelX, currentY, MODULE_HEIGHT)) {
                            if(button == 0) {
                                module.toggle();
                            } else if(button == 1 && !module.getSettings().isEmpty()) {
                                expandedModules.put(module, !expandedModules.getOrDefault(module, false));
                            }
                            return true;
                        }
                        currentY += MODULE_HEIGHT;

                        if(expandedModules.getOrDefault(module, false)) {
                            for(Setting setting : module.getSettings()) {
                                if(isMouseOver(intMouseX, intMouseY, panelX, currentY, SETTING_HEIGHT)) {
                                    if(setting instanceof NumberSetting<?> numberSetting && button == 0) {
                                        currentDraggedNumberSetting = numberSetting;
                                        currentDraggedSettingX = panelX;
                                        updateNumberSettingFromMouse(intMouseX);
                                    } else {
                                        handleSettingClick(setting, button);
                                    }
                                    return true;
                                }
                                currentY += SETTING_HEIGHT;
                            }
                        }
                    }
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void handleSettingClick(Setting setting, int button) {
        if(setting instanceof BooleanSetting boolSetting) {
            boolSetting.setValue(!boolSetting.getValue());
        } else if(setting instanceof ModeSetting modeSetting) {
            if(button == 0) {
                modeSetting.cycle();
            } else if(button == 1) {
                String currentValue = modeSetting.getValue();
                List<String> modes = modeSetting.getModes();
                int currentIndex = modes.indexOf(currentValue);
                int previousIndex = (currentIndex - 1 + modes.size()) % modes.size();
                modeSetting.setValue(modes.get(previousIndex));
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if(dragging && draggingCategory != null) {
            return true;
        }

        if(currentDraggedNumberSetting != null) {
            updateNumberSettingFromMouse((int) mouseX);
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        draggingCategory = null;
        currentDraggedNumberSetting = null;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @SuppressWarnings("unchecked")
    private void updateNumberSettingFromMouse(int mouseX) {
        if(currentDraggedNumberSetting == null) return;

        int settingX = currentDraggedSettingX;
        int sliderStart = settingX + 3;
        int sliderWidth = PANEL_WIDTH - 6;

        int relativeX = mouseX - sliderStart;
        relativeX = Math.max(0, Math.min(relativeX, sliderWidth));

        double percentage = (double) relativeX / sliderWidth;

        double min = currentDraggedNumberSetting.getMinValue().doubleValue();
        double max = currentDraggedNumberSetting.getMaxValue().doubleValue();
        double newValue = min + (percentage * (max - min));

        Object currentValue = currentDraggedNumberSetting.getValue();
        var setting = (NumberSetting<Number>) currentDraggedNumberSetting;
        if(currentValue instanceof Integer) {
            setting.setValue((int) Math.round(newValue));
        } else if(currentValue instanceof Float) {
            setting.setValue((float) newValue);
        } else if(currentValue instanceof Double) {
            setting.setValue(newValue);
        } else if(currentValue instanceof Long) {
            setting.setValue(Math.round(newValue));
        }
    }

    private boolean isMouseOver(int mouseX, int mouseY, int x, int y, int height) {
        return mouseX >= x && mouseX <= x + AstolfoClickGUI.PANEL_WIDTH && mouseY >= y && mouseY <= y + height;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

}