package util;

public record BiValue<T, U>(T first, U second) {

    @Override
    public String toString() {
        return "BiValue{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    public boolean secondNotNull() {
        return second != null;
    }

    public boolean secondNull() {
        return second == null;
    }
}
