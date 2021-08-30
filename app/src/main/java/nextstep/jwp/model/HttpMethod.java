package nextstep.jwp.model;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

public enum HttpMethod {
    GET(GetMethod::new),
    POST(PostMethod::new);

    private final Function<String, Method> matchedMethod;

    HttpMethod(Function<String, Method> matchedMethod) {
        this.matchedMethod = matchedMethod;
    }

    public String matches(final String requestUrl, final String requestBody) {
        return this.matchedMethod.apply(requestUrl).matchFunction(requestBody);
    }

    public static HttpMethod matchHttpMethod(final String requestMethod) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(requestMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("옳지 않은 함수입니다."));
    }
}
