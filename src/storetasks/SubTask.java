package storetasks;

public class SubTask extends Task{
    EpicTask epicTask;

    public SubTask( String title, String description, int status, EpicTask epicTask) {
        super(title, description, status);
        this.epicTask = epicTask;
        addInEpic();
    }

    public SubTask(String title, int status, EpicTask epicTask) {
        this(title, "", status, epicTask);
    }

    private void addInEpic(){
        epicTask.addSubTask(this);
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
