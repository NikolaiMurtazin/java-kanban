package manager;

import history.HistoryManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In-memory implementation of {@link TaskManager}.
 * Stores all tasks, epics, and subtasks in memory using hash maps.
 * Handles task lifecycle and IDs, automatically recalculates epic status,
 * and integrates with a {@link HistoryManager} for task view history.
 */
public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private int nextId;

    private final HistoryManager historyManager;

    /**
     * Creates an in-memory task manager with the given history manager.
     *
     * @param historyManager the history manager to use
     */
    public InMemoryTaskManager(HistoryManager historyManager) {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.nextId = 1;
        this.historyManager = historyManager;
    }

    private int generateId() {
        return nextId++;
    }

    // --- model.Task methods ---

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) return null;
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || !tasks.containsKey(task.getId())) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    // --- model.Epic methods ---

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) return null;
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || !epics.containsKey(epic.getId())) {
            return;
        }
        Epic existingEpic = epics.get(epic.getId());
        existingEpic.setName(epic.getName());
        existingEpic.setDescription(epic.getDescription());
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic epicToRemove = epics.remove(id);
        if (epicToRemove != null) {
            for (Integer subtaskId : new ArrayList<>(epicToRemove.getSubtaskIds())) {
                subtasks.remove(subtaskId);
            }
            epicToRemove.clearSubtaskIds();
        }
        return epicToRemove;
    }

    // --- model.Subtask methods ---

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllSubtasks() {
        List<Integer> epicIdsToUpdate = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (!epicIdsToUpdate.contains(subtask.getEpicId())) {
                epicIdsToUpdate.add(subtask.getEpicId());
            }
        }
        subtasks.clear();
        for (int epicId : epicIdsToUpdate) {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.clearSubtaskIds();
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null) return null;
        Epic parentEpic = epics.get(subtask.getEpicId());
        if (parentEpic == null) {
            System.out.println("Error: model.Epic with ID " + subtask.getEpicId() + " not found.");
            return null;
        }
        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        parentEpic.addSubtaskId(subtask.getId());
        updateEpicStatus(parentEpic);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subtasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask oldSubtask = subtasks.get(subtask.getId());
        if (oldSubtask == null) return;

        Epic oldParentEpic = epics.get(oldSubtask.getEpicId());
        Epic newParentEpic = epics.get(subtask.getEpicId());

        if (newParentEpic == null) {
            System.out.println("Error: New parent epic with ID " + subtask.getEpicId() + " not found.");
            return;
        }

        if (oldSubtask.getEpicId() != subtask.getEpicId()) {
            if (oldParentEpic != null) {
                oldParentEpic.removeSubtaskId(oldSubtask.getId());
                updateEpicStatus(oldParentEpic);
            }
            newParentEpic.addSubtaskId(subtask.getId());
        }

        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(newParentEpic);
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtaskToRemove = subtasks.remove(id);
        if (subtaskToRemove != null) {
            Epic parentEpic = epics.get(subtaskToRemove.getEpicId());
            if (parentEpic != null) {
                parentEpic.removeSubtaskId(id);
                updateEpicStatus(parentEpic);
            }
        }
    }

    // --- Additional methods ---

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                epicSubtasks.add(subtask);
            }
        }
        return epicSubtasks;
    }

    /**
     * Updates the status of the given epic based on its subtasks.
     *
     * @param epic the epic to update
     */
    private void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        List<Integer> subtaskIds = epic.getSubtaskIds();
        if (subtaskIds.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        int newCount = 0;
        int doneCount = 0;
        boolean hasInProgress = false;

        for (int subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                switch (subtask.getStatus()) {
                    case NEW: newCount++; break;
                    case DONE: doneCount++; break;
                    case IN_PROGRESS: hasInProgress = true; break;
                }
            }
        }

        if (hasInProgress || (newCount > 0 && doneCount > 0)) {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        } else if (newCount == subtaskIds.size()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (doneCount == subtaskIds.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}

