package task;

import utils.Status;
import utils.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;
    private static LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        type = Type.EPIC;
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String title, String description, LocalDateTime startTime, int duration) {
        super(title, description, startTime, duration);
        type = Type.EPIC;
        this.subtasksId = new ArrayList<>();
        endTime = super.getEndTime();
    }

    public Epic(Integer id, Type type, String title, String description, Status status, LocalDateTime startTime,
                int duration) {
        super(id, type, title, description, status, startTime, duration);
        this.subtasksId = new ArrayList<>();
        endTime = super.getEndTime();
    }

    public void addSubtask(Subtask subtask) {
        subtasksId.add(subtask.getId());
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasksId;
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public static void setEndTime(LocalDateTime endTime) {
        Epic.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasksId +
                "} " + super.toString();
    }
}
