import java.util.ArrayList;
import java.util.List;

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

        List<Integer> intList = new ArrayList<>();
        intList.add(10);
        intList.add(20);
        intList.add(30);

        List<Double> doubleList = new ArrayList<>();
        doubleList.add(1.5);
        doubleList.add(2.5);
        doubleList.add(3.5);

        System.out.println(sumAll(intList));
        System.out.println(sumAll(doubleList));

        List<Number> numberList = new ArrayList<>();
        addIntegers(numberList);

        System.out.println(numberList);
    }

    static double sumAll(List<? extends Number> numbers) {
        double total = 0;
        for (int i = 0; i < numbers.size(); i++) {
            Number value = numbers.get(i);
            total += value.doubleValue();
        }
        return total;
    }

    static void addIntegers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }
}

// addIntegers(intList);
// SÍ compilaría: List<Integer> satisface List<? super Integer>,
// porque Integer es supertipo de sí mismo en esta relación (el límite inferior
// incluye a T).