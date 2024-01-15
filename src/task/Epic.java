package task;

import utils.Status;
import utils.Type;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description);
        type = Type.EPIC;
        this.subtasksId = new ArrayList<>();
    }

    public Epic(Integer id, Type type, String title, String description, Status status) {
        super(id, type, title, description, status);
        this.subtasksId = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasksId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasksId +
                "} " + super.toString();
    }
}
