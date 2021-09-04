package nextstep.jwp.web.model;

import java.util.Objects;

public class Cookie {
    public static final String NAME_NULL_EXCEPTION = "쿠키의 이름은 null일 수 없습니다.";
    public static final String VALUE_NULL_EXCEPTION = "쿠키의 이름은 null일 수 없습니다.";

    private static final String SET_MESSAGE_CHARACTER = "=";

    private final String name;
    private final String value;

    public Cookie(String name, String value) {
        validateCookie(name, value);
        this.name = name;
        this.value = value;
    }

    private void validateCookie(String name, String value) {
        Objects.requireNonNull(name, NAME_NULL_EXCEPTION);
        Objects.requireNonNull(value, VALUE_NULL_EXCEPTION);
    }

    public String toSetMessage() {
        return this.name + SET_MESSAGE_CHARACTER + this.value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}
