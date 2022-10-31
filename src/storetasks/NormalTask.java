package storetasks;

public class NormalTask extends Task {
    public NormalTask(int id, String title, String description, StatusTask status) {
        super(id, title, description, status);
    }

    public NormalTask(int id, String title, StatusTask status) {
        this(id, title, "", status);
    }

    @Override
    public String toString() {
        return "NormalTask{" + super.toString() + "}";
    }
}
