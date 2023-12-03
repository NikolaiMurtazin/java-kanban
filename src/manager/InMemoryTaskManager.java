package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager {

    private Integer id = 0;

    private final HashMap<Integer, Task> allTasks = new HashMap<>();
    private final HashMap<Integer, Epic> allEpics = new HashMap<>();
    private final HashMap<Integer, Subtask> allSubtasks = new HashMap<>();

    // Вывод созданных задач, эпиков и подзадач (должен возвращать, а не выводит в консоль)
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(allTasks.values());
    }

    public ArrayList<Epic> getAllEpic() {
        return new ArrayList<>(allEpics.values());
    }

    public ArrayList<Subtask> getAllSubtask() {
        return new ArrayList<>(allSubtasks.values());
    }

    //удаление всех задач, эпиков и подзадач
    public void deleteAllTasks() {
        allTasks.clear();
    }

    public void deleteAllEpic() {
        allEpics.clear();
        allSubtasks.clear();
    }

    public void deleteAllSubtask() {
        allSubtasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.getSubtasks().clear();
            epicStatus(epic);
        }
    }

    // Получение по идентификатору
    public Task getTaskById(Integer idTask) {
        return allTasks.get(idTask);
    }

    public Epic getEpicById(Integer idEpic) {
        return allEpics.get(idEpic);
    }

    public Subtask getSubtaskById(Integer idSubtask) {
        return allSubtasks.get(idSubtask);
    }

    // Создание задач, эпиков и подзадач
    public void createNewTask(Task task) {
        allTasks.put(generateId(), task);
        task.setId(id);
    }

    public void createNewEpic(Epic epic) {
        allEpics.put(generateId(), epic);
        epic.setId(id);
    }

    public void createNewSubtask(Subtask subtask) {
        if (allEpics.containsKey(subtask.getEpicId())) {
            allSubtasks.put(generateId(), subtask);
            subtask.setId(id);
            Epic epic = allEpics.get(subtask.getEpicId());
            epic.addSubtask(subtask);
            epicStatus(epic);
        }

    }

    //Обновление задач, эпиков, подзадач
    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
        }

    }

    public void updateEpic(Epic epic) {
        if (allEpics.containsKey(epic.getId())) {
            allEpics.put(epic.getId(), epic);
        }

    }

    public void updateSubtask(Subtask subtask) {
        if (allSubtasks.containsKey((subtask.getId()))) {
            allSubtasks.put(subtask.getId(), subtask);
            Epic epic = allEpics.get(subtask.getEpicId());
            epicStatus(epic);
        }

    }

    // удаление задачи, эпика, подзадачи по id
    public void deleteTask(Integer idTask) {
        allTasks.remove(idTask);
    }

    public void deleteEpic(Integer idEpic) {
        Epic epic = allEpics.remove(idEpic);
        if (epic != null) {
            for (Integer id : epic.getSubtasks()) {
                allSubtasks.remove(id);
            }
        }
    }

    public void deleteSubtask(Integer idSubtask) {
        Subtask subtask = allSubtasks.remove(idSubtask);
        if (subtask != null) {
            Epic epic = allEpics.get(subtask.getEpicId());
            epic.getSubtasks().remove(idSubtask);
            epicStatus(epic);
        }

    }

    // Получение списка всех задач определенного эпика
    public ArrayList<Subtask> getSubtasksByEpic(Integer idEpic) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = allEpics.get(idEpic);
        for (Integer id : epic.getSubtasks()) {
            result.add(allSubtasks.get(id));
        }
        return result;
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

    private Integer generateId() {
        return ++id;
    }
}
