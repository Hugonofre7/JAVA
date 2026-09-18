import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CollectionsAndMemory {
    public static void main(String[] args) {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

        for (int i = 0; i < 100000; i++) {
            arrayList.add(i);
            linkedList.add(i);
        }

        long arrayListTime = benchmarkRandomAccess(arrayList, 10_000);
        long linkedListTime = benchmarkRandomAccess(linkedList, 10_000);
        System.out.println("ArrayList: " + arrayListTime + " ns");
        System.out.println("LinkedList: " + linkedListTime + " ns");

        long badHashTime = benchmarkHashMapInsertion(20_000, true);
        long normalHashTime = benchmarkHashMapInsertion(20_000, false);
        System.out.println("HashMap with bad hash: " + badHashTime + " ns");
        System.out.println("HashMap with normal hash: " + normalHashTime + " ns");

        RingBuffer<String> logs = new RingBuffer<>(3);
        logs.add("A");
        logs.add("B");
        logs.add("C");
        logs.add("D");
        logs.add("E");
        System.out.println("Size: " + logs.size());

        for (int i = 0; i < logs.size(); i++) {
            System.out.println(logs.get(i));
        }

    }

    static long benchmarkRandomAccess(List<Integer> list, int accesses) {
        Random random = new Random();
        long start = System.nanoTime();
        for (int i = 0; i < accesses; i++) {
            int index = random.nextInt(list.size());
            list.get(index);
        }
        long end = System.nanoTime();
        return end - start;
    }

    static long benchmarkHashMapInsertion(int n, boolean useBadHash) {
        Map<Object, Integer> map = new HashMap<>();
        long start = System.nanoTime();
        if (useBadHash) {
            for (int i = 0; i < n; i++) {
                map.put(new BadKey(i), i);
            }
        } else {
            for (int i = 0; i < n; i++) {
                map.put(i, i);
            }
        }
        long end = System.nanoTime();
        return end - start;
    }
}

class BadKey {
    private final int id;

    BadKey(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BadKey other)) {
            return false;
        }

        return id == other.id;
    }
}

class RingBuffer<T> {
    private final Object[] buffer;
    private int head = 0;
    private int size = 0;
    private final int capacity;

    RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new Object[capacity];
    }

    void add(T item) {
        if (size < capacity) {
            int index = (head + size) % capacity;
            buffer[index] = item;
            size++;
        }

        else {
            buffer[head] = item;
            head = (head + 1) % capacity;
        }
    }

    @SuppressWarnings("unchecked")
    T get(int index) {
        int physicalIndex = (head + index) % capacity;
        return (T) buffer[physicalIndex];
    }

    int size() {
        return size;
    }
}