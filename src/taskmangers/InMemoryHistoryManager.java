package taskmangers;

import storetasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    CustomLinkedList history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public void add(Task task) {
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public int size() {
        return history.size();
    }

    @Override
    public void remove(Task task) {
        history.remove(task);
    }

    @Override
    public void clear() {
        history.clear();
    }
}
