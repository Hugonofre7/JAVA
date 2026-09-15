class Box<T> {
    private T value;

    void set(T value) {
        this.value = value;
    }

    T get() {
        return value;
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
    }
}