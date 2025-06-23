public class Main {
    public static void main(String[] args) {
        // Получаем менеджер задач через утилитарный класс Managers
        // Теперь Main не знает, какая конкретно реализация TaskManager используется
        TaskManager manager = Managers.getDefault();

        // Тестирование создания задач, эпиков, подзадач
        System.out.println("--- Создание задач, эпиков, подзадач ---");
        Task task1 = manager.createTask(new Task("Покупки", "Купить продукты на неделю"));
        Task task2 = manager.createTask(new Task("Убраться", "Пропылесосить и вытереть пыль"));

        Epic epic1 = manager.createEpic(new Epic("Организовать праздник", "Подготовка к дню рождения"));
        Subtask subtask1_1 = manager.createSubtask(new Subtask("Купить торт", "Выбрать и заказать торт", TaskStatus.NEW,
                epic1.getId()));
        Subtask subtask1_2 = manager.createSubtask(new Subtask("Пригласить гостей", "Отправить приглашения",
                TaskStatus.NEW, epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Переезд", "Перевезти вещи в новую квартиру"));
        Subtask subtask2_1 = manager.createSubtask(new Subtask("Собрать коробки", "Упаковать вещи",TaskStatus.NEW,
                epic2.getId()));

        System.out.println("Все задачи: " + manager.getAllTasks());
        System.out.println("Все эпики: " + manager.getAllEpics());
        System.out.println("Все подзадачи: " + manager.getAllSubtasks());
        System.out.println("Подзадачи эпика 1: " + manager.getEpicSubtasks(epic1.getId()));
        System.out.println("Подзадачи эпика 2: " + manager.getEpicSubtasks(epic2.getId()));

        // Тестирование получения задач для истории
        System.out.println("\n--- Просмотр задач для истории ---");
        manager.getTaskById(task1.getId());
        manager.getEpicById(epic1.getId());
        manager.getSubtaskById(subtask1_1.getId());
        manager.getTaskById(task2.getId());
        manager.getSubtaskById(subtask2_1.getId());
        manager.getTaskById(task1.getId()); // Дублирование для проверки лимита

        HistoryManager historyManager = Managers.getDefaultHistory();

        System.out.println("История просмотров: " + historyManager.getHistory());
        // Ожидаемый вывод: [task1, epic1, subtask1_1, task2, subtask2_1, task1] (если лимит 10)

        // Тестирование изменения статусов и проверки статуса эпика
        System.out.println("\n--- Изменение статусов и проверка эпиков ---");
        // Меняем статус подзадачи 1.1 на IN_PROGRESS -> эпик 1 должен стать IN_PROGRESS
        subtask1_1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1_1);
        System.out.println("Обновленная подзадача 1.1: " + manager.getSubtaskById(subtask1_1.getId()));
        System.out.println("Обновленный эпик 1 (после изменения подзадачи 1.1): " + manager.getEpicById(epic1.getId())); // Ожидаем IN_PROGRESS

        // Меняем статус подзадачи 1.1 на DONE, подзадачи 1.2 на IN_PROGRESS -> эпик 1 должен остаться IN_PROGRESS
        subtask1_1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1_1);
        subtask1_2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(subtask1_2);
        System.out.println("Обновленная подзадача 1.1: " + manager.getSubtaskById(subtask1_1.getId()));
        System.out.println("Обновленная подзадача 1.2: " + manager.getSubtaskById(subtask1_2.getId()));
        System.out.println("Обновленный эпик 1 (после изменения подзадач): " + manager.getEpicById(epic1.getId())); // Ожидаем IN_PROGRESS

        // Меняем все подзадачи эпика 1 на DONE -> эпик 1 должен стать DONE
        subtask1_2.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1_2);
        System.out.println("Обновленная подзадача 1.2: " + manager.getSubtaskById(subtask1_2.getId()));
        System.out.println("Обновленный эпик 1 (после всех подзадач DONE): " + manager.getEpicById(epic1.getId())); // Ожидаем DONE

        // Тестирование удаления
        System.out.println("\n--- Удаление задач и эпиков ---");
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic2.getId()); // Удаляем эпик 2, должны удалиться и его подзадачи

        System.out.println("Все задачи после удаления: " + manager.getAllTasks());
        System.out.println("Все эпики после удаления: " + manager.getAllEpics());
        System.out.println("Все подзадачи после удаления: " + manager.getAllSubtasks()); // Подзадача epic2_1 должна исчезнуть
        System.out.println("История просмотров после удаления: " + historyManager.getHistory()); // Проверь, как это повлияло на историю
    }
}
