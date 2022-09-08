package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.util.StringUtils.SPACE;

import java.util.Arrays;
import org.apache.coyote.http11.exception.HttpMethodNotFoundException;

public enum HttpMethod {

    GET("GET"),
    POST("POST"),
    ;

    private static final int HTTP_METHOD_INDEX = 0;

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod from(String requestFirstLine) {
        String value = requestFirstLine.split(SPACE)[HTTP_METHOD_INDEX];
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.value.equals(value))
                .findAny()
                .orElseThrow(HttpMethodNotFoundException::new);
    }

    public boolean isGet() {
        return this == GET;
    }

    public boolean isPost() {
        return this == POST;
    }
}
