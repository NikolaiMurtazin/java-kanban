package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    protected void sendJson(HttpExchange h, int status, String json) throws IOException {
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(status, resp.length);
        try (OutputStream os = h.getResponseBody()) { os.write(resp); }
    }

    protected void sendText(HttpExchange h, int status, String text) throws IOException {
        sendJson(h, status, "\"" + text.replace("\"","\\\"") + "\"");
    }

    protected void sendNotFound(HttpExchange h, String msg) throws IOException {
        sendText(h, 404, msg);
    }

    protected void sendHasInteractions(HttpExchange h, String msg) throws IOException {
        sendText(h, 406, msg);
    }

    protected String readBody(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    protected Optional<Integer> getQueryId(HttpExchange h) {
        String query = h.getRequestURI().getQuery(); // например "id=123"
        if (query == null) return Optional.empty();
        for (String p : query.split("&")) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2 && kv[0].equals("id")) {
                try { return Optional.of(Integer.parseInt(kv[1])); }
                catch (NumberFormatException ignored) {}
            }
        }
        return Optional.empty();
    }
}
