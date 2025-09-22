import com.google.gson.Gson;
import history.InMemoryHistoryManager;
import http.HttpTaskServer;
import manager.InMemoryTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

abstract class BaseHttpApiTest {
    protected InMemoryTaskManager manager;
    protected InMemoryHistoryManager history;
    protected HttpTaskServer server;
    protected Gson gson;
    protected HttpClient client;

    @BeforeEach
    void setUp() {
        // свой InMemory менеджер + history (не через Managers.getDefault(), чтобы изолировать тесты)
        history = new InMemoryHistoryManager();
        manager = new InMemoryTaskManager(history);
        server = new HttpTaskServer(manager, history);
        gson = server.getGson();
        client = HttpClient.newHttpClient();

        // чистим менеджер на всякий
        manager.removeAllSubtasks();
        manager.removeAllEpics();
        manager.removeAllTasks();

        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    protected HttpResponse<String> post(String path, String body) throws Exception {
        URI url = URI.create("http://localhost:8080" + path);
        HttpRequest req = HttpRequest.newBuilder(url)
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> get(String path) throws Exception {
        URI url = URI.create("http://localhost:8080" + path);
        HttpRequest req = HttpRequest.newBuilder(url).GET().build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }

    protected HttpResponse<String> delete(String path) throws Exception {
        URI url = URI.create("http://localhost:8080" + path);
        HttpRequest req = HttpRequest.newBuilder(url).DELETE().build();
        return client.send(req, HttpResponse.BodyHandlers.ofString());
    }
}
