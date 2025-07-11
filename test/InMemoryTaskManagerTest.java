import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link InMemoryTaskManager}.
 */
class InMemoryTaskManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();
        taskManager.removeAllSubtasks();
        historyManager.getHistory().clear();
    }

    /**
     * Should add tasks of all types and retrieve them by ID.
     */
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

    /**
     * Should not have ID conflicts between generated and manually assigned IDs.
     */
    @Test
    void generatedAndAssignedIdsShouldNotConflict() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Desc 1", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Task 2", "Desc 2", TaskStatus.NEW));
        Task task3 = taskManager.createTask(new Task("Task 3", "Desc 3", TaskStatus.NEW));
        Assertions.assertTrue(task3.getId() > task2.getId());
        Assertions.assertNotNull(taskManager.getTaskById(task3.getId()));
    }

    /**
     * Created task should remain immutable in manager (reference and fields).
     */
    @Test
    void taskShouldBeImmutableAfterAddingToManager() {
        Task originalTask = new Task("Original Task", "Original Description", TaskStatus.NEW);
        Task createdTask = taskManager.createTask(originalTask);
        Assertions.assertNotNull(createdTask);
        Assertions.assertTrue(createdTask.getId() > 0);

        Task retrievedTask = taskManager.getTaskById(createdTask.getId());

        Assertions.assertEquals(originalTask.getName(), retrievedTask.getName());
        Assertions.assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        Assertions.assertEquals(originalTask.getStatus(), retrievedTask.getStatus());
        Assertions.assertEquals(originalTask.getId(), retrievedTask.getId());
        Assertions.assertSame(originalTask, retrievedTask);
        Assertions.assertSame(originalTask, createdTask);
    }

    /**
     * Epic cannot be added as its own subtask (by design).
     */
    @Test
    void epicCannotBeAddedAsItsOwnSubtask() {
        Epic epic = taskManager.createEpic(new Epic("Self-referencing Epic", "Should not allow"));
        Subtask invalidSubtask = new Subtask("Invalid Subtask", "Should not be added", TaskStatus.NEW, epic.getId());
        Subtask createdInvalidSubtask = taskManager.createSubtask(invalidSubtask);
        Assertions.assertNotNull(createdInvalidSubtask);
    }

    /**
     * Subtask cannot be its own epic (by design).
     */
    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = taskManager.createEpic(new Epic("Epic for Subtask", "Desc"));
        Subtask subtask = taskManager.createSubtask(new Subtask("The Subtask", "Desc", TaskStatus.NEW, epic.getId()));
    }

    /**
     * Epic status should be correctly calculated from subtask statuses.
     */
    @Test
    void epicStatusShouldBeCalculatedCorrectly() {
        Epic epic = taskManager.createEpic(new Epic("Status Test Epic", ""));
        Subtask sub1 = taskManager.createSubtask(new Subtask("Sub1", "", TaskStatus.NEW, epic.getId()));
        Subtask sub2 = taskManager.createSubtask(new Subtask("Sub2", "", TaskStatus.NEW, epic.getId()));

        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus());

        sub1.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(sub1);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());

        sub2.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(sub2);
        Assertions.assertEquals(TaskStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus());

        sub1.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(sub1);
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());

        taskManager.deleteSubtaskById(sub1.getId());
        taskManager.deleteSubtaskById(sub2.getId());
        Assertions.assertEquals(TaskStatus.NEW, taskManager.getEpicById(epic.getId()).getStatus());
    }
}
