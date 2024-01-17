package task;

import utils.Type;
import utils.Status;

public class Task {
    private Integer id;
    protected Type type;
    private String title;
    private String description;
    private Status status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        type = Type.TASK;
        status = Status.NEW;
    }

    public Task(Integer id, Type type, String title, String description, Status status) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s\n",
                id, type, title, status, description);
    }
}
