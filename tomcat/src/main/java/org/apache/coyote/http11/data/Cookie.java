package org.apache.coyote.http11.data;

public class Cookie {
    private final String name;
    private final String value;
    private final String path;

    private Cookie(String name, String value, String path) {
        this.name = name;
        this.value = value;
        this.path = path;
    }

    private static void validate(String name, String value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Cookie name cannot be null or empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Cookie value cannot be null");
        }
    }

    public static Cookie create(
            String name,
            String value,
            String path
    ) {
        validate(name, value);
        return new Cookie(name, value, path);
    }

    public static Cookie create(
            String name,
            String value
    ) {
        return create(name, value, "/");
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public Cookie withPath(String path) {
        return new Cookie(name, value, path);
    }

    @Override
    public String toString() {
        return name + "=" + value + (path == null ? "" : "; Path=" + path);
    }
}
