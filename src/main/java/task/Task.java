package task;

import utils.Type;
import utils.Status;

import java.time.Duration;
import java.time.LocalDateTime;

public class Task {
    private Integer id;
    protected Type type;
    private String title;
    private String description;
    private Status status;
    private LocalDateTime startTime;
    private Duration duration;

    public Task(String title, String description) {
        type = Type.TASK;
        this.title = title;
        this.description = description;
        status = Status.NEW;
    }

    public Task(String title, String description, LocalDateTime startTime, int duration) {
        type = Type.TASK;
        this.title = title;
        this.description = description;
        status = Status.NEW;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public Task(Integer id, Type type, String title, String description, Status status, LocalDateTime startTime,
                int duration) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = Duration.ofMinutes(duration);
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusSeconds(duration.getSeconds());
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
        if (duration != null) {
            int minute = 60;
            return duration.getSeconds() / minute;
        }
        return 0;
    }

        public void setDuration(Duration duration) {
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
                id, type, title, status, description, startTime, getDuration());
    }
}
