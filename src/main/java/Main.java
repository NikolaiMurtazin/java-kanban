

import manager.Managers;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        Task task2 = new Task("Deutsch", "Учить слова");

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");

        taskManager.createNewEpic(epic);

        Subtask subtask4 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId());
        Subtask subtask5 = new Subtask("Подзадача 2", "Завершить разработку", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        Subtask subtask6 = new Subtask("Подзадача 3", "Посмотреть видео по программированию", epic.getId(), LocalDateTime.of(2024, 2, 1, 17, 0, 0), 30);

        taskManager.createNewSubtask(subtask4);
        taskManager.createNewSubtask(subtask5);
        taskManager.createNewSubtask(subtask6);

        Epic epic7 = new Epic("Deutsch", "Сделать домашку");

        taskManager.createNewEpic(epic7);

        System.out.println(taskManager.getPrioritizedTasks());
    }
}
