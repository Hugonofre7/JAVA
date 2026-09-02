public class JvmMemoryInspector {
    public static void main(String[] args) {

    }

    static void printRuntimeIdentity() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Runtime Identity: " + runtime.toString());
    }
}
