import history.InMemoryHistoryManager;
import manager.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @Override
    protected FileBackedTaskManager makeManager() {
        try {
            Path path = File.createTempFile("mgr", ".csv").toPath();
            return new FileBackedTaskManager(new InMemoryHistoryManager(), path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
