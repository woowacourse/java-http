package org.apache.coyote.http11;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static org.apache.coyote.http11.ContentType.PLAINTEXT_UTF8;
import static org.apache.coyote.http11.HttpVersion.HTTP_1_1;

public class HttpResponse {

    public static final String CRLF = "\r\n";
    public static final String EMPTY = "";
    public static final String BLANK = " ";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final ResponseBody body;
    private final Headers headers;

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpResponse(final HttpVersion httpVersion,  HttpStatus httpStatus, final ContentType contentType, final String body,
                        final Headers headers) {
        this.httpVersion = ofNullable(httpVersion).orElse(HTTP_1_1);
        this.httpStatus = requireNonNull(httpStatus);
        this.contentType = ofNullable(contentType).orElse(PLAINTEXT_UTF8);
        this.body = ResponseBody.from(ofNullable(body).orElse(EMPTY));
        this.headers = ofNullable(headers).orElse(new Headers());
    }

    public String buildResponse() {
        StringBuilder stringBuilder = new StringBuilder();

        String startLine = getStartLine();
        String joinedHeader = getHeaders();
        String messageBody = getBody();

        return stringBuilder.append(startLine).append(CRLF)
                .append(joinedHeader).append(CRLF)
                .append(messageBody)
                .toString();
    }

    private String getStartLine() {
        return httpVersion.getVersion() + BLANK + httpStatus + BLANK;
    }

    private String getHeaders() {
        String joinedHeaders = headers.join();
        String bodyHeaders = getBodyHeaders();
        if (!joinedHeaders.isEmpty() && !bodyHeaders.isEmpty()) {
            return joinedHeaders + CRLF + bodyHeaders;
        }
        return joinedHeaders + bodyHeaders;
    }

    private String getBodyHeaders() {
        if (body.isEmpty()) {
            return EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        return builder.append(contentType)
                .append(CRLF)
                .append(body.getContentLength())
                .append(BLANK)
                .toString();
    }

    private String getBody() {
        if (body.isEmpty()) {
            return EMPTY;
        }
        return CRLF + body.getBody();
    }


}

class HttpResponseBuilder {

    private HttpVersion httpVersion;
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

    public HttpResponseBuilder addCookie(HttpCookie httpCookie) {
        headers.put("Set-Cookie", httpCookie.toString());
        return this;
    }

    public HttpResponseBuilder sendRedirect(String location) {
        headers.put("Location", location);
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(httpVersion, httpStatus, contentType, body, headers);
    }

}
