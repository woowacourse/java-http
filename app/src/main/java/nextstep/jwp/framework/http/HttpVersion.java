package nextstep.jwp.framework.http;

import java.util.HashMap;
import java.util.Map;

public enum HttpVersion {
    HTTP_0_9("HTTP/0.9"),
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1");

    private static final Map<String, HttpVersion> HTTP_VERSION_MAP = new HashMap<>();

    static {
        for (HttpVersion httpVersion : values()) {
            HTTP_VERSION_MAP.put(httpVersion.version, httpVersion);
        }
    }

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion resolve(String version) {
        final HttpVersion httpVersion = HTTP_VERSION_MAP.get(version.toUpperCase());
        if (httpVersion == null) {
            throw new IllegalArgumentException("존재하지 않는 HTTP Version 입니다.");
        }
        return httpVersion;
    }

    public String getVersion() {
        return version;
    }
}
