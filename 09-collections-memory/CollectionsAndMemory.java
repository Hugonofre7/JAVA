import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CollectionsAndMemory {
    public static void main(String[] args) {
        List<Integer> arrayList = new ArrayList<>();
        List<Integer> linkedList = new LinkedList<>();

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
}