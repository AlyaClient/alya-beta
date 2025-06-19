package dev.thoq.ui;

import dev.thoq.config.BooleanSetting;
import dev.thoq.config.Setting;
import dev.thoq.config.SettingsManager;
import dev.thoq.utilities.render.FontRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

/**
 * UI screen for client settings.
 */
public class SettingsUI extends Screen {
    private static final int BUTTON_WIDTH = 150;
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 10;

    private final List<ButtonWidget> buttons = new ArrayList<>();
    private final SettingsManager settingsManager;

    public SettingsUI() {
        super(Text.literal("Settings"));
        this.settingsManager = SettingsManager.getInstance();
    }

    @Override
    protected void init() {
        super.init();
        buttons.clear();

        int y = 50;

        // Add title
        // Buttons for each setting
        for (Setting<?> setting : settingsManager.getSettings()) {
            if (setting instanceof BooleanSetting booleanSetting) {
                ButtonWidget button = ButtonWidget.builder(
                        Text.literal(setting.getName() + ": " + formatValue(booleanSetting.getValue())),
                        (buttonWidget) -> {
                            booleanSetting.setValue(!booleanSetting.getValue());
                            buttonWidget.setMessage(Text.literal(setting.getName() + ": " + formatValue(booleanSetting.getValue())));
                            settingsManager.saveSettings();
                        })
                        .dimensions(width / 2 - BUTTON_WIDTH / 2, y, BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build();

                addDrawableChild(button);
                buttons.add(button);
                y += BUTTON_HEIGHT + PADDING;
            }
        }

        // Add save button
        ButtonWidget saveButton = ButtonWidget.builder(
                Text.literal("Save and Close"),
                (buttonWidget) -> {
                    settingsManager.saveSettings();
                    MinecraftClient.getInstance().setScreen(null);
                })
                .dimensions(width / 2 - BUTTON_WIDTH / 2, height - 40, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();

        addDrawableChild(saveButton);
        buttons.add(saveButton);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Apply blur effect to the background
        renderBlurredBackground(context);

        // Draw title
        FontRenderer.getInstance().drawText(
                context,
                "Settings",
                width / 2 - FontRenderer.getInstance().getWidth("Settings") / 2,
                20,
                0xFFFFFFFF,
                true
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    /**
     * Formats a boolean value for display.
     * 
     * @param value The boolean value
     * @return The formatted string
     */
    private String formatValue(boolean value) {
        return value ? Formatting.GREEN + "Enabled" : Formatting.RED + "Disabled";
    }

    /**
     * Renders a blurred background effect for the Settings UI.
     * This simulates Minecraft's blur effect by using a semi-transparent overlay.
     * 
     * @param context The draw context
     */
    private void renderBlurredBackground(DrawContext context) {
        // Get screen dimensions
        int width = this.width;
        int height = this.height;

        // Apply a semi-transparent dark overlay to simulate blur
        context.fill(0, 0, width, height, 0xB0000000);

        // Add a subtle gradient effect
        for (int i = 0; i < height; i += 2) {
            float alpha = 0.03f * (1 - (float)i / height);
            int color = (int)(alpha * 255) << 24;
            context.fill(0, i, width, i + 1, color);
        }
    }
}
