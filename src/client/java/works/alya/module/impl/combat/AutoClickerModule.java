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

package works.alya.module.impl.combat;


import works.alya.AlyaClient;
import works.alya.config.setting.impl.BooleanSetting;
import works.alya.config.setting.impl.ModeSetting;
import works.alya.config.setting.impl.NumberSetting;
import works.alya.event.IEventListener;
import works.alya.event.impl.TickEvent;
import works.alya.module.Module;
import works.alya.module.ModuleCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public class AutoClickerModule extends Module {
    private final ModeSetting clickMode = new ModeSetting("ClickMode", "Type of clicking", "Left", "Left", "Right", "Both");
    private final BooleanSetting randomizeCps = new BooleanSetting("RandomCPS", "Randomize clicks per second", true);
    private final NumberSetting<Integer> cps = new NumberSetting<>("CPS", "Clicks per second", 12, 1, 30);
    private final NumberSetting<Integer> minCps = new NumberSetting<>("MinCPS", "Minimum clicks per second", 8, 1, 30);
    private final NumberSetting<Integer> maxCps = new NumberSetting<>("MaxCPS", "Maximum clicks per second", 14, 1, 30);
    private final BooleanSetting breakBlocks = new BooleanSetting("BreakBlocks", "Continue breaking blocks", true);
    private final BooleanSetting weaponOnly = new BooleanSetting("WeaponOnly", "Only click when holding a weapon", false);
    private final BooleanSetting inventoryFill = new BooleanSetting("InventoryFill", "Click in inventory", false);
    private final BooleanSetting onlyWhenFocused = new BooleanSetting("OnlyWhenFocused", "Only click when game is focused", true);
    private final BooleanSetting smartMode = new BooleanSetting("SmartMode", "Only attack when looking at entities", false);
    private final BooleanSetting respectAttackDelay = new BooleanSetting("RespectDelay", "Respect attack delay module", true);
    private final BooleanSetting jitterClick = new BooleanSetting("JitterClick", "Add random jitter to clicks", false);
    private final NumberSetting<Double> jitterStrength = new NumberSetting<>("JitterStrength", "Strength of jitter effect", 1.0, 0.1, 3.0);
    private final BooleanSetting blockHitAssist = new BooleanSetting("BlockHitAssist", "Assist with block hitting", false);
    private final NumberSetting<Integer> blockHitChance = new NumberSetting<>("BlockHitChance", "Chance to block hit", 30, 1, 100);
    private final BooleanSetting noBlockBreakDelay = new BooleanSetting("NoBlockBreakDelay", "Remove block breaking delay", false);
    private final BooleanSetting mouseDownOnly = new BooleanSetting("MouseDownOnly", "Only click when mouse is held down", false);

    private final Random random = new Random();
    private long lastLeftClick = 0;
    private long lastRightClick = 0;
    private int currentCps = 12;
    private boolean isBlocking = false;
    private long lastBlockTime = 0;
    private long lastUnblockTime = 0;
    private float originalYaw = 0;
    private float originalPitch = 0;
    private boolean leftMouseDown = false;
    private boolean rightMouseDown = false;

    public AutoClickerModule() {
        super("AutoClicker", "Automatically clicks for you", ModuleCategory.COMBAT);

        addSetting(clickMode);
        addSetting(randomizeCps);
        addSetting(cps);
        addSetting(minCps);
        addSetting(maxCps);
        addSetting(breakBlocks);
        addSetting(weaponOnly);
        addSetting(inventoryFill);
        addSetting(onlyWhenFocused);
        addSetting(smartMode);
        addSetting(respectAttackDelay);
        addSetting(jitterClick);
        addSetting(jitterStrength);
        addSetting(blockHitAssist);
        addSetting(blockHitChance);
        addSetting(noBlockBreakDelay);
        addSetting(mouseDownOnly);

        minCps.setVisibilityCondition(randomizeCps::getValue);
        maxCps.setVisibilityCondition(randomizeCps::getValue);
        jitterStrength.setVisibilityCondition(jitterClick::getValue);
        blockHitChance.setVisibilityCondition(blockHitAssist::getValue);
    }

    @SuppressWarnings("unused")
    private final IEventListener<TickEvent> tickEvent = event -> {
        if(!isEnabled() || mc.player == null || mc.world == null) return;
        if(onlyWhenFocused.getValue() && !mc.isWindowFocused()) return;

        updateMouseStates();

        if(randomizeCps.getValue() && System.currentTimeMillis() - Math.max(lastLeftClick, lastRightClick) > 1000) {
            currentCps = random.nextInt(maxCps.getValue() - minCps.getValue() + 1) + minCps.getValue();
        } else {
            currentCps = cps.getValue();
        }

        if(jitterClick.getValue() && (shouldLeftClick() || shouldRightClick())) {
            applyJitter();
        }

        if(shouldLeftClick()) {
            performLeftClick();
        }

        if(shouldRightClick()) {
            performRightClick();
        }

        if(blockHitAssist.getValue() && isLeftClicking() && random.nextInt(100) < blockHitChance.getValue()) {
            handleBlockHit();
        }

        setPrefix(clickMode.getValue() + " " + currentCps);
    };

    private void updateMouseStates() {
        if(mc.getWindow() == null) return;

        leftMouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        rightMouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
    }

    private boolean shouldLeftClick() {
        if(!isLeftClickMode() || mc.interactionManager == null) return false;

        if(mouseDownOnly.getValue() && !leftMouseDown) return false;

        long currentTime = System.currentTimeMillis();
        long clickDelay = calculateClickDelay();
        if(currentTime - lastLeftClick < clickDelay) return false;

        if(weaponOnly.getValue() && !isHoldingWeapon()) return false;
        if(smartMode.getValue() && !isLookingAtEntity()) return false;

        if(respectAttackDelay.getValue()) {
            AttackDelayModule attackDelayModule = AlyaClient.INSTANCE.getModuleRepository().getModule(AttackDelayModule.class);
            if(attackDelayModule != null && attackDelayModule.isEnabled() && !attackDelayModule.canAttack()) {
                return false;
            }
        }

        if(mc.options.attackKey.isPressed() && mc.interactionManager.isBreakingBlock() && !breakBlocks.getValue()) {
            return false;
        }

        return mc.currentScreen == null || inventoryFill.getValue();
    }

    private boolean shouldRightClick() {
        if(!isRightClickMode()) return false;

        if(mouseDownOnly.getValue() && !rightMouseDown) return false;

        long currentTime = System.currentTimeMillis();
        long clickDelay = calculateClickDelay();
        if(currentTime - lastRightClick < clickDelay) return false;

        return mc.currentScreen == null || inventoryFill.getValue();
    }

    private void performLeftClick() {
        if(mc.currentScreen == null) {
            mc.options.attackKey.setPressed(true);
            if(mc.player != null) {
                mc.player.swingHand(Hand.MAIN_HAND);
                if(mc.interactionManager != null && mc.crosshairTarget != null) {
                    if(mc.crosshairTarget instanceof EntityHitResult entityHitResult) {
                        mc.interactionManager.attackEntity(mc.player, entityHitResult.getEntity());
                    }
                }
            }

            mc.options.attackKey.setPressed(false);
        } else if(inventoryFill.getValue()) {
            int mouseX = (int) (mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
            int mouseY = (int) (mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());
            mc.currentScreen.mouseClicked(mouseX, mouseY, 0);
        }

        lastLeftClick = System.currentTimeMillis();
    }

    private void performRightClick() {
        if(mc.currentScreen == null) {
            mc.options.useKey.setPressed(true);
            if(mc.player != null && mc.interactionManager != null) {
                mc.player.swingHand(Hand.MAIN_HAND);
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            }
            mc.options.useKey.setPressed(false);
        } else if(inventoryFill.getValue()) {
            int mouseX = (int) (mc.mouse.getX() * mc.getWindow().getScaledWidth() / mc.getWindow().getWidth());
            int mouseY = (int) (mc.mouse.getY() * mc.getWindow().getScaledHeight() / mc.getWindow().getHeight());
            mc.currentScreen.mouseClicked(mouseX, mouseY, 1);
        }

        lastRightClick = System.currentTimeMillis();
    }

    private void handleBlockHit() {
        if(mc.interactionManager == null) return;
        long currentTime = System.currentTimeMillis();

        if(!isBlocking && currentTime - lastUnblockTime > 200) {
            mc.options.useKey.setPressed(true);
            mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
            isBlocking = true;
            lastBlockTime = currentTime;
        } else if(isBlocking && currentTime - lastBlockTime > 100) {
            mc.options.useKey.setPressed(false);
            isBlocking = false;
            lastUnblockTime = currentTime;
        }
    }

    private void applyJitter() {
        if(mc.player == null) return;

        if(originalYaw == 0) {
            originalYaw = mc.player.getYaw();
            originalPitch = mc.player.getPitch();
        }

        float jitterAmount = (float) (jitterStrength.getValue() * (random.nextFloat() - 0.5f));
        float yawJitter = jitterAmount * 2.0f;

        mc.player.setYaw(mc.player.getYaw() + yawJitter);
        mc.player.setPitch(Math.max(-90, Math.min(90, mc.player.getPitch() + jitterAmount)));
    }

    private long calculateClickDelay() {
        long baseDelay = 1000 / currentCps;

        double randomFactor = 0.8 + (random.nextDouble() * 0.4);
        long actualDelay = Math.round(baseDelay * randomFactor);

        return Math.max(actualDelay, 25);
    }

    private boolean isLeftClickMode() {
        return clickMode.getValue().equals("Left") || clickMode.getValue().equals("Both");
    }

    private boolean isRightClickMode() {
        return clickMode.getValue().equals("Right") || clickMode.getValue().equals("Both");
    }

    private boolean isHoldingWeapon() {
        if(mc.player == null) return false;

        String itemName = mc.player.getMainHandStack().getItem().getName().getString().toLowerCase();
        return itemName.contains("sword") || itemName.contains("axe");
    }

    private boolean isLookingAtEntity() {
        if(mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult) mc.crosshairTarget).getEntity() != null;
        }
        return false;
    }

    private boolean isLeftClicking() {
        return System.currentTimeMillis() - lastLeftClick < 200;
    }

    @Override
    protected void onEnable() {
        lastLeftClick = 0;
        lastRightClick = 0;
        isBlocking = false;
        lastBlockTime = 0;
        lastUnblockTime = 0;
        originalYaw = 0;
        originalPitch = 0;
        leftMouseDown = false;
        rightMouseDown = false;

        if(randomizeCps.getValue()) {
            currentCps = random.nextInt(maxCps.getValue() - minCps.getValue() + 1) + minCps.getValue();
        } else {
            currentCps = cps.getValue();
        }
    }

    @Override
    protected void onDisable() {
        if(mc.options.attackKey.isPressed()) {
            mc.options.attackKey.setPressed(false);
        }

        if(mc.options.useKey.isPressed()) {
            mc.options.useKey.setPressed(false);
        }

        if(mc.player != null && originalYaw != 0) {
            mc.player.setYaw(originalYaw);
            mc.player.setPitch(originalPitch);
        }

        isBlocking = false;
        leftMouseDown = false;
        rightMouseDown = false;
    }
}