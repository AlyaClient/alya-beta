///*
// * Copyright (c) Rye Client 2024-2025.
// *
// * This file belongs to Rye Client,
// * an open-source Fabric injection client.
// * Rye GitHub: https://github.com/RyeClient/rye-v1.git
// *
// * THIS PROJECT DOES NOT HAVE A WARRANTY.
// *
// * Rye (and subsequently, its files) are all licensed under the MIT License.
// * Rye should have come with a copy of the MIT License.
// * If it did not, you may obtain a copy here:
// * MIT License: https://opensource.org/license/mit
// *
// */
//
//package dev.thoq.utilities.integration;
// TODO: implement
//import net.arikia.dev.drpc.DiscordEventHandlers;
//import net.arikia.dev.drpc.DiscordRPC;
//import net.arikia.dev.drpc.DiscordRichPresence;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.network.ServerInfo;
//
///**
// * Discord Rich Presence integration for Rye Client
// * Displays current game status and server information
// */
//public class DiscordIntegration {
//
//    private static final String APPLICATION_ID = "YOUR_DISCORD_APPLICATION_ID";
//    private static final String LARGE_IMAGE_KEY = "rye_logo";
//    private static final String SMALL_IMAGE_KEY = "minecraft_icon";
//
//    private static boolean initialized = false;
//    private static boolean enabled = true;
//
//    /**
//     * Initialize Discord RPC integration
//     */
//    public static void initialize() {
//        if (initialized) return;
//
//        try {
//            DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
//                    .setReadyEventHandler((user) -> {
//                        System.out.println("Discord RPC initialized for user: " + user.username);
//                        updatePresence();
//                    })
//                    .setDisconnectedEventHandler((errorCode, message) -> {
//                        System.err.println("Discord RPC disconnected: " + message);
//                    })
//                    .setErroredEventHandler((errorCode, message) -> {
//                        System.err.println("Discord RPC error: " + message);
//                    })
//                    .build();
//
//            DiscordRPC.discordInitialize(APPLICATION_ID, handlers, true);
//            initialized = true;
//
//            // Start update thread
//            startUpdateThread();
//
//        } catch (Exception e) {
//            System.err.println("Failed to initialize Discord RPC: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * Update Discord presence based on current game state
//     */
//    public static void updatePresence() {
//        if (!initialized || !enabled) return;
//
//        try {
//            MinecraftClient client = MinecraftClient.getInstance();
//            DiscordRichPresence.Builder builder = new DiscordRichPresence.Builder("");
//
//            // Set basic client info
//            builder.setBigImage(LARGE_IMAGE_KEY, "Rye Client");
//            builder.setSmallImage(SMALL_IMAGE_KEY, "Minecraft " + getMinecraftVersion());
//
//            // Update based on game state
//            if (client.world == null) {
//                // In main menu
//                builder.setDetails("In Main Menu");
//                builder.setState("Rye Client v1.0");
//            } else if (client.isInSingleplayer()) {
//                // In singleplayer world
//                builder.setDetails("Playing Singleplayer");
//                builder.setState("Rye Client v1.0");
//            } else if (client.getCurrentServerEntry() != null) {
//                // On multiplayer server
//                ServerInfo serverInfo = client.getCurrentServerEntry();
//                builder.setDetails("Playing on " + serverInfo.name);
//                builder.setState("Rye Client v1.0");
//            } else {
//                // Fallback
//                builder.setDetails("Playing Minecraft");
//                builder.setState("Rye Client v1.0");
//            }
//
//            // Set start timestamp
//            builder.setStartTimestamps(System.currentTimeMillis());
//
//            DiscordRPC.discordUpdatePresence(builder.build());
//
//        } catch (Exception e) {
//            System.err.println("Failed to update Discord presence: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Start the background thread that updates presence periodically
//     */
//    private static void startUpdateThread() {
//        Thread updateThread = new Thread(() -> {
//            while (initialized && enabled) {
//                try {
//                    DiscordRPC.discordRunCallbacks();
//                    Thread.sleep(2000); // Update every 2 seconds
//                } catch (InterruptedException e) {
//                    break;
//                } catch (Exception e) {
//                    System.err.println("Discord RPC update thread error: " + e.getMessage());
//                }
//            }
//        });
//
//        updateThread.setDaemon(true);
//        updateThread.setName("Discord RPC Update Thread");
//        updateThread.start();
//    }
//
//    /**
//     * Get the current Minecraft version
//     */
//    private static String getMinecraftVersion() {
//        try {
//            return MinecraftClient.getInstance().getGameVersion();
//        } catch (Exception e) {
//            return "Unknown";
//        }
//    }
//
//    /**
//     * Enable or disable Discord RPC
//     */
//    public static void setEnabled(boolean enabled) {
//        DiscordIntegration.enabled = enabled;
//        if (!enabled) {
//            clearPresence();
//        } else if (initialized) {
//            updatePresence();
//        }
//    }
//
//    /**
//     * Check if Discord RPC is enabled
//     */
//    public static boolean isEnabled() {
//        return enabled;
//    }
//
//    /**
//     * Clear the Discord presence
//     */
//    public static void clearPresence() {
//        if (initialized) {
//            DiscordRPC.discordClearPresence();
//        }
//    }
//
//    /**
//     * Shutdown Discord RPC
//     */
//    public static void shutdown() {
//        if (initialized) {
//            DiscordRPC.discordShutdown();
//            initialized = false;
//        }
//    }
//}