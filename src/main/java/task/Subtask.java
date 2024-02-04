package task;

import utils.Status;
import utils.Type;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
        type = Type.SUBTASK;
    }

    public Subtask(String title, String description, Integer epicId, LocalDateTime startTime, int duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
        type = Type.SUBTASK;
    }

    public Subtask(Integer id, Type type, String title, String description, Status status,
                   LocalDateTime startTime, int duration, Integer epicId) {
        super(id, type, title, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Task.Subtask{" +
                "epicId=" + epicId +
                "} " + super.toString();
    }

    @Override
    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", getId(), getType(), getTitle(), getStatus(),
                getDescription(), getStartTime(), getDuration(), epicId);
    }
}
