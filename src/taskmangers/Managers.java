package taskmangers;

import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefault() {
        return FileBackedTasksManager.loadFromFile(Path.of("history.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
