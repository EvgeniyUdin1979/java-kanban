package taskmangers;

import com.google.gson.Gson;

public class Managers {
    private final static TaskManager taskManager = new HttpTaskManager("localhost");
    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
    public static Gson getDefaultGson(){
        return new Gson();
    }
}
