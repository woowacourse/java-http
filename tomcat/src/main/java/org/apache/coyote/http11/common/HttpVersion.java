package org.apache.coyote.http11.common;

import java.util.Arrays;

public enum HttpVersion {
    ZERO_POINT_NINE("HTTP/0.9"),
    ONE_POINT_ZERO("HTTP/1.0"),
    ONE_POINT_ONE("HTTP/1.1"),
    TWO_POINT_ZERO("HTTP/2.0");

    private final String detail;

    HttpVersion(String detail) {
        this.detail = detail;
    }

    public static HttpVersion fromDetail(String detail) {
        return Arrays.stream(HttpVersion.values())
                .filter(it -> it.detail.equals(detail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No enum constant with detail: " + detail));
    }

    public String getDetail() {
        return detail;
    }
}
