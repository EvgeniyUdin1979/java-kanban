import storetasks.Task;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestFileBackedTaskManager {
    static TaskManager manager = FileBackedTasksManager.loadFromFile(Path.of("history.csv"));

    public static void main(String[] args) {
        Main.main(new String[]{});
        getAllTasks().forEach(System.out::println);
        System.out.println("TestFileBackedTaskManager----------------------------------Восстановленая распечатка");
        printHistory();
    }

    static List<Task> getAllTasks() {
        List<Task> allTasks = new ArrayList<>(manager.getAllEpicTasks());
        allTasks.addAll(manager.getAllNormalTasks());
        allTasks.addAll(manager.getAllSubTasks());
        return allTasks;
    }

    private static void printHistory() {
        int count = 0;
        for (Task taskInHistory : manager.getHistory()) {

            System.out.print(count++ + " : id - " + taskInHistory.getId() + " | ");
        }
        System.out.println("***history**");
    }
}
