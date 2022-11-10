import storetasks.EpicTask;
import storetasks.NormalTask;
import storetasks.SubTask;
import storetasks.Task;

import java.util.List;

public interface TaskManager {
    List<NormalTask> getAllNormalTasks();

    List<EpicTask> getAllEpicTasks();

    List<SubTask> getAllSubTasks();

    void addNormalTask(NormalTask task);

    void addEpicTask(EpicTask task);

    void addSubTask(SubTask task);

    NormalTask getByIdNormalTask(int Id);

    EpicTask getByIdEpicTask(int Id);

    SubTask getByIdSubTask(int Id);

    boolean deleteNormalTaskById(int Id);

    boolean deleteEpicTaskById(int Id);

    boolean deleteSubTaskById(int Id);

    void upgradeNormalTask(NormalTask task);

    void upgradeEpicTask(EpicTask task);

    void upgradeSubTask(SubTask task);

    void clearAllTasks();

    List<Task> getHistory();


}
