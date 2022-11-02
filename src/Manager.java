import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static storetasks.StatusTask.*;

public class Manager {
    private final HashMap<Integer, NormalTask> normalTasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;
    private int globalId = 1;

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

    public List<NormalTask> getAllNormalTask() {
        return new ArrayList<>(normalTasks.values());
    }

    public List<EpicTask> getAllEpicTask() {
        return new ArrayList<>(epicTasks.values());
    }

    public List<SubTask> getAllSubTask() {
        return new ArrayList<>(subTasks.values());
    }

    private int getGlobalId() {
        return globalId++;
    }

    public void clearAllTasks() {
        normalTasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }


    public boolean deleteByIdNormalTask(int id) {
        if (!normalTasks.containsKey(id)) {
            return false;
        }
        normalTasks.remove(id);
        return true;
    }

    public boolean deleteByIdEpicTask(int id) {
        if (!epicTasks.containsKey(id)) {
            return false;
        }
        EpicTask task = epicTasks.get(id);
        task.getSubTasks().forEach(subTasks::remove);
        epicTasks.remove(id);
        return true;
    }

    public boolean deleteByIdSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            return false;
        }
        SubTask subTask = subTasks.get(id);
        EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
        epicTask.removeSubTusk(id);
        subTasks.remove(id);
        updateStatusEpicTask(epicTask);
        return true;
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
        if (task.getId() != 0) {
            throw new RuntimeException(NormalTask.class.getSimpleName() + "Id отличается от начального: " + task.getId());
        }
        task.setId(getGlobalId());
        normalTasks.put(task.getId(), task);
    }

    public void addEpicTask(EpicTask task) {
        if (task.getId() != 0) {
            throw new RuntimeException(EpicTask.class.getSimpleName() + "Id отличается от начального: " + task.getId());
        }
        task.setId(getGlobalId());
        epicTasks.put(task.getId(), task);
    }

    public void addSubTask(SubTask task) {
        if (task.getId() != 0) {
            throw new RuntimeException(SubTask.class.getSimpleName() + " Id отличается от начального: " + task.getId());
        }
        task.setId(getGlobalId());
        subTasks.put(task.getId(), task);
        EpicTask epicTask = epicTasks.get(task.getEpicTaskId());
        epicTask.addSubTaskInList(task.getId());
        updateStatusEpicTask(epicTask);
    }

    public void upgradeNormalTask(NormalTask task) {
        normalTasks.put(task.getId(), task);
    }

    public void upgradeSubTask(SubTask task) {
        subTasks.put(task.getId(), task);
        EpicTask epicTask = epicTasks.get(task.getEpicTaskId());
        updateStatusEpicTask(epicTask);
    }

    public void upgradeEpicTask(EpicTask task) {
        if (!epicTasks.containsKey(task.getId())) {
            throw new RuntimeException("нет такого " + EpicTask.class.getSimpleName());
        }
        //Если приходит эпик в котором отличается набор сабов - те что отличаются надо удалить иначе,
        // они останутся не привязаны ни к одному эпику, на вебинаре была идея переиспользовать сабы,
        // но я считаю это излишним.
        EpicTask oldTask = epicTasks.get(task.getId());
        oldTask.getSubTasks().forEach(subTaskId -> {
            if (!task.getSubTasks().contains(subTaskId)) {
                deleteByIdSubTask(subTaskId);
            }
        });
        updateStatusEpicTask(task);
        epicTasks.put(task.getId(), task);
    }

    private void updateStatusEpicTask(EpicTask task) {
        if (task.getSubTasks().isEmpty()) {
            task.setStatus(New);
            return;
        }

        boolean done = true;
        boolean fresh = true;
        for (Integer subTaskId : task.getSubTasks()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getStatus() == Done) {
                fresh = false;
            } else if (subTask.getStatus() == In_progress) {
                task.setStatus(In_progress);
                return;
            } else {
                done = false;
            }
        }
        if (!done && !fresh) {
            task.setStatus(In_progress);
        } else if (!done) {
            task.setStatus(New);
        } else {
            task.setStatus(Done);
        }
    }


}
