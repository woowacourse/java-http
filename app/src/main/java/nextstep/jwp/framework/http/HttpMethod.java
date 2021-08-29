package nextstep.jwp.framework.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    private static final Map<String, HttpMethod> METHOD_MAP = new HashMap<>();

    static {
        for (HttpMethod httpMethod : values()) {
            METHOD_MAP.put(httpMethod.name(), httpMethod);
        }
    }

    public static HttpMethod resolve(String method) {
        final HttpMethod httpMethod = METHOD_MAP.get(method);
        if (Objects.isNull(httpMethod)) {
            throw new IllegalArgumentException(method + "는 올바르지 않은 Http Method 입니다.");
        }

        return httpMethod;
    }
}
