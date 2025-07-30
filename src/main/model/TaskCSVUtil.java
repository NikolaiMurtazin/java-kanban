package model;

import exception.ManagerSaveException;
import history.HistoryManager;
import history.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;

public class TaskCSVUtil {
    HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager taskManager = new InMemoryTaskManager(historyManager);

    public static String toCSVString(Task task) {
        String epicId = "";
        TaskType taskType;

        if (task instanceof Task) {
            taskType = TaskType.TASK;
        } else if (task instanceof Epic) {
            taskType = TaskType.EPIC;
        } else if (task instanceof Subtask) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
            taskType = TaskType.SUBTASK;
        } else {
            throw new ManagerSaveException("Unknown task type: " + task.getClass().getSimpleName());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                taskType.toString(),
                task.getName(),
                task.getStatus().toString(),
                task.getDescription(),
                epicId
        );
    }

    public static Task fromCSVString(String value) {
        String[] fields =  value.split(",");

        if (fields.length < 5) {
            throw new ManagerSaveException("Недостаточно полей в строке: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description, status);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, status, epicId);
            default:
                throw new ManagerSaveException("Неизвестный тип задачи: " + type);
        }
    }
}
