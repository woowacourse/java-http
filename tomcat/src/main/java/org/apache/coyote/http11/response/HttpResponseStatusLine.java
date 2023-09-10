package org.apache.coyote.http11.response;

public class HttpResponseStatusLine {

    private String httpVersion;
    private HttpStatusCode statusCode;

    public HttpResponseStatusLine(final String httpVersion) {
        this(httpVersion, null);
    }


    public HttpResponseStatusLine(final String httpVersion, final HttpStatusCode statusCode) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
    }

    public void setHttpVersion(final String version) {
        this.httpVersion = version;
    }

    public void setStatusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
