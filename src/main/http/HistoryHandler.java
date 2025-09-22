package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import history.HistoryManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler implements HttpHandler {
    private final HistoryManager history;
    private final Gson gson;

    public HistoryHandler(HistoryManager history, Gson gson) {
        this.history = history;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            if (!"GET".equals(h.getRequestMethod())) {
                h.sendResponseHeaders(405, -1);
                h.close();
                return;
            }
            List<Task> hist = history.getHistory();
            byte[] resp = gson.toJson(hist).getBytes(java.nio.charset.StandardCharsets.UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            h.sendResponseHeaders(200, resp.length);
            try (var os = h.getResponseBody()) {
                os.write(resp);
            }
        } catch (Exception e) {
            String msg = "Internal Server Error: " + e.getMessage();
            byte[] resp = ("\"" + msg.replace("\"", "\\\"") + "\"").getBytes(java.nio.charset.StandardCharsets.UTF_8);
            h.sendResponseHeaders(500, resp.length);
            try (var os = h.getResponseBody()) {
                os.write(resp);
            }
        }
    }
}