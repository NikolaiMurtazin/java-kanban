import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import history.InMemoryHistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Unit tests for {@link InMemoryTaskManager}.
 */
class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        // fresh instances per test (no singletons here!)
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    @Test
    void shouldAddTasksAndFindThemById() {
        Task task = taskManager.createTask(new Task("Test Task", "Desc Task", TaskStatus.NEW));
        Epic epic = taskManager.createEpic(new Epic("Test Epic", "Desc Epic"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Test Subtask", "Desc Subtask", TaskStatus.NEW, epic.getId()));

        Assertions.assertNotNull(task);
        Assertions.assertNotNull(epic);
        Assertions.assertNotNull(subtask);

        Assertions.assertEquals(task, taskManager.getTaskById(task.getId()));
        Assertions.assertEquals(epic, taskManager.getEpicById(epic.getId()));
        Assertions.assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));

        Assertions.assertEquals(1, taskManager.getAllTasks().size());
        Assertions.assertEquals(1, taskManager.getAllEpics().size());
        Assertions.assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    void generatedAndAssignedIdsShouldNotConflict() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Desc 1", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "Desc 2", TaskStatus.NEW));
        Task task3 = taskManager.createTask(new Task("Task 3", "Desc 3", TaskStatus.NEW));
        Assertions.assertTrue(task3.getId() > task2.getId());
        Assertions.assertNotNull(taskManager.getTaskById(task3.getId()));
    }

    @Test
    void taskShouldBeImmutableAfterAddingToManager_referenceIsSame() {
        Task originalTask = new Task("Original Task", "Original Description", TaskStatus.NEW);
        Task createdTask = taskManager.createTask(originalTask);
        Assertions.assertNotNull(createdTask);
        Assertions.assertTrue(createdTask.getId() > 0);

        Task retrievedTask = taskManager.getTaskById(createdTask.getId());

        // Same content & same instance (manager stores the same reference)
        Assertions.assertEquals(originalTask.getName(), retrievedTask.getName());
        Assertions.assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        Assertions.assertEquals(originalTask.getStatus(), retrievedTask.getStatus());
        Assertions.assertEquals(originalTask.getId(), retrievedTask.getId());
        Assertions.assertSame(originalTask, retrievedTask);
        Assertions.assertSame(originalTask, createdTask);
    }

    // --- Epic status calculation boundary tests (as per sprint 8) ---

    @Test
    void epicStatus_allSubtasksNew_shouldBeNEW() {
        Epic epic = taskManager.createEpic(new Epic("E", ""));
        taskManager.createSubtask(new Subtask("s1", "", TaskStatus.NEW, epic.getId()));
        taskManager.createSubtask(new Subtask("s2", "", TaskStatus.NEW, epic.getId()));
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_allSubtasksDone_shouldBeDONE() {
        Epic epic = taskManager.createEpic(new Epic("E", ""));
        taskManager.createSubtask(new Subtask("s1", "", TaskStatus.DONE, epic.getId()));
        taskManager.createSubtask(new Subtask("s2", "", TaskStatus.DONE, epic.getId()));
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_mixedNewAndDone_shouldBeIN_PROGRESS() {
        Epic epic = taskManager.createEpic(new Epic("E", ""));
        taskManager.createSubtask(new Subtask("s1", "", TaskStatus.NEW, epic.getId()));
        taskManager.createSubtask(new Subtask("s2", "", TaskStatus.DONE, epic.getId()));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_anyInProgress_shouldBeIN_PROGRESS() {
        Epic epic = taskManager.createEpic(new Epic("E", ""));
        taskManager.createSubtask(new Subtask("s1", "", TaskStatus.IN_PROGRESS, epic.getId()));
        taskManager.createSubtask(new Subtask("s2", "", TaskStatus.NEW, epic.getId()));
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());
    }

    // --- Prioritization tests ---

    @Test
    void prioritizedTasks_shouldBeSortedByStartTime_andExcludeEpicsAndNullStart() {
        Epic epic = taskManager.createEpic(new Epic("Epic", ""));

        // sub with time
        Subtask s1 = new Subtask("s1", "", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 25, 10, 0));
        // sub with time later
        Subtask s2 = new Subtask("s2", "", TaskStatus.NEW, epic.getId(),
                Duration.ofMinutes(15), LocalDateTime.of(2025, 8, 25, 12, 0));
        // sub without startTime -> excluded from priority set
        Subtask s3 = new Subtask("s3", "", TaskStatus.NEW, epic.getId());

        taskManager.createSubtask(s2);
        taskManager.createSubtask(s3);
        taskManager.createSubtask(s1);

        // regular task with time
        Task t = new Task("t", "desc", TaskStatus.NEW,
                Duration.ofMinutes(20), LocalDateTime.of(2025, 8, 26, 9, 0));
        taskManager.createTask(t);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        // should contain s1, s2, t (3 items), no epic, no s3 (null startTime)
        Assertions.assertEquals(3, prioritized.size());
        Assertions.assertEquals("s1", prioritized.get(0).getName());
        Assertions.assertEquals("s2", prioritized.get(1).getName());
        Assertions.assertEquals("t", prioritized.get(2).getName());
    }

    // --- Overlap tests ---

    @Test
    void creatingOverlappingTasks_shouldThrow() {
        Task t1 = new Task("A", "", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 26, 10, 0));
        taskManager.createTask(t1);

        Task t2 = new Task("B", "", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 26, 10, 30));
        Assertions.assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(t2));
    }

    @Test
    void updatingTaskToOverlap_shouldThrow() {
        Task t1 = new Task("A", "", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 26, 10, 0));
        Task t2 = new Task("B", "", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025, 8, 26, 12, 0));
        t1 = taskManager.createTask(t1);
        t2 = taskManager.createTask(t2);

        // move t2 to overlap with t1
        t2.setStartTime(LocalDateTime.of(2025, 8, 26, 10, 30));
        Task finalT = t2;
        Assertions.assertThrows(IllegalArgumentException.class, () -> taskManager.updateTask(finalT));
    }
}
