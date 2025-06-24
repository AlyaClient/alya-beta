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

package dev.thoq.module.impl.visual;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import dev.thoq.utilities.render.ThemeUtility;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ConstantValue")
public class ArraylistModule extends Module {
    private static final BooleanSetting showVisualModules = new BooleanSetting("Show Visual", "Should Arraylist show visual modules?", true);
    private static final ModeSetting position = new ModeSetting("Position", "Arraylist Position", "Right", "Left", "Right");

    public ArraylistModule() {
        super("Arraylist", "Render all active modules", ModuleCategory.VISUAL);

        addSetting(showVisualModules);
        addSetting(position);

        this.setEnabled(true);
    }

    private static List<Module> sortModulesByLength(Collection<Module> modules) {
        List<Module> activeModules = new ArrayList<>();
        for(Module module : modules) {
            if(module.isEnabled() && !(module instanceof ArraylistModule)) {
                if(Objects.equals(module.getName(), "ClickGUI")) continue;
                if(!showVisualModules.getValue() && module.getCategory() == ModuleCategory.VISUAL) continue;

                activeModules.add(module);
            }
        }

        activeModules.sort((module1, module2) -> {
            int width1 = TextRendererUtility.getTextWidth(module1.getName());
            int width2 = TextRendererUtility.getTextWidth(module2.getName());
            return Integer.compare(width2, width1);
        });

        return activeModules;
    }

    @Override
    protected void onRender(DrawContext context) {
        Collection<Module> allModules = ModuleRepository.getInstance().getModules();
        List<Module> activeModules = sortModulesByLength(allModules);

        if(activeModules.isEmpty()) return;

        final int padding = 2;
        final int leftTopMargin = 15;
        final int rightTopMargin = 2;
        final int sidePadding = 2;
        final int outlineWidth = 1;

        int screenWidth = mc.getWindow().getScaledWidth();
        boolean isLeftPosition = Objects.equals(position.getValue(), "Left");

        int currentY = isLeftPosition ? leftTopMargin : rightTopMargin;

        Color themeColor = ThemeUtility.getThemeColorFirst();
        int themeColorArgb = ColorUtility.getIntFromColor(themeColor);

        List<Integer> moduleWidths = new ArrayList<>();
        for(Module module : activeModules) {
            moduleWidths.add(TextRendererUtility.getTextWidth(module.getName()));
        }

        for(int i = 0; i < activeModules.size(); i++) {
            Module module = activeModules.get(i);
            String name = module.getName();
            int textWidth = moduleWidths.get(i);
            int x;

            int moduleLeft, moduleRight, moduleTop, moduleBottom;

            if(isLeftPosition) {
                x = sidePadding;
                moduleLeft = x - padding;
                moduleRight = x + textWidth + padding;
            } else {
                x = screenWidth - textWidth - sidePadding;
                moduleLeft = x - padding;
                moduleRight = screenWidth - sidePadding + padding;
            }

            moduleTop = currentY - padding;
            moduleBottom = currentY + mc.textRenderer.fontHeight + padding;

            context.fill(moduleLeft, moduleTop, moduleRight, moduleBottom, 0x90000000);

            if(i == 0) {
                context.fill(moduleLeft - outlineWidth, moduleTop - outlineWidth, moduleRight + outlineWidth, moduleTop, themeColorArgb);
            }

            if(i == activeModules.size() - 1) {
                context.fill(moduleLeft - outlineWidth, moduleBottom, moduleRight + outlineWidth, moduleBottom + outlineWidth, themeColorArgb);
            }

            context.fill(moduleLeft - outlineWidth, moduleTop, moduleLeft, moduleBottom, themeColorArgb);
            context.fill(moduleRight, moduleTop, moduleRight + outlineWidth, moduleBottom, themeColorArgb);

            if(isLeftPosition) {
                if(i < activeModules.size() - 1) {
                    int nextWidth = moduleWidths.get(i + 1);
                    int nextModuleRight = sidePadding + nextWidth + padding;
                    if(moduleRight > nextModuleRight) {
                        context.fill(nextModuleRight, moduleBottom, moduleRight + outlineWidth, moduleBottom + outlineWidth, themeColorArgb);
                    }
                }
            } else {

                if(i < activeModules.size() - 1) {
                    int nextWidth = moduleWidths.get(i + 1);
                    int nextModuleLeft = screenWidth - nextWidth - sidePadding - padding;
                    if(moduleLeft < nextModuleLeft) {
                        context.fill(moduleLeft - outlineWidth, moduleBottom, nextModuleLeft, moduleBottom + outlineWidth, themeColorArgb);
                    }
                }
            }

            TextRendererUtility.renderText(
                    context,
                    name,
                    ColorUtility.Colors.WHITE,
                    x,
                    currentY,
                    true
            );

            currentY += mc.textRenderer.fontHeight + padding * 2;
        }
    }
}