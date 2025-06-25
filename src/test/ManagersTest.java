import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ManagersTest {

    /**
     * Убедитесь, что утилитарный класс всегда возвращает проинициализированные
     * и готовые к работе экземпляры менеджеров.
     * Проверяем, что getDefault() возвращает не null объект.
     */
    @Test
    void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        Assertions.assertNotNull(manager, "getDefault() должен возвращать не null TaskManager.");
        // Дополнительная проверка, что менеджер готов к работе (например, списки задач не null)
        Assertions.assertNotNull(manager.getAllTasks(), "Список задач не должен быть null.");
    }

    /**
     * Убедитесь, что утилитарный класс всегда возвращает проинициализированные
     * и готовые к работе экземпляры менеджеров.
     * Проверяем, что getDefaultHistory() возвращает не null объект.
     */
    @Test
    void getDefaultHistoryShouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager, "getDefaultHistory() должен возвращать не null HistoryManager.");
        // Дополнительная проверка, что менеджер истории готов к работе
        Assertions.assertNotNull(historyManager.getHistory(), "История не должна быть null.");
    }

    /**
     * Проверяем, что getDefault() всегда возвращает один и тот же экземпляр TaskManager (синглтон).
     */
    @Test
    void getDefaultShouldReturnSameInstance() {
        TaskManager manager1 = Managers.getDefault();
        TaskManager manager2 = Managers.getDefault();
        Assertions.assertSame(manager1, manager2, "getDefault() должен возвращать один и тот же экземпляр TaskManager.");
    }

    /**
     * Проверяем, что getDefaultHistory() всегда возвращает один и тот же экземпляр HistoryManager (синглтон).
     */
    @Test
    void getDefaultHistoryShouldReturnSameInstance() {
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        HistoryManager historyManager2 = Managers.getDefaultHistory();
        Assertions.assertSame(historyManager1, historyManager2, "getDefaultHistory() должен возвращать один и тот же экземпляр HistoryManager.");
    }

    /**
     * Проверяем, что TaskManager и HistoryManager, возвращаемые Managers,
     * связаны и используют одну и ту же историю.
     */
    @Test
    void taskManagerAndHistoryManagerShouldBeConnected() {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task("Connected Test Task", "Desc", TaskStatus.NEW);
        manager.createTask(task); // Создаем задачу
        manager.getTaskById(task.getId()); // Просматриваем, чтобы она попала в историю

        List<Task> history = historyManager.getHistory();
        Assertions.assertFalse(history.isEmpty(), "История не должна быть пустой, если TaskManager добавляет в нее задачи.");
        Assertions.assertEquals(task.getId(), history.getLast().getId(), "Последняя добавленная задача должна быть в истории.");
    }
}
