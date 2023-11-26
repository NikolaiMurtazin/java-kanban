package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private Integer id = 0;

    private final HashMap<Integer, Task> ALL_TASKS = new HashMap<>();
    private final HashMap<Integer, Epic> ALL_EPICS = new HashMap<>();
    private final HashMap<Integer, Subtask> ALL_SUBTASKS = new HashMap<>();

    // Вывод созданных задач, эпиков и подзадач (должен возвращать, а не выводит в консоль)
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> result = new ArrayList<>();
        result.addAll(ALL_TASKS.values());
        return result;
    }

    public ArrayList<Epic> getAllEpic() {
        ArrayList<Epic> result = new ArrayList<>();
        result.addAll(ALL_EPICS.values());
        return result;
    }

    public ArrayList<Subtask> getAllSubtask() {
        ArrayList<Subtask> result = new ArrayList<>();
        result.addAll(ALL_SUBTASKS.values());
        return result;
    }

    //удаление всех задач, эпиков и подзадач
    public void deleteAllTasks() {
        ALL_TASKS.clear();
    }

    public void deleteAllEpic() {
        ALL_EPICS.clear();
        ALL_SUBTASKS.clear();
    }

    public void deleteAllSubtask() {
        ALL_SUBTASKS.clear();
        for (Integer id : ALL_EPICS.keySet()) {
            Epic epic = ALL_EPICS.get(id);
            epic.getSubtasks().clear();
        }
    }

    // Получение по идентификатору
    public ArrayList<Task> getTaskById(Integer idTask) {
        ArrayList<Task> result = new ArrayList<>();
        result.add(ALL_TASKS.get(idTask));
        return result;
    }

    public ArrayList<Epic> getEpicById(Integer idEpic) {
        ArrayList<Epic> result = new ArrayList<>();
        result.add(ALL_EPICS.get(idEpic));
        return result;
    }

    public ArrayList<Subtask> getSubtaskById(Integer idSubtask) {
        ArrayList<Subtask> result = new ArrayList<>();
        result.add(ALL_SUBTASKS.get(idSubtask));
        return result;
    }

    // Создание задач, эпиков и подзадач
    public void createNewTask(Task task) {
        ALL_TASKS.put(generateId(), task);
        task.setId(id);
    }

    public void createNewEpic(Epic epic) {
        ALL_EPICS.put(generateId(), epic);
        epic.setId(id);
    }

    public void createNewSubtask(Subtask subtask, Epic epic) {
        ALL_SUBTASKS.put(generateId(), subtask);
        subtask.setId(id);
        epic.addSubtask(subtask);
    }

    //Обновление задач, эпиков, подзадач
    // Не совсем понимаю. Мы через set обновляем, и в мапе уже обновленные данные. Зачем еще раз обновлять?
    public void updateTask(Task task) {
        ALL_TASKS.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        ALL_EPICS.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        ALL_SUBTASKS.put(subtask.getId(), subtask);
    }

    // удаление задачи, эпика, подзадачи по id
    public void deleteTask(Integer idTask) {
        ALL_TASKS.remove(idTask);
    }

    public void deleteEpic(Integer idEpic) {
        Epic epic = ALL_EPICS.get(idEpic);
        for (Integer id : epic.getSubtasks()) {
            ALL_SUBTASKS.remove(id);
        }
        ALL_EPICS.remove(idEpic);
    }

    public void deleteSubtask(Integer idSubtask) {
        ALL_SUBTASKS.remove(idSubtask);
        for (Integer id : ALL_EPICS.keySet()) {
            Epic epic = ALL_EPICS.get(id);
            epic.getSubtasks().remove(idSubtask);
        }
    }

    // Получение списка всех задач определенного эпика
    public ArrayList<Subtask> getSubtasksByEpic(Integer idEpic) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = ALL_EPICS.get(idEpic);
        for (Integer id : epic.getSubtasks()) {
            result.add(ALL_SUBTASKS.get(id));
        }
        return result;
    }

    //Проверка статуса эпика
    public void epicStatus(Epic epic) {
        boolean isSubtaskStatusNew = true;
        boolean isSubtaskStatusDone = true;

        ArrayList<TaskStatus> statuses = new ArrayList<>();

        for (Integer id : ALL_SUBTASKS.keySet()) {
            Subtask subtask = ALL_SUBTASKS.get(id);
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
