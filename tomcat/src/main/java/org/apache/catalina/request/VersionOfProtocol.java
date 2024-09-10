package org.apache.catalina.request;

public class VersionOfProtocol {
    private final HttpProtocol httpProtocol;
    private final String httpVersion;

    public VersionOfProtocol(String value) {
        String[] values = value.split("/");
        if (values.length != 2) {
            throw new IllegalArgumentException("HTTP 프로토콜 및 버전 형식이 잘못되었습니다.");
        }
        this.httpProtocol = HttpProtocol.of(values[0]);
        this.httpVersion = values[1];
    }

    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
