package dev.thoq.module.impl.visual;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.event.IEventListener;
import dev.thoq.event.impl.Render2DEvent;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import dev.thoq.utilities.render.Theme;

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
        float phase = time * 2.0f + waveOffset * 4.0f;

        Theme currentTheme = Theme.getCurrentTheme();
        float factor = (float) (Math.sin(phase) + 1.0) / 2.0f;

        return Theme.interpolateColorInt(currentTheme.getPrimaryColor(), currentTheme.getSecondaryColor(), factor);
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
            int width1 = TextRendererUtility.getTextWidth(module1.getDisplayName());
            int width2 = TextRendererUtility.getTextWidth(module2.getDisplayName());
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
            moduleWidths.add(TextRendererUtility.getTextWidth(module.getDisplayName()));
        }

        for(int i = 0; i < animatingModules.size(); i++) {
            Module module = animatingModules.get(i);
            String name = module.getDisplayName();
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
            TextRendererUtility.renderText(
                    event.getContext(),
                    name,
                    textColor,
                    x,
                    currentY,
                    false
            );
            event.getContext().disableScissor();

            currentY += (int) ((mc.textRenderer.fontHeight + padding * 2) * animValue);
        }
    };
}