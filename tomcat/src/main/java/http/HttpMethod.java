package http;

import java.util.Arrays;

public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod nameOf(String name) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("오! 지원하지 않은 메서드에용!"));
    }
}
