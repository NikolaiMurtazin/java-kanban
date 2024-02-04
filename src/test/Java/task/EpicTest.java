package task;

import interfaces.TaskManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Status;

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

        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.NEW, subtask2.getStatus());
    }

    @Test
    void allSubtasksWithTheDoneStatus() {
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        assertEquals(Status.DONE, subtask1.getStatus());
        assertEquals(Status.DONE, subtask2.getStatus());
    }

    @Test
    void subtasksWithTheNewAndDoneStatus() {
        taskManager.createNewSubtask(subtask1);
        taskManager.createNewSubtask(subtask2);

        subtask2.setStatus(Status.DONE);

        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.DONE, subtask2.getStatus());
    }

    @Test
    void subtasksWithTheInProgressStatus() {
        taskManager.createNewSubtask(subtask1);

        subtask1.setStatus(Status.IN_PROGRESS);

        assertEquals(Status.IN_PROGRESS, subtask1.getStatus());
    }
}