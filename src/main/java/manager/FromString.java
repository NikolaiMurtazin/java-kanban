package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import utils.TaskStatus;
import utils.TypeOfTasksForDirectoryTask;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static utils.TypeOfTasksForDirectoryTask.EPIC;
import static utils.TypeOfTasksForDirectoryTask.TASK;

public class FromString {
    protected static Task taskFromString(String value) {
        String[] elements = value.split(",");

        Integer id = Integer.valueOf(elements[0]);
        TypeOfTasksForDirectoryTask type = TypeOfTasksForDirectoryTask.valueOf(elements[1]);
        String title = elements[2];
        TaskStatus status = TaskStatus.valueOf(elements[3]);
        String description = elements[4];
        LocalDateTime startTime;
        if (elements[5].equals("null")) {
            startTime = null;
        } else {
            startTime = LocalDateTime.parse(elements[5]);
        }
        int duration = Integer.parseInt(elements[6]);

        if (type == TASK) {

            return new Task(id, type, title, description, status, startTime, duration);
        } else if (type == EPIC) {
            return new Epic(id, type, title, description, status, startTime, duration);
        } else {
            Integer epicId = Integer.valueOf(elements[7]);
            return new Subtask(id, type, title, description, status, startTime, duration, epicId);
        }
    }

    protected static List<Integer> historyFromString(String value) {
        List<Integer> historyId = new ArrayList<>();
        if (value != null) {
            String[] idString = value.split(",");
            for (String id : idString) {
                historyId.add(Integer.valueOf(id));
            }
        }
        return historyId;
    }
}
