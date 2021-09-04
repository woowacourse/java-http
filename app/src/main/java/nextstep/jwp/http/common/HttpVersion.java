package nextstep.jwp.http.common;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.NotAllowedHttpVersionException;

public enum HttpVersion {

    HTTP_1_1("HTTP/1.1");

    private static final Map<String, HttpVersion> HTTP_VERSIONS = new HashMap<>();

    static {
        for (HttpVersion httpVersion : values()) {
            HTTP_VERSIONS.put(httpVersion.value, httpVersion);
        }
    }

    private final String value;

    HttpVersion(String version) {
        this.value = version;
    }

    public static HttpVersion matchOf(String requestVersion) {
        if (noExistHttpVersion(requestVersion)) {
            throw new NotAllowedHttpVersionException();
        }

        return HTTP_VERSIONS.get(requestVersion);
    }

    private static boolean noExistHttpVersion(String requestVersion) {
        return !HTTP_VERSIONS.containsKey(requestVersion);
    }

    public String getValue() {
        return value;
    }
}
