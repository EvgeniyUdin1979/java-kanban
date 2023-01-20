import org.junit.jupiter.api.Test;
import storetasks.NormalTask;
import storetasks.Task;
import taskmangers.HistoryManager;
import taskmangers.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static storetasks.StatusTask.New;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();


    @Test
    void add() {
        NormalTask normalTask1 = new NormalTask("normalTask1", New);
        NormalTask normalTask2 = new NormalTask("normalTask2", New);
        NormalTask normalTask3 = new NormalTask("normalTask3", New);
        NormalTask normalTask4 = new NormalTask("normalTask4", New);
        NormalTask normalTask5 = new NormalTask("normalTask5", New);
        normalTask1.setId(1);
        normalTask2.setId(2);
        normalTask3.setId(3);
        normalTask4.setId(4);
        normalTask5.setId(5);
        assertTrue(historyManager.getHistory().isEmpty(), "Лист истории не пустой");
        historyManager.add(normalTask1);
        historyManager.add(normalTask2);
        historyManager.add(normalTask3);
        historyManager.add(normalTask4);
        historyManager.add(normalTask5);
        List<Task> historyTasks = historyManager.getHistory();
        assertEquals(5, historyTasks.size(), "История возвращает лист не правильного размера");
        historyManager.add(normalTask1);
        assertNotEquals(historyManager.getHistory().get(0), normalTask1, "При повторном добавлении Таска находится на первой позиции!");
    }

    @Test
    void getHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "Лист истории не пустой");
        add();
        assertFalse(historyManager.getHistory().isEmpty(), "Лист истории пустой");
    }

    @Test
    void size() {
        assertEquals(0, historyManager.size(), "Лист истории не пустой size() возвращает " + historyManager.size());
        add();
        assertEquals(5, historyManager.size(), "Лист истории не равен 5 size() возвращает " + historyManager.size());
    }

    @Test
    void remove() {
        assertDoesNotThrow(() -> historyManager.remove(new NormalTask("test", New)),
                "Исключений при удаление из пустой истории быть недолжно");
        assertDoesNotThrow(() -> historyManager.remove(null),
                "Исключений при удаление null быть недолжно");
        add();
        NormalTask normalTask6 = new NormalTask("normalTask6", New);
        normalTask6.setId(6);
        assertDoesNotThrow(() -> historyManager.remove(normalTask6),
                "Исключений при удаление Таска кторого нет в списке быть недолжно" +
                        "(Сделано для исключения ошибок при удалении епика у которого есть сабки не попавшие в историю)");
        NormalTask normalTask2 = new NormalTask("normalTask2", New);
        normalTask2.setId(2);
        historyManager.remove(normalTask2);
        assertEquals(4, historyManager.size(), "Таск не удален из истории");
        assertFalse(historyManager.getHistory().contains(normalTask2), "Таск не удален из истории");

    }

    @Test
    void removeFirst() {
        add();
        NormalTask normalTask2 = new NormalTask("normalTask2", New);
        normalTask2.setId(2);
        historyManager.remove(normalTask2);
        assertNotEquals(normalTask2, historyManager.getHistory().get(0), "Первый элемент истории не удален");
    }

    @Test
    void removeInTheCenter() {
        add();
        NormalTask normalTask4 = new NormalTask("normalTask4", New);
        normalTask4.setId(4);
        historyManager.remove(normalTask4);
        assertNotEquals(normalTask4, historyManager.getHistory().get(2), "Элемент в середине листа истории не удален");
    }
    @Test
    void removeLast() {
        add();
        NormalTask normalTask1 = new NormalTask("normalTask1", New);
        normalTask1.setId(1);
        historyManager.remove(normalTask1);
        assertThrows(IndexOutOfBoundsException.class,
                () ->historyManager.getHistory().get(4),
                "Последний элемент не удален");
        assertFalse( historyManager.getHistory().contains(normalTask1),
                "Последний элемент листа истории не удален");
    }

}