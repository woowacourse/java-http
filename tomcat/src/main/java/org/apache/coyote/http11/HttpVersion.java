package org.apache.coyote.http11;

public enum HttpVersion {
    HTTP11("HTTP/1.1"),
    HTTP2("HTTP/2"),
    HTTP3("HTTP/3"),
    ;

    private final String httpVersionLabel;

    HttpVersion(String httpVersionLabel) {
        this.httpVersionLabel = httpVersionLabel;
    }

    public static HttpVersion fromHeaderValue(String headerValue) {
        for (HttpVersion httpVersion : HttpVersion.values()) {
            if (httpVersion.httpVersionLabel.equals(headerValue)) {
                return httpVersion;
            }
        }
        throw new IllegalArgumentException("잘못된 Protocol 입니다. : " + headerValue);
    }

    public String getResponseHeader() {
        return this.httpVersionLabel;
    }
}
