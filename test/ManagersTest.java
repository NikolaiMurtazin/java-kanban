import history.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit tests for the {@link Managers} utility class.
 */
class ManagersTest {

    /**
     * Should always return a non-null initialized manager.TaskManager instance.
     */
    @Test
    void getDefaultShouldReturnInitializedTaskManager() {
        TaskManager manager = Managers.getDefault();
        Assertions.assertNotNull(manager);
        Assertions.assertNotNull(manager.getAllTasks());
    }

    /**
     * Should always return a non-null initialized history.HistoryManager instance.
     */
    @Test
    void getDefaultHistoryShouldReturnInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        Assertions.assertNotNull(historyManager);
        Assertions.assertNotNull(historyManager.getHistory());
    }

    /**
     * getDefault() should always return the same manager.TaskManager instance (singleton).
     */
    @Test
    void getDefaultShouldReturnSameInstance() {
        TaskManager manager1 = Managers.getDefault();
        TaskManager manager2 = Managers.getDefault();
        Assertions.assertSame(manager1, manager2);
    }

    /**
     * getDefaultHistory() should always return the same history.HistoryManager instance (singleton).
     */
    @Test
    void getDefaultHistoryShouldReturnSameInstance() {
        HistoryManager historyManager1 = Managers.getDefaultHistory();
        HistoryManager historyManager2 = Managers.getDefaultHistory();
        Assertions.assertSame(historyManager1, historyManager2);
    }

    /**
     * manager.TaskManager and history.HistoryManager returned by manager.Managers should be connected (use the same history).
     */
    @Test
    void taskManagerAndHistoryManagerShouldBeConnected() {
        TaskManager manager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        Task task = new Task("Connected Test model.Task", "Desc", TaskStatus.NEW);
        manager.createTask(task);
        manager.getTaskById(task.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertFalse(history.isEmpty());
        Assertions.assertEquals(task.getId(), history.getLast().getId());
    }
}
