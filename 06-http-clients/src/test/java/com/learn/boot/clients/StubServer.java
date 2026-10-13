package com.learn.boot.clients;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * A fake remote API built on the JDK's tiny HTTP server: no internet, fully controllable,
 * can be told to fail. Each "path + query" gets a queue of scripted responses; the last one repeats.
 */
public final class StubServer {
    public record Reply(int status, String json) {
    }

    private final HttpServer server;
    private final Map<String, Queue<Reply>> replies = new ConcurrentHashMap<>();
    private final List<String> requests = new CopyOnWriteArrayList<>();

    public StubServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        server.createContext("/", this::handle);
        server.start();
    }

    public String baseUrl() {
        return "http://localhost:" + server.getAddress().getPort();
    }

    public void reply(String method, String pathAndQuery, Reply... scripted) {
        replies.put(method + " " + pathAndQuery, new ArrayDeque<>(List.of(scripted)));
    }

    public List<String> requests() {
        return List.copyOf(requests);
    }

    public void reset() {
        replies.clear();
        requests.clear();
    }

    public void stop() {
        server.stop(0);
    }

    private void handle(HttpExchange exchange) throws IOException {
        String key = exchange.getRequestMethod() + " " + exchange.getRequestURI();
        try (InputStream body = exchange.getRequestBody()) {
            requests.add(key + " " + new String(body.readAllBytes(), StandardCharsets.UTF_8));
        }
        Queue<Reply> queue = replies.get(key);
        Reply reply = queue == null ? new Reply(404, "{}") : (queue.size() > 1 ? queue.poll() : queue.peek());
        byte[] bytes = reply.json().getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(reply.status(), bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
