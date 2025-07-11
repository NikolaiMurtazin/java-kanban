import java.util.ArrayList;
import java.util.List;

/**
 * Represents an epic task, which is a large task composed of one or more {@link Subtask} instances.
 * The status of the epic is automatically calculated based on the statuses of its subtasks
 * and managed by the {@link TaskManager}.
 */
public class Epic extends Task {
    /** List of subtask IDs associated with this epic. */
    private final List<Integer> subtaskIds;

    /**
     * Creates a new epic task with the given name and description.
     * The epic is initially created without subtasks; its status will be managed by the task manager.
     *
     * @param name        the epic name
     * @param description the epic description
     */
    public Epic(String name, String description) {
        super(name, description);
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
     * Adds the ID of a subtask to this epic.
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
     */
    public void clearSubtaskIds() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}