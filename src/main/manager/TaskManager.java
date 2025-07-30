package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

/**
 * Contract for a task manager. Provides CRUD operations for tasks, epics, and subtasks,
 * as well as retrieval of all items by type and subtask lookup for epics.
 */
public interface TaskManager {
    // --- model.Task methods ---

    /**
     * Returns all regular tasks.
     */
    List<Task> getAllTasks();

    /**
     * Removes all regular tasks.
     */
    void removeAllTasks();

    /**
     * Returns a task by its unique identifier.
     *
     * @param id the task ID
     * @return the task or null if not found
     */
    Task getTaskById(int id);

    /**
     * Creates a new task, assigning it a unique ID.
     *
     * @param task the task to create
     * @return the created task with assigned ID
     */
    Task createTask(Task task);

    /**
     * Updates an existing task by ID.
     *
     * @param task the task with updated data
     */
    void updateTask(Task task);

    /**
     * Deletes a task by ID.
     *
     * @param id the task ID
     */
    void deleteTaskById(int id);

    // --- model.Epic methods ---

    /**
     * Returns all epics.
     */
    List<Epic> getAllEpics();

    /**
     * Removes all epics (and all their subtasks).
     */
    void removeAllEpics();

    /**
     * Returns an epic by ID.
     *
     * @param id the epic ID
     * @return the epic or null if not found
     */
    Epic getEpicById(int id);

    /**
     * Creates a new epic, assigning it a unique ID.
     *
     * @param epic the epic to create
     * @return the created epic
     */
    Epic createEpic(Epic epic);

    /**
     * Updates an existing epic's name and description by ID.
     * Subtasks and status are managed internally.
     *
     * @param epic the epic with updated data
     */
    void updateEpic(Epic epic);

    /**
     * Deletes an epic by ID and all its subtasks.
     *
     * @param id the epic ID
     * @return the deleted epic or null if not found
     */
    Epic deleteEpicById(int id);

    // --- model.Subtask methods ---

    /**
     * Returns all subtasks.
     */
    List<Subtask> getAllSubtasks();

    /**
     * Removes all subtasks (and updates affected epics).
     */
    void removeAllSubtasks();

    /**
     * Returns a subtask by ID.
     *
     * @param id the subtask ID
     * @return the subtask or null if not found
     */
    Subtask getSubtaskById(int id);

    /**
     * Creates a new subtask for the specified epic.
     * Assigns it a unique ID and updates the parent epic's status.
     *
     * @param subtask the subtask to create
     * @return the created subtask or null if parent epic not found
     */
    Subtask createSubtask(Subtask subtask);

    /**
     * Updates an existing subtask by ID.
     * If the epic reference changes, re-binds it.
     * Updates the status of affected epics.
     *
     * @param subtask the subtask with updated data
     */
    void updateSubtask(Subtask subtask);

    /**
     * Deletes a subtask by ID and updates the parent epic's status.
     *
     * @param id the subtask ID
     */
    void deleteSubtaskById(int id);

    /**
     * Returns all subtasks of the given epic.
     *
     * @param epicId the epic ID
     * @return list of subtasks (empty if epic not found or has no subtasks)
     */
    List<Subtask> getEpicSubtasks(int epicId);
}
