package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpHeaders;

public class HttpResponse {

    private final String httpVersion;
    private final int statusCode;
    private final String reasonPhrase;
    private final HttpHeaders header;
    private final byte[] body;

    public HttpResponse(
            String httpVersion,
            int statusCode,
            String reasonPhrase,
            HttpHeaders header,
            byte[] body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.header = header;
        this.body = body;
    }

    public String getStatusLine() {
        return httpVersion + " " + statusCode + " " + reasonPhrase;
    }

    public int getContentLength() {
        return body.length;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public HttpHeaders getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }
}
