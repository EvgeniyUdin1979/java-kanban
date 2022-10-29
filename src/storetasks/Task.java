package storetasks;

import java.util.Objects;


public abstract class Task {
    private final int id;
    private String title;
    private String description;
    private int status;

    public Task(String title, String description, int status) {
        this.id = GlobalId.getGlobalId();
        this.title = title;
        this.description = description;
        this.status = status;
    }

    protected void update(Task task) {
        this.title = task.getTitle();
        this.status = task.getStatus();
        this.description = task.getDescription();
    }

    public Task(String title, int status) {
        this(title, "", status);
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && status == task.status && Objects.equals(title, task.title) && Objects.equals(description, task.description);
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
                '}';
    }
}
