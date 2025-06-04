public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // 1. Создайте две задачи, а также эпик с двумя подзадачами и эпик с одной подзадачей.
        System.out.println("## Создание задач, эпиков и подзадач ##");
        Task task1 = manager.createTask(new Task("Помыть посуду", "Очень грязная посуда после ужина"));
        Task task2 = manager.createTask(new Task("Позвонить бабушке", "Узнать, как у нее дела"));

        Epic epic1 = manager.createEpic(new Epic("Организовать большой семейный праздник", "Юбилей дедушки"));
        Subtask subtask1_1 = manager.createSubtask(new Subtask("Составить список гостей", "Не забыть всех родственников", TaskStatus.NEW, epic1.getId()));
        Subtask subtask1_2 = manager.createSubtask(new Subtask("Выбрать ресторан", "Уютное место с хорошей кухней", TaskStatus.NEW, epic1.getId()));

        Epic epic2 = manager.createEpic(new Epic("Купить квартиру", "Двухкомнатная в центре"));
        Subtask subtask2_1 = manager.createSubtask(new Subtask("Накопить первоначальный взнос", "Откладывать 30% от ЗП", TaskStatus.NEW, epic2.getId()));

        // 2. Распечатайте списки эпиков, задач и подзадач через System.out.println(..).
        System.out.println("\n## Текущие списки ##");
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println("\nЭпики:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println("\nПодзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        // 3. Измените статусы созданных объектов, распечатайте их.
        // Проверьте, что статус задачи и подзадачи сохранился, а статус эпика рассчитался по статусам подзадач.
        System.out.println("\n## Изменение статусов ##");

        // Изменяем статус обычной задачи
        if (task1 != null) {
            task1.setStatus(TaskStatus.IN_PROGRESS);
            manager.updateTask(task1);
            System.out.println("Обновленная задача 1: " + manager.getTaskById(task1.getId()));
        }

        // Изменяем статусы подзадач и проверяем эпики
        if (subtask1_1 != null) {
            subtask1_1.setStatus(TaskStatus.IN_PROGRESS);
            manager.updateSubtask(subtask1_1);
        }
        System.out.println("Эпик 1 после обновления подзадачи 1.1: " + manager.getEpicById(epic1.getId()));

        if (subtask1_2 != null) {
            subtask1_2.setStatus(TaskStatus.DONE);
            manager.updateSubtask(subtask1_2);
        }
        System.out.println("Эпик 1 после обновления подзадачи 1.2: " + manager.getEpicById(epic1.getId()));

        if (subtask1_1 != null) {
            subtask1_1.setStatus(TaskStatus.DONE);
            manager.updateSubtask(subtask1_1);
        }
        System.out.println("Эпик 1 после обновления всех подзадач на DONE: " + manager.getEpicById(epic1.getId()));


        if (subtask2_1 != null) {
            subtask2_1.setStatus(TaskStatus.IN_PROGRESS);
            manager.updateSubtask(subtask2_1);
        }
        System.out.println("Эпик 2 после обновления подзадачи 2.1: " + manager.getEpicById(epic2.getId()));


        System.out.println("\n## Списки после изменения статусов ##");
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println("\nЭпики:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println("\nПодзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        // 4. И, наконец, попробуйте удалить одну из задач и один из эпиков.
        System.out.println("\n## Удаление объектов ##");
        if (task2 != null) {
            System.out.println("Удаляем задачу: " + task2.getName());
            manager.deleteTaskById(task2.getId());
        }

        if (epic1 != null) {
            System.out.println("Удаляем эпик: " + epic1.getName() + " (id: " + epic1.getId() + ")");
            manager.deleteEpicById(epic1.getId()); // Это также удалит subtask1_1 и subtask1_2
        }

        System.out.println("\n## Финальные списки ##");
        System.out.println("Задачи:");
        manager.getAllTasks().forEach(System.out::println);
        System.out.println("\nЭпики:");
        manager.getAllEpics().forEach(System.out::println);
        System.out.println("\nПодзадачи (должны остаться только от epic2, если epic1 удален):");
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("\nПодзадачи эпика 2 (epicId: " + (epic2 != null ? epic2.getId() : "N/A") + "):");
        if (epic2 != null) {
            manager.getEpicSubtasks(epic2.getId()).forEach(System.out::println);
        }

        // Проверка удаления всех подзадач
        System.out.println("\n## Удаление всех подзадач ##");
        manager.removeAllSubtasks();
        System.out.println("\nПодзадачи после removeAllSubtasks():");
        manager.getAllSubtasks().forEach(System.out::println);
        System.out.println("\nЭпики после removeAllSubtasks() (статусы должны обновиться):");
        manager.getAllEpics().forEach(System.out::println); // Статус epic2 должен стать NEW
    }
}
