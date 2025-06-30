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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

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
        int categoryIndex = 0;

        for(Map.Entry<ModuleCategory, List<Module>> entry : categorizedModules.entrySet()) {
            ModuleCategory category = entry.getKey();
            List<Module> modules = entry.getValue();

            boolean expanded = expandedCategories.get(category);
            int categoryX = PANEL_X + (PANEL_WIDTH + PANEL_SPACING) * categoryIndex;
            int y = PANEL_Y;
            boolean hoverCategory = isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, CATEGORY_HEIGHT);

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
                    boolean hoverModule = isMouseOver(mouseX, mouseY, categoryX, y, PANEL_WIDTH, MODULE_HEIGHT);

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

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == 0) {
            handleLeftClick((int)mouseX, (int)mouseY);
        } else if(button == 1) {
            handleRightClick((int)mouseX, (int)mouseY);
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

    private void renderRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}