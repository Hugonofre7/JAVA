import java.util.function.Predicate;

@FunctionalInterface
interface RetryPolicy {
    boolean shouldRetry(int attemptNumber);
}

public class InterfacesAndLambdas {

    public static void main(String[] args) {
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
    }

    static boolean isEven(int n) {
        return n % 2 == 0;
    }

}
