/*
 * Copyright (c) Tenacity Client 2024-2025.
 *
 * This file belongs to Tenacity Client,
 * an open-source Fabric injection client.
 * Tenacity GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Tenacity (and subsequently, its files) are all licensed under the MIT License.
 * Tenacity should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package rip.tenacity;

import rip.tenacity.command.CommandBuilder;
import rip.tenacity.command.CommandRepository;
import rip.tenacity.command.impl.*;
import rip.tenacity.config.KeybindManager;
import rip.tenacity.config.VisualManager;
import rip.tenacity.event.EventBus;
import rip.tenacity.module.ModuleBuilder;
import rip.tenacity.module.ModuleRepository;
import rip.tenacity.module.impl.combat.AttackDelayModule;
import rip.tenacity.module.impl.combat.killaura.KillauraModule;
import rip.tenacity.module.impl.movement.*;
import rip.tenacity.module.impl.utility.antivoid.AntiVoidModule;
import rip.tenacity.module.impl.utility.disabler.DisablerModule;
import rip.tenacity.module.impl.visual.*;
import rip.tenacity.module.impl.world.TickBaseModule;
import rip.tenacity.module.impl.movement.flight.FlightModule;
import rip.tenacity.module.impl.combat.VelocityModule;
import rip.tenacity.module.impl.movement.longjump.LongJumpModule;
import rip.tenacity.module.impl.movement.speed.SpeedModule;
import rip.tenacity.module.impl.world.NukerModule;
import rip.tenacity.module.impl.combat.ReachModule;
import rip.tenacity.module.impl.utility.fastplace.FastPlaceModule;
import rip.tenacity.module.impl.utility.nofall.NoFallModule;
import rip.tenacity.module.impl.world.TimerModule;
import rip.tenacity.module.impl.visual.clickgui.ClickGUIModule;
import rip.tenacity.module.impl.visual.esp.ESPModule;
import rip.tenacity.utilities.misc.IconLoader;
import rip.tenacity.utilities.misc.TenacityConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.apache.logging.log4j.LogManager;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class TenacityClient implements ClientModInitializer {
    public static final String MOD_ID = "rye";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static TenacityClient INSTANCE = new TenacityClient();
    private static String ryeState = "loading";
    private final ModuleRepository moduleRepository = ModuleRepository.getInstance();
    private static Vec3d lastPos = null;
    private static long lastMoveTime = 0;
    private static double lastBps = 0.0;
    private static final Set<Integer> previouslyPressedKeys = new HashSet<>();
    private static EventBus eventBus;
    private static boolean wasInGame = false;

    @Override
    public void onInitializeClient() {
        TenacityClient.setState("loading");

        eventBus = new EventBus();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> IconLoader.setWindowIcon(client.getWindow().getHandle()));

        initializeModules();
        initializeCommands();

        KeybindManager.getInstance().initialize();
        VisualManager.getInstance().initialize();

        CommandRegistrationCallback.EVENT.register((
                dispatcher,
                registryAccess,
                environment
        ) -> CommandRepository.registerCommands(dispatcher));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(!KeybindManager.getInstance().shouldHandleKeyPress()) {
                return;
            }

            long handle = client.getWindow().getHandle();
            Set<Integer> currentlyPressed = new HashSet<>();

            for(int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_LAST; key++) {
                if(InputUtil.isKeyPressed(handle, key)) {
                    currentlyPressed.add(key);
                    if(!previouslyPressedKeys.contains(key)) {
                        KeybindManager.getInstance().handleKeyPress(key);
                    }
                }
            }

            previouslyPressedKeys.clear();
            previouslyPressedKeys.addAll(currentlyPressed);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isInGame = client.player != null && client.world != null;

            if(isInGame && !wasInGame) {
                VisualManager.getInstance().applyVisualData();
                LOGGER.info("Applied visual module data after joining world");
            }

            if(!isInGame && wasInGame) {
                VisualManager.getInstance().saveVisualData();
                LOGGER.info("Saved visual module data after leaving world");
            }

            wasInGame = isInGame;
        });
    }

    private static void initializeModules() {
        ModuleBuilder.create()
                .putAll(
                        new ClickGUIModule(),
                        new SprintModule(),
                        new JumpCooldownModule(),
                        new FastPlaceModule(),
                        new FlightModule(),
                        new FullbrightModule(),
                        new AntiInvisModule(),
                        new VelocityModule(),
                        new ESPModule(),
                        new NoFallModule(),
                        new SpeedModule(),
                        new LongJumpModule(),
                        new TimerModule(),
                        new KillauraModule(),
                        new HUDModule(),
                        new ArraylistModule(),
                        new ScaffoldModule(),
                        new DebugModule(),
                        new TickBaseModule(),
                        new KeyStrokesModule(),
                        new NukerModule(),
                        new ReachModule(),
                        new PerformanceModule(),
                        new AttackDelayModule(),
                        new StrafeModule(),
                        new HighJumpModule(),
                        new AmbienceModule(),
                        new DiscordRPCModule(),
                        new DisablerModule(),
                        new CapeModule(),
                        new AntiGravityModule(),
                        new AntiVoidModule(),
                        new TargetStrafeModule(),
                        new SpeedMonitorModule()
                );
    }

    private static void initializeCommands() {
        CommandBuilder.create()
                .putAll(
                        new ToggleCommand(),
                        new ConfigCommand(),
                        new BindCommand(),
                        new SettingsCommand(),
                        new VClipCommand()
                );
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public ModuleRepository getModuleRepository() {
        return moduleRepository;
    }

    public static String getState() {
        return ryeState;
    }

    public static void setState(String state) {
        ryeState = state;
    }

    public static String getName() {
        return TenacityConstants.NAME;
    }

    public static String getEdition() {
        return TenacityConstants.VERSION;
    }

    public static String getType() {
        return TenacityConstants.KIND;
    }

    public static String getBuildNumber() {
        return TenacityConstants.BUILD_NUMBER;
    }

    public static String getFps() {
        return String.valueOf(MinecraftClient.getInstance().getCurrentFps());
    }

    public static String getBps() {
        final MinecraftClient mc = MinecraftClient.getInstance();
        final DecimalFormat df = new DecimalFormat("#.##");
        double bps = 0.0;

        if(mc.player != null) {
            Vec3d currentPos = mc.player.getPos();
            long currentTime = System.currentTimeMillis();

            if(lastPos != null) {
                double distance = Math.sqrt(
                        Math.pow(currentPos.x - lastPos.x, 2) +
                                Math.pow(currentPos.z - lastPos.z, 2)
                );
                double timeElapsed = (currentTime - lastMoveTime) / 1000.0;

                if(timeElapsed >= 0.1) {
                    bps = distance / timeElapsed;
                    bps = (bps + lastBps) / 2;

                    lastPos = currentPos;
                    lastMoveTime = currentTime;
                    lastBps = bps;
                } else {
                    bps = lastBps;
                }
            } else {
                lastPos = currentPos;
                lastMoveTime = currentTime;
            }
        }

        return df.format(bps);
    }

    public static String getTime() {
        return new java.text.SimpleDateFormat("hh:mm a").format(new Date());
    }
}
