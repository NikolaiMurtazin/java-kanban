import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

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

        // Вывод всех задач, эпиков, подзадач
        System.out.println("All tasks:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("All epics:");
        System.out.println(taskManager.getAllEpic());
        System.out.println("All subtasks:");
        System.out.println(taskManager.getAllSubtask());

        // Изменение статусов задач
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task1.setTitle("Yandex");
        task1.setDescription("Дописать трекер задач");
        task2.setStatus(TaskStatus.DONE);

        // Вывод задач без обновлений
        System.out.println("All tasks:");
        System.out.println(taskManager.getAllTasks());

        // Обновление
        taskManager.updateTask(task1);
        taskManager.updateTask(task2);

        // Вывод задач после обновлений
        System.out.println("All tasks:");
        System.out.println(taskManager.getAllTasks());

        // Обновление и вывод эпиков
        epic1.setTitle("Маленький проект");
        epic1.setDescription("Реализация маленького проекта");

        System.out.println("All epics:");
        System.out.println(taskManager.getAllEpic());

        taskManager.updateEpic(epic1);

        System.out.println("All epics:");
        System.out.println(taskManager.getAllEpic());

        // Обновление и вывод подзадач
        subtask11.setStatus(TaskStatus.DONE);
        subtask11.setTitle("1");
        subtask12.setStatus(TaskStatus.IN_PROGRESS);

        subtask21.setStatus(TaskStatus.DONE);

        System.out.println("All subtasks:");
        System.out.println(taskManager.getAllSubtask());

        taskManager.updateSubtask(subtask11);
        taskManager.updateSubtask(subtask12);
        taskManager.updateSubtask(subtask21);

        System.out.println("All subtasks:");
        System.out.println(taskManager.getAllSubtask());

        // Вывод после проверки статусов
        System.out.println("All epics:");
        System.out.println(taskManager.getAllEpic());

        // Вывод задач по айди
        System.out.println("TaskById:");
        System.out.println(taskManager.getTaskById(1));

        // Вывод эпиков по айди
        System.out.println("EpicById:");
        System.out.println(taskManager.getEpicById(3));

        // Вывод подзадач по айди
        System.out.println("SubtaskById:");
        System.out.println(taskManager.getSubtaskById(4));

        // Вывод подзадач определенного эпика
        System.out.println("SubtasksByEpic:");
        System.out.println(taskManager.getSubtasksByEpic(3));

        // Удаление задач, эпиков, подзадач по id
        taskManager.deleteTask(2);
        taskManager.deleteEpic(6);
        taskManager.deleteSubtask(4);

        // Вывод
        System.out.println("All tasks:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("All epics:");
        System.out.println(taskManager.getAllEpic());
        System.out.println("All subtasks:");
        System.out.println(taskManager.getAllSubtask());

        // Удаление всех задач, эпиков, подзадач
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpic();
        taskManager.deleteAllSubtask();

        // Вывод
        System.out.println("All tasks:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("All epics:");
        System.out.println(taskManager.getAllEpic());
        System.out.println("All subtasks:");
        System.out.println(taskManager.getAllSubtask());
    }
}
