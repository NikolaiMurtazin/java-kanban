package manager;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        manager = (InMemoryTaskManager) Managers.getDefault();
    }

}