import storetasks.NormalTask;
import storetasks.StatusTask;
import storetasks.Task;
import taskmangers.FileBackedTasksManager;
import taskmangers.TaskManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TestFileBackedTaskManager {
//    static {
//        Main.main(new String[]{});
//    }

    static TaskManager manager = FileBackedTasksManager.loadFromFile(Path.of("history.csv"));
//    static TaskManager manager = HttpTaskManager.loadFromServer(("localhost"));


    public static void main(String[] args) {


        getAllTasks().forEach(System.out::println);
        System.out.println("TestFileBackedTaskManager----------------------------------Восстановленая распечатка");
        printHistory();
        NormalTask normalTask = new NormalTask("NormalTest", "Проверка", StatusTask.New);
        manager.addNormalTask(normalTask);
        getAllTasks().forEach(System.out::println);
        System.out.println("TestFileBackedTaskManager----------------------------------Распечатка после добавления");
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
