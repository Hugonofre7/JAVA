
record CpuSnapshot(String nodeId, double usagePercent, long timestampMs) {
}

public class RecordsAndPolymorphism {

    public static void main(String[] args) {
        CpuSnapshot snapshot1 = new CpuSnapshot("node-01", 75.5, 1000);
        CpuSnapshot snapshot2 = new CpuSnapshot("node-01", 75.5, 1000);

        System.out.println(snapshot1.equals(snapshot2));
        System.out.println(snapshot1 == snapshot2);

        System.out.println(snapshot1);
        System.out.println(snapshot1.nodeId());
    }

}
