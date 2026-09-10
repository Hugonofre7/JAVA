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

}
