package nextstep.jwp.framework.common;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum HttpVersion {
    HTTP_1_1("HTTP/1.1");

    private static final Map<String, HttpVersion> mappings = new HashMap<>();

    static {
        for (HttpVersion httpVersion : values()) {
            mappings.put(httpVersion.value, httpVersion);
        }
    }

    private final String value;

    HttpVersion(String value) {
        this.value = value;
    }

    public static HttpVersion from(String version) {
        return mappings.computeIfAbsent(
                version,
                key -> {
                    throw new NoSuchElementException(String.format("존재하지 않는 HTTP 버전입니다.(%s)", key));
                }
        );
    }

    public String getValue() {
        return value;
    }
}
