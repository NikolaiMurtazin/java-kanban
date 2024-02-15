package manager;

import interfaces.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private TaskManager taskManager;

    Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
            LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
    Task task2 = new Task("Deutsch", "Учить слова",
            LocalDateTime.of(2024, 2, 1, 13, 0, 0), 60);
    Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void shouldAddTasksToHistoryList() {
        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        assertEquals(List.of(task1, task2), taskManager.getHistory());
    }

    @Test
    void shouldReturnAnEmptyListIfThereIsNoHistory() {
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldRemoveATaskFromHistory() {
        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.deleteTask(1);

        assertEquals(List.of(task2), taskManager.getHistory());
    }

    @Test
    void shouldDoNothingIfPassedIdIsIncorrect() {
        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.deleteTask(3);

        assertEquals(List.of(task1, task2), taskManager.getHistory());
    }

    @Test
    void shouldNotAddExistingTaskToHistoryList() {
        taskManager.createNewTask(task1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);

        assertEquals(List.of(task1), taskManager.getHistory());
    }

    @Test
    void shouldRemoveParticularTaskFromHistoryList() {
        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);
        taskManager.createNewTask(epic);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.deleteTask(task2.getId());

        assertEquals(List.of(task1, epic), taskManager.getHistory());
    }
}