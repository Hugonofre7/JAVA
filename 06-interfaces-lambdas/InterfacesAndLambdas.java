import java.util.function.Predicate;
import java.util.function.Function;

@FunctionalInterface
interface RetryPolicy {
    boolean shouldRetry(int attemptNumber);
}

public class InterfacesAndLambdas {

    public static void main(String[] args) {
        benchmarkLambdaCreation(1);
        benchmarkLambdaCreation(1_000_000);

        int maxAttempts = 3;

        Predicate<Integer> lambdaVersion = n -> n % 2 == 0;
        Predicate<Integer> methodRefVersion = InterfacesAndLambdas::isEven;
        Predicate<Integer> greaterThanTen = n -> n > 10;
        Predicate<Integer> combined = methodRefVersion.and(greaterThanTen);

        System.out.println(lambdaVersion.test(4));
        System.out.println(methodRefVersion.test(4));
        System.out.println(greaterThanTen.test(15));
        System.out.println(combined.test(4));
        System.out.println(combined.test(12));

        RetryPolicy policy = (attemptNumber) -> attemptNumber < maxAttempts;

        for (int i = 0; i < 5; i++) {
            if (policy.shouldRetry(i)) {
                System.out.println("Reintentando... intento " + i);
            } else {
                System.out.println("Abortando en intento " + i);
                break;
            }
        }
        Function<String, String> toUpperCase = s -> s.toUpperCase();
        System.out.println(toUpperCase.apply("node-01"));

        Function<String, String> toLowerCase = s -> s.toLowerCase();
        Function<String, String> addServer = s -> "Server_" + s;
        Function<String, String> pipeline = toUpperCase.andThen(addServer);
        System.out.println(toLowerCase.apply("NODE-01"));
        System.out.println(addServer.apply("node-01"));
        System.out.println(pipeline.apply("node-01"));
    }

    static boolean isEven(int n) {
        return n % 2 == 0;
    }

    static void benchmarkLambdaCreation(int n) {
        long start = System.nanoTime();
        for (int i = 0; i < n; i++) {
            Runnable r = () -> {
            };
            r.run();
        }
        long elapsed = System.nanoTime() - start;
        System.out.println("n=" + n + " | total=" + elapsed + " ns | promedio=" + (elapsed / n) + " ns/iter");
    }

}
