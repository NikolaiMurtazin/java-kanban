package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

/**
 * Interface defining the contract for a task manager.
 * Provides CRUD operations for {@link Task}, {@link Epic}, and {@link Subtask},
 * as well as lookup methods and utilities for working with task hierarchies.
 */
public interface TaskManager {

    // --- Task methods ---

    /**
     * Returns a list of all regular tasks.
     *
     * @return list of tasks; empty if none exist
     */
    List<Task> getAllTasks();

    /**
     * Removes all regular tasks from the manager.
     */
    void removeAllTasks();

    /**
     * Retrieves a task by its unique identifier.
     *
     * @param id the task ID
     * @return the task if found; otherwise {@code null}
     */
    Task getTaskById(int id);

    /**
     * Creates and stores a new task, assigning it a unique ID.
     *
     * @param task the task to create
     * @return the created task with assigned ID
     */
    Task createTask(Task task);

    /**
     * Updates an existing task with new data.
     * The task must already exist in the manager.
     *
     * @param task the task containing updated fields
     */
    void updateTask(Task task);

    /**
     * Deletes a task by its unique identifier.
     *
     * @param id the ID of the task to delete
     */
    void deleteTaskById(int id);

    // --- Epic methods ---

    /**
     * Returns a list of all epics.
     *
     * @return list of epics; empty if none exist
     */
    List<Epic> getAllEpics();

    /**
     * Removes all epics and all associated subtasks.
     */
    void removeAllEpics();

    /**
     * Retrieves an epic by its unique identifier.
     *
     * @param id the epic ID
     * @return the epic if found; otherwise {@code null}
     */
    Epic getEpicById(int id);

    /**
     * Creates a new epic and assigns it a unique ID.
     *
     * @param epic the epic to create
     * @return the created epic with assigned ID
     */
    Epic createEpic(Epic epic);

    /**
     * Updates an epic's name and description.
     * The epic must already exist; its status and subtasks are managed internally.
     *
     * @param epic the epic with updated fields
     */
    void updateEpic(Epic epic);

    /**
     * Deletes an epic by ID and removes all its associated subtasks.
     *
     * @param id the ID of the epic to delete
     */
    void deleteEpicById(int id);

    // --- Subtask methods ---

    /**
     * Returns a list of all subtasks.
     *
     * @return list of subtasks; empty if none exist
     */
    List<Subtask> getAllSubtasks();

    /**
     * Removes all subtasks and updates statuses of affected epics.
     */
    void removeAllSubtasks();

    /**
     * Retrieves a subtask by its unique identifier.
     *
     * @param id the subtask ID
     * @return the subtask if found; otherwise {@code null}
     */
    Subtask getSubtaskById(int id);

    /**
     * Creates a new subtask and assigns it to the specified epic.
     * Also assigns a unique ID to the subtask and updates the parent epic's status.
     *
     * @param subtask the subtask to create
     * @return the created subtask; or {@code null} if the parent epic is not found
     */
    Subtask createSubtask(Subtask subtask);

    /**
     * Updates an existing subtask.
     * Handles reassigning to a different epic if needed, and updates statuses accordingly.
     *
     * @param subtask the subtask with updated fields
     */
    void updateSubtask(Subtask subtask);

    /**
     * Deletes a subtask by its ID and updates the status of the parent epic.
     *
     * @param id the subtask ID
     */
    void deleteSubtaskById(int id);

    /**
     * Retrieves all subtasks assigned to a specific epic.
     *
     * @param epicId the ID of the epic
     * @return list of subtasks; empty if epic not found or no subtasks exist
     */
    List<Subtask> getEpicSubtasks(int epicId);

    /**
     * Returns a snapshot list of prioritized tasks/subtasks (sorted by start time).
     *
     * @return immutable copy ordered by priority
     */
    List<Task> getPrioritizedTasks();
}
