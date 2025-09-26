package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import history.HistoryManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HTTP handler for serving task history from {@link HistoryManager}.
 * <p>
 * Endpoint: {@code /history}
 * <ul>
 *     <li><b>GET /history</b> — returns the list of recently viewed tasks (JSON array)</li>
 * </ul>
 * <p>
 * Response codes:
 * <ul>
 *     <li>200 — request successful, returns history as JSON</li>
 *     <li>405 — method not allowed (only GET supported)</li>
 *     <li>500 — internal server error</li>
 * </ul>
 */
public record HistoryHandler(HistoryManager history, Gson gson) implements HttpHandler {
    /**
     * Creates a new history handler.
     *
     * @param history history manager to fetch recently viewed tasks
     * @param gson    gson instance for JSON serialization
     */
    public HistoryHandler {
    }

    /**
     * Handles HTTP requests for {@code /history}.
     *
     * @param h HTTP exchange
     * @throws IOException if reading/writing response fails
     */
    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            if (!"GET".equals(h.getRequestMethod())) {
                h.sendResponseHeaders(405, -1);
                h.close();
                return;
            }
            List<Task> hist = history.getHistory();
            byte[] resp = gson.toJson(hist).getBytes(StandardCharsets.UTF_8);

            h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
            h.sendResponseHeaders(200, resp.length);
            try (var os = h.getResponseBody()) {
                os.write(resp);
            }
        } catch (Exception e) {
            String msg = "Internal Server Error: " + e.getMessage();
            byte[] resp = ("\"" + msg.replace("\"", "\\\"") + "\"")
                    .getBytes(StandardCharsets.UTF_8);

            h.sendResponseHeaders(500, resp.length);
            try (var os = h.getResponseBody()) {
                os.write(resp);
            }
        }
    }
}