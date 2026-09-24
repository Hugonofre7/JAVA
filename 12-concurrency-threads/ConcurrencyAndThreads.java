import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrencyAndThreads {
    static int counter = 0;
    static int synchronizedCounter = 0;

    static void incrementCounter(int times) {
        for (int i = 0; i < times; i++) {
            counter++;
        }
    }

    static synchronized void incrementSynchronized() {
        synchronizedCounter++;
    }

    static void incrementSynchronizedCounter(int times) {
        for (int i = 0; i < times; i++) {
            incrementSynchronized();
        }
    }

    static void runWithThreadPool(int poolSize, int numTasks) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        for (int i = 1; i <= numTasks; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Hilo: " + Thread.currentThread().getName() + " | Tarea: " + taskId);
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable task = () -> incrementCounter(100_000);
        Runnable synchronizedTask = () -> incrementSynchronizedCounter(100_000);

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(task);
        }

        long unsynchronizedStart = System.nanoTime();
        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        long unsynchronizedEnd = System.nanoTime();
        long unsynchronizedTime = unsynchronizedEnd - unsynchronizedStart;

        Thread[] synchronizedThreads = new Thread[10];

        for (int i = 0; i < synchronizedThreads.length; i++) {
            synchronizedThreads[i] = new Thread(synchronizedTask);
        }

        long synchronizedStart = System.nanoTime();

        for (Thread thread : synchronizedThreads) {
            thread.start();
        }

        for (Thread thread : synchronizedThreads) {
            thread.join();
        }

        long synchronizedEnd = System.nanoTime();
        long synchronizedTime = synchronizedEnd - synchronizedStart;

        System.out.println("Final counter: " + counter);
        System.out.println("Unsynchronized time: " + unsynchronizedTime + " ns");
        System.out.println("Final synchronized counter: " + synchronizedCounter);
        System.out.println("Synchronized time: " + synchronizedTime + " ns");

        System.out.println("\n--- Ejecutando Reto 3: ExecutorService ---");
        runWithThreadPool(3, 10);
    }
}