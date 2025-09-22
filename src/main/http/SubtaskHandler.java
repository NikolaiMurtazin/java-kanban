package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager manager, Gson gson) { super(manager, gson); }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String path = h.getRequestURI().getPath(); // /subtasks или /subtasks/epic
            if (path.endsWith("/epic") && "GET".equals(h.getRequestMethod())) {
                handleGetByEpic(h);
                return;
            }

            switch (h.getRequestMethod()) {
                case "GET"    -> handleGet(h);
                case "POST"   -> handlePost(h);
                case "DELETE" -> handleDelete(h);
                default       -> sendText(h, 405, "Method Not Allowed");
            }
        } catch (IllegalArgumentException overlap) {
            sendHasInteractions(h, "Subtask time overlaps: " + overlap.getMessage());
        } catch (Exception e) {
            sendText(h, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            List<Subtask> subs = manager.getAllSubtasks();
            sendJson(h, 200, gson.toJson(subs));
            return;
        }
        int id = idOpt.get();
        Subtask s = manager.getSubtaskById(id);
        if (s == null) { sendNotFound(h, "Subtask id=" + id + " not found"); return; }
        sendJson(h, 200, gson.toJson(s));
    }

    private void handleGetByEpic(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) { sendText(h, 400, "epic id is required"); return; }
        int epicId = idOpt.get();
        Epic e = manager.getEpicById(epicId);
        if (e == null) { sendNotFound(h, "Epic id=" + epicId + " not found"); return; }
        sendJson(h, 200, gson.toJson(manager.getEpicSubtasks(epicId)));
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Subtask incoming = gson.fromJson(body, Subtask.class);
        if (incoming == null) { sendText(h, 400, "Bad Request: empty/invalid JSON"); return; }

        // проверим существование эпика
        if (manager.getEpicById(incoming.getEpicId()) == null) {
            sendNotFound(h, "Epic id=" + incoming.getEpicId() + " not found");
            return;
        }

        if (incoming.getId() > 0 && manager.getSubtaskById(incoming.getId()) != null) {
            manager.updateSubtask(incoming);
            sendJson(h, 200, gson.toJson(incoming));
        } else {
            Subtask created = manager.createSubtask(incoming);
            sendJson(h, 201, gson.toJson(created));
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            manager.removeAllSubtasks();
            sendText(h, 200, "All subtasks removed");
            return;
        }
        int id = idOpt.get();
        if (manager.getSubtaskById(id) == null) { sendNotFound(h, "Subtask id=" + id + " not found"); return; }
        manager.deleteSubtaskById(id);
        sendText(h, 200, "Subtask id=" + id + " removed");
    }
}
