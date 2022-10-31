package storetasks;

import java.util.ArrayList;

import static storetasks.StatusTask.*;

public class EpicTask extends Task {
    ArrayList<SubTask> subTasks;

    public EpicTask(int id, String title, String description) {
        super(id, title, description, New);
        subTasks = new ArrayList<>();
    }

    public EpicTask(int id, String title) {
        this(id, title, "");
    }

    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void addSubTaskInList(SubTask subTask) {
        subTasks.add(subTask);
        updateStatusEpicTask();
    }

    public void removeSubTusk(SubTask subTask) {
        subTasks.remove(subTask);
        updateStatusEpicTask();
    }

    public void updateSubTask() {
        updateStatusEpicTask();
    }

    private void updateStatusEpicTask() {
        if (subTasks.isEmpty()) {
            setStatus(New);
            return;
        }

        boolean done = true;
        boolean fresh = true;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus() == Done) {
                fresh = false;
            } else if (subTask.getStatus() == In_progress) {
                setStatus(In_progress);
                return;
            } else {
                done = false;
            }
        }
        if (!done && !fresh) {
            super.setStatus(In_progress);
        } else if (!done) {
            super.setStatus(New);
        } else {
            super.setStatus(Done);
        }
    }

    @Override
    public void setStatus(StatusTask status) {
        throw new RuntimeException("изменение статуса напрямую не предусмотрено!");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EpicTask{")
                .append(super.toString())
                .append("\nsubTasks=");
        if (subTasks.isEmpty()){
            sb.append(0);
        }else {
            subTasks.forEach(subTask -> {
                sb.append("\n").append(subTask.toString());
            });
        }
        sb.append("}");
        return sb.toString();
    }
}
