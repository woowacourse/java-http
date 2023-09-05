package org.apache.coyote.http11;

import static java.lang.String.join;
import static org.apache.coyote.http11.ContentType.PLAINTEXT_UTF8;

public class HttpResponse {
    public static final String CRLF = "\r\n";
    public static final String EMPTY_STRING = "";

    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String body;
    private final Headers headers;

    public HttpResponse(final HttpStatus httpStatus) {
        this(httpStatus, PLAINTEXT_UTF8);
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType) {
        this(httpStatus, contentType, EMPTY_STRING);
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String body) {
        this(httpStatus, contentType, body, new Headers());
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String body,
                        final Headers headers) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
        this.headers = headers;
    }

    public HttpResponse setCookie(String key, String value) {
        headers.setCookie(key, value);
        return this;
    }

    public HttpResponse sendRedirect(String location) {
        headers.put("Location", location);
        return this;
    }

    private String getHeaders() {
        String joinedHeaders = headers.join();
        String body = getBodyHeaders();
        if (!joinedHeaders.isEmpty() && !body.isEmpty()) {
            return join(CRLF, joinedHeaders, body);
        }
        return joinedHeaders + body;
    }

    private String getBodyHeaders() {
        if (body.isEmpty()) {
            return EMPTY_STRING;
        }
        return join(CRLF,
                contentType.toString(),
                "Content-Length: " + body.getBytes().length + " ");
    }

    private String getBody() {
        if (body.isEmpty()) {
            return EMPTY_STRING;
        }
        return CRLF + body;
    }

    public String buildResponse() {
        String joinedHeader = getHeaders();

        String startLine = "HTTP/1.1 " + httpStatus + " ";
        String withHeader = join(CRLF, startLine, joinedHeader);

        return join(CRLF, withHeader, getBody());
    }

}
