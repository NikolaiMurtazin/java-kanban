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
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1);

        task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(1);

        task3 = new Task("Task 3", "Description 3");
        task3.setId(2);

        epic1 = new Epic("Epic 1", "Epic Description");
        epic1.setId(1);

        subtask1 = new Subtask("Subtask 1", "Sub Description", TaskStatus.NEW, 100);
        subtask1.setId(1);
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1, task2);
    }

    @Test
    void tasksWithDifferentIdShouldNotBeEqual() {
        Assertions.assertNotEquals(task1, task3);
    }

    @Test
    void hashCodesOfTasksWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1.hashCode(), task2.hashCode());
    }

    @Test
    void hashCodesOfTasksWithDifferentIdShouldNotBeEqual() {
        Assertions.assertNotEquals(task1.hashCode(), task3.hashCode());
    }

    @Test
    void subclassesOfTaskWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1, epic1);
        Assertions.assertEquals(task1, subtask1);
        Assertions.assertEquals(epic1, subtask1);
    }

    @Test
    void toStringShouldReturnCorrectRepresentation() {
        String expected = "Task{id=1, name='Task 1', description='Description 1', status=NEW, startTime=null, duration=null, endTime=null}";
        Assertions.assertEquals(expected, task1.toString());
    }
}

