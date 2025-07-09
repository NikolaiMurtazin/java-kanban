/**
 * Represents a subtask belonging to a specific {@link Epic}.
 */
public class Subtask extends Task {
    /** ID of the parent epic for this subtask. */
    private final int epicId;

    /**
     * Creates a new subtask with the given parameters.
     *
     * @param name        subtask name
     * @param description subtask description
     * @param status      initial status
     * @param epicId      ID of the parent epic
     */
    public Subtask(String name, String description, TaskStatus status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    /**
     * Returns the ID of the parent epic for this subtask.
     *
     * @return epic ID
     */
    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", epicId=" + epicId +
                '}';
    }
}


