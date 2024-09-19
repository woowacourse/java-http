package org.apache.coyote.http11.message.request;

import java.util.Optional;

import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeaders;

public class HttpRequest {

    private static final String HTTP_REQUEST_MESSAGE_LINE_SEPARATOR = "\r\n";
    private static final String HTTP_REQUEST_BODY_AND_OTHERS_SEPARATOR = "\r\n\r\n";
    private static final int HTTP_REQUEST_LINE_INDEX = 0;
    private static final int HTTP_REQUEST_HEADERS_AND_BODY_INDEX = 1;
    private static final int HTTP_REQUEST_HEADERS_INDEX = 0;
    private static final int HTTP_REQUEST_BODY_INDEX = 1;

    private final String message;
    private final HttpRequestLine requestLine;
    private final HttpHeaders headers;

    public HttpRequest(final String httpRequestMessage) {
        validateHttpRequestMessageIsNullOrBlank(httpRequestMessage);
        this.message = httpRequestMessage;
        this.requestLine = parseHttpRequestLine(httpRequestMessage);
        this.headers = parseHttpRequestHeaders(httpRequestMessage);
    }

    private void validateHttpRequestMessageIsNullOrBlank(final String httpRequestMessage) {
        if (httpRequestMessage == null || httpRequestMessage.isBlank()) {
            throw new IllegalArgumentException("Http Request Message는 null 혹은 빈 값이 입력될 수 없습니다. - " + httpRequestMessage);
        }
    }

    private HttpRequestLine parseHttpRequestLine(final String httpRequestMessage) {
        final String httpRequestLine = httpRequestMessage
                .split(HTTP_REQUEST_MESSAGE_LINE_SEPARATOR, 2)[HTTP_REQUEST_LINE_INDEX];

        return new HttpRequestLine(httpRequestLine);
    }

    private HttpHeaders parseHttpRequestHeaders(final String httpRequestMessage) {
        final String httpRequestHeadersAndBody = httpRequestMessage
                .split(HTTP_REQUEST_MESSAGE_LINE_SEPARATOR, 2)[HTTP_REQUEST_HEADERS_AND_BODY_INDEX];
        final String httpRequestHeaders = httpRequestHeadersAndBody
                .split(HTTP_REQUEST_BODY_AND_OTHERS_SEPARATOR, 2)[HTTP_REQUEST_HEADERS_INDEX];

        return new HttpHeaders(httpRequestHeaders);
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Optional<HttpBody> getBody() {
        final String[] httpRequestHeadersAndBody = message
                .split(HTTP_REQUEST_MESSAGE_LINE_SEPARATOR, 2)[HTTP_REQUEST_HEADERS_AND_BODY_INDEX]
                .split(HTTP_REQUEST_BODY_AND_OTHERS_SEPARATOR);

        if (httpRequestHeadersAndBody.length < 2) {
            return Optional.empty();
        }

        final String httpRequestBody = httpRequestHeadersAndBody[HTTP_REQUEST_BODY_INDEX];
        return Optional.of(new HttpBody(httpRequestBody));
    }
}
