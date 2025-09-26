package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * HTTP handler for {@link Epic} resources.
 * <p>
 * Supports the following endpoints under {@code /epics}:
 * <ul>
 *     <li><b>GET /epics</b> — returns all epics as JSON</li>
 *     <li><b>GET /epics?id={id}</b> — returns epic by ID</li>
 *     <li><b>POST /epics</b> — creates a new epic (if ID not set) or updates existing epic</li>
 *     <li><b>DELETE /epics</b> — deletes all epics and their subtasks</li>
 *     <li><b>DELETE /epics?id={id}</b> — deletes epic by ID</li>
 * </ul>
 * <p>
 * Response codes:
 * <ul>
 *     <li>200 — request successful</li>
 *     <li>201 — new epic created</li>
 *     <li>400 — invalid request (e.g. malformed JSON)</li>
 *     <li>404 — epic not found</li>
 *     <li>405 — unsupported HTTP method</li>
 *     <li>500 — internal server error</li>
 * </ul>
 */
public class EpicHandler extends BaseHttpHandler {

    /**
     * Creates a new handler for epic operations.
     *
     * @param manager task manager providing epic storage
     * @param gson    gson instance for JSON serialization
     */
    public EpicHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    /**
     * Main dispatcher for HTTP methods.
     *
     * @param h HTTP exchange object
     * @throws IOException if reading/writing the exchange fails
     */
    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            switch (h.getRequestMethod()) {
                case "GET" -> handleGet(h);
                case "POST" -> handlePost(h);
                case "DELETE" -> handleDelete(h);
                default -> sendText(h, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            sendText(h, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Handles GET requests.
     * <ul>
     *     <li>If no {@code id} parameter: returns all epics</li>
     *     <li>If {@code id} provided: returns single epic</li>
     * </ul>
     *
     * @param h HTTP exchange
     * @throws IOException if response writing fails
     */
    private void handleGet(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            List<Epic> epics = manager.getAllEpics();
            sendJson(h, 200, gson.toJson(epics));
            return;
        }
        int id = idOpt.get();
        Epic e = manager.getEpicById(id);
        if (e == null) {
            sendNotFound(h, "Epic id=" + id + " not found");
            return;
        }
        sendJson(h, 200, gson.toJson(e));
    }

    /**
     * Handles POST requests.
     * <ul>
     *     <li>If epic ID not set or not found: creates new epic</li>
     *     <li>If epic ID exists: updates epic name/description only</li>
     * </ul>
     *
     * @param h HTTP exchange
     * @throws IOException if response writing fails
     */
    private void handlePost(HttpExchange h) throws IOException {
        String body = readBody(h);
        Epic incoming = gson.fromJson(body, Epic.class);
        if (incoming == null) {
            sendText(h, 400, "Bad Request: empty/invalid JSON");
            return;
        }

        if (incoming.getId() > 0 && manager.getEpicById(incoming.getId()) != null) {
            manager.updateEpic(incoming);
            sendJson(h, 200, gson.toJson(incoming));
        } else {
            Epic created = manager.createEpic(incoming);
            sendJson(h, 201, gson.toJson(created));
        }
    }

    /**
     * Handles DELETE requests.
     * <ul>
     *     <li>If no {@code id} parameter: deletes all epics and their subtasks</li>
     *     <li>If {@code id} provided: deletes epic by ID</li>
     * </ul>
     *
     * @param h HTTP exchange
     * @throws IOException if response writing fails
     */
    private void handleDelete(HttpExchange h) throws IOException {
        Optional<Integer> idOpt = getQueryId(h);
        if (idOpt.isEmpty()) {
            manager.removeAllEpics();
            sendText(h, 200, "All epics (and subtasks) removed");
            return;
        }
        int id = idOpt.get();
        if (manager.getEpicById(id) == null) {
            sendNotFound(h, "Epic id=" + id + " not found");
            return;
        }
        manager.deleteEpicById(id);
        sendText(h, 200, "Epic id=" + id + " removed");
    }
}