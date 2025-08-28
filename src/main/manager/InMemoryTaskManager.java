package manager;

import history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * In-memory {@link TaskManager} implementation.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Stores tasks/epics/subtasks in memory (hash maps by ID).</li>
 *   <li>Generates unique IDs for new items.</li>
 *   <li>Maintains a prioritized view (by {@code startTime}) of tasks and subtasks (epics are excluded).</li>
 *   <li>Recalculates epic status and time fields ({@code duration}, {@code startTime}, {@code endTime}).</li>
 *   <li>Prevents time overlaps between tasks/subtasks on create/update.</li>
 *   <li>Integrates with {@link HistoryManager} for view history.</li>
 * </ul>
 *
 * <p><strong>Thread-safety:</strong> not thread-safe.</p>
 */
public class InMemoryTaskManager implements TaskManager {

    /** Regular tasks by ID. */
    private final Map<Integer, Task> tasks;

    /** Epics by ID. */
    private final Map<Integer, Epic> epics;

    /** Subtasks by ID. */
    private final Map<Integer, Subtask> subtasks;

    /** Counter used to generate unique IDs. */
    private int nextId;

    /** History manager for tracking recently viewed items. */
    private final HistoryManager historyManager;

    /**
     * Prioritized set of tasks and subtasks sorted by {@code startTime} (ascending), then by {@code id}.
     * <p>Epics are not added here. Items with {@code null startTime} are excluded.</p>
     */
    private final Set<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        if (t1.getStartTime() == null && t2.getStartTime() == null) return Integer.compare(t1.getId(), t2.getId());
        if (t1.getStartTime() == null) return 1;
        if (t2.getStartTime() == null) return -1;
        int cmp = t1.getStartTime().compareTo(t2.getStartTime());
        return (cmp != 0) ? cmp : Integer.compare(t1.getId(), t2.getId());
    });

    /**
     * Creates an in-memory task manager.
     *
     * @param historyManager non-null history manager
     */
    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.nextId = 1;
        this.historyManager = historyManager;
    }

    /**
     * Generates the next unique identifier.
     *
     * @return next available ID
     */
    private int generateId() {
        return nextId++;
    }

    /**
     * Sets the ID counter (used when restoring state from storage).
     *
     * @param nextId starting value for the ID counter
     */
    protected void setNextId(int nextId) {
        this.nextId = nextId;
    }

    /**
     * Exposes internal task map to subclasses (e.g., file-backed manager).
     */
    protected Map<Integer, Task> getTasks() {
        return tasks;
    }

    /**
     * Exposes internal epic map to subclasses (e.g., file-backed manager).
     */
    protected Map<Integer, Epic> getEpics() {
        return epics;
    }

    /**
     * Exposes internal subtask map to subclasses (e.g., file-backed manager).
     */
    protected Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    // --- Task methods ---

    /** {@inheritDoc} */
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllTasks() {
        tasks.values().forEach(this::removeFromPrioritizedTasks);
        tasks.clear();
    }

    /** {@inheritDoc} */
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the task time interval overlaps with an existing one
     */
    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        if (hasOverlapping(task)) {
            throw new IllegalArgumentException("Task overlaps with an existing task");
        }
        task.setId(generateId());
        tasks.put(task.getId(), task);
        updatePrioritizedTasks(task);
        return task;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the updated task overlaps with an existing one
     */
    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }
        if (hasOverlapping(task)) {
            throw new IllegalArgumentException("Task update overlaps with an existing task");
        }
        tasks.put(task.getId(), task);
        updatePrioritizedTasks(task);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteTaskById(int id) {
        Task removed = tasks.remove(id);
        if (removed != null) {
            removeFromPrioritizedTasks(removed);
        }
    }

    // --- Epic methods ---

    /** {@inheritDoc} */
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllEpics() {
        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        subtasks.clear();
        epics.clear();
    }

    /** {@inheritDoc} */
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    /** {@inheritDoc} */
    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    /** {@inheritDoc} */
    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
        // status/time fields are derived; see updateEpicStatus/updateEpicTimeFields
    }

    /** {@inheritDoc} */
    @Override
    public void deleteEpicById(int id) {
        Epic epicToRemove = epics.remove(id);
        if (epicToRemove != null) {
            epicToRemove.getSubtaskIds().forEach(stId -> {
                Subtask removed = subtasks.remove(stId);
                if (removed != null) {
                    removeFromPrioritizedTasks(removed);
                }
            });
            epicToRemove.clearSubtaskIds();
        }
    }

    // --- Subtask methods ---

    /** {@inheritDoc} */
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    /** {@inheritDoc} */
    @Override
    public void removeAllSubtasks() {
        // Collect impacted epics first
        List<Integer> epicIdsToUpdate = subtasks.values().stream()
                .map(Subtask::getEpicId)
                .distinct()
                .toList();

        // Clear prioritized and storage
        subtasks.values().forEach(this::removeFromPrioritizedTasks);
        subtasks.clear();

        // Reset epics
        for (int epicId : epicIdsToUpdate) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.clearSubtaskIds();
                updateEpicStatus(epic);
                updateEpicTimeFields(epic);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if:
     *   <ul>
     *     <li>referenced epic does not exist;</li>
     *     <li>the subtask overlaps with an existing task/subtask.</li>
     *   </ul>
     */
    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        Epic parentEpic = epics.get(subtask.getEpicId());
        if (parentEpic == null) {
            throw new IllegalArgumentException("Epic with ID " + subtask.getEpicId() + " not found.");
        }
        if (hasOverlapping(subtask)) {
            throw new IllegalArgumentException("Subtask overlaps with an existing subtask");
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        parentEpic.addSubtaskId(subtask.getId());
        updateEpicStatus(parentEpic);
        updateEpicTimeFields(parentEpic);
        updatePrioritizedTasks(subtask);
        return subtask;
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if:
     *   <ul>
     *     <li>referenced new epic does not exist;</li>
     *     <li>the updated subtask overlaps with an existing task/subtask.</li>
     *   </ul>
     */
    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }
        if (hasOverlapping(subtask)) {
            throw new IllegalArgumentException("Subtask update overlaps with an existing subtask");
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        Epic oldEpic = epics.get(oldSubtask.getEpicId());
        Epic newEpic = epics.get(subtask.getEpicId());
        if (newEpic == null) {
            throw new IllegalArgumentException("New parent epic with ID " + subtask.getEpicId() + " not found.");
        }

        // Rebind epic if changed
        if (oldSubtask.getEpicId() != subtask.getEpicId()) {
            if (oldEpic != null) {
                oldEpic.removeSubtaskId(oldSubtask.getId());
                updateEpicStatus(oldEpic);
                updateEpicTimeFields(oldEpic);
            }
            newEpic.addSubtaskId(subtask.getId());
        }

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(newEpic);
        updateEpicTimeFields(newEpic);
        updatePrioritizedTasks(subtask);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSubtaskById(int id) {
        Subtask removed = subtasks.remove(id);
        if (removed != null) {
            Epic epic = epics.get(removed.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic);
                updateEpicTimeFields(epic);
            }
            removeFromPrioritizedTasks(removed);
        }
    }

    // --- Additional methods ---

    /** {@inheritDoc} */
    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return List.of();
        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    // region Prioritization and Overlap

    /** {@inheritDoc} */
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    /**
     * Rebuilds all derived structures after bulk mutations (e.g., after loading from file):
     * <ul>
     *   <li>rebuilds {@code prioritizedTasks};</li>
     *   <li>recalculates epic status and time fields.</li>
     * </ul>
     */
    protected void rebuildDerivedState() {
        prioritizedTasks.clear();
        tasks.values().forEach(this::updatePrioritizedTasks);
        subtasks.values().forEach(this::updatePrioritizedTasks);

        epics.values().forEach(epic -> {
            updateEpicStatus(epic);
            updateEpicTimeFields(epic);
        });
    }

    /**
     * Inserts/updates a task in the prioritized set.
     * <p>Tasks with {@code null startTime} are excluded by design.</p>
     */
    private void updatePrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    /** Removes a task from the prioritized set (no-op if absent). */
    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    /**
     * Checks two tasks' time intervals for overlap using inclusive bounds:
     * {@code (start1..end1) ∩ (start2..end2) ≠ ∅}.
     *
     * @return true if intervals overlap; false otherwise or if any bound is null
     */
    private boolean isOverlapping(Task t1, Task t2) {
        if (t1.getStartTime() == null || t1.getEndTime() == null ||
                t2.getStartTime() == null || t2.getEndTime() == null) {
            return false;
        }
        return !(t1.getEndTime().isBefore(t2.getStartTime()) || t1.getStartTime().isAfter(t2.getEndTime()));
    }

    /**
     * Checks whether the given task overlaps with any task/subtask in the prioritized set
     * (excluding itself by ID).
     */
    private boolean hasOverlapping(Task newTask) {
        return prioritizedTasks.stream()
                .filter(t -> t.getId() != newTask.getId())
                .anyMatch(t -> isOverlapping(t, newTask));
    }

    /**
     * Scans the prioritized list to detect any adjacent overlaps.
     * <p>Useful for diagnostics; not required by the core API.</p>
     *
     * @return true if any overlap exists; false otherwise
     */
    public boolean hasOverlappingTasks() {
        Task previous = null;
        for (Task current : getPrioritizedTasks()) {
            if (previous != null &&
                    previous.getEndTime() != null &&
                    current.getStartTime() != null &&
                    !previous.getEndTime().isBefore(current.getStartTime())) {
                return true;
            }
            previous = current;
        }
        return false;
    }

    /**
     * Recalculates epic time fields based on its subtasks:
     * <ul>
     *   <li>{@code duration} = sum of subtasks' durations (null values ignored, treated as zero);</li>
     *   <li>{@code startTime} = earliest subtask start;</li>
     *   <li>{@code endTime}   = latest subtask end.</li>
     * </ul>
     * If the epic has no subtasks, sets {@code duration = 0}, {@code startTime = null}, {@code endTime = null}.
     */
    private void updateEpicTimeFields(Epic epic) {
        List<Integer> ids = epic.getSubtaskIds();
        if (ids.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration total = Duration.ZERO;
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (int id : ids) {
            Subtask s = subtasks.get(id);
            if (s == null) continue;

            if (s.getDuration() != null) {
                total = total.plus(s.getDuration());
            }
            LocalDateTime start = s.getStartTime();
            LocalDateTime end = s.getEndTime();

            if (start != null) {
                if (earliest == null || start.isBefore(earliest)) earliest = start;
                if (end != null && (latest == null || end.isAfter(latest))) latest = end;
            }
        }

        epic.setDuration(total);
        epic.setStartTime(earliest);
        epic.setEndTime(latest);
    }

    /**
     * Recalculates the epic status according to its subtasks:
     * <ul>
     *   <li>no subtasks → {@code NEW};</li>
     *   <li>all {@code NEW}   → {@code NEW};</li>
     *   <li>all {@code DONE}  → {@code DONE};</li>
     *   <li>mixed or any {@code IN_PROGRESS} → {@code IN_PROGRESS}.</li>
     * </ul>
     */
    private void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        List<Integer> ids = epic.getSubtaskIds();
        if (ids.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;
        boolean hasInProgress = false;

        for (int id : ids) {
            Subtask s = subtasks.get(id);
            if (s == null) continue;
            switch (s.getStatus()) {
                case NEW -> newCount++;
                case DONE -> doneCount++;
                case IN_PROGRESS -> hasInProgress = true;
            }
        }

        if (hasInProgress || (newCount > 0 && doneCount > 0)) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (newCount == ids.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (doneCount == ids.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}

