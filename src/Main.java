
import manager.Managers;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        // Добавил задачи, эпики, подзадачи
        Task task1 = new Task("Yandex.Practicum", "Начать писать уже трекер задач");
        Task task2 = new Task("Deutsch", "Учить слова");

        taskManager.createNewTask(task1);
        taskManager.createNewTask(task2);

        Epic epic3 = new Epic("Крупный проект", "Реализация крупного проекта");

        taskManager.createNewEpic(epic3);

        Subtask subtask4 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic3.getId());
        Subtask subtask5 = new Subtask("Подзадача 2", "Завершить разработку", epic3.getId());
        Subtask subtask6 = new Subtask("Подзадача 3", "Посмотреть видео по программированию", epic3.getId());

        taskManager.createNewSubtask(subtask4);
        taskManager.createNewSubtask(subtask5);
        taskManager.createNewSubtask(subtask6);

        Epic epic7 = new Epic("Deutsch", "Сделать домашку");

        taskManager.createNewEpic(epic7);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getEpicById(7);

        System.out.println(taskManager.getHistory());

        taskManager.getTaskById(1);
        taskManager.getSubtaskById(4);

        System.out.println(taskManager.getHistory());

        taskManager.deleteTask(1);

        System.out.println(taskManager.getHistory());

        taskManager.deleteEpic(3);

        System.out.println(taskManager.getHistory());
    }
}
