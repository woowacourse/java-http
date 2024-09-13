package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.HttpStatusCode;

public class ResponseLine {

    private final String version;
    private HttpStatusCode statusCode;

    public ResponseLine(String version, HttpStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String toResponseString() {
        return String.format("%s %s", version, statusCode);
    }

    @Override
    public String toString() {
        return "ResponseLine{" +
                "version=" + version +
                ", statusCode=" + statusCode +
                '}';
    }
}
