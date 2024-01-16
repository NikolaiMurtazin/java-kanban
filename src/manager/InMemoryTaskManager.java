package manager;

import interfaces.HistoryManager;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import utils.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    protected final HistoryManager historyManager = Managers.getDefaultHistory();

    private Integer id = 0;

    protected final HashMap<Integer, Task> allTasks = new HashMap<>();
    protected final HashMap<Integer, Epic> allEpics = new HashMap<>();
    protected final HashMap<Integer, Subtask> allSubtasks = new HashMap<>();

    // Вывод созданных задач, эпиков и подзадач (должен возвращать, а не выводит в консоль)
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

    //удаление всех задач, эпиков и подзадач
    @Override
    public void deleteAllTasks() {
        for (Integer id : allTasks.keySet()) {
            historyManager.remove(id);
        }
        allTasks.clear();
    }

    @Override
    public void deleteAllEpic() {
        for (Integer id : allEpics.keySet()) {
            historyManager.remove(id);
        }
        for (Integer id : allSubtasks.keySet()) {
            historyManager.remove(id);
        }
        allEpics.clear();
        allSubtasks.clear();
    }

    @Override
    public void deleteAllSubtask() {
        for (Integer id : allSubtasks.keySet()) {
            historyManager.remove(id);
        }
        allSubtasks.clear();
        for (Epic epic : allEpics.values()) {
            epic.getSubtasks().clear();
            epicStatus(epic);
        }
    }

    // Получение по идентификатору и занесение id в историю просмотров
    @Override
    public Task getTaskById(Integer idTask) {
        Task task = allTasks.get(idTask);
        historyManager.add(task);
        return allTasks.get(idTask);
    }

    @Override
    public Epic getEpicById(Integer idEpic) {
        Epic epic = allEpics.get(idEpic);
        historyManager.add(epic);
        return allEpics.get(idEpic);
    }

    @Override
    public Subtask getSubtaskById(Integer idSubtask) {
        Subtask subtask = allSubtasks.get(idSubtask);
        historyManager.add(subtask);
        return allSubtasks.get(idSubtask);
    }

    // Создание задач, эпиков и подзадач
    @Override
    public void createNewTask(Task task) {
        allTasks.put(generateId(), task);
        task.setId(id);
    }

    @Override
    public void createNewEpic(Epic epic) {
        allEpics.put(generateId(), epic);
        epic.setId(id);
    }

    @Override
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
    @Override
    public void updateTask(Task task) {
        if (allTasks.containsKey(task.getId())) {
            allTasks.put(task.getId(), task);
        }

    }

    @Override
    public void updateEpic(Epic epic) {
        if (allEpics.containsKey(epic.getId())) {
            allEpics.put(epic.getId(), epic);
        }

    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (allSubtasks.containsKey((subtask.getId()))) {
            allSubtasks.put(subtask.getId(), subtask);
            Epic epic = allEpics.get(subtask.getEpicId());
            epicStatus(epic);
        }

    }

    // удаление задачи, эпика, подзадачи по id
    @Override
    public void deleteTask(Integer idTask) {
        allTasks.remove(idTask);
        historyManager.remove(idTask);
    }

    @Override
    public void deleteEpic(Integer idEpic) {
        Epic epic = allEpics.remove(idEpic);
        historyManager.remove(idEpic);
        if (epic != null) {
            for (Integer id : epic.getSubtasks()) {
                allSubtasks.remove(id);
                historyManager.remove(id);
            }
        }
    }

    @Override
    public void deleteSubtask(Integer idSubtask) {
        Subtask subtask = allSubtasks.remove(idSubtask);
        historyManager.remove(idSubtask);
        if (subtask != null) {
            Epic epic = allEpics.get(subtask.getEpicId());
            epic.getSubtasks().remove(idSubtask);
            epicStatus(epic);
        }

    }

    // Получение списка всех задач определенного эпика
    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Integer idEpic) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = allEpics.get(idEpic);
        for (Integer id : epic.getSubtasks()) {
            result.add(allSubtasks.get(id));
        }
        return result;
    }

    // Вывод истории просмотров задач по id
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Проверка статуса эпика
    private void epicStatus(Epic epic) {
        boolean isSubtaskStatusNew = true;
        boolean isSubtaskStatusDone = true;

        ArrayList<Status> statuses = new ArrayList<>();

        for (Integer id : allSubtasks.keySet()) {
            Subtask subtask = allSubtasks.get(id);
            if (subtask.getEpicId().equals(epic.getId())) {
                statuses.add(subtask.getStatus());
            }
        }

        for (Status status : statuses) {
            if (!status.equals(Status.NEW)) {
                isSubtaskStatusNew = false;
                break;
            }
        }

        for (Status status : statuses) {
            if (!status.equals(Status.DONE)) {
                isSubtaskStatusDone = false;
                break;
            }
        }

        if (epic.getSubtasks().isEmpty() || isSubtaskStatusNew) {
            epic.setStatus(Status.NEW);
        } else if (isSubtaskStatusDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private Integer generateId() {
        return ++id;
    }
}
