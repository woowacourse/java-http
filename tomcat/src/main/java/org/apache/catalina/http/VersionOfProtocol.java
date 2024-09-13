package org.apache.catalina.http;

import java.util.List;
import java.util.Objects;

public class VersionOfProtocol {

    private static final String DELIMITER = "/";
    private static final int PROTOCOL_INDEX = 0;
    private static final int VERSION_INDEX = 1;
    private static final int PART_COUNT = 2;
    private final HttpProtocol httpProtocol;
    private final String httpVersion;

    public VersionOfProtocol(String value) {
        List<String> values = List.of(value.split(DELIMITER, PART_COUNT));
        if (values.size() != PART_COUNT) {
            throw new IllegalArgumentException(value + ": HTTP 프로토콜 및 버전 형식이 잘못되었습니다.");
        }
        this.httpProtocol = HttpProtocol.of(values.get(PROTOCOL_INDEX));
        this.httpVersion = values.get(VERSION_INDEX);
    }

    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    @Override
    public String toString() {
        return httpProtocol + DELIMITER + httpVersion;
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
