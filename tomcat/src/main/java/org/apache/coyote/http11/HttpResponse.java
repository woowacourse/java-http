package org.apache.coyote.http11;

import java.util.Objects;
import java.util.Optional;

import static java.lang.String.join;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.apache.coyote.http11.ContentType.PLAINTEXT_UTF8;

public class HttpResponse {

    public static final String CRLF = "\r\n";
    public static final String EMPTY_STRING = "";

    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String body;
    private final Headers headers;

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpResponse(final HttpStatus httpStatus, final ContentType contentType, final String body,
                        final Headers headers) {
        this.httpStatus = requireNonNull(httpStatus);
        this.contentType = ofNullable(contentType).orElse(PLAINTEXT_UTF8);
        this.body = ofNullable(body).orElse(EMPTY_STRING);
        this.headers = ofNullable(headers).orElse(new Headers());
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

class HttpResponseBuilder {

    private HttpStatus httpStatus;
    private ContentType contentType;
    private String body;
    private Headers headers = new Headers();

    public HttpResponseBuilder setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponseBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    public HttpResponseBuilder setCookie(String key, String value) {
        headers.setCookie(key, value);
        return this;
    }

    public HttpResponseBuilder sendRedirect(String location) {
        headers.put("Location", location);
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpStatus, contentType, body, headers);
    }

}
