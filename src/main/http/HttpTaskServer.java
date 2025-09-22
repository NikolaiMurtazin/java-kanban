package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import history.HistoryManager;
import http.json.DurationAdapter;
import http.json.LocalDateTimeAdapter;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;

    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private final Gson gson;
    private HttpServer server;

    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public HttpTaskServer() {
        this(Managers.getDefault(), Managers.getDefaultHistory());
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/tasks", new TaskHandler(taskManager, gson));
            server.createContext("/epics", new EpicHandler(taskManager, gson));
            server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            server.createContext("/history", new HistoryHandler(historyManager, gson));
            server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));

            server.setExecutor(null);
            server.start();
            System.out.println("HTTP server started on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start HTTP server on port " + PORT, e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP server stopped");
        }
    }

    public Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
        System.out.println("Task server is running on http://localhost:" + PORT);
    }
}
