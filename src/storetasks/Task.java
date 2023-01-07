package storetasks;

import java.time.LocalDateTime;
import java.util.Objects;


public abstract class Task {
    private int id;
    private String title;
    private String description;
    private StatusTask status;
    private LocalDateTime startTime;
    private long duration;


    public Task(String title, String description, StatusTask status, LocalDateTime startTime, long duration) {
        this.id = 0;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String title, StatusTask status) {
        this(title, "", status, null, 0);
    }
    public Task(String title, String description,StatusTask status) {
        this(title, description, status, null, 0);
    }

    public Task(String title, StatusTask status, LocalDateTime startTime) {
        this(title, "", status, startTime, 0);
    }

    public Task(String title, StatusTask status, LocalDateTime startTime, long duration) {
        this(title,"",status,startTime,duration);
    }

    public LocalDateTime getEndTime() {
        if (duration == 0){
            return startTime;
        }
        return startTime.plusMinutes(duration);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + (startTime == null? startTime : startTime.toString()) +
                ", endTime=" + (getEndTime() == null? getEndTime() : getEndTime().toString()) +
                '}';
    }
}
