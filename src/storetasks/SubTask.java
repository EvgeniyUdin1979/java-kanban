package storetasks;

public class SubTask extends Task {
    EpicTask epicTask;

    public SubTask(int id, String title, String description, StatusTask status, EpicTask epicTask) {
        super(id, title, description, status);
        this.epicTask = epicTask;
    }

    public SubTask(int id, String title, StatusTask status, EpicTask epicTask) {
        this(id, title, "", status, epicTask);
    }

    private void addInEpic() {
        epicTask.addSubTaskInList(this);
    }

    public EpicTask getEpicTask() {
        return epicTask;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "epicTask=" + epicTask.getTitle() +
                "} " + super.toString();
    }
}
