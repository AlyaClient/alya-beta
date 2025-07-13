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

package works.alya;

import works.alya.command.CommandBuilder;
import works.alya.command.CommandRepository;
import works.alya.command.impl.*;
import works.alya.config.KeybindManager;
import works.alya.config.VisualManager;
import works.alya.event.EventBus;
import works.alya.module.ModuleBuilder;
import works.alya.module.ModuleRepository;
import works.alya.module.impl.combat.AimAssistModule;
import works.alya.module.impl.combat.AttackDelayModule;
import works.alya.module.impl.combat.AutoClickerModule;
import works.alya.module.impl.combat.killaura.KillauraModule;
import works.alya.module.impl.movement.*;
import works.alya.module.impl.utility.antivoid.AntiVoidModule;
import works.alya.module.impl.utility.disabler.DisablerModule;
import works.alya.module.impl.visual.*;
import works.alya.module.impl.world.TickBaseModule;
import works.alya.module.impl.movement.flight.FlightModule;
import works.alya.module.impl.combat.VelocityModule;
import works.alya.module.impl.movement.longjump.LongJumpModule;
import works.alya.module.impl.movement.speed.SpeedModule;
import works.alya.module.impl.world.NukerModule;
import works.alya.module.impl.combat.ReachModule;
import works.alya.module.impl.utility.fastplace.FastPlaceModule;
import works.alya.module.impl.utility.nofall.NoFallModule;
import works.alya.module.impl.world.TimerModule;
import works.alya.module.impl.visual.clickgui.ClickGUIModule;
import works.alya.module.impl.visual.esp.ESPModule;
import works.alya.script.core.ScriptManager;
import works.alya.utilities.misc.IconLoader;
import works.alya.utilities.misc.AlyaConstants;
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

public class AlyaClient implements ClientModInitializer {
    public static final String MOD_ID = "alya";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static AlyaClient INSTANCE = new AlyaClient();
    private static String tenaState = "loading";
    private final ModuleRepository moduleRepository = ModuleRepository.getInstance();
    private static Vec3d lastPos = null;
    private static long lastMoveTime = 0;
    private static double lastBps = 0.0;
    private static final Set<Integer> previouslyPressedKeys = new HashSet<>();
    private static EventBus eventBus;
    private static boolean wasInGame = false;

    @Override
    public void onInitializeClient() {
        AlyaClient.setState("loading");

        eventBus = new EventBus();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> IconLoader.setWindowIcon(client.getWindow().getHandle()));

        initializeModules();
        initializeCommands();

        KeybindManager.getInstance().initialize();
        VisualManager.getInstance().initialize();

        ScriptManager.getInstance().init();

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
                        new SpeedMonitorModule(),
                        new AutoClickerModule(),
                        new AimAssistModule()
                );
    }

    private static void initializeCommands() {
        CommandBuilder.create()
                .putAll(
                        new ToggleCommand(),
                        new ConfigCommand(),
                        new BindCommand(),
                        new SettingsCommand(),
                        new VClipCommand(),
                        new ScriptCommand(),
                        new NameCommand()
                );
    }

    public static EventBus getEventBus() {
        return eventBus;
    }

    public ModuleRepository getModuleRepository() {
        return moduleRepository;
    }

    public static String getState() {
        return tenaState;
    }

    public static void setState(String state) {
        tenaState = state;
    }

    public static String getName() {
        return AlyaConstants.NAME;
    }

    public static String getEdition() {
        return AlyaConstants.VERSION;
    }

    public static String getType() {
        return AlyaConstants.KIND;
    }

    public static String getBuildNumber() {
        return AlyaConstants.BUILD_NUMBER;
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
