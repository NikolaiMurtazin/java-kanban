
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

        Epic epic1 = new Epic("Крупный проект", "Реализация крупного проекта");

        taskManager.createNewEpic(epic1);

        Subtask subtask11 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic1.getId());
        Subtask subtask12 = new Subtask("Подзадача 2", "Завершить разработку", epic1.getId());

        taskManager.createNewSubtask(subtask11);
        taskManager.createNewSubtask(subtask12);

        Epic epic2 = new Epic("Deutsch", "Сделать домашку");

        taskManager.createNewEpic(epic2);

        Subtask subtask21 = new Subtask("Задача 1", "Прочитать текст и пересказать", epic2.getId());

        taskManager.createNewSubtask(subtask21);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(6);
        taskManager.getSubtaskById(7);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);

        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(3);
        taskManager.getSubtaskById(4);
        taskManager.getSubtaskById(5);

        System.out.println(taskManager.getHistory());
    }
}
