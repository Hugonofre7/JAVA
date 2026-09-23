public class ConcurrencyAndThreads {
    static int counter = 0;
}

static void incrementCounter(int times) {
    for (int i = 0; i < times; i++) {
        counter++;
    }
}

public static void main(String[] args) {
    Runnable task = () -> incrementCounter(100_000);
