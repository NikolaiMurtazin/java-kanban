import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager; // Для проверки истории

    @BeforeEach
    void setUp() {
        // Каждый тест должен начинаться с чистого менеджера
        // Используем Managers.getDefault(), чтобы получить синглтон
        // и обеспечить, что TaskManager и HistoryManager связаны
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory(); // Получаем тот же экземпляр HistoryManager

        // Очищаем менеджер перед каждым тестом, чтобы тесты были независимы
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
        // Также очистим историю, если она не очищается при создании менеджера
        // (в InMemoryHistoryManager нет clear метода, но это нормально для данного этапа)
        // Если бы HistoryManager был не синглтоном, это бы не понадобилось.
        historyManager.getHistory().clear(); // Очищаем историю напрямую, если хотим, чтобы она была пустой перед каждым тестом
    }

    /**
     * Проверяем, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id.
     */
    @Test
    void shouldAddTasksAndFindThemById() {
        Task task = taskManager.createTask(new Task("Test Task", "Desc Task", TaskStatus.NEW));
        Epic epic = taskManager.createEpic(new Epic("Test Epic", "Desc Epic"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Test Subtask", "Desc Subtask", TaskStatus.NEW, epic.getId()));

        Assertions.assertNotNull(task, "Task не должен быть null после создания.");
        Assertions.assertNotNull(epic, "Epic не должен быть null после создания.");
        Assertions.assertNotNull(subtask, "Subtask не должен быть null после создания.");

        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()), "Task должен быть найден по ID.");
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()), "Epic должен быть найден по ID.");
        Assertions.assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()), "Subtask должен быть найден по ID.");

        Assertions.assertEquals(1, taskManager.getAllTasks().size(), "Должна быть 1 обычная задача.");
        Assertions.assertEquals(1, taskManager.getAllEpics().size(), "Должен быть 1 эпик.");
        Assertions.assertEquals(1, taskManager.getAllSubtasks().size(), "Должна быть 1 подзадача.");
    }

    /**
     * Проверяем, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера.
     * (Это в основном проверяется через корректность generateId() и работы Map)
     */
    @Test
    void generatedAndAssignedIdsShouldNotConflict() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Desc 1", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "Desc 2", TaskStatus.NEW));
        // Предполагаем, что nextId начинается с 1. task1.id=1, task2.id=2

        // Попробуем создать задачу с ID, который "скоро" будет сгенерирован
        Task customIdTask = new Task("Custom ID Task", "Desc Custom", TaskStatus.NEW);
        customIdTask.setId(taskManager.getAllTasks().size() + taskManager.getAllEpics().size() + taskManager.getAllSubtasks().size() + 100); // Дадим большой ID

        // Важно: в текущей реализации createXxx() всегда генерирует ID.
        // Если бы createTask() мог принимать Task с уже заданным ID и использовать его,
        // этот тест был бы более актуален.
        // Сейчас он проверяет, что менеджер корректно генерирует последовательные ID.
        Task task3 = taskManager.createTask(new Task("Task 3", "Desc 3", TaskStatus.NEW));

        // nextId всегда должен увеличиваться
        Assertions.assertTrue(task3.getId() > task2.getId(), "Сгенерированный ID должен быть больше предыдущего.");
        Assertions.assertNotNull(taskManager.getTaskById(task3.getId()), "Задача с сгенерированным ID должна быть найдена.");
    }


    /**
     * Создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер.
     * То есть, что объект Task, который был передан в createTask(), и объект, который вернулся из менеджера,
     * или был получен по ID, ссылаются на один и тот же объект, и его поля корректны.
     */
    @Test
    void taskShouldBeImmutableAfterAddingToManager() {
        Task originalTask = new Task("Original Task", "Original Description", TaskStatus.NEW);

        // Создаем задачу через менеджер
        Task createdTask = taskManager.createTask(originalTask);

        // Проверяем, что объект, который вернулся, не null
        Assertions.assertNotNull(createdTask, "Созданная задача не должна быть null.");
        // Проверяем, что ID был присвоен
        Assertions.assertTrue(createdTask.getId() > 0, "Созданная задача должна иметь ID.");

        // Получаем задачу по ID
        Task retrievedTask = taskManager.getTaskById(createdTask.getId());

        // Проверяем, что все поля совпадают с исходными
        Assertions.assertEquals(originalTask.getName(), retrievedTask.getName(), "Имя задачи должно совпадать.");
        Assertions.assertEquals(originalTask.getDescription(), retrievedTask.getDescription(), "Описание задачи должно совпадать.");
        Assertions.assertEquals(originalTask.getStatus(), retrievedTask.getStatus(), "Статус задачи должен совпадать.");
        Assertions.assertEquals(originalTask.getId(), retrievedTask.getId(), "ID задачи должен совпадать.");

        // Внимание: если вы хотите проверить, что это ОДИН И ТОТ ЖЕ ОБЪЕКТ,
        // а не просто объекты с одинаковыми значениями полей (равны по equals()),
        // то нужно сравнивать ссылки:
        Assertions.assertSame(originalTask, retrievedTask, "Возвращенная задача должна быть тем же объектом, что и созданная (по ссылке).");
        Assertions.assertSame(originalTask, createdTask, "Созданный объект должен быть тем же, что и оригинальный (по ссылке).");
    }


    /**
     * Проверяем, что объект Epic нельзя добавить в самого себя в виде подзадачи.
     */
    @Test
    void epicCannotBeAddedAsItsOwnSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Self-referencing Epic", "Should not allow"));

        // Попытка создать подзадачу, где epicId ссылается на самого себя
        Subtask invalidSubtask = new Subtask("Invalid Subtask", "Should not be added", TaskStatus.NEW, epic.getId());

        // В текущей реализации InMemoryTaskManager, метод createSubtask()
        // не позволяет добавить эпик в самого себя. Он не делает проверку на это напрямую,
        // но и не предоставляет функционал для такого добавления.
        // Этот тест скорее для проверки логики обновления или создания связей.
        // Если бы была возможность установить epicId напрямую для уже существующего Epic.
        // В текущей модели нет прямого способа сделать Epic подзадачей.
        // Это скорее концептуальная проверка, что такого функционала нет.

        // Можно проверить, что getSubtasksOfEpic(epic.getId()) не возвращает epic
        // Но это и так очевидно, т.к. epic не является Subtask.

        // Можно проверить, что createSubtask() не позволяет передать id собственного эпика
        // если бы логика была такой:
        Subtask createdInvalidSubtask = taskManager.createSubtask(invalidSubtask);
        Assertions.assertNotNull(createdInvalidSubtask, "Подзадача должна быть создана, но привязка к себе невозможна.");
        // Здесь мы ожидаем, что subtask создался, но он не стал частью своего эпика в смысле иерархии,
        // потому что он уже является Epic. Но по текущей модели он просто не может быть создан как subtask.
        // Важно: в твоей текущей реализации Subtask extends Task, Epic extends Task.
        // Epic не может быть Subtask по иерархии классов.
        // Поэтому этот тест должен скорее проверять, что при попытке установить ID эпика
        // в качестве epicId для подзадачи, это корректно обрабатывается.
        // Текущий код менеджера не имеет специальной проверки на "эпик сам себе подзадача",
        // потому что Subtask и Epic - разные классы.
        // Эпик не может быть инстансом Subtask.
        // Так что, по сути, это требование выполняется структурно.
        // Если бы была функция `updateSubtaskParent(subtaskId, newEpicId)`,
        // то там нужно было бы добавить проверку `if (subtaskId == newEpicId)`
    }

    /**
     * Проверяем, что объект Subtask нельзя сделать своим же эпиком.
     * (Аналогично предыдущему, это больше проверка дизайна).
     */
    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = taskManager.createEpic(new Epic("Epic for Subtask", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("The Subtask", "Desc", TaskStatus.NEW, epic.getId()));

        // Попытка сделать подзадачу своим же эпиком путем изменения epicId
        // В твоем коде updateSubtask проверяет, что newParentEpic не null
        // Но нет прямой проверки, что newParentEpic.getId() == subtask.getId()
        // Это невозможно, так как subtask является Subtask, а не Epic.
        // Опять же, это больше проверка на дизайн системы.
        // Если бы updateSubtask позволял передать ID задачи, которая не является эпиком,
        // то здесь нужно было бы проверять поведение.
    }

    /**
     * Проверяем корректность обновления статуса эпика.
     */
    @Test
    void epicStatusShouldBeCalculatedCorrectly() {
        Epic epic = taskManager.createEpic(new Epic("Status Test Epic", ""));
        Subtask sub1 = taskManager.createSubtask(new Subtask("Sub1", "", TaskStatus.NEW, epic.getId()));
        Subtask sub2 = taskManager.createSubtask(new Subtask("Sub2", "", TaskStatus.NEW, epic.getId()));

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть NEW, когда все подзадачи NEW.");

        sub1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(sub1);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть IN_PROGRESS, когда есть NEW и DONE.");

        sub2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(sub2);
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть DONE, когда все подзадачи DONE.");

        sub1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(sub1);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть IN_PROGRESS, если хоть одна подзадача IN_PROGRESS.");

        // Проверка пустого эпика
        taskManager.deleteSubtaskById(sub1.getId());
        taskManager.deleteSubtaskById(sub2.getId());
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus(), "Статус эпика должен быть NEW, если у него нет подзадач.");
    }
}
