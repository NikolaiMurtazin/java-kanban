package task;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;

    public Epic(String title, String description) {
        super(title, description);
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
        return "Task.Epic{" +
                "subtasks=" + subtasksId +
                "} " + super.toString();
    }
}
