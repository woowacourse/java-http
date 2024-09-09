package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatusCode;

public class ResponseLine {

    private final String version;
    private final HttpStatusCode statusCode;

    public ResponseLine(String version, HttpStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public String toResponseString() {
        return version + " " + statusCode.toString() + " ";
    }

    @Override
    public String toString() {
        return "ResponseLine{" +
                "version=" + version +
                ", statusCode=" + statusCode +
                '}';
    }
}
