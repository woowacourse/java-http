package nextstep.jwp.http.entity;

import java.util.Locale;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod of(String methodName) {
        return HttpMethod.valueOf(methodName.toUpperCase(Locale.ROOT));
    }
}
