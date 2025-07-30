package model;

import exception.ManagerSaveException;

import java.util.Objects;

/**
 * Base class representing a generic task (including epics and subtasks).
 * Contains common attributes: name, description, unique identifier, and status.
 */
public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    /**
     * Creates a new task with status {@link TaskStatus#NEW}.
     * The unique task ID should be assigned by the task manager.
     *
     * @param name        short task name
     * @param description detailed description
     */
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    /**
     * Creates a task with the specified status.
     * The unique task ID should be assigned by the task manager.
     *
     * @param name        task name
     * @param description task description
     * @param status      task status
     */
    public Task(String name, String description, TaskStatus status) {
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "id=" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}