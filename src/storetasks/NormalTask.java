package storetasks;

public class NormalTask extends Task {
    public NormalTask(String title, String description, StatusTask status) {
        super(title, description, status);
    }

    public NormalTask(String title, StatusTask status) {
        this(title, "", status);
    }

    @Override
    public String toString() {
        return "NormalTask{" + super.toString() + "}";
    }
}
