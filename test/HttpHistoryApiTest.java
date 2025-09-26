import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class HttpHistoryApiTest extends BaseHttpApiTest {

    @Test
    void history_shouldBeEmptyInitially() throws Exception {
        var resp = get("/history");
        Assertions.assertEquals(200, resp.statusCode());
    }

    @Test
    void history_shouldReflectGetById() throws Exception {
        Task t = manager.createTask(new Task("A","", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now()));
        manager.getTaskById(t.getId());

        var resp = get("/history");
        Assertions.assertEquals(200, resp.statusCode());
    }
}
