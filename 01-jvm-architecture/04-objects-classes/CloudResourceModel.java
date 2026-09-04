
public class CloudResourceModel {
    public static void main(String[] args) {
        CloudNode original = new CloudNode("node-01", 16, 64);
        CloudNode alias = original;
        alias.cpuCores = 32;

        System.out.println("original.cpuCores = " + original.cpuCores);
        System.out.println("alias.cpuCores = " + alias.cpuCores);

        CloudNode copy = new CloudNode("node-01", 16, 64);
        copy.cpuCores = 64;
        System.out.println("copy.cpuCores = " + copy.cpuCores);

        compareMemoryFootprint(1_000_000);
    }

    static void compareMemoryFootprint(int n) {
        CloudNode[] nodes = new CloudNode[n];
        for (int i = 0; i < n; i++) {

            nodes[i] = new CloudNode("node-" + i, 16, 64);

        }
        int[] rawCpuCores = new int[n];

        long rawCpuBytes = (long) n * Integer.BYTES;

        long cloudNodeObjectBytes = 32L;

        long cloudNodeObjectsBytes = (long) n * cloudNodeObjectBytes;

        long cloudNodeReferencesBytes = (long) n * 4L;

        long cloudNodeTotalBytes = cloudNodeObjectsBytes + cloudNodeReferencesBytes;

        double rawCpuMiB = (double) rawCpuBytes / (1024 * 1024);

        double cloudNodeMiB = (double) cloudNodeTotalBytes / (1024 * 1024);

        System.out.println("n = " + n);

        System.out.println("int[] rawCpuCores ≈ " + rawCpuMiB + " MiB");

        System.out.println("CloudNode[] nodes ≈ " + cloudNodeMiB + " MiB");

    }

}

class CloudNode {

    String id;
    int cpuCores;
    int memoryGb;

    CloudNode(String id, int cpuCores, int memoryGb) {

        this.id = id;
        this.cpuCores = cpuCores;
        this.memoryGb = memoryGb;
    }

}
