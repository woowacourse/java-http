package org.apache.coyote.http11;

public enum HttpVersion {
    V_11("HTTP/1.1"),
    V_2("HTTP/2"),
    V_3("HTTP/3"),
    ;

    private final String versionLabel;

    HttpVersion(String versionLabel) {
        this.versionLabel = versionLabel;
    }

    public static HttpVersion fromHeaderValue(String headerValue) {
        for (HttpVersion httpVersion : HttpVersion.values()) {
            if (httpVersion.versionLabel.equals(headerValue)) {
                return httpVersion;
            }
        }
        throw new IllegalArgumentException("잘못된 Http version 입니다. : " + headerValue);
    }

    public String getResponseHeader() {
        return this.versionLabel;
    }
}
