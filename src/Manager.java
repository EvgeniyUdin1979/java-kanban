import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.util.Collection;
import java.util.HashMap;

public class Manager {
    private final HashMap<Integer, Task> tasks;

    public Manager() {
        tasks = new HashMap<>();
    }

    public Collection<Task> getAllTask() {
        return tasks.values();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void deleteById(int id) {
        tasks.remove(id);
    }


    public Task getById(int id) {
        return tasks.get(id);
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void upgradeTask(Task task) {
        if (task instanceof NormalTask) {
            NormalTask normalTask = (NormalTask) tasks.get(task.getId());
            normalTask.upgradeNormalTask(task);
        } else if (task instanceof SubTask) {
            SubTask subTask = (SubTask) tasks.get(task.getId());
            subTask.getEpicTask().updateSubTask((SubTask) task);
        }
    }
}
