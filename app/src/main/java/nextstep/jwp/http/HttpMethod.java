package nextstep.jwp.http;

import java.util.Arrays;
import java.util.function.Function;

public enum HttpMethod {
    GET(GetMethod::new),
    POST(PostMethod::new);

    private final Function<HttpRequest, Method> matchedMethod;

    HttpMethod(Function<HttpRequest, Method> matchedMethod) {
        this.matchedMethod = matchedMethod;
    }

    public static HttpMethod matchHttpMethod(final String requestMethod) {
        return Arrays.stream(values())
                .filter(method -> method.name().equals(requestMethod))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("옳지 않은 함수입니다."));
    }

    public String matches(final HttpRequest httpRequest) {
        return this.matchedMethod.apply(httpRequest).matchFunction();
    }
}
