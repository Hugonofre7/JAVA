public class ConcurrencyAndThreads {
    static int counter = 0;

    static void incrementCounter(int times) {
        for (int i = 0; i < times; i++) {
            counter++;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> incrementCounter(100_000);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Final counter: " + counter);
    }
}