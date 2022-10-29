package storetasks;

public class NormalTask extends Task {
    public NormalTask(String title, String description, int status) {
        super(title, description, status);
    }

    public void upgradeNormalTask(Task task) {
        update(task);
    }

    public NormalTask(String title, int status) {
        this(title, "", status);
    }
}
