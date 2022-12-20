package storetasks;

public class SubTask extends Task {
    int epicTaskId;

    public SubTask(String title, String description, StatusTask status, int epicTaskId) {
        super(title, description, status);
        this.epicTaskId = epicTaskId;
    }

    public SubTask(String title, StatusTask status, int epicTaskId) {
        this(title, "", status, epicTaskId);
    }

    public SubTask(String title, String description, StatusTask status) {
        super(title, description, status);
    }

    public int getEpicTaskId() {
        return epicTaskId;
    }

    public void setEpicTaskId(int epicTaskId) {
        this.epicTaskId = epicTaskId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicTaskId=" + epicTaskId +
                "} " + super.toString();
    }
}
