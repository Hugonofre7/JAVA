
@FunctionalInterface
interface RetryPolicy {
    boolean shouldRetry(int attemptNumber);
}

public class InterfacesAndLambdas {

    public static void main(String[] args) {
        int maxAttempts = 3;
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

}
