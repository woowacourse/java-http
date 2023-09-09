package org.apache.coyote.request;

import org.apache.coyote.common.Headers;
import org.apache.coyote.common.MediaType;
import org.apache.coyote.common.MessageBody;
import org.apache.coyote.session.Session;

import java.util.Objects;

public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final MediaType mediaType;
    private final RequestBody requestBody;

    private HttpRequest(final RequestLine requestLine,
                        final RequestHeaders requestHeaders,
                        final MediaType mediaType,
                        final RequestBody requestBody
    ) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.mediaType = mediaType;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final RequestLine requestLine, final Headers headers, final MessageBody messageBody) {
        final RequestHeaders requestHeaders = RequestHeaders.from(headers);
        final MediaType mediaType = MediaType.from(requestLine.requestPath().value());
        final RequestBody requestBody = RequestBody.from(messageBody);

        return new HttpRequest(requestLine, requestHeaders, mediaType, requestBody);
    }

    public String getCookieValue(final String cookieName) {
        return requestHeaders.getCookieValue(cookieName);
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public HttpMethod httpMethod() {
        return requestLine.httpMethod();
    }

    public RequestPath requestPath() {
        return requestLine.requestPath();
    }

    public QueryParams queryParams() {
        return requestLine.queryParams();
    }

    public Session session() {
        return requestHeaders.session();
    }

    public MediaType mediaType() {
        return mediaType;
    }

    public RequestBody requestBody() {
        return requestBody;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final HttpRequest that = (HttpRequest) o;
        return Objects.equals(requestLine, that.requestLine) && Objects.equals(requestHeaders, that.requestHeaders) && mediaType == that.mediaType && Objects.equals(requestBody, that.requestBody);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestLine, requestHeaders, mediaType, requestBody);
    }

    @Override
    public String toString() {
        return "HttpRequest{" + System.lineSeparator() +
               "    requestLine = " + requestLine + ", " + System.lineSeparator() +
               "    requestHeaders = " + requestHeaders + System.lineSeparator() +
               "    mediaType = " + mediaType + System.lineSeparator() +
               "    requestBody = " + requestBody + System.lineSeparator() +
               '}';
    }
}
