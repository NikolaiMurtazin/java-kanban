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

/**
 * Main HTTP server that exposes the Task Manager API.
 * <p>
 * Endpoints:
 * <ul>
 *     <li><b>/tasks</b> — CRUD operations for {@link model.Task}</li>
 *     <li><b>/epics</b> — CRUD operations for {@link model.Epic}</li>
 *     <li><b>/subtasks</b> — CRUD operations for {@link model.Subtask}</li>
 *     <li><b>/history</b> — retrieves recently viewed tasks</li>
 *     <li><b>/prioritized</b> — retrieves prioritized tasks sorted by start time</li>
 * </ul>
 * <p>
 * JSON serialization is powered by {@link Gson}, with custom adapters for
 * {@link LocalDateTime} and {@link Duration}.
 * <p>
 * The server listens on port {@value PORT}.
 */
public class HttpTaskServer {
    /** Default HTTP port for the server. */
    public static final int PORT = 8080;

    private final TaskManager taskManager;
    private final HistoryManager historyManager;
    private final Gson gson;
    private HttpServer server;

    /**
     * Creates a new HTTP server with the given managers.
     *
     * @param taskManager    task manager to handle core operations
     * @param historyManager history manager to serve history data
     */
    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    /**
     * Creates a new HTTP server using default managers from {@link Managers}.
     */
    public HttpTaskServer() {
        this(Managers.getDefault(), Managers.getDefaultHistory());
    }

    /**
     * Starts the HTTP server and registers all endpoint contexts.
     * <p>
     * Endpoints are handled by their respective handlers.
     *
     * @throws RuntimeException if the server cannot start
     */
    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            server.createContext("/tasks", new TaskHandler(taskManager, gson));
            server.createContext("/epics", new EpicHandler(taskManager, gson));
            server.createContext("/subtasks", new SubtaskHandler(taskManager, gson));
            server.createContext("/history", new HistoryHandler(historyManager, gson));
            server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));

            server.setExecutor(null); // default executor
            server.start();
            System.out.println("HTTP server started on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start HTTP server on port " + PORT, e);
        }
    }

    /**
     * Stops the HTTP server immediately.
     */
    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("HTTP server stopped");
        }
    }

    /**
     * Returns the {@link Gson} instance used for serialization/deserialization.
     *
     * @return gson instance
     */
    public Gson getGson() {
        return gson;
    }

    /**
     * Application entry point.
     * Starts the server on {@value PORT} using default managers.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        HttpTaskServer server = new HttpTaskServer();
        server.start();
        System.out.println("Task server is running on http://localhost:" + PORT);
    }
}