package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Base class for all HTTP handlers in the Task Tracker application.
 * <p>
 * Provides utility methods for:
 * <ul>
 *     <li>Sending JSON and plain-text responses with appropriate headers</li>
 *     <li>Standard error responses (404, 406)</li>
 *     <li>Reading the request body</li>
 *     <li>Extracting query parameters (e.g., {@code id=123})</li>
 * </ul>
 *
 * All specific handlers (e.g. {@code TasksHandler}, {@code EpicsHandler})
 * should extend this class instead of implementing {@link HttpHandler} directly,
 * to reduce boilerplate code and ensure consistent response formatting.
 */
public abstract class BaseHttpHandler implements HttpHandler {
    /** Reference to the task manager used for CRUD operations. */
    protected final TaskManager manager;

    /** Shared Gson instance for JSON serialization/deserialization. */
    protected final Gson gson;

    /**
     * Constructs a new base handler.
     *
     * @param manager the task manager to operate on
     * @param gson    the Gson instance for JSON conversion
     */
    protected BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    /**
     * Sends a JSON response with the given HTTP status code.
     *
     * @param h      HTTP exchange object
     * @param status HTTP status code (e.g. 200, 201, 404)
     * @param json   JSON string to send as response
     * @throws IOException if writing to the response fails
     */
    protected void sendJson(HttpExchange h, int status, String json) throws IOException {
        byte[] resp = json.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        h.sendResponseHeaders(status, resp.length);
        try (OutputStream os = h.getResponseBody()) {
            os.write(resp);
        }
    }

    /**
     * Sends a plain text response (wrapped into JSON string format).
     * Escapes quotes to ensure valid JSON output.
     *
     * @param h      HTTP exchange object
     * @param status HTTP status code
     * @param text   text message
     * @throws IOException if writing to the response fails
     */
    protected void sendText(HttpExchange h, int status, String text) throws IOException {
        sendJson(h, status, "\"" + text.replace("\"", "\\\"") + "\"");
    }

    /**
     * Convenience method: sends a 404 (Not Found) response with a message.
     *
     * @param h   HTTP exchange object
     * @param msg explanation text
     * @throws IOException if writing to the response fails
     */
    protected void sendNotFound(HttpExchange h, String msg) throws IOException {
        sendText(h, 404, msg);
    }

    /**
     * Convenience method: sends a 406 (Not Acceptable) response with a message.
     * Used when a new or updated task overlaps with existing tasks.
     *
     * @param h   HTTP exchange object
     * @param msg explanation text
     * @throws IOException if writing to the response fails
     */
    protected void sendHasInteractions(HttpExchange h, String msg) throws IOException {
        sendText(h, 406, msg);
    }

    /**
     * Reads the full request body as a UTF-8 string.
     *
     * @param h HTTP exchange object
     * @return request body as string
     * @throws IOException if reading fails
     */
    protected String readBody(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Extracts the {@code id} query parameter from the request URI, if present.
     * <p>
     * Example: for {@code /tasks?id=42}, returns {@code Optional.of(42)}.
     *
     * @param h HTTP exchange object
     * @return optional integer ID, empty if not present or invalid
     */
    protected Optional<Integer> getQueryId(HttpExchange h) {
        String query = h.getRequestURI().getQuery(); // e.g. "id=123"
        if (query == null) return Optional.empty();
        for (String p : query.split("&")) {
            String[] kv = p.split("=", 2);
            if (kv.length == 2 && kv[0].equals("id")) {
                try {
                    return Optional.of(Integer.parseInt(kv[1]));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return Optional.empty();
    }
}