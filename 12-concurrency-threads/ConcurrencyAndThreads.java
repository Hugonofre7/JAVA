import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrencyAndThreads {
    static int counter = 0;
    static int synchronizedCounter = 0;

    static final Object lockA = new Object();
    static final Object lockB = new Object();

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

    static void acquireAThenB() {
        synchronized (lockA) {
            System.out.println(Thread.currentThread().getName() + " tiene lockA, esperando lockB...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (lockB) {
                System.out.println(Thread.currentThread().getName() + " tiene ambos locks");
            }
        }
    }

    static void acquireBThenA() {
        synchronized (lockB) {
            System.out.println(Thread.currentThread().getName() + " tiene lockB, esperando lockA...");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            synchronized (lockA) {
                System.out.println(Thread.currentThread().getName() + " tiene ambos locks");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        /*
         * // Retos 1 y 2 comentados temporalmente para una salida más limpia
         * Runnable task = () -> incrementCounter(100_000);
         * Runnable synchronizedTask = () -> incrementSynchronizedCounter(100_000);
         * 
         * Thread[] threads = new Thread[10];
         * for (int i = 0; i < threads.length; i++) {
         * threads[i] = new Thread(task);
         * }
         * 
         * long unsynchronizedStart = System.nanoTime();
         * for (Thread thread : threads) {
         * thread.start();
         * }
         * 
         * for (Thread thread : threads) {
         * thread.join();
         * }
         * 
         * long unsynchronizedEnd = System.nanoTime();
         * long unsynchronizedTime = unsynchronizedEnd - unsynchronizedStart;
         * 
         * Thread[] synchronizedThreads = new Thread[10];
         * 
         * for (int i = 0; i < synchronizedThreads.length; i++) {
         * synchronizedThreads[i] = new Thread(synchronizedTask);
         * }
         * 
         * long synchronizedStart = System.nanoTime();
         * 
         * for (Thread thread : synchronizedThreads) {
         * thread.start();
         * }
         * 
         * for (Thread thread : synchronizedThreads) {
         * thread.join();
         * }
         * 
         * long synchronizedEnd = System.nanoTime();
         * long synchronizedTime = synchronizedEnd - synchronizedStart;
         * 
         * System.out.println("Final counter: " + counter);
         * System.out.println("Unsynchronized time: " + unsynchronizedTime + " ns");
         * System.out.println("Final synchronized counter: " + synchronizedCounter);
         * System.out.println("Synchronized time: " + synchronizedTime + " ns");
         * 
         * System.out.println("\n--- Ejecutando Reto 3: ExecutorService ---");
         * runWithThreadPool(3, 10);
         */
        System.out.println("--- Ejecutando Reto 4: Simulación de Deadlock ---");

        // Reto 1 de Diagnostics: Pausa de 20 segundos para dar tiempo de correr jps
        System.out.println("Pausa de 20 segundos... Ejecuta 'jps -l' en otra terminal AHORA.");
        Thread.sleep(20_000);

        Thread thread1 = new Thread(ConcurrencyAndThreads::acquireAThenB, "Hilo-1");
        Thread thread2 = new Thread(ConcurrencyAndThreads::acquireBThenA, "Hilo-2");

        thread1.start();
        thread2.start();

        thread1.join(3000);
        thread2.join(3000);

        System.out.println("\n--- Estado después del timeout de 3 segundos ---");
        System.out.println("Hilo 1 sigue vivo (Deadlock): " + thread1.isAlive());
        System.out.println("Hilo 2 sigue vivo (Deadlock): " + thread2.isAlive());
    }
}