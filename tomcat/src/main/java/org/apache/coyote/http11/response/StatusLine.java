package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpVersion;

public class StatusLine {
    private static HttpVersion DEFAULT_VERSION = HttpVersion.ONE_POINT_ONE;

    private final HttpVersion version;
    private final StatusCode code;

    private StatusLine(final HttpVersion version, final StatusCode code) {
        this.version = version;
        this.code = code;
    }

    public static StatusLine create(StatusCode code) {
        return new StatusLine(DEFAULT_VERSION, code);
    }

    public static StatusLine create(String version, StatusCode code) {
        return new StatusLine(HttpVersion.fromDetail(version), code);
    }

    @Override
    public String toString() {
        return String.join(" ",
                version.getDetail(),
                code.getCode(),
                code.getText(), "");
    }
}
