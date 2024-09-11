package org.apache.catalina.http;

import java.util.Objects;

public class VersionOfProtocol {
    public static final String SLASH = "/";
    private final HttpProtocol httpProtocol;
    private final String httpVersion;

    public VersionOfProtocol(String value) {
        String[] values = value.split(SLASH);
        if (values.length != 2) {
            throw new IllegalArgumentException(value + ": HTTP 프로토콜 및 버전 형식이 잘못되었습니다.");
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

    @Override
    public String toString() {
        return httpProtocol + SLASH + httpVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VersionOfProtocol that = (VersionOfProtocol) o;
        return httpProtocol == that.httpProtocol && Objects.equals(httpVersion, that.httpVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpProtocol, httpVersion);
    }
}
