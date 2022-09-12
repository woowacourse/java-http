package nextstep.jwp.view;

import java.util.stream.Stream;

public enum Url {

    ROOT("/"),
    LOGIN("/login"),
    REGISTER("/register"),
    UNAUTHORIZED("/401")
    ;

    private final String value;

    Url(final String value) {
        this.value = value;
    }

    public static Url of(final String value) {
        return Stream.of(values())
                .filter(v -> v.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No such url"));
    }

    public String getValue() {
        return value;
    }
}
