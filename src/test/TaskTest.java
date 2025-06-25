import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {

    private Task task1;
    private Task task2;
    private Task task3;
    private Epic epic1; // Для проверки сравнения с наследниками
    private Subtask subtask1; // Для проверки сравнения с наследниками

    @BeforeEach
    void setUp() {
        task1 = new Task("Task 1", "Description 1");
        task1.setId(1); // Устанавливаем ID вручную для тестов equals/hashCode

        task2 = new Task("Task 2", "Description 2", TaskStatus.IN_PROGRESS);
        task2.setId(1); // Тот же ID, что и у task1

        task3 = new Task("Task 3", "Description 3");
        task3.setId(2); // Другой ID

        epic1 = new Epic("Epic 1", "Epic Description");
        epic1.setId(1); // Тот же ID, что и у task1

        subtask1 = new Subtask("Subtask 1", "Sub Description", TaskStatus.NEW, 100); // epicId неважен для этого теста
        subtask1.setId(1); // Тот же ID, что и у task1
    }

    /**
     * Проверяем, что экземпляры класса Task равны друг другу, если равен их id.
     */
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны.");
    }

    /**
     * Проверяем, что экземпляры класса Task с разными id не равны.
     */
    @Test
    void tasksWithDifferentIdShouldNotBeEqual() {
        Assertions.assertNotEquals(task1, task3, "Задачи с разными ID не должны быть равны.");
    }

    /**
     * Проверяем, что хэш-коды задач с одинаковым id совпадают.
     */
    @Test
    void hashCodesOfTasksWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1.hashCode(), task2.hashCode(),
                "Хэш-коды задач с одинаковым ID должны быть равны.");
    }

    /**
     * Проверяем, что хэш-коды задач с разными id отличаются.
     */
    @Test
    void hashCodesOfTasksWithDifferentIdShouldNotBeEqual() {
        Assertions.assertNotEquals(task1.hashCode(), task3.hashCode(),
                "Хэш-коды задач с разными ID должны отличаться.");
    }

    /**
     * Проверяем, что наследники класса Task равны друг другу (и базовому Task), если равен их id.
     * Это важно, так как equals/hashCode переопределены только в Task.
     */
    @Test
    void subclassesOfTaskWithSameIdShouldBeEqual() {
        Assertions.assertEquals(task1, epic1, "Task и Epic с одинаковым ID должны быть равны.");
        Assertions.assertEquals(task1, subtask1, "Task и Subtask с одинаковым ID должны быть равны.");
        Assertions.assertEquals(epic1, subtask1, "Epic и Subtask с одинаковым ID должны быть равны.");
    }

    /**
     * Проверяем, что метод toString() возвращает корректное строковое представление.
     */
    @Test
    void toStringShouldReturnCorrectRepresentation() {
        String expected = "Task{id=1, name='Task 1', description='Description 1', status=NEW}";
        Assertions.assertEquals(expected, task1.toString(), "toString() должен возвращать корректное представление.");
    }
}
