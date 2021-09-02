package nextstep.jwp.http.common;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.value.equals(version))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("존재하지 않는 HTTP 버전입니다.(%s)", version))
                );
    }

    public String getValue() {
        return value;
    }
}
