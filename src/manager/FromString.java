package manager;

import task.Epic;
import task.Subtask;
import task.Task;
import utils.Status;
import utils.Type;

import java.util.ArrayList;
import java.util.List;

import static utils.Type.EPIC;
import static utils.Type.TASK;

public class FromString {
    protected static Task taskFromString(String value) {
        String[] elements = value.split(",");

        Integer id = Integer.valueOf(elements[0]);
        Type type = Type.valueOf(elements[1]);
        String title = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];

        if (type == TASK) {
            return new Task(id, type, title, description, status);
        } else if (type == EPIC) {
            return new Epic(id, type, title, description, status);
        }  else {
            Integer epicId = Integer.valueOf(elements[5]);
            return new Subtask(id, type, title, description, status, epicId);
        }
    }

    protected static List<Integer> historyFromString(String value) {
        List<Integer> historyId = new ArrayList<>();
        if(value != null) {
            String[] idString = value.split(",");
            for (String id : idString) {
                historyId.add(Integer.valueOf(id));
            }
        }
        return historyId;
    }
}
