import model.Epic;
import model.Subtask;
import model.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class HttpSubtasksApiTest extends BaseHttpApiTest {

    @Test
    void createSubtask_requiresExistingEpic_else404() throws Exception {
        Subtask s = new Subtask("Sub","", TaskStatus.NEW, 999);
        var resp = post("/subtasks", gson.toJson(s));
        Assertions.assertEquals(404, resp.statusCode());
    }

    @Test
    void createSubtask_ok_201_andEpicTimeRecalc() throws Exception {
        Epic e = manager.createEpic(new Epic("E",""));
        Subtask s = new Subtask("S","", TaskStatus.NEW, e.getId());
        s.setStartTime(LocalDateTime.of(2025,9,1,10,0));
        s.setDuration(Duration.ofMinutes(30));

        var resp = post("/subtasks", gson.toJson(s));
        Assertions.assertEquals(201, resp.statusCode());
        Assertions.assertEquals(1, manager.getAllSubtasks().size());

        Epic updated = manager.getEpicById(e.getId());
        Assertions.assertEquals(LocalDateTime.of(2025,9,1,10,0), updated.getStartTime());
        Assertions.assertEquals(Duration.ofMinutes(30), updated.getDuration());
    }

    @Test
    void getSubtasksByEpic_shouldReturnList() throws Exception {
        Epic e = manager.createEpic(new Epic("E",""));
        Subtask s = new Subtask("S","", TaskStatus.NEW, e.getId());
        s.setStartTime(LocalDateTime.of(2025,9,1,10,0));
        s.setDuration(Duration.ofMinutes(30));
        manager.createSubtask(s);

        var resp = get("/subtasks/epic?id=" + e.getId());
        Assertions.assertEquals(200, resp.statusCode());
    }
}
