public class JvmMemoryInspector {
    public static void main(String[] args) {
        printRuntimeIdentity();

        long elapsed = benchmarkTightLoop(100_000_000);

        System.out.println("Elapsed time: " + elapsed + " ns");

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
