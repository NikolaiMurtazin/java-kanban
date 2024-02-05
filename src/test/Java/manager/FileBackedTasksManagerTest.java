package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    File file = new File(System.getProperty("user.dir") + "/src/main/resources/tasks.csv");
    Task task = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
            LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
    Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");

    @BeforeEach
    void setUp() {
        file = new File(System.getProperty("user.dir") + "/src/main/resources/tasks.csv");
        manager = new FileBackedTasksManager(file);
    }

    @Test
    void save() {
        manager.createNewTask(task);
        manager.createNewEpic(epic);
        List<Task> listOfTasks = manager.getAllTask();
        List<Epic> listOfEpics = manager.getAllEpic();

        assertEquals(List.of(task), listOfTasks);
        assertEquals(List.of(epic), listOfEpics);
    }

    @Test
    void saveEmptyTask() {
        assertTrue(manager.getAllTask().isEmpty());
        assertTrue(manager.getAllEpic().isEmpty());
        assertTrue(manager.getAllSubtask().isEmpty());
    }

    @Test
    void saveEmptyHistory() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void load() {
        manager.createNewTask(task);
        manager.createNewEpic(epic);
        manager.getTaskById(1);
        manager.getEpicById(2);
        List<Task> listOfTasks = manager.getAllTask();
        List<Epic> listOfEpics = manager.getAllEpic();
        List<Task> listOfHistory = manager.getHistory();

        FileBackedTasksManager testLoad = FileBackedTasksManager.loadFromFile(file);
        List<Task> listOfUploadedTasks = testLoad.getAllTask();
        List<Epic> listOfUploadedEpics = testLoad.getAllEpic();
        List<Task> listOfUploadedHistory = testLoad.getHistory();

        assertEquals(listOfTasks, listOfUploadedTasks);
        assertEquals(listOfEpics, listOfUploadedEpics);
        assertEquals(listOfHistory, listOfUploadedHistory);
    }
}