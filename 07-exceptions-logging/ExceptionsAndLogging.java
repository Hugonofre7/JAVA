import java.io.FileWriter;
import java.io.IOException;

public class ExceptionsAndLogging {
    public static void main(String[] args) {
        try {
            riskyOperation(-5);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());

        }

        try {
            riskyOperation(0);
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println(riskyOperation(10));
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }

        try {
            writeLogEntry("app.log", "entrada de prueba");
        } catch (LogWriteException e) {

        }

        try {
            writeLogEntry("/ruta/que/no/existe/app.log", "entrada inválida");
        } catch (LogWriteException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().getMessage());

        }

    }

    static int riskyOperation(int input) {
        try {
            if (input < 0) {
                throw new IllegalArgumentException("input negativo: " + input);
            }
            if (input == 0) {
                throw new ArithmeticException("división por cero simulada");
            }
            return 100 / input;

        } finally {
            System.out.println("finally ejecutado para input=" + input);

        }
    }

    static void writeLogEntry(String path, String entry) throws LogWriteException {
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(entry + "\n");
        } catch (IOException e) {
            throw new LogWriteException("Error al escribir en el log: " + path, e);

        }

    }

}

class LogWriteException extends Exception {
    LogWriteException(String message, Throwable cause) {
        super(message, cause);
    }

}
