package org.apache.coyote.http11.response;

import java.util.Objects;

public class Http11ResponseStartLine {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpStatusCode statusCode;

    public Http11ResponseStartLine(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public static Http11ResponseStartLine defaultLine() {
        return new Http11ResponseStartLine(HttpStatusCode.OK);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Http11ResponseStartLine that = (Http11ResponseStartLine) o;
        return statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(statusCode);
    }

    @Override
    public String toString() {
        return HTTP_VERSION + " " + statusCode.toString() + " ";
    }
}
