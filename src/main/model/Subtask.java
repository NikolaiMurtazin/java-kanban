package model;

/**
 * Represents a subtask that belongs to a specific {@link Epic}.
 * A subtask cannot exist without a parent epic, identified by {@code epicId}.
 */
public class Subtask extends Task {

    /** The ID of the epic this subtask belongs to. */
    private final int epicId;

    /**
     * Constructs a new subtask with the given name, description, status, and epic ID.
     * The task ID should be assigned by the task manager.
     *
     * @param name        the name of the subtask
     * @param description the description of the subtask
     * @param status      the initial status of the subtask
     * @param epicId      the ID of the parent epic
     */
    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    /**
     * Constructs a new subtask with a predefined ID (e.g., when loading from file).
     *
     * @param id          the ID of the subtask
     * @param name        the name of the subtask
     * @param description the description of the subtask
     * @param status      the status of the subtask
     * @param epicId      the ID of the parent epic
     */
    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    /**
     * Returns the ID of the parent epic for this subtask.
     *
     * @return the epic ID
     */
    public int getEpicId() {
        return epicId;
    }

    /**
     * Returns the type of this task, which is {@link TaskType#SUBTASK}.
     *
     * @return {@link TaskType#SUBTASK}
     */
    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    /**
     * Returns a string representation of the subtask, including all its properties.
     *
     * @return a string representation of the subtask
     */
    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + epicId +
                '}';
    }
}


