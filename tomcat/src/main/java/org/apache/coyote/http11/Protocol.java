package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Protocol {

    HTTP_1_1("HTTP/1.1"),
    ;

    private static final Map<String, Protocol> SUIT_CASE = Arrays.stream(Protocol.values())
            .collect(Collectors.toMap(Protocol::getValue, Function.identity()));

    private final String value;

    Protocol(final String value) {
        this.value = value;
    }

    public static Protocol from(final String target) {
        if (SUIT_CASE.containsKey(target)) {
            return SUIT_CASE.get(target);
        }
        throw new IllegalArgumentException(target + "은 지원하지 않는 프로토콜입니다.");
    }

    public String getValue() {
        return value;
    }
}
