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
    private HttpStatus httpStatus;
    private ContentType contentType;
    private ResponseBody body;
    private final Headers headers;

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpResponse(final HttpVersion httpVersion) {
        this.httpVersion = ofNullable(httpVersion).orElse(HTTP_1_1);
        this.headers = new Headers();
    }

    public HttpResponse(final HttpVersion httpVersion, final HttpStatus httpStatus, final ContentType contentType,
                        final ResponseBody body,
                        final Headers headers) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
        this.headers = headers;
    }

    public static HttpResponse prepareFrom(final HttpRequest request) {
        return new HttpResponse(request.getVersion());
    }


    public String buildResponse() {
        httpStatus = ofNullable(httpStatus).orElse(HttpStatus.OK); // todo: httpStatus가 없을 땐 어떻게 해야할까?
        contentType = ofNullable(contentType).orElse(PLAINTEXT_UTF8);
        body = ofNullable(body).orElse(ResponseBody.EMPTY);
        StringBuilder stringBuilder = new StringBuilder();

        String startLine = buildStartLine();
        String joinedHeader = buildHeaders();
        String messageBody = buildMessageBody();

        return stringBuilder.append(startLine).append(CRLF)
                .append(joinedHeader).append(CRLF)
                .append(messageBody)
                .toString();
    }

    private String buildStartLine() {
        return httpVersion.getVersion() + BLANK + httpStatus + BLANK;
    }

    private String buildHeaders() {
        String joinedHeaders = headers.join();
        String bodyHeaders = buildBodyHeaders();
        if (!joinedHeaders.isEmpty() && !bodyHeaders.isEmpty()) {
            return joinedHeaders + CRLF + bodyHeaders;
        }
        return joinedHeaders + bodyHeaders;
    }

    private String buildBodyHeaders() {
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

    private String buildMessageBody() {
        if (body.isEmpty()) {
            return EMPTY;
        }
        return CRLF + body.getBody();
    }


    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

}

class HttpResponseBuilder {

    private HttpVersion httpVersion;
    private HttpStatus httpStatus;
    private ContentType contentType;
    private ResponseBody body;
    private Headers headers = new Headers();

    public HttpResponseBuilder setHttpVersion(HttpVersion httpVersion) {
        this.httpVersion = httpVersion;
        return this;
    }

    public HttpResponseBuilder setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
        return this;
    }

    public HttpResponseBuilder setContentType(ContentType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponseBuilder setBody(ResponseBody body) {
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
