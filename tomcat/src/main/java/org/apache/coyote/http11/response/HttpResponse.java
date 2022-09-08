package org.apache.coyote.http11.response;

import java.util.Map.Entry;
import org.apache.coyote.http11.Headers;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.MediaType;

public class HttpResponse {

    private final String httpVersion;
    private HttpStatus httpStatus;
    private final Headers headers;
    private String body;

    public HttpResponse() {
        this("HTTP/1.1", HttpStatus.OK, new Headers(), "");
    }

    private HttpResponse(final String httpVersion, final HttpStatus httpStatus, final Headers headers,
                         final String body) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse httpStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponse addHeader(final String key, final String value) {
        headers.add(key, value);
        return this;
    }

    public HttpResponse redirect(final String location) {
        httpStatus = HttpStatus.FOUND;
        headers.add("Location", location);
        return this;
    }

    public HttpResponse setCookie(final HttpCookie httpCookie) {
        headers.add("Set-Cookie", httpCookie.joinToString());
        return this;
    }

    public HttpResponse body(final String body, final MediaType mediaType) {
        this.body = body;
        return this.addHeader("Content-Type", mediaType.getValue())
                .addHeader("Content-Length", Integer.toString(this.body.getBytes().length));
    }

    public String getValue() {
        return String.join("\r\n", toStartLine(), toHeaders(), body);
    }

    private String toStartLine() {
        return String.join(" ", httpVersion, httpStatus.getCode(), httpStatus.getMessage(), "");
    }

    private String toHeaders() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> header : headers.getValues().entrySet()) {
            stringBuilder.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(" \r\n");
        }
        return stringBuilder.toString();
    }
}
