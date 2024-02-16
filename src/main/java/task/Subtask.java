package task;

import utils.TaskStatus;
import utils.TypeOfTasksForDirectoryTask;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private final Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
        type = TypeOfTasksForDirectoryTask.SUBTASK;
    }

    public Subtask(String title, String description, Integer epicId, LocalDateTime startTime, long duration) {
        super(title, description, startTime, duration);
        this.epicId = epicId;
        type = TypeOfTasksForDirectoryTask.SUBTASK;
    }

    public Subtask(Integer id, TypeOfTasksForDirectoryTask type, String title, String description, TaskStatus status,
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
