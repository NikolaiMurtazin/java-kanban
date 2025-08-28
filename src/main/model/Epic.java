package model;

import manager.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an epic task, which is a large task composed of one or more {@link Subtask} instances.
 * The status of the epic is not set manually, but automatically calculated based on the statuses
 * of its subtasks and managed by the {@link TaskManager}.
 */
public class Epic extends Task {

    private LocalDateTime endTime;

    /**
     * List of subtask IDs associated with this epic.
     */
    private final List<Integer> subtaskIds;

    /**
     * Creates a new epic with the given name and description.
     * The status is set to {@link TaskStatus#NEW} by default.
     *
     * @param name        the name of the epic
     * @param description the description of the epic
     */
    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    /**
     * Creates an epic with the given ID, name, description, and status.
     * Used when loading epics from storage or file.
     *
     * @param id          the unique ID of the epic
     * @param name        the name of the epic
     * @param description the description of the epic
     * @param taskStatus  the status of the epic (managed by task manager)
     */
    public Epic(int id, String name, String description, TaskStatus taskStatus, Duration duration, LocalDateTime startTime) {
        super(id, name, description, taskStatus, duration, startTime);
        this.subtaskIds = new ArrayList<>();
    }

    /**
     * Returns a copy of the list of subtask IDs associated with this epic.
     *
     * @return list of subtask IDs
     */
    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    /**
     * Adds a subtask ID to this epic if it's not already present.
     *
     * @param subtaskId the subtask ID to add
     */
    public void addSubtaskId(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    /**
     * Removes a subtask ID from this epic.
     *
     * @param subtaskId the subtask ID to remove
     */
    public void removeSubtaskId(Integer subtaskId) {
        subtaskIds.remove(subtaskId);
    }

    /**
     * Removes all subtask IDs from this epic.
     * Note: this does not delete the subtasks themselves.
     */
    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    /**
     * Returns the task type of this instance, which is always {@link TaskType#EPIC}.
     *
     * @return the task type {@link TaskType#EPIC}
     */
    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Returns a string representation of the epic, including its ID, name,
     * description, status, and subtask IDs.
     *
     * @return a string representation of the epic
     */
    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + " min" : "null") +
                ", endTime=" + getEndTime() +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}