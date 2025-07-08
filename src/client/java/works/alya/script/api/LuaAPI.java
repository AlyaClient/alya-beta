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

package works.alya.script.api;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.TwoArgFunction;
import works.alya.AlyaClient;
import works.alya.event.IEventListener;
import works.alya.event.impl.MotionEvent;
import works.alya.module.Module;
import works.alya.module.ModuleRepository;
import works.alya.script.core.Script;
import works.alya.script.integration.ScriptModule;
import works.alya.script.integration.ScriptRenderQueue;
import works.alya.utilities.misc.ChatUtility;
import works.alya.utilities.player.MoveUtility;
import works.alya.utilities.render.ColorUtility;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Box;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.ThreeArgFunction;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collection;

@SuppressWarnings("CallToPrintStackTrace")
public class LuaAPI {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static LuaFunction motionEventCallback;
    private static Script currentScript;

    public static void register(Globals globals, Script script) {
        currentScript = script;
        LuaTable alyaTable = new LuaTable();
        globals.set("alya", alyaTable);

        registerMinecraftAPI(alyaTable);
        registerAlyaAPI(alyaTable);
        registerRenderAPI(alyaTable);
        registerUtilityAPI(alyaTable);
        registerWorldAPI(alyaTable);
        registerPlayerAPI(alyaTable);
        registerInventoryAPI(alyaTable);
        registerMathAPI(alyaTable);
        registerNetworkAPI(alyaTable);
        registerSoundAPI(alyaTable);
        registerEntityAPI(alyaTable);
        registerMotionEventAPI(alyaTable);
    }

    public static void updateCurrentScript(Script script) {
        currentScript = script;
    }

