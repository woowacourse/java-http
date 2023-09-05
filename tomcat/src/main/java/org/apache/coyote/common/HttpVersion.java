package org.apache.coyote.common;

import org.apache.exception.UnMatchedHttpVersionException;

import java.util.HashMap;
import java.util.Map;

public enum HttpVersion {
    HTTP11("HTTP/1.1");

    private static final Map<String, HttpVersion> map = new HashMap<>();

    static {
        for (HttpVersion httpVersion : HttpVersion.values()) {
            map.put(httpVersion.version, httpVersion);
        }
    }

    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion get(String version) {
        if (!map.containsKey(version)) {
            throw new UnMatchedHttpVersionException();
        }
        return map.get(version);
    }
}
