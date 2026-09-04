
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
