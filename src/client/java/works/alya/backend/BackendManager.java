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

package works.alya.backend;

import works.alya.AlyaClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

public class BackendManager {
    private final BackendAPI client;
    private final ScheduledExecutorService scheduler;
    private final String userId;
    private final String sessionId;
    private volatile boolean isOnline = false;

    public BackendManager(String userId) {
        this.client = new BackendAPI();
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.userId = userId;
        this.sessionId = UUID.randomUUID().toString();
    }

    public void goOnline() {
        try {
            client.joinOnline(userId, sessionId);
            isOnline = true;

            scheduler.scheduleAtFixedRate(() -> {
                if (isOnline) {
                    try {
                        client.sendHeartbeat(userId, sessionId);
                    } catch (Exception e) {
                        AlyaClient.LOGGER.error("Heartbeat failed: {}", e.getMessage());
                    }
                }
            }, 120, 120, TimeUnit.SECONDS);
        } catch (Exception e) {
            AlyaClient.LOGGER.error("Failed to go online: {}", e.getMessage());
        }
    }

    public void goOffline() {
        try {
            isOnline = false;
            client.leaveOnline(userId, sessionId);
            scheduler.shutdown();
        } catch (Exception e) {
            AlyaClient.LOGGER.error("Failed to go offline: {}", e.getMessage());
        }
    }

    public int getCurrentOnlineCount() {
        try {
            return client.getOnlineCount();
        } catch (Exception e) {
            AlyaClient.LOGGER.error("Failed to get online count: {}", e.getMessage());
            return -1;
        }
    }
}