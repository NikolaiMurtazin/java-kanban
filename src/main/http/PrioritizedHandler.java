package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

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
