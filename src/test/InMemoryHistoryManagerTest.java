import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

/**
 * Unit tests for {@link InMemoryHistoryManager}.
 * Tests the new version of history storage (custom linked list with O(1) removals and no duplicates).
 */
class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    /**
     * Tasks added to history should retain their data.
     */
    @Test
    void addedTasksShouldRetainTheirDataInHistory() {
        Task task1 = new Task("Task 1", "Desc 1", TaskStatus.NEW);
        task1.setId(1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        Assertions.assertNotNull(history);
        Assertions.assertEquals(1, history.size());

        Task retrievedTask = history.getFirst();
        Assertions.assertEquals(task1.getId(), retrievedTask.getId());
        Assertions.assertEquals(task1.getName(), retrievedTask.getName());
        Assertions.assertEquals(task1.getDescription(), retrievedTask.getDescription());
        Assertions.assertEquals(task1.getStatus(), retrievedTask.getStatus());
        Assertions.assertSame(task1, retrievedTask);
    }

    /**
     * History should not allow duplicates; most recent view is retained.
     */
    @Test
    void historyShouldNotHaveDuplicatesAndRetainOnlyMostRecent() {
        Task t1 = new Task("Task 1", "Desc", TaskStatus.NEW); t1.setId(1);
        Task t2 = new Task("Task 2", "Desc", TaskStatus.NEW); t2.setId(2);
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t1); // t1 should move to the end

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(2, history.size());
        Assertions.assertEquals(t2.getId(), history.get(0).getId());
        Assertions.assertEquals(t1.getId(), history.get(1).getId());
    }

    /**
     * Removing a task from history should work and not affect others.
     */
    @Test
    void removingTaskShouldRemoveItFromHistory() {
        Task t1 = new Task("Task 1", "Desc", TaskStatus.NEW); t1.setId(1);
        Task t2 = new Task("Task 2", "Desc", TaskStatus.NEW); t2.setId(2);
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.remove(t1.getId());

        List<Task> history = historyManager.getHistory();
        Assertions.assertEquals(1, history.size());
        Assertions.assertEquals(t2.getId(), history.get(0).getId());
    }

    /**
     * Adding null should not affect history.
     */
    @Test
    void addingNullTaskShouldNotAffectHistory() {
        historyManager.add(null);
        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }

    /**
     * getHistory() should return a copy, not a direct reference to the internal list.
     */
    @Test
    void getHistoryShouldReturnCopy() {
        Task t = new Task("Test Task", "Desc", TaskStatus.NEW); t.setId(1);
        historyManager.add(t);

        List<Task> h1 = historyManager.getHistory();
        List<Task> h2 = historyManager.getHistory();

        Assertions.assertNotSame(h1, h2);
        Assertions.assertEquals(h1, h2);

        h1.clear();
        Assertions.assertFalse(historyManager.getHistory().isEmpty());
    }
}