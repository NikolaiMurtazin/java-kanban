package task;

import utils.Type;
import utils.Status;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private Integer id;
    protected Type type;
    private String title;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private long duration;

    public Task(String title, String description) {
        type = Type.TASK;
        this.title = title;
        this.description = description;
        status = Status.NEW;
    }

    public Task(String title, String description, LocalDateTime startTime, long duration) {
        type = Type.TASK;
        this.title = title;
        this.description = description;
        status = Status.NEW;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(Integer id, Type type, String title, String description, Status status, LocalDateTime startTime,
                long duration) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        if (duration > 0) {
            return startTime.plusMinutes(duration);
        }
        return null;
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

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", start time='" + startTime + '\'' +
                ", end time='" + getEndTime() + '\'' +
                '}';
    }

    public String toStringForFile() {
        return String.format("%s,%s,%s,%s,%s,%s,%s\n",
                id, type, title, status, description, startTime, duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return duration == task.duration && Objects.equals(id, task.id) && type == task.type && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, title, description, status, startTime, duration);
    }
}
