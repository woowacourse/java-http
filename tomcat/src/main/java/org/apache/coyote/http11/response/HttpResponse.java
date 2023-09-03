package org.apache.coyote.http11.response;

import java.util.stream.Stream;

public enum HttpResponse {
    OK(200),
    FOUND(302);

    private final int statusCode;

    HttpResponse(int statusCode) {
        this.statusCode = statusCode;
    }

    public static HttpResponse of(int statusCode) {
        return Stream.of(HttpResponse.values())
            .filter(httpResponse -> httpResponse.statusCode == statusCode)
            .findAny()
            .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 상태 코드 입니다."));
    }

    public int getStatusCode() {
        return statusCode;
    }
}
