package storetasks;

import java.util.ArrayList;
import java.util.Optional;

public class EpicTask extends Task {
    ArrayList<SubTask> subTasks;

    public EpicTask(String title, String description) {
        super(title, description, StatusTask.New.ordinal());
        subTasks = new ArrayList<>();
    }

    public EpicTask(String title) {
        this(title, "");
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        updateStatusEpicTask();
    }

    public void removeSubTusk(SubTask subTask) {
        subTasks.remove(subTask);
        updateStatusEpicTask();
    }

    public void updateSubTask(SubTask subTask) {
        int taskId = subTask.getId();
        Optional<SubTask> optionalSubTask = subTasks.stream()
                .filter(x -> x.getId() == taskId)
                .findFirst();
        if (optionalSubTask.isPresent()) {
            SubTask currentTask = optionalSubTask.get();
            currentTask.update(subTask);
            updateStatusEpicTask();
        }
    }

    private void updateStatusEpicTask() {
        if (subTasks.isEmpty()) {
            setStatus(StatusTask.New.ordinal());
            return;
        }

        boolean done = true;
        boolean fresh = true;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == StatusTask.Done.ordinal()) {
                fresh = false;
            } else if (subTask.getStatus() == StatusTask.In_progress.ordinal()) {
                setStatus(StatusTask.In_progress.ordinal());
                return;
            } else {
                done = false;
            }
        }
        if (!done && !fresh) {
            super.setStatus(StatusTask.In_progress.ordinal());
        } else if (!done) {
            super.setStatus(StatusTask.New.ordinal());
        } else {
            super.setStatus(StatusTask.Done.ordinal());
        }
    }

    @Override
    public void setStatus(int status) {
        System.out.println("изменение статуса напрямую не предусмотрено!");
    }

    @Override
    public String toString() {
        return "EpicTask{" + super.toString() +
                "\n subTasks=" + subTasks.size() +
                "} ";
    }
}
