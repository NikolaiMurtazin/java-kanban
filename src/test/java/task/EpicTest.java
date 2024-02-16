package task;

import interfaces.TaskManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.TaskStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;
    Epic epic = new Epic("Крупный проект", "Реализация крупного проекта");
    Subtask subtask1 = new Subtask("Подзадача 1", "Выполнить часть проекта", epic.getId(),
            LocalDateTime.of(2024, 2, 1, 15, 0, 0), 30);
    Subtask subtask2 = new Subtask("Подзадача 2", "Завершить разработку", epic.getId(),
            LocalDateTime.of(2024, 2, 1, 16, 0, 0), 30);

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        taskManager.createNewEpic(epic);
    }

    @Test
    void anEmptyListOfSubtasks() {
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    void allSubtasksWithTheNewStatus() {
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        assertEquals(TaskStatus.NEW, subtask1.getStatus());
        assertEquals(TaskStatus.NEW, subtask2.getStatus());
    }

    @Test
    void allSubtasksWithTheDoneStatus() {
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, subtask1.getStatus());
        assertEquals(TaskStatus.DONE, subtask2.getStatus());
    }

    @Test
    void subtasksWithTheNewAndDoneStatus() {
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        subtask2.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.NEW, subtask1.getStatus());
        assertEquals(TaskStatus.DONE, subtask2.getStatus());
    }

    @Test
    void subtasksWithTheInProgressStatus() {
        taskManager.createNewSubtask(subtask1);

        subtask1.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, subtask1.getStatus());
    }
}