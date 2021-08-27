package nextstep.jwp.http;

import java.util.Arrays;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod of(String name) {
        return Arrays.stream(values())
            .filter(method -> method.name().equalsIgnoreCase(name))
            .findAny()
            .orElseThrow(() -> new RuntimeException(
                String.format("해당하는 Http 메소드가 없습니다. 입력된 이름 : %s", name))
            );
    }
}
