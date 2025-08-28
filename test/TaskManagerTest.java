
import manager.TaskManager;
import model.*;

import org.junit.jupiter.api.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base test suite for any TaskManager implementation.
 * Covers CRUD, epic status rules, prioritization, and overlap checks.
 */
public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    /** Each implementation must provide a fresh manager instance. */
    protected abstract T makeManager();

    @BeforeEach
    void setUp() {
        manager = makeManager();
    }

    // --- CRUD sanity checks ---

    @Test
    void shouldCreateAndFetchTasksEpicsSubtasks() {
        Task t = manager.createTask(new Task("T", "d", TaskStatus.NEW));
        Epic e = manager.createEpic(new Epic("E", "d"));
        Subtask s = manager.createSubtask(new Subtask("S", "d", TaskStatus.NEW, e.getId()));

        assertNotNull(t);
        assertNotNull(e);
        assertNotNull(s);

        assertEquals(t, manager.getTaskById(t.getId()));
        assertEquals(e, manager.getEpicById(e.getId()));
        assertEquals(s, manager.getSubtaskById(s.getId()));

        assertEquals(1, manager.getAllTasks().size());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals(1, manager.getAllSubtasks().size());
    }

    // --- Epic status boundary conditions ---

    @Test
    void epicStatus_allNEW_isNEW() {
        Epic e = manager.createEpic(new Epic("E", ""));
        manager.createSubtask(new Subtask("s1", "", TaskStatus.NEW, e.getId()));
        manager.createSubtask(new Subtask("s2", "", TaskStatus.NEW, e.getId()));
        assertEquals(TaskStatus.NEW, manager.getEpicById(e.getId()).getStatus());
    }

    @Test
    void epicStatus_allDONE_isDONE() {
        Epic e = manager.createEpic(new Epic("E", ""));
        manager.createSubtask(new Subtask("s1", "", TaskStatus.DONE, e.getId()));
        manager.createSubtask(new Subtask("s2", "", TaskStatus.DONE, e.getId()));
        assertEquals(TaskStatus.DONE, manager.getEpicById(e.getId()).getStatus());
    }

    @Test
    void epicStatus_mixedNEWandDONE_isIN_PROGRESS() {
        Epic e = manager.createEpic(new Epic("E", ""));
        manager.createSubtask(new Subtask("s1", "", TaskStatus.NEW, e.getId()));
        manager.createSubtask(new Subtask("s2", "", TaskStatus.DONE, e.getId()));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(e.getId()).getStatus());
    }

    @Test
    void epicStatus_anyIN_PROGRESS_isIN_PROGRESS() {
        Epic e = manager.createEpic(new Epic("E", ""));
        manager.createSubtask(new Subtask("s1", "", TaskStatus.IN_PROGRESS, e.getId()));
        manager.createSubtask(new Subtask("s2", "", TaskStatus.NEW, e.getId()));
        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicById(e.getId()).getStatus());
    }

    // --- Prioritization ---

    @Test
    void prioritizedTasks_sortedByStartTime_excludesEpicsAndNullStart() {
        Epic e = manager.createEpic(new Epic("E", ""));

        Subtask s1 = new Subtask("s1", "", TaskStatus.NEW, e.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 25, 10, 0));
        Subtask s2 = new Subtask("s2", "", TaskStatus.NEW, e.getId(),
                Duration.ofMinutes(15), LocalDateTime.of(2025, 8, 25, 12, 0));
        Subtask s3 = new Subtask("s3", "", TaskStatus.NEW, e.getId()); // no startTime

        manager.createSubtask(s2);
        manager.createSubtask(s3);
        manager.createSubtask(s1);

        Task t = new Task("t", "d", TaskStatus.NEW,
                Duration.ofMinutes(20), LocalDateTime.of(2025, 8, 26, 9, 0));
        manager.createTask(t);

        List<Task> p = manager.getPrioritizedTasks();
        assertEquals(3, p.size());                  // s1, s2, t
        assertEquals("s1", p.get(0).getName());
        assertEquals("s2", p.get(1).getName());
        assertEquals("t",  p.get(2).getName());
    }

    // --- Overlap checks ---

    @Test
    void creatingOverlappingTasks_throws() {
        Task a = new Task("A", "", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 26, 10, 0));
        manager.createTask(a);

        Task b = new Task("B", "", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 26, 10, 30));
        assertThrows(IllegalArgumentException.class, () -> manager.createTask(b));
    }

    @Test
    void updatingToOverlap_throws() {
        Task a = manager.createTask(new Task("A", "", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 26, 10, 0)));
        Task b = manager.createTask(new Task("B", "", TaskStatus.NEW,
                Duration.ofMinutes(45), LocalDateTime.of(2025, 8, 26, 12, 0)));

        // move b to overlap with a
        b.setStartTime(LocalDateTime.of(2025, 8, 26, 10, 15));
        assertThrows(IllegalArgumentException.class, () -> manager.updateTask(b));
    }

    // --- Subtasks must have an existing epic ---

    @Test
    void creatingSubtaskWithMissingEpic_shouldReturnNullOrThrow() {
        Subtask s = new Subtask("S", "", TaskStatus.NEW, 9999);
        // твоя реализация возвращает null или выбрасывает исключение — проверь и зафиксируй поведение:
        // assertNull(manager.createSubtask(s));
        assertThrows(IllegalArgumentException.class, () -> manager.createSubtask(s),
                "Expected IllegalArgumentException when epic is missing");
    }
}
