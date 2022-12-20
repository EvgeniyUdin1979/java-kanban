import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static storetasks.StatusTask.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, NormalTask> normalTasks;
    protected final HashMap<Integer, EpicTask> epicTasks;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager history = Managers.getDefaultHistory();
    private int globalId = 1;

    public InMemoryTaskManager() {
        normalTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
    }

    @Override
    public List<NormalTask> getAllNormalTasks() {
        return new ArrayList<>(normalTasks.values());
    }

    @Override
    public List<EpicTask> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    private int getGlobalId() {
        return globalId++;
    }

    @Override
    public void clearAllTasks() {
        normalTasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }

    @Override
    public boolean deleteNormalTaskById(int id) {
        if (!normalTasks.containsKey(id)) {
            return false;
        }
        history.remove(normalTasks.get(id));
        normalTasks.remove(id);
        return true;
    }

    @Override
    public boolean deleteEpicTaskById(int id) {
        if (!epicTasks.containsKey(id)) {
            return false;
        }
        EpicTask task = epicTasks.get(id);
        task.getSubTasks().forEach(key -> {
            history.remove(subTasks.get(key));
            subTasks.remove(key);
        });
        history.remove(task);
        epicTasks.remove(id);
        return true;
    }

    @Override
    public boolean deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            return false;
        }
        SubTask subTask = subTasks.get(id);
        EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
        epicTask.removeSubTusk(id);
        subTasks.remove(id);
        history.remove(subTask);
        updateStatusEpicTask(epicTask);
        return true;
    }

    @Override
    public NormalTask getByIdNormalTask(int id) {
        NormalTask normalTask = normalTasks.get(id);
        history.add(normalTask);
        return normalTask;
    }

    @Override
    public EpicTask getByIdEpicTask(int id) {
        EpicTask epicTask = epicTasks.get(id);
        history.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getByIdSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        history.add(subTask);
        return subTask;
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

    @Override
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
                deleteSubTaskById(subTaskId);
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

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }


}
