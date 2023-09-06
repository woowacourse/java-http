package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpBody {

    private static final HttpBody EMPTY = new HttpBody("");

    private final String value;

    public HttpBody(final String value) {
        this.value = value;
    }

    public static HttpBody empty() {
        return EMPTY;
    }

    public Map<String, String> parseBodyParameters() {
        if (value.isBlank()) {
            throw new IllegalStateException("Empty body 는 key-value 로 parsing 할 수 없습니다.");
        }

        return Arrays.stream(value.split("&"))
                .map(param -> param.split("="))
                .collect(Collectors.toMap(param -> param[0], param -> param[1]));
    }
}
