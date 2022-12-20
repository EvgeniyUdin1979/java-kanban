import storetasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CustomLinkedList {
    private final HashMap<Integer, Node> historyMap;
    private int size;
    private Node head;
    private Node tail;

    public CustomLinkedList() {
        historyMap = new HashMap<>();
    }

    public void add(Task t) {
        if (historyMap.containsKey(t.getId())) {
            remove(t);
        }
        if (historyMap.isEmpty()) {//данное ветвление добавляет начальный нод и он уникален
            Node newNode = new Node(null, t, null);
            head = newNode;
            tail = newNode;
            historyMap.put(t.getId(), newNode);
            size++;
        } else {
            Node newTail = new Node(tail, t, null);
            tail.next = newTail;
            tail = newTail;
            historyMap.put(t.getId(), newTail);
            size++;
        }
    }

    public void remove(Task t) {
        if (historyMap.isEmpty() || t == null || !historyMap.containsKey(t.getId())) {
            return;
        }
        if (historyMap.size() == 1) {
            historyMap.remove(t.getId());
            size--;
            return;
        }
        Node removedNode = historyMap.get(t.getId());
        if (removedNode.entry.getId() == tail.entry.getId()) {
            Node newTail = removedNode.prev;
            newTail.next = null;
            tail = newTail;
            historyMap.remove(t.getId());
            size--;
        } else if (removedNode.entry.getId() == head.entry.getId()) {
            Node newHead = removedNode.next;
            newHead.prev = null;
            head = newHead;
            historyMap.remove(t.getId());
            size--;
        } else {
            Node prev = removedNode.prev;
            Node next = removedNode.next;
            prev.next = next;
            next.prev = prev;
            historyMap.remove(t.getId());
            size--;
        }
    }

    public List<Task> getHistory() {
        List<Task> list = new ArrayList<>();
        for (Node i = head; i != null; i = i.next) {
            list.add(i.entry);
        }
        return list;
    }

    public int size() {
        return size;
    }

    private class Node {
        Node prev;
        Node next;
        Task entry;

        public Node(Node prev, Task entry, Node next) {
            this.prev = prev;
            this.entry = entry;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return entry.equals(node.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entry);
        }
    }


}
