import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class HttpTasksApiTest extends BaseHttpApiTest {

    @Test
    void createTask_shouldReturn201_andPersist() throws Exception {
        Task t = new Task("Read", "Clean Code", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025,9,1,10,0));
        String json = gson.toJson(t);

        var resp = post("/tasks", json);
        Assertions.assertEquals(201, resp.statusCode());

        var all = manager.getAllTasks();
        Assertions.assertEquals(1, all.size());
        Assertions.assertEquals("Read", all.get(0).getName());
    }

    @Test
    void updateTask_shouldReturn200_andModifyExisting() throws Exception {
        Task created = manager.createTask(new Task("X","Y", TaskStatus.NEW,
                Duration.ofMinutes(10), LocalDateTime.of(2025,9,1,9,0)));
        created.setName("Updated");
        String json = gson.toJson(created);

        var resp = post("/tasks", json);
        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertEquals("Updated", manager.getTaskById(created.getId()).getName());
    }

    @Test
    void getById_unknown_shouldReturn404() throws Exception {
        var resp = get("/tasks?id=999");
        Assertions.assertEquals(404, resp.statusCode());
    }

    @Test
    void overlap_onCreate_shouldReturn406() throws Exception {
        manager.createTask(new Task("A","", TaskStatus.NEW,
                Duration.ofMinutes(60), LocalDateTime.of(2025,9,1,10,0)));

        Task overlapping = new Task("B","", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2025,9,1,10,30)); // пересечение
        var resp = post("/tasks", gson.toJson(overlapping));
        Assertions.assertEquals(406, resp.statusCode());
    }

    @Test
    void deleteAll_shouldReturn200_andEmptyList() throws Exception {
        manager.createTask(new Task("A","", TaskStatus.NEW, Duration.ofMinutes(1), LocalDateTime.now()));
        var resp = delete("/tasks");
        Assertions.assertEquals(200, resp.statusCode());
        Assertions.assertTrue(manager.getAllTasks().isEmpty());
    }
}
