package nextstep.jwp.webserver;

import java.util.Arrays;
import java.util.Locale;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod from(String method) {
        String methodName = method.toUpperCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(methodName))
                .findAny()
                .orElseThrow(RuntimeException::new);
    }
}
