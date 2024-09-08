package org.apache.coyote.http11.request.startLine;

import java.util.Objects;

public class HttpVersion {

    private final String httpVersion;

    public HttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HttpVersion that = (HttpVersion) o;
        return Objects.equals(httpVersion, that.httpVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(httpVersion);
    }
}
