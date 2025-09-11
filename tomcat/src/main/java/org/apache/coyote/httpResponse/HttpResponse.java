package org.apache.coyote.httpResponse;

import org.apache.coyote.Cookie;

public class HttpResponse {

    private final HttpResponseHeader responseHeader;
    private String body;

    public HttpResponse() {
        this.responseHeader = new HttpResponseHeader();
        this.body = null;
    }

    public void updateStatusLine(final String protocol, final StatusCode statusCode) {
        responseHeader.updateStatusLine(protocol, statusCode);
    }

    public void updateBody(final String body) {
        this.body = body;
    }

    public String getResponse() {
        final String headers = responseHeader.getHeaders();
        return String.join(
                "\r\n",
                headers,
                "",
                body
        );
    }

    public void addHeader(
            final String key,
            final String value
    ) {
        responseHeader.addHeader(key, value);
    }

    public void addCookie(final Cookie cookie) {
        String cookieValue = cookie.getKey() + "=" + cookie.getValue();
        responseHeader.addHeader("Set-Cookie", cookieValue);
    }
}
