package manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import server.KVTaskClient;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient taskClient;
    private static final Gson gson = new Gson();

    public HttpTaskManager(String url, KVTaskClient taskClient) {
        super(url);
        this.taskClient = taskClient;
    }

    @Override
    public void save() {
        taskClient.put("tasks", gson.toJson(allTasks.values()));
        taskClient.put("epics", gson.toJson(allEpics.values()));
        taskClient.put("subtasks", gson.toJson(allSubtasks.values()));
        List<Integer> historyIds = getHistory()
                .stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        taskClient.put("history", gson.toJson(historyIds));
    }

    public void loadFromServer() {
        loadTasks("tasks");
        loadTasks("epics");
        loadTasks("subtasks");
        loadHistory();
    }

    private void loadTasks(String key) {
        JsonElement jsonElement = JsonParser.parseString(taskClient.load(key));
        JsonArray jsonTasksArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonTasksArray) {
            Task task;
            Epic epic;
            Subtask subtask;
            switch (key) {
                case "tasks":
                    task = gson.fromJson(element.getAsJsonObject(), Task.class);
                    allTasks.put(task.getId(), task);
                    addTaskToPrioritizedList(task);
                    break;
                case "epics":
                    epic = gson.fromJson(element.getAsJsonObject(), Epic.class);
                    allEpics.put(epic.getId(), epic);
                    break;
                case "subtasks":
                    subtask = gson.fromJson(element.getAsJsonObject(), Subtask.class);
                    allSubtasks.put(subtask.getId(), subtask);
                    addTaskToPrioritizedList(subtask);
                    break;
                default:
                    System.out.println("Не удается загрузить задачи");
                    return;
            }
        }
    }

    private void loadHistory() {
        JsonElement jsonElement = JsonParser.parseString(taskClient.load("history"));
        JsonArray jsonHistoryArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonHistoryArray) {
            int id = element.getAsInt();
            if (allTasks.containsKey(id)) {
                historyManager.add(allTasks.get(id));
            } else if (allEpics.containsKey(id)) {
                historyManager.add(allEpics.get(id));
            } else if (allSubtasks.containsKey(id)) {
                historyManager.add(allSubtasks.get(id));
            }
        }
    }
}
