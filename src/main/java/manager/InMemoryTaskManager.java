package manager;

import exceptions.TaskConflictException;
import interfaces.HistoryManager;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import utils.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    private Integer id = 0;
    protected final HashMap<Integer, Task> allTasks = new HashMap<>();
    protected final HashMap<Integer, Epic> allEpics = new HashMap<>();
    protected final HashMap<Integer, Subtask> allSubtasks = new HashMap<>();
    private final Comparator<Task> comparator = Comparator.<Task, LocalDateTime>comparing(task -> {
        if (task.getStartTime() == null) {
            return LocalDateTime.MAX;
        } else {
            return task.getStartTime();
        }
    }).thenComparing(Task::getId);
    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator);

    @Override
    public ArrayList<Task> getAllTask() {
        return new ArrayList<>(allTasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(allEpics.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(allSubtasks.values());
    }

    // Создание задач, эпиков и подзадач
    @Override
    public Task createNewTask(Task task) {
        if (task != null && !allTasks.containsKey(task.getId())) {
            allTasks.put(generateId(), task);
            task.setId(id);
            addTaskToPrioritizedList(task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Epic createNewEpic(Epic epic) {
        if (epic != null && !allEpics.containsKey(epic.getId())) {
            allEpics.put(generateId(), epic);
            epic.setId(id);
            return epic;
        } else {
            return null;
        }
    }

    @Override
    public Subtask createNewSubtask(Subtask subtask) {
        if (subtask != null && !allSubtasks.containsKey(subtask.getId()) && allEpics.containsKey(subtask.getEpicId())) {
            allSubtasks.put(generateId(), subtask);
            subtask.setId(id);
            Epic epic = allEpics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            addTaskToPrioritizedList(subtask);
            epicStatus(epic);
            if (subtask.getStartTime() != null) {
                updateEpicTime(epic);
            }
            return subtask;
        } else {
            return null;
        }
    }

    //Обновление задач, эпиков, подзадач
    @Override
    public void updateTask(Task task) {
        if (task != null && allTasks.containsKey(task.getId()) && allTasks.containsKey(task.getId())) {
            Task taskRemove = allTasks.get(task.getId());
            prioritizedTasks.remove(taskRemove);
            allTasks.put(task.getId(), task);
            addTaskToPrioritizedList(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null && allEpics.containsKey(epic.getId()) && allEpics.containsKey(epic.getId())) {
            allEpics.put(epic.getId(), epic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null && allSubtasks.containsKey(subtask.getId()) && allSubtasks.containsKey((subtask.getId()))) {
            Subtask subtaskRemove = allSubtasks.get(subtask.getId());
            prioritizedTasks.remove(subtaskRemove);
            allSubtasks.put(subtask.getId(), subtask);
            Epic epic = allEpics.get(subtask.getEpicId());
            epicStatus(epic);
            addTaskToPrioritizedList(subtask);
            if (subtask.getStartTime() != null) {
                updateEpicTime(epic);
            }
        }
    }

    // Получение по идентификатору и занесение id в историю просмотров
    @Override
    public Task getTaskById(Integer idTask) {
        Task task = allTasks.get(idTask);
        if (task != null && allTasks.containsKey(idTask)) {
            historyManager.add(task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpicById(Integer idEpic) {
        Epic epic = allEpics.get(idEpic);
        if (epic != null && allEpics.containsKey(idEpic)) {
            historyManager.add(epic);
            return epic;
        } else {
            return null;
        }
    }

    @Override
    public Subtask getSubtaskById(Integer idSubtask) {
        Subtask subtask = allSubtasks.get(idSubtask);
        if (subtask != null && allSubtasks.containsKey(idSubtask)) {
            historyManager.add(subtask);
            return subtask;
        } else {
            return null;
        }
    }

    // Получение списка всех задач определенного эпика
    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Integer idEpic) {
        if (allEpics.containsKey(idEpic)) {
            ArrayList<Subtask> result = new ArrayList<>();
            Epic epic = allEpics.get(idEpic);
            for (Integer idSubtask : epic.getSubtasks()) {
                result.add(allSubtasks.get(idSubtask));
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    // Вывод истории просмотров задач по id
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    // удаление задачи, эпика, подзадачи по id
    @Override
    public void deleteTask(Integer idTask) {
        if (allTasks.containsKey(idTask)) {
            allTasks.remove(idTask);
            historyManager.remove(idTask);
            prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), idTask));
        }
    }

    @Override
    public void deleteEpic(Integer idEpic) {
        if (allEpics.containsKey(idEpic)) {
            Epic epic = allEpics.remove(idEpic);
            historyManager.remove(idEpic);
            if (epic != null) {
                for (Integer idSubtask : epic.getSubtasks()) {
                    allSubtasks.remove(idSubtask);
                    historyManager.remove(idSubtask);
                    prioritizedTasks.removeIf(task -> Objects.equals(task.getId(), idSubtask));
                }
            }
        }
    }

    @Override
    public void deleteSubtask(Integer idSubtask) {
        if (allSubtasks.containsKey(idSubtask)) {
            Subtask subtask = allSubtasks.remove(idSubtask);
            historyManager.remove(idSubtask);
            if (subtask != null) {
                Epic epic = allEpics.get(subtask.getEpicId());
                epic.getSubtasks().remove(idSubtask);
                epicStatus(epic);
                if (subtask.getStartTime() != null) {
                    updateEpicTime(epic);
                }
                prioritizedTasks.remove(subtask);
            }
        }
    }

    //удаление всех задач, эпиков и подзадач
    @Override
    public void deleteAllTasks() {
        for (Integer idTask : allTasks.keySet()) {
            historyManager.remove(idTask);
        }
        allTasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (Integer idEpic : allEpics.keySet()) {
            historyManager.remove(idEpic);
        }
        for (Integer idSubtask : allSubtasks.keySet()) {
            historyManager.remove(idSubtask);
        }
        allEpics.clear();
        allSubtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Integer idSubtask : allSubtasks.keySet()) {
            Subtask subtask = allSubtasks.get(idSubtask);
            prioritizedTasks.remove(subtask);
            historyManager.remove(idSubtask);
        }
        allSubtasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.getSubtasks().clear();
            epicStatus(epic);
        }
    }

    //Проверка статуса эпика
    private void epicStatus(Epic epic) {
        boolean isSubtaskStatusNew = true;
        boolean isSubtaskStatusDone = true;

        ArrayList<TaskStatus> statuses = new ArrayList<>();

        for (Integer id : allSubtasks.keySet()) {
            Subtask subtask = allSubtasks.get(id);
            if (subtask.getEpicId().equals(epic.getId())) {
                statuses.add(subtask.getStatus());
            }
        }

        for (TaskStatus status : statuses) {
            if (!status.equals(TaskStatus.NEW)) {
                isSubtaskStatusNew = false;
                break;
            }
        }

        for (TaskStatus status : statuses) {
            if (!status.equals(TaskStatus.DONE)) {
                isSubtaskStatusDone = false;
                break;
            }
        }

        if (epic.getSubtasks().isEmpty() || isSubtaskStatusNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (isSubtaskStatusDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateEpicTime(Epic epic) {
        List<Subtask> subtasks = getSubtasksByEpic(epic.getId());
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;

        for (Subtask subtask : subtasks) {
            if (subtask.getStartTime() != null) {
                if (startTime == null || subtask.getStartTime().isBefore(startTime)) {
                    startTime = subtask.getStartTime();
                }
            }

            if (subtask.getEndTime() != null) {
                if (endTime == null || subtask.getEndTime().isAfter(endTime)) {
                    endTime = subtask.getEndTime();
                }
            }
        }

        epic.setStartTime(startTime);
        epic.setEndTime(endTime);

        if (startTime != null && endTime != null) {
            long duration = startTime.getMinute() - endTime.getMinute();
            epic.setDuration(duration);
        }
    }

    public void addTaskToPrioritizedList(Task task) {
        boolean isValidated = isTaskIntersects(task);
        if (!isValidated) {
            prioritizedTasks.add(task);
        } else {
        throw new TaskConflictException("Проблема, вызванная временем выполнения аналогичных задач");
        }
    }

    private boolean isTaskIntersects(Task task) {
        LocalDateTime startOfTask = task.getStartTime();
        LocalDateTime endOfTask = task.getEndTime();

        if (startOfTask == null || endOfTask == null) {
            return false;
        }

        for (Task taskValue : prioritizedTasks) {
            if (taskValue.getStartTime() == null) {
                continue;
            }

            LocalDateTime startTime = taskValue.getStartTime();
            LocalDateTime endTime = taskValue.getEndTime();

            if (startTime.isBefore(endOfTask) && endTime.isAfter(startOfTask)) {
                return true;
            }
        }
        return false;
    }

    private Integer generateId() {
        return ++id;
    }
}
