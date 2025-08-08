package model;

import java.util.Objects;

/**
 * Base class representing a generic task.
 * Used for regular tasks and as a parent class for epics and subtasks.
 * Contains common fields: ID, name, description, and status.
 */
public class Task {
    /**
     * Unique identifier of the task.
     */
    private int id;

    /**
     * Short name or title of the task.
     */
    private String name;

    /**
     * Detailed description of what the task is about.
     */
    private String description;

    /**
     * Current status of the task (e.g., NEW, IN_PROGRESS, DONE).
     */
    private TaskStatus status;

    /**
     * Constructs a new task with default status {@link TaskStatus#NEW}.
     *
     * @param name        the short name of the task
     * @param description the detailed description of the task
     */
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    /**
     * Constructs a new task with a given status.
     *
     * @param name        the name of the task
     * @param description the description of the task
     * @param status      the status of the task
     */
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /**
     * Constructs a fully defined task with ID, name, description, and status.
     *
     * @param id          the unique task ID
     * @param name        the task name
     * @param description the task description
     * @param status      the task status
     */
    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    /**
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the task name.
     *
     * @param name the new task name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the task description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the task description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the unique task ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the task ID.
     * Normally used by the manager during creation.
     *
     * @param id the unique identifier to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the current status of the task
     */
    public TaskStatus getStatus() {
        return status;
    }

    /**
     * Sets the task status.
     *
     * @param status the new status to assign
     */
    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    /**
     * @return the type of task. Always returns {@link TaskType#TASK} for this class.
     */
    public TaskType getType() {
        return TaskType.TASK;
    }

    /**
     * Checks equality based on the unique task ID.
     *
     * @param o the object to compare
     * @return true if the IDs match, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    /**
     * @return the hash code based on the task ID
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * @return a string representation of the task for debugging
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}