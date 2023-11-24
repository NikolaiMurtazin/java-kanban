public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        //Добавил задачи, эпики, подзадачи
        Task task1 = taskManager.createNewTask("Yandex.Practicum", "Начать писать уже трекер задач");
        Task task2 = taskManager.createNewTask("Deutsch", "Учить слова");

        Epic epic1 = taskManager.createNewEpic("Крупный проект", "Реализация крупного проекта");
        Subtask subtask11 = taskManager.createNewSubtask("Подзадача 1", "Выполнить часть проекта", epic1);
        Subtask subtask12 = taskManager.createNewSubtask("Подзадача 2", "Завершить разработку", epic1);

        Epic epic2 = taskManager.createNewEpic("Deutsch", "Сделать домашку");
        Subtask subtask21 = taskManager.createNewSubtask("Задача 1", "Прочитать текст и пересказать", epic2);

        // Вывод задач, эпиков, подзадач
        taskManager.printAllTasks();
        taskManager.printAllEpic();
        taskManager.printAllSubtask();

        //Изменение статусов задач, эпиков, подзадач
        task1.setStatus(TaskStatus.IN_PROGRESS);
        task2.setStatus(TaskStatus.DONE);

        subtask11.setStatus(TaskStatus.DONE);
        subtask12.setStatus(TaskStatus.IN_PROGRESS);

        subtask21.setStatus(TaskStatus.DONE);

        // Проверка статусов эпиков и изменение, если надо
        taskManager.epicStatus(epic1);
        taskManager.epicStatus(epic2);

        // Вывод
        taskManager.printAllTasks();
        taskManager.printAllEpic();
        taskManager.printAllSubtask();

        // Удаление задач, эпиков, подзадач по id
        taskManager.deleteTask(2);
        taskManager.deleteEpic(6);
        taskManager.deleteSubtask(4);

        // Вывод
        taskManager.printAllTasks();
        taskManager.printAllEpic();
        taskManager.printAllSubtask();
    }
}
