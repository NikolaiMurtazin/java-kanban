import java.util.ArrayList;
import java.util.HashMap;

class TaskManager {

    private Integer id = 0;

    private Integer generateId() {
        this.id++;
        return this.id;
    }

    HashMap<Integer, Task> allTasks = new HashMap<>();
    HashMap<Integer, Epic> allEpics =
            new HashMap<>();
    HashMap<Integer, Subtask> allSubtasks = new HashMap<>();

    // Вывод созданных задач, эпиков и подзадач
    public void printAllTasks() {
        System.out.println("Все задачи:");
        System.out.println(allTasks);
    }

    public void printAllEpic() {
        System.out.println("Все эпики:");
        System.out.println(allEpics);
    }

    public void printAllSubtask() {
        System.out.println("Все подзадачи:");
        System.out.println(allSubtasks);
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
    }

    // Получение по идентификатору
    public void getTaskById(Integer id) {
        allTasks.get(id);
    }

    public void getEpicById(Integer id) {
        allEpics.get(id);
    }

    public void getSubtaskById(Integer id) {
        allSubtasks.get(id);
    }

    // Создание задач, эпиков и подзадач
    public Task createNewTask(String title, String description) {
        Task task = new Task(generateId(), title, description);
        allTasks.put(task.getId(), task);
        return task;
    }

    public Subtask createNewSubtask(String title, String description, Epic epic) {
        Subtask subtask = new Subtask(generateId(), title, description, epic.getId());
        allSubtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        return subtask;
    }

    public Epic createNewEpic(String title, String description) {
        Epic epic = new Epic(generateId(), title, description);
        allEpics.put(epic.getId(), epic);
        return epic;
    }

    //Обновление задач, эпиков, подзадач
    public void updateTask(Task task, String title, String description) {
        Task newTask = new Task(task.getId(), title, description);
        allTasks.put(task.getId(), newTask);
    }

    public void updateEpic(Epic epic, String title, String description) {
        Task newEpic = new Task(epic.getId(), title, description);
        allTasks.put(epic.getId(), newEpic);
    }

    public void updateSubtask(Subtask subtask, String title, String description) {
        Task newSubtask = new Task(subtask.getId(), title, description);
        allTasks.put(subtask.getId(), newSubtask);
    }

    // удаление задачи, эпика, подзадачи по id
    public void deleteTask(Integer idTask) {
        allTasks.remove(idTask);
    }

    public void deleteEpic(Integer idEpic) {
        for (Integer id : allSubtasks.keySet()) {
            Subtask subtask = allSubtasks.get(id);
            if (idEpic.equals(subtask.getEpicId())) {
                allSubtasks.remove(subtask.getId());
            }
        }
        allEpics.remove(idEpic);
    }

    public void deleteSubtask(Integer idSubtask) {
        allSubtasks.remove(idSubtask);
    }

    // Получение списка всех задач определенного эпика
    public void getSubtasksByEpic(Epic epic) {
        for (Integer id : allSubtasks.keySet()) {
            Subtask subtask = allSubtasks.get(id);
            if (epic.getId().equals(subtask.getEpicId())) {
                allSubtasks.get(subtask.getId());
            }
        }
    }

    //Проверка статуса эпика
    public void epicStatus(Epic epic) {
        boolean isSubtaskStatusNew = true;
        boolean isSubtaskStatusDone = true;

        ArrayList<String> statuses = new ArrayList<>();

        for (Integer id : allSubtasks.keySet()) {
            Subtask subtask = allSubtasks.get(id);
            if (subtask.getEpicId().equals(epic.getId())) {
                statuses.add(String.valueOf(subtask.getStatus()));
            }
        }

        for (String status : statuses) {
            if (!status.equals("NEW")) {
                isSubtaskStatusNew = false;
                break;
            }
        }

        for (String status : statuses) {
            if (!"DONE".equals(status)) {
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
}
