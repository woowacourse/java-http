package org.apache.http;

import java.util.List;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    HTTP_3_0("HTTP/3.0");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public HttpVersion parse(String input) {
        List<HttpVersion> versions = List.of(HttpVersion.values());
        for (HttpVersion version : versions) {
            if (version.value.equals(input)) {
                return version;
            }
        }
        throw new IllegalArgumentException("존재하지 않는 HTTP 버전입니다.");
    }
}
