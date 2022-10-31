import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Manager {
    private final HashMap<Integer, NormalTask> normalTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;
    private int globalId = 0;

    public Manager() {
        normalTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    public List<Task> getAllTask() {
        ArrayList<Task> listTasks = new ArrayList<>(normalTasks.values());
        listTasks.addAll(epicTasks.values());
        listTasks.addAll(subTasks.values());//Добавлено только для проверки правильного удаления EpicTask.
        return listTasks;
    }

    public int getGlobalId() {
        return globalId++;
    }

    public void clearAllTasks() {
        normalTasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }


    public void deleteByIdNormalTask(int id) {
        normalTasks.remove(id);
    }

    public void deleteByIdEpicTask(int id) {
        EpicTask task = epicTasks.get(id);
        task.getSubTasks().forEach(subTask -> subTasks.remove(subTask.getId()));
        epicTasks.remove(id);
    }

    public void deleteByIdSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        EpicTask epicTask = subTask.getEpicTask();
        epicTask.removeSubTusk(subTask);
        subTasks.remove(id);
    }


    public NormalTask getByIdNormalTask(int id) {
        return normalTasks.get(id);
    }

    public EpicTask getByIdEpicTask(int id) {
        return epicTasks.get(id);
    }

    public SubTask getByIdSubTask(int id) {
        return subTasks.get(id);
    }

    public void addNormalTask(NormalTask task) {
        normalTasks.put(task.getId(), task);
    }

    public void addEpicTask(EpicTask task) {
        epicTasks.put(task.getId(), task);
    }

    public void addSubTask(SubTask task) {
        subTasks.put(task.getId(), task);
        EpicTask epicTask = task.getEpicTask();
        epicTask.addSubTaskInList(task);
    }

    public void upgradeNormalTask(NormalTask task) {
        addNormalTask(task);

    }

    public void upgradeSubTask(SubTask task) {
        subTasks.put(task.getId(), task);
        EpicTask epicTask = task.getEpicTask();
        epicTask.updateSubTask();
    }


}
