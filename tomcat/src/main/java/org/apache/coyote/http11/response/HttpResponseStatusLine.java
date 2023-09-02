package org.apache.coyote.http11.response;

public class HttpResponseStatusLine {

    private final String httpVersion;
    private final HttpResponseStatus httpResponseStatus;

    private HttpResponseStatusLine(final String httpVersion, final HttpResponseStatus httpResponseStatus) {
        this.httpVersion = httpVersion;
        this.httpResponseStatus = httpResponseStatus;
    }

    public static HttpResponseStatusLine of(final String httpVersion, final HttpResponseStatus httpResponseStatus) {
        return new HttpResponseStatusLine(httpVersion, httpResponseStatus);
    }
}
