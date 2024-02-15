package manager;

import interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import utils.Status;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    @Test
    void createNewTask() {
        Task task = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        manager.createNewTask(task);
        List<Task> listOfTasks = manager.getAllTask();

        assertNotNull(task.getStatus());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(List.of(task), listOfTasks);
    }

    @Test
    void createNewEpic() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        List<Epic> listOfEpics = manager.getAllEpic();

        assertNotNull(epic.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        assertEquals(List.of(epic), listOfEpics);
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void createNewSubtask() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
        manager.createNewSubtask(subtask);

        List<Subtask> listOfSubtasks = manager.getAllSubtask();

        assertNotNull(subtask.getStatus());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(List.of(subtask), listOfSubtasks);
        assertEquals(List.of(subtask.getId()), epic.getSubtasksId());
    }

    @Test
    void updateTaskAndGetTaskById() {
        Task task = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        manager.createNewTask(task);
        task.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task);

        assertEquals(Status.IN_PROGRESS, manager.getTaskById(1).getStatus());
    }

    @Test
    void updateEpicAndGetEpicById() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        epic.setStatus(Status.IN_PROGRESS);
        manager.updateEpic(epic);

        assertEquals(Status.IN_PROGRESS, manager.getEpicById(1).getStatus());
    }

    @Test
    void updateSubtaskAndGetSubtaskById() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
        manager.createNewSubtask(subtask);
        subtask.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask);

        assertEquals(Status.IN_PROGRESS, manager.getSubtaskById(2).getStatus());
    }

    @Test
    void getHistory() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        manager.getEpicById(epic.getId());
        List<Task> listOfHistory = manager.getHistory();

        assertEquals(1, listOfHistory.size());
    }

    @Test
    void getPrioritizedTasks() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        Subtask subtask = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
        manager.createNewSubtask(subtask);
        Task task = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        manager.createNewTask(task);
        List<Task> list = List.of(task, subtask);
        List<Task> listOfPrioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(list, listOfPrioritizedTasks);
        assertFalse(listOfPrioritizedTasks.isEmpty());
    }

    @Test
    void deleteTask() {
        Task task = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        manager.createNewTask(task);
        manager.deleteTask(1);
        List<Task> listOfTasks = manager.getAllTask();
        List<Task> listOfPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(listOfTasks.isEmpty());
        assertTrue(listOfPrioritizedTasks.isEmpty());
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        manager.deleteEpic(1);
        List<Epic> listOfEpics = manager.getAllEpic();

        assertTrue(listOfEpics.isEmpty());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
        Subtask subtask2 = new Subtask("Подзадача 2", "Завершить разработку", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        manager.deleteSubtask(2);
        List<Subtask> listOfSubtasks = manager.getAllSubtask();
        List<Task> listOfPrioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(1, listOfSubtasks.size());
        assertEquals(1, listOfPrioritizedTasks.size());
    }

    @Test
    void deleteAllTask() {
        Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        Task task2 = new Task("Deutsch", "Учить слова");
        manager.createNewTask(task1);
        manager.createNewTask(task2);
        manager.deleteAllTasks();
        List<Task> listOfTasks = manager.getAllTask();
        List<Task> listOfPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(listOfTasks.isEmpty());
        assertTrue(listOfPrioritizedTasks.isEmpty());
    }

    @Test
    void deleteAllEpic() {
        Epic epic1 = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic1);
        Epic epic2 = new Epic("Deutsch", "Сделать домашку");
        manager.createNewEpic(epic2);
        manager.deleteAllEpic();
        List<Epic> listOfEpics = manager.getAllEpic();

        assertTrue(listOfEpics.isEmpty());
    }

    @Test
    void deleteAllSubtask() {
        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
        manager.createNewEpic(epic);
        Subtask subtask1 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
        Subtask subtask2 = new Subtask("Подзадача 2", "Завершить разработку", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        manager.createNewSubtask(subtask1);
        manager.createNewSubtask(subtask2);
        manager.deleteAllSubtask();
        List<Subtask> listOfSubtasks = manager.getAllSubtask();
        List<Task> listOfPrioritizedTasks = manager.getPrioritizedTasks();

        assertTrue(listOfSubtasks.isEmpty());
        assertTrue(listOfPrioritizedTasks.isEmpty());
    }
}