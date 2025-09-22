package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
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
