import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        // Каждый тест должен начинаться с чистого менеджера истории
        historyManager = new InMemoryHistoryManager();
    }

    /**
     * Убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
     * (Тут имеется в виду, что объект Task, который был добавлен, не теряет свои данные)
     */
    @Test
    void addedTasksShouldRetainTheirDataInHistory() {
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW);
        task1.setId(1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history, "История не должна быть null.");
        Assertions.assertEquals(1, history.size(), "История должна содержать 1 элемент.");

        Task retrievedTask = history.getFirst();
        Assertions.assertEquals(task1.getId(), retrievedTask.getId(), "ID задачи в истории должен совпадать.");
        Assertions.assertEquals(task1.getName(), retrievedTask.getName(), "Имя задачи в истории должно совпадать.");
        Assertions.assertEquals(task1.getDescription(), retrievedTask.getDescription(), "Описание задачи в истории должно совпадать.");
        Assertions.assertEquals(task1.getStatus(), retrievedTask.getStatus(), "Статус задачи в истории должен совпадать.");
        Assertions.assertSame(task1, retrievedTask, "Объект задачи в истории должен быть тем же самым (по ссылке).");
    }

    /**
     * Проверяем, что история содержит не более 10 элементов (MAX_HISTORY_SIZE).
     */
    @Test
    void historyShouldKeepLimitedSize() {
        // Добавляем 10 задач
        for (int i = 0; i < 10; i++) {
            Task task = new Task("Task " + i, "Desc " + i, TaskStatus.NEW);
            task.setId(i);
            historyManager.add(task);
        }
        Assertions.assertEquals(10, historyManager.getHistory().size(), "История должна содержать 10 элементов.");

        // Добавляем 11-ю задачу
        Task task11 = new Task("Task 11", "Desc 11", TaskStatus.NEW);
        task11.setId(11);
        historyManager.add(task11);

        Assertions.assertEquals(10, historyManager.getHistory().size(), "История должна по-прежнему содержать 10 элементов после добавления 11-й.");
        // Проверяем, что самая старая задача (Task 0) была удалена, а Task 11 добавлена
        Assertions.assertNotEquals(0, historyManager.getHistory().get(0).getId(), "Самая старая задача должна быть удалена.");
        Assertions.assertEquals(11, historyManager.getHistory().get(9).getId(), "Новая задача должна быть в конце истории.");
    }

    /**
     * Проверяем, что история корректно обрабатывает добавление null задач (не должна их добавлять).
     */
    @Test
    void addingNullTaskShouldNotAffectHistory() {
        historyManager.add(null);
        Assertions.assertTrue(historyManager.getHistory().isEmpty(), "История должна быть пустой после попытки добавить null.");
    }

    /**
     * Проверяем, что getHistory() возвращает копию, а не прямую ссылку на внутренний список.
     */
    @Test
    void getHistoryShouldReturnImmutableCopy() {
        Task task = new Task("Test Task", "Desc", TaskStatus.NEW);
        task.setId(1);
        historyManager.add(task);

        List<Task> history1 = historyManager.getHistory();
        List<Task> history2 = historyManager.getHistory();

        Assertions.assertNotSame(history1, history2, "getHistory() должен возвращать разные объекты списка.");
        Assertions.assertEquals(history1, history2, "Содержимое возвращаемых списков должно быть одинаковым.");

        // Попытка изменить возвращенный список не должна влиять на внутренний список менеджера
        history1.clear();
        Assertions.assertFalse(historyManager.getHistory().isEmpty(), "Очистка возвращенной копии не должна влиять на внутреннюю историю.");
    }
}