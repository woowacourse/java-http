package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.NoSuchElementException;

enum HttpVersion {

    HTTP_1_1("HTTP/1.1"),
    ;

    private final String versionName;

    HttpVersion(String versionName) {
        this.versionName = versionName;
    }

    public static HttpVersion from(String versionName) {
        return Arrays.stream(values())
                .filter(it -> it.versionName.equals(versionName))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    public String getVersionName() {
        return versionName;
    }
}
