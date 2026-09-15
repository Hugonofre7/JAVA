class Box<T> {
    private T value;

    void set(T value) {
        this.value = value;
    }

    T get() {
        return value;
    }
}

class NumericBox<T extends Number> {
    private T value;

    void set(T value) {
        this.value = value;
    }

    T get() {
        return value;
    }

    double doubleValue() {
        return value.doubleValue();
    }
}

public class GenericsAndTypeErasure {
    public static void main(String[] args) {
        Box<String> stringBox = new Box<>();
        stringBox.set("Java");

        Box<Integer> intBox = new Box<>();
        intBox.set(42);

        System.out.println(stringBox.getClass() == intBox.getClass());
        System.out.println(Box.class.getTypeParameters().length);

        NumericBox<Integer> intNumBox = new NumericBox<>();
        intNumBox.set(42);
        System.out.println(intNumBox.getClass() == NumericBox.class);

        NumericBox<Double> doubleNumBox = new NumericBox<>();
        doubleNumBox.set(42.5);
        System.out.println(intNumBox.doubleValue());
        System.out.println(doubleNumBox.doubleValue());
        // NumericBox<String> stringNumBox = new NumericBox<>(); // Error de
        // compilación:
        // String no extiende Number, y el bound "T extends Number" exige que cualquier
        // tipo usado como T sea Number o una subclase suya. El compilador rechaza esto
        // ANTES de generar bytecode, sin necesidad de ejecutar nada.
    }
}
