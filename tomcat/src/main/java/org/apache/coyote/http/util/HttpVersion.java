package org.apache.coyote.http.util;

import java.util.Arrays;
import org.apache.coyote.http.util.exception.UnsupportedHttpVersionException;

public enum HttpVersion {

    HTTP11("HTTP/1.1");

    private final String versionContent;

    HttpVersion(final String versionContent) {
        this.versionContent = versionContent;
    }

    public static HttpVersion findVersion(final String targetVersionContent) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.versionContent.equalsIgnoreCase(targetVersionContent))
                .findAny()
                .orElseThrow(UnsupportedHttpVersionException::new);
    }

    public String versionContent() {
        return versionContent;
    }
}
