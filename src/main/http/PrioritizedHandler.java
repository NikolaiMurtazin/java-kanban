package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

/**
 * HTTP handler for prioritized tasks endpoint.
 * <p>
 * Endpoint: {@code /prioritized}
 * <ul>
 *     <li><b>GET /prioritized</b> — returns all tasks and subtasks
 *         sorted by {@link Task#getStartTime()} (JSON array)</li>
 * </ul>
 *
 * <p>Response codes:
 * <ul>
 *     <li>200 — request successful, returns list of prioritized tasks</li>
 *     <li>405 — method not allowed (only GET is supported)</li>
 *     <li>500 — internal server error</li>
 * </ul>
 */
public class PrioritizedHandler extends BaseHttpHandler {

    /**
     * Creates a new handler for prioritized tasks.
     *
     * @param manager task manager to fetch prioritized tasks from
     * @param gson    gson instance for JSON serialization
     */
    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    /**
     * Handles incoming HTTP requests for {@code /prioritized}.
     *
     * @param h HTTP exchange
     * @throws IOException if writing response fails
     */
    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            if (!"GET".equals(h.getRequestMethod())) {
                sendText(h, 405, "Method Not Allowed");
                return;
            }
            List<Task> prioritized = manager.getPrioritizedTasks();
            sendJson(h, 200, gson.toJson(prioritized));
        } catch (Exception e) {
            sendText(h, 500, "Internal Server Error: " + e.getMessage());
        }
    }
}