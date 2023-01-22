package taskmangers;

import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;
import taskmangers.erros.ManagerIllegalIdException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import static storetasks.StatusTask.*;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, NormalTask> normalTasks;
    protected final HashMap<Integer, EpicTask> epicTasks;
    protected final HashMap<Integer, SubTask> subTasks;
    protected final HistoryManager history = Managers.getDefaultHistory();
    protected final TreeSet<Task> prioritizedTasks;
    protected int globalId = 1;

    public InMemoryTaskManager() {
        normalTasks = new HashMap<>();
        epicTasks = new HashMap<>();
        subTasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>((o1, o2) -> {
            LocalDateTime time1;
            if (o1.getStartTime() == null) {
                time1 = LocalDateTime.MAX.minusMinutes(o1.getId());
            } else {
                time1 = o1.getStartTime();
            }
            LocalDateTime time2;
            if (o2.getStartTime() == null) {
                time2 = LocalDateTime.MAX.minusMinutes(o2.getId());
            } else {
                time2 = o2.getStartTime();
            }
            return time1.compareTo(time2);
        });
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
        prioritizedTasks.clear();
        history.clear();
    }

    @Override
    public void deleteNormalTaskById(int id) {
        if (!normalTasks.containsKey(id)) {
            throw new IllegalArgumentException("Попытка удалить Нормала по несуществующему id");
        }
        NormalTask normalTask = normalTasks.get(id);
        prioritizedTasks.remove(normalTask);
        history.remove(normalTask);
        normalTasks.remove(id);
    }

    @Override
    public void deleteEpicTaskById(int id) {
        if (!epicTasks.containsKey(id)) {
            throw new IllegalArgumentException("Попытка удалить Эпика по несуществующему id");
        }
        EpicTask epicTask = epicTasks.get(id);
        epicTask.getSubTasks().forEach(key -> {
            SubTask subTask = subTasks.get(key);
            prioritizedTasks.remove(subTask);
            history.remove(subTask);
            subTasks.remove(key);
        });
        prioritizedTasks.remove(epicTask);
        history.remove(epicTask);
        epicTasks.remove(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        if (!subTasks.containsKey(id)) {
            throw new IllegalArgumentException("Попытка удалить Саба по несуществующему id");
        }
        SubTask subTask = subTasks.get(id);
        EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
        epicTask.removeSubTusk(id);
        subTasks.remove(id);
        prioritizedTasks.remove(subTask);
        history.remove(subTask);
        updateStatusEpicTask(epicTask);
    }

    @Override
    public NormalTask getByIdNormalTask(int id) {
        if (!normalTasks.containsKey(id)) {
            throw new IllegalArgumentException("Попытка получить Нормала по несуществующему id");
        }
        NormalTask normalTask = normalTasks.get(id);
        history.add(normalTask);
        return normalTask;
    }

    @Override
    public EpicTask getByIdEpicTask(int id) {
        if (!epicTasks.containsKey(id)) {
            throw new IllegalArgumentException("Попытка получить Эпика по несуществующему id");
        }
        EpicTask epicTask = epicTasks.get(id);
        history.add(epicTask);
        return epicTask;
    }

    @Override
    public SubTask getByIdSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            throw new IllegalArgumentException("Попытка получить Саба по несуществующему id");
        }
        SubTask subTask = subTasks.get(id);
        history.add(subTask);
        return subTask;
    }

    public void addNormalTask(NormalTask normalTask) {
        if (normalTask.getId() != 0) {
            throw new RuntimeException(NormalTask.class.getSimpleName() + "Id отличается от начального: " + normalTask.getId());
        }
        if (checkingTheIntersectionOfTimeSegments(normalTask)) {
            throw new IllegalArgumentException("Пересечение отрезков времени!");
        }
        normalTask.setId(getGlobalId());
        normalTasks.put(normalTask.getId(), normalTask);
        prioritizedTasks.add(normalTask);
    }

    public void addEpicTask(EpicTask epicTask) {
        if (epicTask.getId() != 0) {
            throw new RuntimeException(EpicTask.class.getSimpleName() + "Id отличается от начального: " + epicTask.getId());
        }
        epicTask.setId(getGlobalId());
        epicTasks.put(epicTask.getId(), epicTask);
        prioritizedTasks.add(epicTask);
        changeEpicTaskPriority(epicTask);
    }

    public void addSubTask(SubTask subTask) {
        if (subTask.getId() != 0) {
            throw new RuntimeException(SubTask.class.getSimpleName() + " Id отличается от начального: " + subTask.getId());
        }
        if (checkingTheIntersectionOfTimeSegments(subTask)) {
            throw new IllegalArgumentException("Пересечение отрезков времени!");
        }
        subTask.setId(getGlobalId());
        subTasks.put(subTask.getId(), subTask);
        EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
        epicTask.addSubTaskInList(subTask.getId());
        updateStatusEpicTask(epicTask);
        changeEpicTaskPriority(epicTask);
        prioritizedTasks.add(subTask);
    }

    public void upgradeNormalTask(NormalTask normalTask) {
        if (!normalTasks.containsKey(normalTask.getId())) {
            throw new ManagerIllegalIdException("Попытка обновления Нормала по несуществующему id");
        }
        NormalTask oldNormalTask = normalTasks.get(normalTask.getId());
        prioritizedTasks.remove(normalTasks.get(normalTask.getId()));
        if (checkingTheIntersectionOfTimeSegments(normalTask)) {
            prioritizedTasks.add(oldNormalTask);
            throw new IllegalArgumentException("Пересечение отрезков времени!");
        }
        prioritizedTasks.add(normalTask);
        normalTasks.put(normalTask.getId(), normalTask);

    }

    @Override
    public void upgradeSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            throw new ManagerIllegalIdException("Попытка обновления Саба по несуществующему id");
        }
        SubTask oldSubTask = subTasks.get(subTask.getId());
        prioritizedTasks.remove(subTasks.get(subTask.getId()));
        EpicTask epicTask = epicTasks.get(subTask.getEpicTaskId());
        epicTask.removeSubTusk(subTask.getId());
        changeEpicTaskPriority(epicTask);
        if (checkingTheIntersectionOfTimeSegments(subTask)) {
            epicTask.addSubTaskInList(subTask.getId());
            prioritizedTasks.add(oldSubTask);
            changeEpicTaskPriority(epicTask);
            throw new IllegalArgumentException("Пересечение отрезков времени!");
        }
        epicTask.addSubTaskInList(subTask.getId());
        prioritizedTasks.add(subTask);
        subTasks.put(subTask.getId(), subTask);
        updateStatusEpicTask(epicTask);

    }

    public void upgradeEpicTask(EpicTask epicTask) {
        if (!epicTasks.containsKey(epicTask.getId())) {
            throw new ManagerIllegalIdException("Попытка обновления Эпика по несуществующему id");
        }
        //Если приходит эпик в котором отличается набор сабов - те что отличаются надо удалить иначе,
        // они останутся не привязаны ни к одному эпику, на вебинаре была идея переиспользовать сабы,
        // но я считаю это излишним.
        EpicTask oldEpicTask = epicTasks.get(epicTask.getId());
        oldEpicTask.getSubTasks().forEach(subTaskId -> {
            if (!epicTask.getSubTasks().contains(subTaskId)) {
                deleteSubTaskById(subTaskId);
            }
        });
        prioritizedTasks.remove(oldEpicTask);
        changeEpicTaskPriority(epicTask);
        epicTasks.put(epicTask.getId(), epicTask);
    }

    private void updateStatusEpicTask(EpicTask epicTask) {
        if (epicTask.getSubTasks().isEmpty()) {
            epicTask.setStatus(New);
            return;
        }

        boolean done = true;
        boolean fresh = true;
        for (Integer subTaskId : epicTask.getSubTasks()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getStatus() == Done) {
                fresh = false;
            } else if (subTask.getStatus() == In_progress) {
                epicTask.setStatus(In_progress);
                return;
            } else {
                done = false;
            }
        }
        if (!done && !fresh) {
            epicTask.setStatus(In_progress);
        } else if (!done) {
            epicTask.setStatus(New);
        } else {
            epicTask.setStatus(Done);
        }
    }

    protected void changeEpicTaskPriority(EpicTask epicTask) {
        prioritizedTasks.remove(epicTask);
        updateStartTimeAndDurationEpicTask(epicTask);
        prioritizedTasks.add(epicTask);
    }

    private void updateStartTimeAndDurationEpicTask(EpicTask epicTask) {
        List<Integer> listSubId = epicTask.getSubTasks();
        if (listSubId.isEmpty()) {
            epicTask.setStartTime(null);
            return;
        }
        LocalDateTime lowerStartTime = LocalDateTime.MAX;
        LocalDateTime higherEndTime = LocalDateTime.MIN;
        for (Integer subId : listSubId) {
            SubTask subTask = subTasks.get(subId);
            if (subTask.getStartTime() != null && subTask.getStartTime().isBefore(lowerStartTime)) {
                lowerStartTime = LocalDateTime.from(subTask.getStartTime());
            }
            if (subTask.getEndTime() != null && subTask.getEndTime().isAfter(higherEndTime)) {
                higherEndTime = LocalDateTime.from(subTask.getEndTime());
            }
        }
        if (lowerStartTime.equals(LocalDateTime.MAX)) {
            epicTask.setStartTime(null);
        } else {
            epicTask.setStartTime(lowerStartTime.minusSeconds(1));
        }
        if (higherEndTime.equals(LocalDateTime.MIN)) {
            epicTask.setDuration(0);
        } else {
            epicTask.setDuration(Duration.between(lowerStartTime, higherEndTime).toMinutes());
        }
    }

    private boolean checkingTheIntersectionOfTimeSegments(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }
        Task ceilingTask = prioritizedTasks.ceiling(task);
        Task lowerTask = prioritizedTasks.lower(task);
        if (ceilingTask == null && lowerTask == null) {
            return false;
        }
        LocalDateTime startTimeTestingTask = formatTimeForChecking(task, task.getStartTime());
        LocalDateTime endTimeTestingTask = formatTimeForChecking(task, task.getEndTime());

        if (lowerTask == null) {
            return checkingLowerTask(ceilingTask, startTimeTestingTask, endTimeTestingTask);

        } else if (ceilingTask == null) {
            return checkingCeilingTask(lowerTask, startTimeTestingTask);
        } else {
            return checkingLowerTask(ceilingTask, startTimeTestingTask, endTimeTestingTask) ||
                    checkingCeilingTask(lowerTask, startTimeTestingTask);
        }
    }

    private boolean checkingCeilingTask(Task lowerTask, LocalDateTime startTimeTestingTask) {
        if (lowerTask instanceof EpicTask && lowerTask.getStartTime() != null) {
            lowerTask = getSubTaskFromEpicTask((EpicTask) lowerTask);
        }
        LocalDateTime endTimeLowerTask = formatTimeForChecking(lowerTask, lowerTask.getEndTime());
        return startTimeTestingTask.isBefore(endTimeLowerTask);
    }

    private boolean checkingLowerTask(Task ceilingTask, LocalDateTime startTimeTestingTask, LocalDateTime endTimeTestingTask) {
        if (ceilingTask instanceof EpicTask && ceilingTask.getStartTime() != null) {
            ceilingTask = getSubTaskFromEpicTask((EpicTask) ceilingTask);
        }
        LocalDateTime startTimeCeilingTask = formatTimeForChecking(ceilingTask, ceilingTask.getStartTime());

        if (startTimeCeilingTask
                .equals(startTimeTestingTask)) {
            return true;
        }
        return endTimeTestingTask.isAfter(startTimeCeilingTask);
    }

    private Task getSubTaskFromEpicTask(EpicTask epicTask) {
        for (Integer subTaskId : epicTask.getSubTasks()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (subTask.getStartTime().equals(epicTask.getStartTime().plusSeconds(1))) {
                return subTask;
            }
        }
        throw new RuntimeException("не нашел сабтаску");
    }

    private LocalDateTime formatTimeForChecking(Task task, LocalDateTime ldt) {
        if (task == null) {
            return LocalDateTime.MAX;
        } else if (ldt == null) {
            return LocalDateTime.MAX.minusMinutes(task.getId()).withSecond(0).withNano(0);
        }
        return ldt.withSecond(0).withNano(0);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

}
