package task;

import utils.Status;
import utils.Type;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
        type = Type.SUBTASK;
    }

    public Subtask(String title, String description, Integer epicId, LocalDateTime startTime, long duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
        type = Type.SUBTASK;
    }

    public Subtask(Integer id, Type type, String title, String description, Status status,
                   LocalDateTime startTime, long duration, Integer epicId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
