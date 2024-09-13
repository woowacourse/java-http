package org.apache.coyote.http11.message.request;

import org.apache.coyote.http11.message.body.HttpBody;
import org.apache.coyote.http11.message.header.HttpHeader;

public class HttpRequest {

    private static final int TOTAL_REQUEST_MESSAGE_DATA_PART_SIZE = 3;
    private static final String HTTP_REQUEST_LINE_AND_HEADER_SEPARATOR = "\n";
    private static final String HTTP_BODY_LINE_SEPARATOR = "\n\n";
    private static final int HTTP_REQUEST_LINE_AND_HEADER_INDEX = 0;
    private static final int HTTP_REQUEST_LINE_INDEX = 0;
    private static final int HTTP_HEADER_INDEX = 1;
    private static final int HTTP_BODY_INDEX = 2;

    private final HttpRequestLine requestLine;
    private final HttpHeader header;
    private final HttpBody body;

    public HttpRequest(final String httpRequestValue) {
        validateHttpRequestValueIsNullOrBlank(httpRequestValue);
        this.requestLine = parseHttpRequestLine(httpRequestValue);
        this.header = parseHttpRequestHeader(httpRequestValue);
        this.body = parseHttpBody(httpRequestValue);
    }

    private HttpRequest(
            final HttpRequestLine requestLine,
            final HttpHeader header,
            final HttpBody body
    ) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    private void validateHttpRequestValueIsNullOrBlank(final String httpRequestValue) {
        if (httpRequestValue == null || httpRequestValue.isBlank()) {
            throw new IllegalArgumentException("Http Request 값은 Null 혹은 빈 값이 될 수 없습니다. - " + httpRequestValue);
        }
    }

    private HttpRequestLine parseHttpRequestLine(final String value) {
        final String[] split = value.split("\r\n", 2);
        return new HttpRequestLine(split[0]);
    }

    private HttpHeader parseHttpRequestHeader(final String value) {
        final String headerAndBody = value.split("\r\n", 2)[1];
        final String header = headerAndBody.split("\r\n\r\n")[0];
        return new HttpHeader(header);
    }

    private HttpBody parseHttpBody(final String value) {
        final String[] headerAndBody = value.split("\r\n", 2)[1]
                .split("\r\n\r\n");

        if (headerAndBody.length < 2) {
            return new HttpBody("");
        }

        return new HttpBody(headerAndBody[1]);
    }

    private String[] splitValue(final String value) {
        String[] values = new String[TOTAL_REQUEST_MESSAGE_DATA_PART_SIZE];
        final String[] splitValue = value.split(HTTP_BODY_LINE_SEPARATOR);
        if (splitValue.length != 2) {
            values[HTTP_BODY_INDEX] = "";
        }

        final String[] requestLineAndHeader = splitValue[HTTP_REQUEST_LINE_AND_HEADER_INDEX]
                .split(HTTP_REQUEST_LINE_AND_HEADER_SEPARATOR, 2);
        values[HTTP_REQUEST_LINE_INDEX] = requestLineAndHeader[HTTP_REQUEST_LINE_INDEX];
        values[HTTP_HEADER_INDEX] = requestLineAndHeader[HTTP_HEADER_INDEX];

        return values;
    }

    public HttpRequest convertNotFoundRequest(final String notFoundRequestUri) {
        final HttpRequestLine notFoundRequestLine = new HttpRequestLine(notFoundRequestUri);
        return new HttpRequest(
                notFoundRequestLine,
                this.header,
                this.body
        );
    }

    public HttpRequestLine getRequestLine() {
        return requestLine;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public HttpBody getBody() {
        return body;
    }
}
