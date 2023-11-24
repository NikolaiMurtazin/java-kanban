import java.util.ArrayList;

class Epic extends Task {
    private ArrayList<Integer> subtasks;

    public Epic(Integer id, String title, String description) {
        super(id, title, description);
        this.subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                "} " + super.toString();
    }
}
