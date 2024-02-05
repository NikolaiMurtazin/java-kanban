package task;

import utils.Status;
import utils.Type;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksId;
    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        type = Type.EPIC;
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String title, String description, LocalDateTime startTime, long duration) {
        super(title, description, startTime, duration);
        type = Type.EPIC;
        this.subtasksId = new ArrayList<>();
        endTime = super.getEndTime();
    }

    public Epic(Integer id, Type type, String title, String description, Status status, LocalDateTime startTime,
                long duration) {
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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasksId +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasksId, epic.subtasksId) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksId, endTime);
    }
}
