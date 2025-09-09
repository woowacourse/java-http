package org.apache.coyote.httpResponse;

import org.apache.coyote.Cookie;

public class HttpResponse {

    private final HttpResponseHeader responseHeader;
    private final String body;

    public HttpResponse(
            final String protocol,
            final StatusCode statusCode,
            final String body
    ) {
        final StatusLine statusLine = new StatusLine(protocol, statusCode);
        this.responseHeader = new HttpResponseHeader(statusLine);
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

    public void addCookie(final Cookie cookie){
        String cookieValue = cookie.getKey()+"="+cookie.getValue();
        responseHeader.addHeader("Set-Cookie",cookieValue);
    }
}
