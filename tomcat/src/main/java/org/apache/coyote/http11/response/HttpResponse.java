package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.header.ResponseHeader;

public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final Headers headers;
    private final String version;
    private final byte[] body;

    public HttpResponse(final HttpStatusCode statusCode, final Headers headers, final String version, final byte[] body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.version = version;
        this.body = body;
    }

    public String getStatusLine() {
        return String.join(" ", version, statusCode.getCodeString(), statusCode.getReasonPhrase()) + " ";
    }

    public Headers getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public void addCookie(final String cookie, final String value) {
        headers.put(ResponseHeader.SET_COOKIE, cookie + "=" + value);
    }
}
