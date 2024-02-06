package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
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

        FileBackedTasksManager testLoad = FileBackedTasksManager.loadFromFile(file);
        List<Task> listOfUploadedTasks = testLoad.getAllTask();
        List<Epic> listOfUploadedEpics = testLoad.getAllEpic();
        List<Subtask> listOfUploadedSubtasks = testLoad.getAllSubtask();
        List<Task> listOfUploadedHistory = testLoad.getHistory();
        List<Task> listOfUploadedPrioritizedTasks = testLoad.getPrioritizedTasks();

        assertEquals(listOfTasks, listOfUploadedTasks);
        assertEquals(listOfEpics, listOfUploadedEpics);
        assertEquals(listOfSubtasks, listOfUploadedSubtasks);
        assertEquals(listOfHistory, listOfUploadedHistory);
        assertEquals(listOfPrioritizedTasks, listOfUploadedPrioritizedTasks);
    }
}