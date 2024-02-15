package manager;

import com.google.gson.*;
import interfaces.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.*;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private TaskManager manager;
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private static final Gson gson = new Gson();
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

    Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач", LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
    Task task2 = new Task("Deutsch", "Учить слова");
    Epic epic1 = new Epic("Крупный проект", "Реализация крупного проекта");
    Epic epic2 = new Epic("Deutsch", "Сделать домашку");


    @BeforeEach
    public void startServers() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault("http://localhost:8078");
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    @Test
    void shouldPOSTTask() throws IOException, InterruptedException {
        manager.createNewTask(task1);
        URI uri = URI.create("http://localhost:8080/tasks/task/");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(task1.toString(), manager.getTaskById(task1.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETTask() throws IOException, InterruptedException {
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        List<Task> listOfTasks = manager.getAllTask();

        assertEquals(200, response.statusCode());
        assertEquals(2, listOfTasks.size());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        manager.createNewTask(task1);
        int id = task1.getId();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=" + id);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        int jsonId = jsonObject.get("id").getAsInt();
        String jsonDescription = jsonObject.get("description").getAsString();

        assertEquals(200, response.statusCode());
        assertEquals(id, jsonId);
        assertTrue(jsonElement.isJsonObject(), "Incorrect JSON");
        assertEquals(task1.getDescription(), jsonDescription);
    }

    @Test
    void shouldDELETETask() throws IOException, InterruptedException {
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все tasks были удалены", response.body());
    }

    @Test
    void shouldPOSTEpic() throws IOException, InterruptedException {
        manager.createNewEpic(epic1);
        URI uri = URI.create("http://localhost:8080/tasks/epic/");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(epic1.toString(), manager.getEpicById(epic1.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETEpic() throws IOException, InterruptedException {
        manager.createNewEpic(epic1);
        manager.createNewEpic(epic2);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        List<Epic> listOfEpics = manager.getAllEpic();

        assertEquals(200, response.statusCode());
        assertEquals(2, listOfEpics.size());
    }

    @Test
    void shouldDELETEEpic() throws IOException, InterruptedException {
        manager.createNewEpic(epic1);
        manager.createNewEpic(epic2);
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все epics были удалены", response.body());
    }

    @Test
    void shouldPOSTSubtask() throws IOException, InterruptedException {
        manager.createNewEpic(epic1);
        Subtask subtask = new Subtask("Подзадача 2", "Завершить разработку", epic1.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        manager.createNewSubtask(subtask);
        URI uri = URI.create("http://localhost:8080/tasks/subtask/");
        String json = gson.toJson(subtask);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(subtask.toString(), manager.getSubtaskById(subtask.getId()).toString());
        assertEquals(201, response.statusCode());
    }

    @Test
    void shouldGETSubtask() throws IOException, InterruptedException {
        manager.createNewEpic(epic1);
        Subtask subtask = new Subtask("Подзадача 2", "Завершить разработку", epic1.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        manager.createNewSubtask(subtask);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        List<Subtask> listOfSubtasks = manager.getAllSubtask();

        assertEquals(200, response.statusCode());
        assertEquals(1, listOfSubtasks.size());
    }

    @Test
    void shouldDELETESubtask() throws IOException, InterruptedException {
        manager.createNewEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic1.getId());
        Subtask subtask2 = new Subtask("Подзадача 2", "Завершить разработку", epic1.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все subtasks были удалены", response.body());
    }

    @Test
    void shouldGETHistory() throws IOException, InterruptedException {
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        manager.getTaskById(task1.getId());
        manager.getTaskById(task2.getId());
        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray(), "Неверный JSON");
        assertEquals(2, jsonArray.size());
    }

    @Test
    void shouldGETPrioritized() throws IOException, InterruptedException {
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, handler);
        JsonElement jsonElement = JsonParser.parseString(response.body());

        assertEquals(200, response.statusCode());
        assertTrue(jsonElement.isJsonArray(), "Неверный JSON");
        assertEquals(2, jsonElement.getAsJsonArray().size());
    }

    @AfterEach
    public void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }
}
