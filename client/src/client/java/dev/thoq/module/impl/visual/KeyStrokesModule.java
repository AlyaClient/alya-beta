package dev.thoq.module.impl.visual;

import dev.thoq.config.setting.impl.BooleanSetting;
import dev.thoq.module.Module;
import dev.thoq.module.ModuleCategory;
import dev.thoq.utilities.render.ColorUtility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;

public class KeyStrokesModule extends Module {
    private final BooleanSetting spaceBar = new BooleanSetting("Spacebar", "Draw Spacebar?", true);

    public KeyStrokesModule() {
        super("Keystrokes", "Draws your Keystrokes", ModuleCategory.VISUAL);

        addSetting(spaceBar);
    }

    @Override
    protected void onRender(DrawContext context) {
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

        context.fill(startX + keySize + gap, startY, startX + keySize + gap + keySize, startY + keySize, wColor);
        context.drawCenteredTextWithShadow(mc.textRenderer, "W", startX + keySize + gap + keySize / 2, startY + keySize / 2 - 4, 0xFFFFFFFF);

        context.fill(startX, startY + keySize + gap, startX + keySize, startY + keySize + gap + keySize, aColor);
        context.drawCenteredTextWithShadow(mc.textRenderer, "A", startX + keySize / 2, startY + keySize + gap + keySize / 2 - 4, 0xFFFFFFFF);

        context.fill(startX + keySize + gap, startY + keySize + gap, startX + keySize + gap + keySize, startY + keySize + gap + keySize, sColor);
        context.drawCenteredTextWithShadow(mc.textRenderer, "S", startX + keySize + gap + keySize / 2, startY + keySize + gap + keySize / 2 - 4, 0xFFFFFFFF);

        context.fill(startX + (keySize + gap) * 2, startY + keySize + gap, startX + (keySize + gap) * 2 + keySize, startY + keySize + gap + keySize, dColor);
        context.drawCenteredTextWithShadow(mc.textRenderer, "D", startX + (keySize + gap) * 2 + keySize / 2, startY + keySize + gap + keySize / 2 - 4, 0xFFFFFFFF);

        if (spaceBar.getValue()) {
            int spaceWidth = keySize * 3 + gap * 2;
            int spaceY = startY + (keySize + gap) * 2;
            context.fill(startX, spaceY, startX + spaceWidth, spaceY + keySize, spaceColor);
            context.drawCenteredTextWithShadow(mc.textRenderer, "SPACE", startX + spaceWidth / 2, spaceY + keySize / 2 - 4, 0xFFFFFFFF);
        }
    }
}