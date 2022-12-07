import storetasks.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    CustomLinkedList history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public void add(Task task) {
        if (!history.add(task)) {
            throw new RuntimeException("Проблема с добавлением!!!");
        }
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public void remove(Task task) {

        if (!history.remove(task)) {
            throw new RuntimeException("Проблема с удалением!!!");
        }
    }


}
