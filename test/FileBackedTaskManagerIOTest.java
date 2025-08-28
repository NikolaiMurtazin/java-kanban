import exception.ManagerSaveException;
import history.InMemoryHistoryManager;
import manager.FileBackedTaskManager;
import model.*;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerIOTest {

    @Test
    void saveAndLoad_multiple_shouldRestoreState_andDerivedData() throws Exception {
        Path path = File.createTempFile("mgr-multi", ".csv").toPath();
        FileBackedTaskManager mgr = new FileBackedTaskManager(new InMemoryHistoryManager(), path);

        Task t = new Task("T", "d", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025, 8, 26, 10, 0));
        mgr.createTask(t);

        Epic e = mgr.createEpic(new Epic("E", "d"));
        Subtask s1 = new Subtask("S1", "d", TaskStatus.NEW, e.getId(),
                Duration.ofMinutes(15), LocalDateTime.of(2025, 8, 25, 9, 0));
        Subtask s2 = new Subtask("S2", "d", TaskStatus.DONE, e.getId(),
                Duration.ofMinutes(45), LocalDateTime.of(2025, 8, 25, 10, 0));
        mgr.createSubtask(s1);
        mgr.createSubtask(s2);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(path);

        assertEquals(1, loaded.getAllTasks().size());
        assertEquals(1, loaded.getAllEpics().size());
        assertEquals(2, loaded.getAllSubtasks().size());

        Epic le = loaded.getAllEpics().getFirst();
        assertEquals(Duration.ofMinutes(60), le.getDuration());
        assertEquals(LocalDateTime.of(2025, 8, 25, 9, 0), le.getStartTime());
        assertEquals(LocalDateTime.of(2025, 8, 25, 10, 45), le.getEndTime());

        List<Task> p = loaded.getPrioritizedTasks();
        assertEquals(3, p.size());
        assertEquals("S1", p.get(0).getName());
        assertEquals("S2", p.get(1).getName());
        assertEquals("T",  p.get(2).getName());
    }

    @Test
    void load_malformedFile_shouldThrow() throws Exception {
        Path path = File.createTempFile("mgr-bad", ".csv").toPath();
        java.nio.file.Files.writeString(path, "id,type,name,status,description,duration,start,epic\nBROKEN,LINE");

        assertThrows(ManagerSaveException.class, () -> FileBackedTaskManager.loadFromFile(path));
    }

    @Test
    void load_emptyFile_shouldNotThrow() throws Exception {
        Path path = File.createTempFile("mgr-empty", ".csv").toPath();
        // заголовок и ничего больше
        java.nio.file.Files.writeString(path, "id,type,name,status,description,duration,start,epic\n");

        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(path));
    }
}
