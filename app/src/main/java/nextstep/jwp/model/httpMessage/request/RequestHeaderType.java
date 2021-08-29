package nextstep.jwp.model.httpMessage.request;

import java.util.Arrays;

public enum RequestHeaderType {
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    COOKIE("Cookie"),
    AUTHORIZATION("Authorization");

    private final String value;

    RequestHeaderType(String value) {
        this.value = value;
    }

    public static boolean contains(String name) {
        return Arrays.stream(values())
                .anyMatch(type -> type.value.equals(name));
    }

    public static RequestHeaderType of(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Not found request Header type (input : " + value + ")"));
    }

    public String value() {
        return value;
    }
}
