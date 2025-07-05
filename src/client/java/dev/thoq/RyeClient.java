/*
 * Copyright (c) Rye Client 2024-2025.
 *
 * This file belongs to Rye Client,
 * an open-source Fabric injection client.
 * Rye GitHub: https://github.com/RyeClient/rye-v1.git
 *
 * THIS PROJECT DOES NOT HAVE A WARRANTY.
 *
 * Rye (and subsequently, its files) are all licensed under the MIT License.
 * Rye should have come with a copy of the MIT License.
 * If it did not, you may obtain a copy here:
 * MIT License: https://opensource.org/license/mit
 *
 */

package dev.thoq;

import dev.thoq.command.CommandBuilder;
import dev.thoq.command.CommandRepository;
import dev.thoq.command.impl.*;
import dev.thoq.config.KeybindManager;
import dev.thoq.config.VisualManager;
import dev.thoq.event.EventBus;
import dev.thoq.module.ModuleBuilder;
import dev.thoq.module.ModuleRepository;
import dev.thoq.module.impl.combat.AttackDelayModule;
import dev.thoq.module.impl.combat.killaura.KillauraModule;
import dev.thoq.module.impl.movement.*;
import dev.thoq.module.impl.utility.antivoid.AntiVoidModule;
import dev.thoq.module.impl.utility.disabler.DisablerModule;
import dev.thoq.module.impl.world.TickBaseModule;
import dev.thoq.module.impl.movement.flight.FlightModule;
import dev.thoq.module.impl.combat.VelocityModule;
import dev.thoq.module.impl.movement.longjump.LongJumpModule;
import dev.thoq.module.impl.movement.speed.SpeedModule;
import dev.thoq.module.impl.world.NukerModule;
import dev.thoq.module.impl.combat.ReachModule;
import dev.thoq.module.impl.utility.fastplace.FastPlaceModule;
import dev.thoq.module.impl.utility.nofall.NoFallModule;
import dev.thoq.module.impl.visual.*;
import dev.thoq.module.impl.world.TimerModule;
import dev.thoq.module.impl.visual.clickgui.ClickGUIModule;
import dev.thoq.module.impl.visual.esp.ESPModule;
import dev.thoq.utilities.misc.IconLoader;
import dev.thoq.utilities.misc.RyeConstants;
import dev.thoq.utilities.render.Theme;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.apache.logging.log4j.LogManager;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class RyeClient implements ClientModInitializer {
    public static final String MOD_ID = "rye";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static RyeClient INSTANCE = new RyeClient();
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
        RyeClient.setState("loading");

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
        return RyeConstants.NAME;
    }

    public static String getEdition() {
        return "v" + RyeConstants.VERSION;
    }

    public static String getType() {
        return RyeConstants.KIND;
    }

    public static String getBuildNumber() {
        return RyeConstants.BUILD_NUMBER;
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
