package task;

import utils.Status;
import utils.Type;

public class Subtask extends Task {
    private final Integer epicId;

    public Subtask(String title, String description, Integer epicId) {
        super(title, description);
        this.epicId = epicId;
        type = Type.SUBTASK;
    }

    public Subtask(Integer id, Type type, String title, String description, Status status, Integer epicId) {
        super(id, type, title, description, status);
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
    public String toStringFromFile() {
        return String.format("%s,%s,%s,%s,%s,%s", getId(), getType(), getTitle(),
                getStatus(), getDescription(), epicId);
    }
}
