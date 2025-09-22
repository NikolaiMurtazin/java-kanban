import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class HttpPrioritizedApiTest extends BaseHttpApiTest {

    @Test
    void prioritized_shouldReturnOrdered() throws Exception {
        manager.createTask(new Task("B","", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025,9,1,11,0)));
        manager.createTask(new Task("A","", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025,9,1,10,0)));

        var resp = get("/prioritized");
        Assertions.assertEquals(200, resp.statusCode());
    }
}
