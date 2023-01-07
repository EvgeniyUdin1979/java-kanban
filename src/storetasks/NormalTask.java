package storetasks;

import java.time.LocalDateTime;

public class NormalTask extends Task {
    public NormalTask(String title, String description, StatusTask status) {
        super(title, description, status);
    }

    public NormalTask(String title, StatusTask status) {
        super(title, status);
    }

    public NormalTask(String title, StatusTask status, LocalDateTime startTime) {
        super(title, status, startTime);
    }

    public NormalTask(String title, String description, StatusTask status, LocalDateTime startTime, long duration) {
        super(title, description, status, startTime, duration);
    }

    public NormalTask(String title, StatusTask status, LocalDateTime startTime, long duration) {
        super(title, status, startTime, duration);
    }

    @Override
    public String toString() {
        return "NormalTask{" + super.toString() + "}";
    }
}