    private static void registerMotionEventAPI(LuaTable alyaTable) {
        LuaTable motionEventTable = new LuaTable();

        IEventListener<MotionEvent> motionListener = event -> {
            if(motionEventCallback != null && isScriptEnabled()) {
                try {
                    motionEventCallback.call(CoerceJavaToLua.coerce(event));
                } catch(Exception ex) {
                    ChatUtility.sendError("Error in motion event callback");
                    ChatUtility.sendScriptError(ex);
                    ex.printStackTrace();
                }
            }
        };

        AlyaClient.getEventBus().register(motionListener);

        motionEventTable.set("setCallback", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                if(arg2.isfunction()) {
                    motionEventCallback = arg2.checkfunction();
                }
                return NIL;
            }
        });

        alyaTable.set("motionEvent", motionEventTable);
    }

    private static boolean isScriptEnabled() {
        if(currentScript == null) return false;

        for(Module module : AlyaClient.INSTANCE.getModuleRepository().getModules()) {
            if(module instanceof ScriptModule scriptModule) {
                if(scriptModule.getScript() == currentScript) {
                    return module.isEnabled();
                }
            }
        }

        return false;
    }

    private static void registerMinecraftAPI(LuaTable alyaTable) {
        LuaTable mcTable = new LuaTable();
        alyaTable.set("mc", mcTable);

        mcTable.set("getPlayer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? CoerceJavaToLua.coerce(mc.player) : LuaValue.NIL;
            }
        });

        mcTable.set("getPlayerPosition", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player == null) return LuaValue.NIL;
                Vec3d pos = mc.player.getPos();
                LuaTable posTable = new LuaTable();
                posTable.set("x", pos.x);
                posTable.set("y", pos.y);
                posTable.set("z", pos.z);
                return posTable;
            }
        });

        mcTable.set("getPlayerSpeed", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(Double.parseDouble(AlyaClient.getBps()));
            }
        });

        mcTable.set("getWorld", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.world != null ? CoerceJavaToLua.coerce(mc.world) : LuaValue.NIL;
            }
        });

        mcTable.set("getEntities", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.world == null) return LuaValue.NIL;
                List<Entity> entities = new ArrayList<>();
                for(Entity entity : mc.world.getEntities()) {
                    entities.add(entity);
                }
                return CoerceJavaToLua.coerce(entities);
            }
        });

        mcTable.set("getPlayers", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.world == null) return LuaValue.NIL;
                List<PlayerEntity> players = new ArrayList<>();
                for(Entity entity : mc.world.getEntities()) {
                    if(entity instanceof PlayerEntity) {
                        players.add((PlayerEntity) entity);
                    }
                }
                return CoerceJavaToLua.coerce(players);
            }
        });

        mcTable.set("getFPS", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.getCurrentFps());
            }
        });

        mcTable.set("getGameTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.world != null ? LuaValue.valueOf(mc.world.getTime()) : LuaValue.valueOf(0);
            }
        });

        mcTable.set("getDayTime", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.world != null ? LuaValue.valueOf(mc.world.getTimeOfDay()) : LuaValue.valueOf(0);
            }
        });

        mcTable.set("isInGame", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.player != null && mc.world != null);
            }
        });

        mcTable.set("getMouseX", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.mouse.getX());
            }
        });

        mcTable.set("getMouseY", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.mouse.getY());
            }
        });

        mcTable.set("getScreenWidth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.getWindow().getScaledWidth());
            }
        });

        mcTable.set("getScreenHeight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.getWindow().getScaledHeight());
            }
        });
    }

    private static void registerAlyaAPI(LuaTable alyaTable) {
        alyaTable.set("getModule", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String moduleName = arg.checkjstring();
                Module module = ModuleRepository.getInstance().getModuleByName(moduleName);
                return module != null ? CoerceJavaToLua.coerce(module) : LuaValue.NIL;
            }
        });

        alyaTable.set("isModuleEnabled", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String moduleName = arg.checkjstring();
                Module module = ModuleRepository.getInstance().getModuleByName(moduleName);
                return LuaValue.valueOf(module != null && module.isEnabled());
            }
        });

        alyaTable.set("enableModule", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String moduleName = arg.checkjstring();
                Module module = ModuleRepository.getInstance().getModuleByName(moduleName);
                if(module != null && !module.isEnabled()) {
                    module.setEnabled(true);
                    return LuaValue.TRUE;
                }
                return LuaValue.FALSE;
            }
        });

        alyaTable.set("disableModule", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String moduleName = arg.checkjstring();
                Module module = ModuleRepository.getInstance().getModuleByName(moduleName);
                if(module != null && module.isEnabled()) {
                    module.setEnabled(false);
                    return LuaValue.TRUE;
                }
                return LuaValue.FALSE;
            }
        });

        alyaTable.set("toggleModule", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String moduleName = arg.checkjstring();
                Module module = ModuleRepository.getInstance().getModuleByName(moduleName);
                if(module != null) {
                    module.toggle();
                    return LuaValue.valueOf(module.isEnabled());
                }
                return LuaValue.FALSE;
            }
        });

        alyaTable.set("getModules", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Collection<Module> modules = ModuleRepository.getInstance().getModules();
                List<Module> moduleList = new ArrayList<>(modules);
                return CoerceJavaToLua.coerce(moduleList);
            }
        });

        alyaTable.set("getEnabledModules", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                Collection<Module> modules = ModuleRepository.getInstance().getModules();
                List<Module> enabled = modules.stream()
                        .filter(Module::isEnabled)
                        .collect(Collectors.toList());
                return CoerceJavaToLua.coerce(enabled);
            }
        });
    }

    private static void registerRenderAPI(LuaTable alyaTable) {
        LuaTable renderTable = new LuaTable();
        alyaTable.set("render", renderTable);

        renderTable.set("color", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(args.narg() == 1) {
                    String colorName = args.checkjstring(1);
                    try {
                        ColorUtility.Colors color = ColorUtility.Colors.valueOf(colorName.toUpperCase());
                        return LuaValue.valueOf(ColorUtility.getColor(color));
                    } catch(IllegalArgumentException e) {
                        ChatUtility.sendError("Invalid color name: " + colorName);
                        ChatUtility.sendScriptError(e);
                        return LuaValue.valueOf(0xFFFFFFFF);
                    }
                } else if(args.narg() >= 3) {
                    int r = args.checkint(1);
                    int g = args.checkint(2);
                    int b = args.checkint(3);
                    int a = args.optint(4, 255);
                    Color color = new Color(r, g, b, a);
                    return LuaValue.valueOf(ColorUtility.getIntFromColor(color));
                }
                return LuaValue.valueOf(0xFFFFFFFF);
            }
        });

        renderTable.set("drawText", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                String text = args.checkjstring(1);
                int x = args.checkint(2);
                int y = args.checkint(3);
                int color = args.optint(4, 0xFFFFFFFF);
                boolean shadow = args.optboolean(5, false);
                ScriptRenderQueue.addTextRenderCommand(text, x, y, color, shadow);
                return LuaValue.NIL;
            }
        });

        renderTable.set("drawRect", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                float x = (float) args.checkdouble(1);
                float y = (float) args.checkdouble(2);
                float width = (float) args.checkdouble(3);
                float height = (float) args.checkdouble(4);
                int color = args.optint(5, 0xFFFFFFFF);
                ScriptRenderQueue.addRectRenderCommand(x, y, width, height, color);
                return LuaValue.NIL;
            }
        });

        renderTable.set("getStringWidth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String text = arg.checkjstring();
                return LuaValue.valueOf(mc.textRenderer.getWidth(text));
            }
        });

        renderTable.set("getStringHeight", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(mc.textRenderer.fontHeight);
            }
        });
    }

    private static void registerUtilityAPI(LuaTable alyaTable) {
        LuaTable utilTable = new LuaTable();
        alyaTable.set("util", utilTable);

        utilTable.set("log", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String message = arg.checkjstring();
                System.out.println("[Script] " + message);
                return LuaValue.NIL;
            }
        });

        utilTable.set("chatInfo", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String message = arg.checkjstring();
                ChatUtility.sendInfo(message);
                return LuaValue.NIL;
            }
        });

        utilTable.set("chatError", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                String message = arg.checkjstring();
                ChatUtility.sendError(message);
                return LuaValue.NIL;
            }
        });

        utilTable.set("setSpeed", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg$1, LuaValue arg$2) {
                double speed = arg$1.checkdouble();
                boolean strafe = arg$2.checkboolean();
                MoveUtility.setSpeed(speed, strafe);
                return LuaValue.NIL;
            }
        });

        utilTable.set("currentTimeMillis", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(System.currentTimeMillis());
            }
        });

        utilTable.set("sleep", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                long ms = arg.checklong();
                try {
                    Thread.sleep(ms);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return LuaValue.NIL;
            }
        });

        utilTable.set("randomInt", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                int min = arg1.checkint();
                int max = arg2.checkint();
                return LuaValue.valueOf(min + (int) (Math.random() * (max - min + 1)));
            }
        });

        utilTable.set("randomDouble", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                double min = arg1.checkdouble();
                double max = arg2.checkdouble();
                return LuaValue.valueOf(min + Math.random() * (max - min));
            }
        });
    }

    private static void registerWorldAPI(LuaTable alyaTable) {
        LuaTable worldTable = new LuaTable();
        alyaTable.set("world", worldTable);

        worldTable.set("getBlock", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.world == null) return LuaValue.NIL;
                int x = args.checkint(1);
                int y = args.checkint(2);
                int z = args.checkint(3);
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = mc.world.getBlockState(pos);
                return CoerceJavaToLua.coerce(state.getBlock());
            }
        });

        worldTable.set("getBlockState", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.world == null) return LuaValue.NIL;
                int x = args.checkint(1);
                int y = args.checkint(2);
                int z = args.checkint(3);
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = mc.world.getBlockState(pos);
                return CoerceJavaToLua.coerce(state);
            }
        });

        worldTable.set("isAir", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.world == null) return LuaValue.FALSE;
                int x = args.checkint(1);
                int y = args.checkint(2);
                int z = args.checkint(3);
                BlockPos pos = new BlockPos(x, y, z);
                return LuaValue.valueOf(mc.world.getBlockState(pos).isAir());
            }
        });

        worldTable.set("getLightLevel", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.world == null) return LuaValue.valueOf(0);
                int x = args.checkint(1);
                int y = args.checkint(2);
                int z = args.checkint(3);
                BlockPos pos = new BlockPos(x, y, z);
                return LuaValue.valueOf(mc.world.getLightLevel(pos));
            }
        });

        worldTable.set("getEntitiesInRange", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.world == null) return LuaValue.NIL;
                double x = args.checkdouble(1);
                double y = args.checkdouble(2);
                double z = args.checkdouble(3);
                double range = args.checkdouble(4);

                Vec3d center = new Vec3d(x, y, z);
                Box box = new Box(center.subtract(range, range, range), center.add(range, range, range));
                List<Entity> entities = mc.world.getOtherEntities(null, box);
                return CoerceJavaToLua.coerce(entities);
            }
        });

        worldTable.set("raycast", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.world == null || mc.player == null) return LuaValue.NIL;
                double distance = args.optdouble(1, 4.5);
                HitResult hit = mc.player.raycast(distance, 0, false);
                return CoerceJavaToLua.coerce(hit);
            }
        });

        worldTable.set("getDimension", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.world == null) return LuaValue.NIL;
                return LuaValue.valueOf(mc.world.getRegistryKey().getValue().toString());
            }
        });

        worldTable.set("getWeather", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.world == null) return LuaValue.NIL;
                LuaTable weather = new LuaTable();
                weather.set("raining", LuaValue.valueOf(mc.world.isRaining()));
                weather.set("thundering", LuaValue.valueOf(mc.world.isThundering()));
                weather.set("rainGradient", LuaValue.valueOf(mc.world.getRainGradient(0)));
                weather.set("thunderGradient", LuaValue.valueOf(mc.world.getThunderGradient(0)));
                return weather;
            }
        });
    }

    private static void registerPlayerAPI(LuaTable alyaTable) {
        LuaTable playerTable = new LuaTable();
        alyaTable.set("player", playerTable);

        playerTable.set("getHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getHealth()) : LuaValue.valueOf(0);
            }
        });

        playerTable.set("getMaxHealth", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getMaxHealth()) : LuaValue.valueOf(0);
            }
        });

        playerTable.set("getFoodLevel", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getHungerManager().getFoodLevel()) : LuaValue.valueOf(0);
            }
        });

        playerTable.set("getSaturation", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getHungerManager().getSaturationLevel()) : LuaValue.valueOf(0);
            }
        });

        playerTable.set("getExperience", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.experienceLevel) : LuaValue.valueOf(0);
            }
        });

        playerTable.set("getVelocity", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player == null) return LuaValue.NIL;
                Vec3d vel = mc.player.getVelocity();
                LuaTable velTable = new LuaTable();
                velTable.set("x", vel.x);
                velTable.set("y", vel.y);
                velTable.set("z", vel.z);
                return velTable;
            }
        });

        playerTable.set("getYaw", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getYaw()) : LuaValue.valueOf(0);
            }
        });

        playerTable.set("getPitch", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getPitch()) : LuaValue.valueOf(0);
            }
        });

        playerTable.set("isOnGround", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.isOnGround()) : LuaValue.FALSE;
            }
        });

        playerTable.set("isTouchingWater", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.isTouchingWater()) : LuaValue.FALSE;
            }
        });

        playerTable.set("isInLava", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.isInLava()) : LuaValue.FALSE;
            }
        });

        playerTable.set("isCreative", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getAbilities().creativeMode) : LuaValue.FALSE;
            }
        });

        playerTable.set("canFly", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return mc.player != null ? LuaValue.valueOf(mc.player.getAbilities().allowFlying) : LuaValue.FALSE;
            }
        });

        playerTable.set("setPosition", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.player == null) return LuaValue.NIL;
                double x = args.checkdouble(1);
                double y = args.checkdouble(2);
                double z = args.checkdouble(3);
                mc.player.setPosition(x, y, z);
                return LuaValue.NIL;
            }
        });

        playerTable.set("setVelocity", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.player == null) return LuaValue.NIL;
                double x = args.checkdouble(1);
                double y = args.checkdouble(2);
                double z = args.checkdouble(3);
                mc.player.setVelocity(x, y, z);
                return LuaValue.NIL;
            }
        });

        playerTable.set("setYaw", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null) return LuaValue.NIL;
                float yaw = (float) arg.checkdouble();
                mc.player.setYaw(yaw);
                return LuaValue.NIL;
            }
        });

        playerTable.set("setPitch", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null) return LuaValue.NIL;
                float pitch = (float) arg.checkdouble();
                mc.player.setPitch(pitch);
                return LuaValue.NIL;
            }
        });

        playerTable.set("jump", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player != null) {
                    mc.player.jump();
                }
                return LuaValue.NIL;
            }
        });

        playerTable.set("swingHand", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player != null) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
                return LuaValue.NIL;
            }
        });
    }

    private static void registerInventoryAPI(LuaTable alyaTable) {
        LuaTable invTable = new LuaTable();
        alyaTable.set("inventory", invTable);

        invTable.set("getHeldItem", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player == null) return LuaValue.NIL;
                ItemStack stack = mc.player.getMainHandStack();
                return CoerceJavaToLua.coerce(stack);
            }
        });

        invTable.set("getOffHandItem", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player == null) return LuaValue.NIL;
                ItemStack stack = mc.player.getOffHandStack();
                return CoerceJavaToLua.coerce(stack);
            }
        });

        invTable.set("getSlot", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null) return LuaValue.NIL;
                int slot = arg.checkint();
                if(slot < 0 || slot >= mc.player.getInventory().size()) return LuaValue.NIL;
                ItemStack stack = mc.player.getInventory().getStack(slot);
                return CoerceJavaToLua.coerce(stack);
            }
        });

        invTable.set("getSelectedSlot", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player == null) return LuaValue.valueOf(0);
                return LuaValue.valueOf(mc.player.getInventory().getSelectedSlot());
            }
        });

        invTable.set("setSelectedSlot", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null || mc.getNetworkHandler() == null) return LuaValue.NIL;
                int slot = arg.checkint();
                if(slot >= 0 && slot < 9) {
                    mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                }
                return LuaValue.NIL;
            }
        });

        invTable.set("findItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null) return LuaValue.valueOf(-1);
                String itemName = arg.checkjstring();
                for(int i = 0; i < mc.player.getInventory().size(); i++) {
                    ItemStack stack = mc.player.getInventory().getStack(i);
                    if(!stack.isEmpty() && stack.getItem().toString().contains(itemName)) {
                        return LuaValue.valueOf(i);
                    }
                }
                return LuaValue.valueOf(-1);
            }
        });

        invTable.set("getItemCount", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null) return LuaValue.valueOf(0);
                String itemName = arg.checkjstring();
                int count = 0;
                for(int i = 0; i < mc.player.getInventory().size(); i++) {
                    ItemStack stack = mc.player.getInventory().getStack(i);
                    if(!stack.isEmpty() && stack.getItem().toString().contains(itemName)) {
                        count += stack.getCount();
                    }
                }
                return LuaValue.valueOf(count);
            }
        });
    }

    private static void registerMathAPI(LuaTable alyaTable) {
        LuaTable mathTable = new LuaTable();
        alyaTable.set("math", mathTable);

        mathTable.set("distance", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                double x1 = args.checkdouble(1);
                double y1 = args.checkdouble(2);
                double z1 = args.checkdouble(3);
                double x2 = args.checkdouble(4);
                double y2 = args.checkdouble(5);
                double z2 = args.checkdouble(6);
                double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2) + Math.pow(z2 - z1, 2));
                return LuaValue.valueOf(distance);
            }
        });

        mathTable.set("distance2D", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                double x1 = args.checkdouble(1);
                double z1 = args.checkdouble(2);
                double x2 = args.checkdouble(3);
                double z2 = args.checkdouble(4);
                double distance = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(z2 - z1, 2));
                return LuaValue.valueOf(distance);
            }
        });

        mathTable.set("wrapAngle", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                float angle = (float) arg.checkdouble();
                return LuaValue.valueOf(MathHelper.wrapDegrees(angle));
            }
        });

        mathTable.set("angleTo", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.player == null) return LuaValue.NIL;
                double x = args.checkdouble(1);
                double y = args.checkdouble(2);
                double z = args.checkdouble(3);

                Vec3d playerPos = mc.player.getPos();
                double deltaX = x - playerPos.x;
                double deltaZ = z - playerPos.z;
                double deltaY = y - (playerPos.y + mc.player.getEyeHeight(mc.player.getPose()));

                double yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90;
                double pitch = -Math.toDegrees(Math.atan2(deltaY, Math.sqrt(deltaX * deltaX + deltaZ * deltaZ)));

                LuaTable angles = new LuaTable();
                angles.set("yaw", MathHelper.wrapDegrees((float) yaw));
                angles.set("pitch", MathHelper.wrapDegrees((float) pitch));
                return angles;
            }
        });

        mathTable.set("lerp", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                double start = arg1.checkdouble();
                double end = arg2.checkdouble();
                double factor = arg3.checkdouble();
                return LuaValue.valueOf(start + (end - start) * factor);
            }
        });

        mathTable.set("clamp", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
                double value = arg1.checkdouble();
                double min = arg2.checkdouble();
                double max = arg3.checkdouble();
                return LuaValue.valueOf(Math.max(min, Math.min(max, value)));
            }
        });
    }

    private static void registerNetworkAPI(LuaTable alyaTable) {
        LuaTable netTable = new LuaTable();
        alyaTable.set("network", netTable);

        netTable.set("sendPacket", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null || mc.getNetworkHandler() == null) return LuaValue.NIL;
                Object packet = arg.checkuserdata();
                if(packet instanceof net.minecraft.network.packet.Packet) {
                    mc.getNetworkHandler().sendPacket((net.minecraft.network.packet.Packet<?>) packet);
                }
                return LuaValue.NIL;
            }
        });

        netTable.set("sendMovementPacket", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.player == null || mc.getNetworkHandler() == null) return LuaValue.NIL;
                double x = args.checkdouble(1);
                double y = args.checkdouble(2);
                double z = args.checkdouble(3);
                float yaw = (float) args.optdouble(4, mc.player.getYaw());
                float pitch = (float) args.optdouble(5, mc.player.getPitch());
                boolean onGround = args.optboolean(6, mc.player.isOnGround());
                boolean changePosition = args.optboolean(7, true);

                PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround, changePosition);
                mc.getNetworkHandler().sendPacket(packet);
                return LuaValue.NIL;
            }
        });

        netTable.set("sendHandSwing", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player == null || mc.getNetworkHandler() == null) return LuaValue.NIL;
                mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                return LuaValue.NIL;
            }
        });
    }

    private static void registerSoundAPI(LuaTable alyaTable) {
        LuaTable soundTable = new LuaTable();
        alyaTable.set("sound", soundTable);

        soundTable.set("playSound", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.player == null || mc.world == null) return LuaValue.NIL;
                String soundName = args.checkjstring(1);
                float volume = (float) args.optdouble(2, 1.0);
                float pitch = (float) args.optdouble(3, 1.0);

                try {
                    String[] parts = soundName.split(":");
                    if(parts.length != 2) return LuaValue.NIL;

                    Identifier soundId = Identifier.of(parts[0], parts[1]);
                    SoundEvent sound = Registries.SOUND_EVENT.get(soundId);
                    if(sound != null) {
                        mc.world.playSound(mc.player, mc.player.getBlockPos(), sound, SoundCategory.MASTER, volume, pitch);
                    }
                } catch(Exception e) {
                    ChatUtility.sendError("Failed to play sound \"" + soundName + "\": " + e.getMessage());
                    ChatUtility.sendScriptError(e);
                }
                return LuaValue.NIL;
            }
        });

        soundTable.set("playSoundAt", new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                if(mc.world == null) return LuaValue.NIL;
                String soundName = args.checkjstring(1);
                double x = args.checkdouble(2);
                double y = args.checkdouble(3);
                double z = args.checkdouble(4);
                float volume = (float) args.optdouble(5, 1.0);
                float pitch = (float) args.optdouble(6, 1.0);

                try {
                    String[] parts = soundName.split(":");
                    if(parts.length != 2) return LuaValue.NIL;

                    Identifier soundId = Identifier.of(parts[0], parts[1]);
                    SoundEvent sound = Registries.SOUND_EVENT.get(soundId);
                    if(sound != null) {
                        mc.world.playSound(null, new BlockPos((int) x, (int) y, (int) z), sound, SoundCategory.MASTER, volume, pitch);
                    }
                } catch(Exception e) {
                    ChatUtility.sendError("Invalid sound name: " + soundName);
                    ChatUtility.sendScriptError(e);
                }
                return LuaValue.NIL;
            }
        });
    }

    private static void registerEntityAPI(LuaTable alyaTable) {
        LuaTable entityTable = new LuaTable();
        alyaTable.set("entity", entityTable);

        entityTable.set("getClosestPlayer", new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                if(mc.player == null || mc.world == null) return LuaValue.NIL;

                PlayerEntity closest = null;
                double closestDistance = Double.MAX_VALUE;

                for(Entity entity : mc.world.getEntities()) {
                    if(entity instanceof PlayerEntity player && entity != mc.player) {
                        double distance = mc.player.distanceTo(player);
                        if(distance < closestDistance) {
                            closestDistance = distance;
                            closest = player;
                        }
                    }
                }

                return closest != null ? CoerceJavaToLua.coerce(closest) : LuaValue.NIL;
            }
        });

        entityTable.set("getClosestEntity", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null || mc.world == null) return LuaValue.NIL;

                String entityType = arg.checkjstring();
                Entity closest = null;
                double closestDistance = Double.MAX_VALUE;

                for(Entity entity : mc.world.getEntities()) {
                    if(entity != mc.player && entity.getType().toString().contains(entityType)) {
                        double distance = mc.player.distanceTo(entity);
                        if(distance < closestDistance) {
                            closestDistance = distance;
                            closest = entity;
                        }
                    }
                }

                return closest != null ? CoerceJavaToLua.coerce(closest) : LuaValue.NIL;
            }
        });

        entityTable.set("getEntityPosition", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Entity entity = (Entity) arg.checkuserdata();
                if(entity == null) return LuaValue.NIL;

                Vec3d pos = entity.getPos();
                LuaTable posTable = new LuaTable();
                posTable.set("x", pos.x);
                posTable.set("y", pos.y);
                posTable.set("z", pos.z);
                return posTable;
            }
        });

        entityTable.set("getEntityHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Entity entity = (Entity) arg.checkuserdata();
                if(entity instanceof LivingEntity living) {
                    return LuaValue.valueOf(living.getHealth());
                }
                return LuaValue.valueOf(0);
            }
        });

        entityTable.set("getEntityMaxHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Entity entity = (Entity) arg.checkuserdata();
                if(entity instanceof LivingEntity living) {
                    return LuaValue.valueOf(living.getMaxHealth());
                }
                return LuaValue.valueOf(0);
            }
        });

        entityTable.set("isEntityAlive", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Entity entity = (Entity) arg.checkuserdata();
                return LuaValue.valueOf(entity != null && entity.isAlive());
            }
        });

        entityTable.set("getEntityName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Entity entity = (Entity) arg.checkuserdata();
                return entity != null ? LuaValue.valueOf(entity.getName().getString()) : LuaValue.NIL;
            }
        });

        entityTable.set("getEntityType", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                Entity entity = (Entity) arg.checkuserdata();
                return entity != null ? LuaValue.valueOf(entity.getType().toString()) : LuaValue.NIL;
            }
        });

        entityTable.set("getPlayersInRange", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                if(mc.player == null || mc.world == null) return LuaValue.NIL;

                double range = arg.checkdouble();
                List<PlayerEntity> players = new ArrayList<>();

                for(Entity entity : mc.world.getEntities()) {
                    if(entity instanceof PlayerEntity player && entity != mc.player) {
                        if(mc.player.distanceTo(player) <= range) {
                            players.add(player);
                        }
                    }
                }

                return CoerceJavaToLua.coerce(players);
            }
        });

        entityTable.set("getEntitiesInRange", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue arg1, LuaValue arg2) {
                if(mc.player == null || mc.world == null) return LuaValue.NIL;

                double range = arg1.checkdouble();
                String entityType = arg2.checkjstring();
                List<Entity> entities = new ArrayList<>();

                for(Entity entity : mc.world.getEntities()) {
                    if(entity != mc.player && entity.getType().toString().contains(entityType)) {
                        if(mc.player.distanceTo(entity) <= range) {
                            entities.add(entity);
                        }
                    }
                }

                return CoerceJavaToLua.coerce(entities);
            }
        });
    }
}
