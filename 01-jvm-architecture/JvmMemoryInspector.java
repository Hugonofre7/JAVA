public class JvmMemoryInspector {
    public static void main(String[] args) {
        printRuntimeIdentity();

        long[] sizes = { 1_000, 1_000_000, 100_000_000 };

        for (long size : sizes) {
            for (int run = 1; run <= 3; run++) {
                long elapsed = benchmarkTightLoop(size);
                System.out.println("iterations=" + size + " | run " + run + " | " + elapsed + " ns");
            }
        }
    }

    static void printRuntimeIdentity() {
        System.out.println("Runtime version: " + Runtime.version());

        System.out.println("JVM name: " + System.getProperty("java.vm.name"));

        System.out.println("Java home: " + System.getProperty("java.home"));

        System.out.println("PID: " + ProcessHandle.current().pid());

        System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors() + " cores");
    }

    static long benchmarkTightLoop(long iterations) {

        long startTime = System.nanoTime();

        long sum = 0;

        for (long i = 0; i < iterations; i++) {
            sum += i;
        }

        long endTime = System.nanoTime();

        return endTime - startTime;
    }

}
