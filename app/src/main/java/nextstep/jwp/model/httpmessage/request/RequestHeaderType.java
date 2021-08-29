package nextstep.jwp.model.httpmessage.request;

import java.util.Arrays;

public enum RequestHeaderType {
    HOST("Host"),
    CONNECTION("Connection"),
    ACCEPT("Accept"),
    COOKIE("Cookie");

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
                .get(); // 모든 헤더를 저장하지 않아 일단 임시로 get()
    }

    public String value() {
        return value;
    }
}
