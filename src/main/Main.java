import history.InMemoryHistoryManager;
import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.File;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            Path tempPath = File.createTempFile("manager", ".csv").toPath();
            FileBackedTaskManager manager = getFileBackedTaskManager(tempPath);

            System.out.println("Исходный менеджер:");
            System.out.println("Задачи: " + manager.getAllTasks());
            System.out.println("Эпики: " + manager.getAllEpics());
            System.out.println("Подзадачи: " + manager.getAllSubtasks());

            // 2. Загружаем из файла новый менеджер
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);

            System.out.println("\nЗагруженный менеджер:");
            System.out.println("Задачи: " + loadedManager.getAllTasks());
            System.out.println("Эпики: " + loadedManager.getAllEpics());
            System.out.println("Подзадачи: " + loadedManager.getAllSubtasks());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FileBackedTaskManager getFileBackedTaskManager(Path tempPath) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), tempPath);

        // 1. Создаём задачи
        Task task1 = new Task("Почитать книгу", "Прочитать Clean Code", TaskStatus.NEW);
        Task task2 = new Task("Написать код", "Реализовать FileBackedTaskManager", TaskStatus.IN_PROGRESS);

        Epic epic1 = new Epic("Переезд", "Подготовка к переезду");
        Epic epic2 = new Epic("Организация свадьбы", "Планирование");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask sub1 = new Subtask("Собрать вещи", "Сложить всё по коробкам", TaskStatus.NEW, epic1.getId());
        Subtask sub2 = new Subtask("Снять грузовик", "Забронировать машину", TaskStatus.DONE, epic1.getId());
        Subtask sub3 = new Subtask("Выбрать ресторан", "Позвонить в 3 заведения", TaskStatus.NEW, epic2.getId());

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);
        return manager;
    }
}
