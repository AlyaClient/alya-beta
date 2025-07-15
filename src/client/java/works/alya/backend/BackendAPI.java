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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import works.alya.AlyaClient;

public class BackendAPI {
    private static final String BASE_URL = "https://rye.thoq.dev/api/v1/users";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public BackendAPI() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public void joinOnline(String userId, String sessionId) throws Exception {
        String requestBody = """
                {
                    "userId": "%s",
                    "sessionId": "%s",
                    "action": "join"
                }
                """.formatted(userId, sessionId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() != 200) {
            AlyaClient.LOGGER.error("Error: {} - {}", response.statusCode(), response.body());
        }
    }

    public void sendHeartbeat(String userId, String sessionId) throws Exception {
        String requestBody = """
                {
                    "userId": "%s",
                    "sessionId": "%s",
                    "action": "heartbeat"
                }
                """.formatted(userId, sessionId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public void leaveOnline(String userId, String sessionId) throws Exception {
        String requestBody = """
                {
                    "userId": "%s",
                    "sessionId": "%s",
                    "action": "leave"
                }
                """.formatted(userId, sessionId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(30))
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public int getOnlineCount() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .GET()
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200) {
            JsonNode jsonResponse = objectMapper.readTree(response.body());
            return jsonResponse.get("onlineUsers").asInt();
        }
        return -1;
    }
}