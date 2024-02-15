package manager;

import server.KVServer;
import task.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private static KVServer server;

    @BeforeEach
    void setManager() {
        manager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
    }

    @BeforeAll
    static void startServer() throws IOException {
        server = new KVServer();
        server.start();
    }

    @Test
    void shouldLoadFromServer() {
        Task task = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        manager.createNewTask(task);
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
        Subtask subtask2 = new Subtask("Подзадача 2", "Завершить разработку", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        manager.getTaskById(1);
        manager.getEpicById(2);
        manager.getSubtaskById(3);
        manager.getSubtaskById(4);

        List<Task> listOfTasks = manager.getAllTask();
        List<Epic> listOfEpics = manager.getAllEpic();
        List<Subtask> listOfSubtasks = manager.getAllSubtask();
        List<Task> listOfHistory = manager.getHistory();
        List<Task> listOfPrioritizedTasks = manager.getPrioritizedTasks();

        HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        httpTaskManager.loadFromServer();
        List<Task> listOfUploadedTasks = httpTaskManager.getAllTask();
        List<Epic> listOfUploadedEpics = httpTaskManager.getAllEpic();
        List<Subtask> listOfUploadedSubtasks = httpTaskManager.getAllSubtask();
        List<Task> listOfUploadedHistory = httpTaskManager.getHistory();
        List<Task> listOfUploadedPrioritizedTasks = httpTaskManager.getPrioritizedTasks();

        assertEquals(listOfTasks, listOfUploadedTasks);
        assertEquals(listOfEpics, listOfUploadedEpics);
        assertEquals(listOfSubtasks, listOfUploadedSubtasks);
        assertEquals(listOfHistory, listOfUploadedHistory);
        assertEquals(listOfPrioritizedTasks, listOfUploadedPrioritizedTasks);

    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
}