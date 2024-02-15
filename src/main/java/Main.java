import manager.HttpTaskManager;
import manager.Managers;
import server.HttpTaskServer;
import server.KVServer;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();
        HttpTaskManager httpTaskManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        new HttpTaskServer(httpTaskManager).start();

        Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач",
                LocalDateTime.of(2024, 2, 1, 12, 0, 0), 60);
        Task task2 = new Task("Deutsch", "Учить слова");

        httpTaskManager.createNewTask(task1);
        httpTaskManager.createNewTask(task2);

        Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");

        httpTaskManager.createNewEpic(epic);

        Subtask subtask4 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId());
        Subtask subtask5 = new Subtask("Подзадача 2", "Завершить разработку", epic.getId(),
                LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);
        Subtask subtask6 = new Subtask("Подзадача 3", "Посмотреть видео по программированию", epic.getId(), LocalDateTime.of(2024, 2, 1, 17, 0, 0), 30);

        httpTaskManager.createNewSubtask(subtask4);
        httpTaskManager.createNewSubtask(subtask5);
        httpTaskManager.createNewSubtask(subtask6);

        Epic epic7 = new Epic("Deutsch", "Сделать домашку");

        httpTaskManager.createNewEpic(epic7);

        httpTaskManager.getTaskById(1);
        httpTaskManager.getEpicById(3);
        httpTaskManager.getSubtaskById(4);

        httpTaskManager = (HttpTaskManager) Managers.getDefault("http://localhost:8078");
        httpTaskManager.loadFromServer();

        System.out.println(httpTaskManager.getAllTask());
        System.out.println(httpTaskManager.getAllEpic());
        System.out.println(httpTaskManager.getAllSubtask());
        System.out.println(httpTaskManager.getHistory());
        System.out.println(httpTaskManager.getPrioritizedTasks());
    }
}
