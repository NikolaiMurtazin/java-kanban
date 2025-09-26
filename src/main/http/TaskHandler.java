package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * HTTP handler for tasks endpoint.
 * <p>
 * Base endpoint: {@code /tasks}
 * <ul>
 *     <li><b>GET /tasks</b> — returns all tasks (JSON array)</li>
 *     <li><b>GET /tasks?id={id}</b> — returns a task by ID</li>
 *     <li><b>POST /tasks</b> — creates or updates a task (expects JSON body)</li>
 *     <li><b>DELETE /tasks</b> — removes all tasks</li>
 *     <li><b>DELETE /tasks?id={id}</b> — removes a task by ID</li>
 * </ul>
 *
 * <p>Response codes:
 * <ul>
 *     <li>200 — request successful</li>
 *     <li>201 — task created</li>
 *     <li>400 — invalid input (e.g. empty/invalid JSON)</li>
 *     <li>404 — task not found</li>
 *     <li>405 — method not allowed</li>
 *     <li>406 — task time overlaps with another task</li>
 *     <li>500 — internal server error</li>
 * </ul>
 */
public class TaskHandler extends BaseHttpHandler {

    /**
     * Creates a new task handler.
     *
     * @param manager task manager to delegate business logic
     * @param gson    gson instance for JSON serialization/deserialization
     */
    public TaskHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            switch (h.getRequestMethod()) {
                case "GET" -> handleGet(h);
                case "POST" -> handlePost(h);
                case "DELETE" -> handleDelete(h);
                default -> sendText(h, 405, "Method Not Allowed");
            }
        } catch (IllegalArgumentException overlap) {
            sendHasInteractions(h, "Task time overlaps: " + overlap.getMessage());
        } catch (Exception e) {
            sendText(h, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            List<Task> tasks = manager.getAllTasks();
            sendJson(h, 200, gson.toJson(tasks));
            return;
        }
        int id = idOpt.get();
        Task t = manager.getTaskById(id);
        if (t == null) {
            sendNotFound(h, "Task id=" + id + " not found");
            return;
        }
        sendJson(h, 200, gson.toJson(t));
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Task incoming = gson.fromJson(body, Task.class);
        if (incoming == null) {
            sendText(h, 400, "Bad Request: empty/invalid JSON");
            return;
        }

        if (incoming.getId() > 0 && manager.getTaskById(incoming.getId()) != null) {
            manager.updateTask(incoming);
            sendJson(h, 200, gson.toJson(incoming));
        } else {
            Task created = manager.createTask(incoming);
            sendJson(h, 201, gson.toJson(created));
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            manager.removeAllTasks();
            sendText(h, 200, "All tasks removed");
            return;
        }
        int id = idOpt.get();
        if (manager.getTaskById(id) == null) {
            sendNotFound(h, "Task id=" + id + " not found");
            return;
        }
        manager.deleteTaskById(id);
        sendText(h, 200, "Task id=" + id + " removed");
    }
}