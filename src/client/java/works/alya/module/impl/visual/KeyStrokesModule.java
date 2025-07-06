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
import works.alya.event.IEventListener;
import works.alya.event.impl.Render2DEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import works.alya.utilities.render.ColorUtility;
import net.minecraft.client.util.InputUtil;

public class KeyStrokesModule extends Module {
    private final BooleanSetting spaceBar = new BooleanSetting("Spacebar", "Draw Spacebar?", true);

    public KeyStrokesModule() {
        super("Keystrokes", "Key Strokes", "Draws your Keystrokes", ModuleCategory.VISUAL);

        addSetting(spaceBar);
    }

    private final IEventListener<Render2DEvent> renderEvent = event -> {
        int startX = 1;
        int startY = 100;
        int keySize = 30;
        int gap = 2;

        boolean wPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.GLFW_KEY_W);
        boolean aPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.GLFW_KEY_A);
        boolean sPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.GLFW_KEY_S);
        boolean dPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.GLFW_KEY_D);
        boolean spacePressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.GLFW_KEY_SPACE);

        int wColor = wPressed ? 0xCC222222 : ColorUtility.getColor(ColorUtility.Colors.PANEL);
        int aColor = aPressed ? 0xCC222222 : ColorUtility.getColor(ColorUtility.Colors.PANEL);
        int sColor = sPressed ? 0xCC222222 : ColorUtility.getColor(ColorUtility.Colors.PANEL);
        int dColor = dPressed ? 0xCC222222 : ColorUtility.getColor(ColorUtility.Colors.PANEL);
        int spaceColor = spacePressed ? 0xCC222222 : ColorUtility.getColor(ColorUtility.Colors.PANEL);

        event.getContext().fill(startX + keySize + gap, startY, startX + keySize + gap + keySize, startY + keySize, wColor);
        event.getContext().drawCenteredTextWithShadow(mc.textRenderer, "W", startX + keySize + gap + keySize / 2, startY + keySize / 2 - 4, 0xFFFFFFFF);

        event.getContext().fill(startX, startY + keySize + gap, startX + keySize, startY + keySize + gap + keySize, aColor);
        event.getContext().drawCenteredTextWithShadow(mc.textRenderer, "A", startX + keySize / 2, startY + keySize + gap + keySize / 2 - 4, 0xFFFFFFFF);

        event.getContext().fill(startX + keySize + gap, startY + keySize + gap, startX + keySize + gap + keySize, startY + keySize + gap + keySize, sColor);
        event.getContext().drawCenteredTextWithShadow(mc.textRenderer, "S", startX + keySize + gap + keySize / 2, startY + keySize + gap + keySize / 2 - 4, 0xFFFFFFFF);

        event.getContext().fill(startX + (keySize + gap) * 2, startY + keySize + gap, startX + (keySize + gap) * 2 + keySize, startY + keySize + gap + keySize, dColor);
        event.getContext().drawCenteredTextWithShadow(mc.textRenderer, "D", startX + (keySize + gap) * 2 + keySize / 2, startY + keySize + gap + keySize / 2 - 4, 0xFFFFFFFF);

        if (spaceBar.getValue()) {
            int spaceWidth = keySize * 3 + gap * 2;
            int spaceY = startY + (keySize + gap) * 2;
            event.getContext().fill(startX, spaceY, startX + spaceWidth, spaceY + keySize, spaceColor);
            event.getContext().drawCenteredTextWithShadow(mc.textRenderer, "SPACE", startX + spaceWidth / 2, spaceY + keySize / 2 - 4, 0xFFFFFFFF);
        }
    };
}