package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.HttpVersion;

public class StatusLine {

    private static final String RESPONSE_SPACE = " ";
    private final HttpVersion version;
    private final HttpStatusCode statusCode;

    private StatusLine(HttpVersion version, HttpStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public static StatusLine ofHTTP11(HttpStatusCode statusCode) {
        return new StatusLine(HttpVersion.HTTP_1_1, statusCode);
    }

    public String getReponseString() {
        return version.getVersionString() + RESPONSE_SPACE + statusCode.toStatus() + RESPONSE_SPACE;
    }

    @Override
    public String toString() {
        return "StatusLine{" +
                "version=" + version +
                ", statusCode=" + statusCode +
                '}';
    }
}
