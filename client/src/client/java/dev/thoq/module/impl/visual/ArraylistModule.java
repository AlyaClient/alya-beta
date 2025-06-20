package dev.thoq.module.impl.visual;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.config.setting.impl.ModeSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.module.ModuleRepository;
import dev.thoq.utilities.render.ColorUtility;
import dev.thoq.utilities.render.TextRendererUtility;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("ConstantValue")
public class ArraylistModule extends Module {
    private static final BooleanSetting showVisualModules = new BooleanSetting("Show Visual", "Should Arraylist show visual modules?", true);
    private static final ModeSetting position = new ModeSetting("Position", "Arraylist Position", "Left", "Left", "Right");

    public ArraylistModule() {
        super("Arraylist", "Render all active modules", ModuleCategory.VISUAL);

        addSetting(showVisualModules);
        addSetting(position);
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

        int screenWidth = mc.getWindow().getScaledWidth();
        boolean isLeftPosition = Objects.equals(position.getValue(), "Left");
        
        int y = isLeftPosition ? leftTopMargin : rightTopMargin;

        for(Module module : activeModules) {
            String name = module.getName();
            int textWidth = TextRendererUtility.getTextWidth(name);
            int x;
            
            if(isLeftPosition) {
                x = sidePadding;
                
                context.fill(
                    x - padding, 
                    y - padding,
                    x + textWidth + padding,
                    y + mc.textRenderer.fontHeight + padding,
                    0x90000000
                );
            } else {
                x = screenWidth - textWidth - sidePadding;
                
                context.fill(
                    x - padding, 
                    y - padding,
                    screenWidth - sidePadding + padding,
                    y + mc.textRenderer.fontHeight + padding,
                    0x90000000
                );
            }

            TextRendererUtility.renderText(
                    context,
                    name,
                    ColorUtility.Colors.WHITE,
                    x,
                    y,
                    true
            );

            y += mc.textRenderer.fontHeight + padding * 2;
        }
    }
}
