import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CustomLinkedList<T> {
    private final HashMap<T, Node> historyMap;
    private int size;
    private Node head;
    private Node tail;

    public CustomLinkedList() {
        historyMap = new HashMap<>();
    }

    public boolean add(T t) {
        if (historyMap.isEmpty()) {
            Node newNode = new Node(null, t, null);
            head = newNode;
            tail = newNode;
            historyMap.put(t, newNode);
            size++;
            return true;
        } else {
            if (historyMap.containsKey(t)) {
                Node oldNode = historyMap.get(t);
                if (oldNode.equals(tail)) {
                    return true;
                } else if (oldNode.equals(head)) {
                    Node newHead = oldNode.next;
                    newHead.prev = null;
                    head = newHead;
                    historyMap.remove(t);
                    size--;
                } else {
                    remove(t);
                }
            }
            Node newTail = new Node(tail, t, null);
            tail.next = newTail;
            tail = newTail;
            historyMap.put(t, newTail);
            size++;
            return true;
        }
    }

    public boolean remove(T t) {
        if (historyMap.isEmpty() || t == null || !historyMap.containsKey(t)) {
            return false;
        }
        Node removedNode = historyMap.get(t);
        if (removedNode.equals(tail)) {
            Node newTail = removedNode.prev;
            newTail.next = null;
            tail = newTail;
            historyMap.remove(t);
            size--;
            return true;
        } else if (removedNode.equals(head)) {
            Node newHead = removedNode.next;
            newHead.prev = null;
            head = newHead;
            historyMap.remove(t);
            size--;
        } else {
            Node prev = removedNode.prev;
            Node next = removedNode.next;
            prev.next = next;
            next.prev = prev;
            historyMap.remove(t);
            size--;
            return true;
        }
        return false;
    }

    public List<T> getHistory() {
        List<T> list = new ArrayList<>();
        for (Node i = head; i != null; i = i.next) {
            list.add(i.entry);
        }
        return list;
    }

    private class Node {
        Node prev;
        Node next;
        T entry;

        public Node(Node prev, T entry, Node next) {
            this.prev = prev;
            this.entry = entry;
            this.next = next;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(entry, node.entry);
        }

        @Override
        public int hashCode() {
            return Objects.hash(entry);
        }
    }


}
