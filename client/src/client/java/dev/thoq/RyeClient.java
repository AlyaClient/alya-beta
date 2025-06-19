package dev.thoq;

import dev.thoq.command.CommandBuilder;
import dev.thoq.command.CommandRepository;
import dev.thoq.command.impl.BindCommand;
import dev.thoq.command.impl.ConfigCommand;
import dev.thoq.command.impl.SettingsCommand;
import dev.thoq.command.impl.ToggleCommand;
import dev.thoq.config.KeybindManager;
import dev.thoq.module.ModuleBuilder;
import dev.thoq.module.ModuleRepository;
import dev.thoq.module.impl.movement.flight.FlightModule;
import dev.thoq.module.impl.combat.VelocityModule;
import dev.thoq.module.impl.movement.longjump.LongJumpModule;
import dev.thoq.module.impl.movement.speed.SpeedModule;
import dev.thoq.module.impl.player.fastplace.FastPlaceModule;
import dev.thoq.module.impl.player.nofall.NoFallModule;
import dev.thoq.module.impl.player.nojumpdelay.NoJumpDelayModule;
import dev.thoq.module.impl.player.sprint.SprintModule;
import dev.thoq.module.impl.visual.AntiInvisModule;
import dev.thoq.module.impl.visual.ClickGUIModule;
import dev.thoq.module.impl.visual.FullbrightModule;
import dev.thoq.module.impl.visual.GlowESP;
import dev.thoq.utilities.misc.IconLoader;
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

    @Override
    public void onInitializeClient() {
        RyeClient.setState("loading");

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> IconLoader.setWindowIcon(client.getWindow().getHandle()));

        ClickGUIModule clickGUIModule = new ClickGUIModule();

        ModuleBuilder.create()
                .putAll(
                        new SprintModule(),
                        new NoJumpDelayModule(),
                        new FastPlaceModule(),
                        new FlightModule(),
                        new FullbrightModule(),
                        new AntiInvisModule(),
                        new VelocityModule(),
                        new GlowESP(),
                        new NoFallModule(),
                        new SpeedModule(),
                        clickGUIModule,
                        new LongJumpModule()
                );

        KeybindManager.getInstance().bind(clickGUIModule, GLFW.GLFW_KEY_RIGHT_SHIFT);

        CommandBuilder.create()
                .putAll(
                        new ToggleCommand(),
                        new ConfigCommand(),
                        new BindCommand(),
                        new SettingsCommand()
                );

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
    }

    public static String getState() {
        return ryeState;
    }

    public static void setState(String state) {
        ryeState = state;
    }

    public static String getName() {
        return "Rye";
    }

    public static String getEdition() {
        return "v1.1";
    }

    public static String getType() {
        return "Development";
    }

    public static String getBuildNumber() {
        return "(61825)";
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

    public ModuleRepository getModuleRepository() {
        return moduleRepository;
    }
}
