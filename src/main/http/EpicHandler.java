package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager manager, Gson gson) { super(manager, gson); }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            switch (h.getRequestMethod()) {
                case "GET"    -> handleGet(h);
                case "POST"   -> handlePost(h);
                case "DELETE" -> handleDelete(h);
                default       -> sendText(h, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            sendText(h, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            List<Epic> epics = manager.getAllEpics();
            sendJson(h, 200, gson.toJson(epics));
            return;
        }
        int id = idOpt.get();
        Epic e = manager.getEpicById(id);
        if (e == null) { sendNotFound(h, "Epic id=" + id + " not found"); return; }
        sendJson(h, 200, gson.toJson(e));
    }

    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Epic incoming = gson.fromJson(body, Epic.class);
        if (incoming == null) { sendText(h, 400, "Bad Request: empty/invalid JSON"); return; }

        if (incoming.getId() > 0 && manager.getEpicById(incoming.getId()) != null) {
            manager.updateEpic(incoming);   // обновляем только имя/описание
            sendJson(h, 200, gson.toJson(incoming));
        } else {
            Epic created = manager.createEpic(incoming);
            sendJson(h, 201, gson.toJson(created));
        }
    }

    private void handleDelete(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            manager.removeAllEpics();
            sendText(h, 200, "All epics (and subtasks) removed");
            return;
        }
        int id = idOpt.get();
        if (manager.getEpicById(id) == null) { sendNotFound(h, "Epic id=" + id + " not found"); return; }
        manager.deleteEpicById(id);
        sendText(h, 200, "Epic id=" + id + " removed");
    }
}
