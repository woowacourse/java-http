package org.apache.coyote.http11.httpmessage.request;

import java.util.Arrays;
import nextstep.jwp.exception.notfound.HttpVersionNotFoundException;

public enum HttpVersion {

    HTTP_11_VERSION("HTTP/1.1");
    private final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion from(String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equals(version))
                .findFirst()
                .orElseThrow(() -> new HttpVersionNotFoundException(version + " 는 처리할 수 없는 Http Version 입니다."));
    }

    public String getVersion() {
        return version;
    }
}
