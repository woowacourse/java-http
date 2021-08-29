package nextstep.jwp.model.httpMessage;

import java.util.Arrays;

public enum HttpHeaderType {
    LOCATION("Location"),
    CONTENT_TYPE(ContentType.NAME),
    CONTENT_LENGTH("Content-Length");

    private final String value;

    HttpHeaderType(String value) {
        this.value = value;
    }

    public static boolean contains(String name) {
        return Arrays.stream(values())
                .anyMatch(type -> type.value.equals(name));
    }

    public static HttpHeaderType of(String value) {
        return Arrays.stream(values())
                .filter(headerType -> headerType.value.equals(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 헤더 타입이 존재하지 않습니다."));
    }

    public String value() {
        return value;
    }
}
