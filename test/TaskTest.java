import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Task} class and its equals/hashCode semantics.
 */
class TaskTest {

    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1;
    private Subtask subtask1;

    @BeforeEach
    void setUp() {
        task1 = new Task("model.Task 1", "Description 1");
        task1.setId(1);

        task2 = new Task("model.Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(1);

        task3 = new Task("model.Task 3", "Description 3");
        task3.setId(2);

        epic1 = new Epic("model.Epic 1", "model.Epic Description");
        epic1.setId(1);

        subtask1 = new Subtask("model.Subtask 1", "Sub Description", TaskStatus.NEW, 100);
        subtask1.setId(1);
    }

    /**
     * Two model.Task instances with the same ID should be equal.
     */
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1, task2);
    }

    /**
     * Tasks with different IDs should not be equal.
     */
    @Test
    void tasksWithDifferentIdShouldNotBeEqual() {
        Assertions.assertNotEquals(task1, task3);
    }

    /**
     * Tasks with the same ID should have the same hash code.
     */
    @Test
    void hashCodesOfTasksWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
    }

    /**
     * Tasks with different IDs should have different hash codes.
     */
    @Test
    void hashCodesOfTasksWithDifferentIdShouldNotBeEqual() {
        Assertions.assertNotEquals(task1.hashCode(), task3.hashCode());
    }

    /**
     * model.Task, model.Epic, and model.Subtask with the same ID should be considered equal.
     */
    @Test
    void subclassesOfTaskWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1, epic1);
        Assertions.assertEquals(task1, subtask1);
        Assertions.assertEquals(epic1, subtask1);
    }

    /**
     * toString() should return a correct string representation.
     */
    @Test
    void toStringShouldReturnCorrectRepresentation() {
        String expected = "Task{id=1, name='model.Task 1', description='Description 1', status=NEW}";
        Assertions.assertEquals(expected, task1.toString());
    }
}

