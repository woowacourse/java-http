package nextstep.jwp.http.request;

import java.util.Arrays;
import java.util.function.Predicate;
import nextstep.jwp.exception.http.HttpMethodNotFoundException;

public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod from(String method) {
        return Arrays.stream(HttpMethod.values())
            .filter(exists(method))
            .findFirst()
            .orElseThrow(HttpMethodNotFoundException::new);
    }

    private static Predicate<HttpMethod> exists(String method) {
        return httpMethod -> getName(httpMethod).equals(method);
    }

    private static String getName(HttpMethod httpMethod) {
        return httpMethod.name();
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
