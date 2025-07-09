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

package works.alya.module.impl.visual;

import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.Render2DEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.module.ModuleRepository;
import works.alya.utilities.render.ColorUtility;
import works.alya.utilities.render.TextRendererUtility;
import works.alya.utilities.render.Theme;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArraylistModule extends Module {
    private static final BooleanSetting showVisualModules = new BooleanSetting("Show Visual", "Should Arraylist show visual modules?", true);
    private static final ModeSetting position = new ModeSetting("Position", "Arraylist Position", "Right", "Left", "Right");

    private final Map<Module, Float> moduleAnimations = new HashMap<>();
    private final Map<Module, Long> animationStartTimes = new HashMap<>();
    private final Map<Module, Boolean> wasEnabled = new HashMap<>();
    private static final long SLIDE_DURATION = 1000;

    public ArraylistModule() {
        super("Arraylist", "Array List", "Render all active modules", ModuleCategory.VISUAL);

        addSetting(showVisualModules);
        addSetting(position);

        this.setEnabled(true);
    }

    private float getAnimationValue(Module module) {
        if(!moduleAnimations.containsKey(module)) {
            moduleAnimations.put(module, module.isEnabled() ? 1.0f : 0.0f);
            wasEnabled.put(module, module.isEnabled());
            return moduleAnimations.get(module);
        }

        boolean currentlyEnabled = module.isEnabled();
        boolean previouslyEnabled = wasEnabled.getOrDefault(module, false);

        if(currentlyEnabled != previouslyEnabled) {
            animationStartTimes.put(module, System.currentTimeMillis());
            wasEnabled.put(module, currentlyEnabled);
        }

        Long startTime = animationStartTimes.get(module);
        if(startTime == null) {
            return currentlyEnabled ? 1.0f : 0.0f;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(elapsed / (float) SLIDE_DURATION, 1.0f);
        progress = 1.0f - (float) Math.pow(1.0f - progress, 3);

        float targetValue = currentlyEnabled ? 1.0f : 0.0f;
        float currentValue = moduleAnimations.get(module);
        float newValue = currentValue + (targetValue - currentValue) * progress;

        moduleAnimations.put(module, newValue);
        return newValue;
    }

    private int getWaveColor(int index, int totalModules) {
        float time = System.currentTimeMillis() / 1000.0f;
        float waveOffset = (float) index / Math.max(1, totalModules - 1);
        float phase = time * 4.0f + waveOffset * 6.0f;
        float factor = (float) (Math.sin(phase) + 1.0) / 2.0f;

        return Theme.getInterpolatedColors(factor);
    }

    /**
     * Gets the full display name for a module including any suffixes
     * Format: ModuleName [Suffix]
     */
    private String getModuleDisplayName(Module module) {
        StringBuilder displayName = new StringBuilder();

        displayName.append(module.getDisplayName());

        if(module.getPrefix() != null && !module.getPrefix().isEmpty()) {
            displayName.append(" ").append(module.getPrefix());
        }

        return displayName.toString();
    }

    @SuppressWarnings("unused")
    private final IEventListener<Render2DEvent> renderEvents = event -> {
        Collection<Module> allModules = ModuleRepository.getInstance().getModules();

        List<Module> animatingModules = new ArrayList<>();
        for(Module module : ModuleRepository.getInstance().getModules()) {
            float animValue = getAnimationValue(module);
            if(animValue > 0.0f) {
                animatingModules.add(module);
            }
        }


        animatingModules.sort((module1, module2) -> {
            String displayName1 = getModuleDisplayName(module1);
            String displayName2 = getModuleDisplayName(module2);

            int width1 = TextRendererUtility.getTextWidth(displayName1);
            int width2 = TextRendererUtility.getTextWidth(displayName2);

            return Integer.compare(width2, width1);
        });

        if(animatingModules.isEmpty()) return;

        final int padding = 2;
        final int leftTopMargin = 15;
        final int rightTopMargin = 2;
        final int sidePadding = 2;
        final int outlineWidth = 1;

        int screenWidth = mc.getWindow().getScaledWidth();
        boolean isLeftPosition = Objects.equals(position.getValue(), "Left");

        int currentY = isLeftPosition ? leftTopMargin : rightTopMargin;

        List<Integer> moduleWidths = new ArrayList<>();
        for(Module module : animatingModules) {
            String displayName = getModuleDisplayName(module);
            int width = TextRendererUtility.getTextWidth(displayName);
            moduleWidths.add(width);
        }

        for(int i = 0; i < animatingModules.size(); i++) {
            Module module = animatingModules.get(i);
            int textWidth = moduleWidths.get(i);
            float animValue = getAnimationValue(module);

            if(animValue <= 0.0f) continue;

            int animatedWidth = (int) (textWidth * animValue);
            int x;

            int moduleLeft, moduleRight, moduleTop, moduleBottom;

            if(isLeftPosition) {
                x = sidePadding - (int) ((textWidth - animatedWidth) * animValue);
                moduleLeft = x - padding;
                moduleRight = x + animatedWidth + padding;
            } else {
                x = screenWidth - animatedWidth - sidePadding + (int) ((textWidth - animatedWidth) * animValue);
                moduleLeft = x - padding;
                moduleRight = screenWidth - sidePadding + padding - (int) ((textWidth - animatedWidth) * animValue);
            }

            moduleTop = currentY - padding;
            moduleBottom = currentY + mc.textRenderer.fontHeight + padding;

            int panelColor = ColorUtility.getColor(ColorUtility.Colors.PANEL);
            int panelAlpha = (int) (255 * animValue);
            panelColor = (panelColor & 0x00FFFFFF) | (panelAlpha << 24);

            event.getContext().fill(moduleLeft, moduleTop, moduleRight, moduleBottom, panelColor);

            int waveColor = getWaveColor(i, animatingModules.size());
            int waveAlpha = (int) (255 * animValue);
            waveColor = (waveColor & 0x00FFFFFF) | (waveAlpha << 24);

            if(i == 0) {
                event.getContext().fill(moduleLeft - outlineWidth, moduleTop - outlineWidth, moduleRight + outlineWidth, moduleTop, waveColor);
            }

            if(i == animatingModules.size() - 1) {
                event.getContext().fill(moduleLeft - outlineWidth, moduleBottom, moduleRight + outlineWidth, moduleBottom + outlineWidth, waveColor);
            }

            event.getContext().fill(moduleLeft - outlineWidth, moduleTop, moduleLeft, moduleBottom, waveColor);
            event.getContext().fill(moduleRight, moduleTop, moduleRight + outlineWidth, moduleBottom, waveColor);

            if(isLeftPosition) {
                if(i < animatingModules.size() - 1) {
                    Module nextModule = animatingModules.get(i + 1);
                    float nextAnimValue = getAnimationValue(nextModule);
                    int nextWidth = (int) (moduleWidths.get(i + 1) * nextAnimValue);
                    int nextModuleRight = sidePadding + nextWidth + padding;
                    if(moduleRight > nextModuleRight) {
                        event.getContext().fill(nextModuleRight, moduleBottom, moduleRight + outlineWidth, moduleBottom + outlineWidth, waveColor);
                    }
                }
            } else {
                if(i < animatingModules.size() - 1) {
                    Module nextModule = animatingModules.get(i + 1);
                    float nextAnimValue = getAnimationValue(nextModule);
                    int nextWidth = (int) (moduleWidths.get(i + 1) * nextAnimValue);
                    int nextModuleLeft = screenWidth - nextWidth - sidePadding - padding;
                    if(moduleLeft < nextModuleLeft) {
                        event.getContext().fill(moduleLeft - outlineWidth, moduleBottom, nextModuleLeft, moduleBottom + outlineWidth, waveColor);
                    }
                }
            }

            int textColor = ColorUtility.getColor(ColorUtility.Colors.WHITE);
            int textAlpha = (int) (255 * animValue);
            textColor = (textColor & 0x00FFFFFF) | (textAlpha << 24);

            event.getContext().enableScissor(moduleLeft, moduleTop, moduleRight, moduleBottom);

            renderModuleText(event, module, textColor, x, currentY, animValue);

            event.getContext().disableScissor();

            currentY += (int) ((mc.textRenderer.fontHeight + padding * 2) * animValue);
        }
    };

    /**
     * Renders the module text with proper prefix coloring
     * Format: [GlobalPrefix]ModuleName [ModulePrefix]
     */
    private void renderModuleText(Render2DEvent event, Module module, int textColor, int x, int currentY, float animValue) {
        int currentX = x;

        TextRendererUtility.renderText(event.getContext(), module.getDisplayName(), textColor, currentX, currentY, false);
        currentX += TextRendererUtility.getTextWidth(module.getDisplayName());

        if(module.getPrefix() != null && !module.getPrefix().isEmpty()) {
            TextRendererUtility.renderText(event.getContext(), " ", textColor, currentX, currentY, false);
            currentX += TextRendererUtility.getTextWidth(" ");

            int modulePrefixColor = ColorUtility.getColor(ColorUtility.Colors.LIGHT_GRAY);
            int prefixAlpha = (int) (255 * animValue);
            modulePrefixColor = (modulePrefixColor & 0x00FFFFFF) | (prefixAlpha << 24);

            TextRendererUtility.renderText(event.getContext(), module.getPrefix(), modulePrefixColor, currentX, currentY, false);
        }
    }
}