package nextstep.jwp.web.http.request;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    OPTIONS,
    HEAD,
    TRACE,
    PATCH;

    public static HttpMethod findByName(String name) {
        return Arrays.stream(values())
            .filter(method -> method.name().equalsIgnoreCase(name))
            .findAny()
            .orElseThrow(() -> new RuntimeException(
                String.format("해당하는 Http 메소드가 없습니다. 입력된 이름 : %s", name))
            );
    }
}
