package storetasks;

import java.util.ArrayList;

import static storetasks.StatusTask.New;

public class EpicTask extends Task {
    ArrayList<Integer> subTasks;

    public EpicTask(String title, String description) {
        super(title, description, New);
        subTasks = new ArrayList<>();
    }

    public EpicTask(String title) {
        this(title, "");
    }

    public EpicTask(String title, String description, StatusTask status) {
        super(title, description, status);
        subTasks = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTasks() {
        return new ArrayList<>(subTasks);
    }

    public void addSubTaskInList(int subTaskId) {
        subTasks.add(subTaskId);
    }

    public void removeSubTusk(Integer subTaskId) {
        subTasks.remove(subTaskId);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EpicTask{")
                .append(super.toString())
                .append("\nsubTasks=");
        if (subTasks.isEmpty()) {
            sb.append(0);
        } else {
            subTasks.forEach(subTask -> sb.append("\n").append("SubTask # ").append(subTask.toString()));
        }
        sb.append("}");
        return sb.toString();
    }
}
