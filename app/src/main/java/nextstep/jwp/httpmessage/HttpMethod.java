package nextstep.jwp.httpmessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public enum HttpMethod {

    GET, POST, PUT, DELETE, OPTIONS;

    private static final Map<String, HttpMethod> httpMethods = new HashMap<>();

    static {
        Arrays.stream(values())
                .forEach(it -> httpMethods.put(it.name(), it));
    }

    public static HttpMethod findBy(String httpMethod) {
        if (Objects.isNull(httpMethod)) {
            return null;
        }
        return httpMethods.get(httpMethod);
    }
}
