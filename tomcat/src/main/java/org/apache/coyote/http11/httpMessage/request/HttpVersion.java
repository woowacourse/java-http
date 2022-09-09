package org.apache.coyote.http11.httpmessage.request;

public enum HttpVersion {

    HTTP_11_VERSION("HTTP/1.1");
    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion of(String version) {
        validate(version);
        return HTTP_11_VERSION;
    }

    private static void validate(String version) {
        if (!"HTTP/1.1".equals(version)) {
            throw new IllegalArgumentException("처리할 수 없는 Http Version 입니다.");
        }
    }

    public String getVersion() {
        return version;
    }
}
