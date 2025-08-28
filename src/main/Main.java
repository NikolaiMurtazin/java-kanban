import history.InMemoryHistoryManager;
import manager.FileBackedTaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Path tempPath = File.createTempFile("manager", ".csv").toPath();
            FileBackedTaskManager manager = getFileBackedTaskManager(tempPath);

            System.out.println("Исходный менеджер:");
            System.out.println("Задачи: " + manager.getAllTasks());
            System.out.println("Эпики: " + manager.getAllEpics());
            System.out.println("Подзадачи: " + manager.getAllSubtasks());

            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempPath);

            System.out.println("\nЗагруженный менеджер:");
            System.out.println("Задачи: " + loadedManager.getAllTasks());
            System.out.println("Эпики: " + loadedManager.getAllEpics());
            System.out.println("Подзадачи: " + loadedManager.getAllSubtasks());

            System.out.println("\nСписок по приоритету:");
            List<Task> prioritized = loadedManager.getPrioritizedTasks();
            System.out.println("Prioritized tasks count: " + prioritized.size());
            for (Task task : prioritized) {
                System.out.println(task.getName() + " | " + task.getStartTime() + " – " + task.getEndTime());
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static FileBackedTaskManager getFileBackedTaskManager(Path tempPath) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new InMemoryHistoryManager(), tempPath);

        Task task1 = new Task("Почитать книгу", "Прочитать Clean Code", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 26, 10, 0));
        Task task2 = new Task("Написать код", "Реализовать FileBackedTaskManager", TaskStatus.IN_PROGRESS,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 26, 11, 0));

        Epic epic1 = new Epic("Переезд", "Подготовка к переезду");
        Epic epic2 = new Epic("Организация свадьбы", "Планирование");

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask sub1 = new Subtask(
                "Собрать вещи", "Сложить всё по коробкам", TaskStatus.NEW, epic1.getId(),
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 25, 10, 0));

        Subtask sub2 = new Subtask(
                "Снять грузовик", "Забронировать машину", TaskStatus.DONE, epic1.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 25, 12, 0));

        Subtask sub3 = new Subtask(
                "Выбрать ресторан", "Позвонить в 3 заведения", TaskStatus.NEW, epic2.getId(),
                Duration.ofMinutes(45), LocalDateTime.of(2025, 8, 25, 13, 0));

        manager.createSubtask(sub1);
        manager.createSubtask(sub2);
        manager.createSubtask(sub3);

        return manager;
    }

}

