package org.apache.coyote.http11.message.request;

import lombok.Getter;
import lombok.ToString;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.common.HttpMethod;

@ToString
@Getter
public class HttpRequest {

    private static final String NEW_LINE = "\r\n";
    private static final String BODY_SEPARATOR = NEW_LINE + NEW_LINE;

    private static final int MESSAGE_BEGIN_INDEX = 0;

    private final RequestLine requestLine;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public HttpRequest(final RequestLine requestLine, final HttpHeaders httpHeaders, final String requestBody) {
        this.requestLine = requestLine;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    private HttpRequest(final String requestStartLine, final String header, final String body) {
        this(RequestLine.parse(requestStartLine), HttpHeaders.parse(header), body);
    }

    public static HttpRequest parse(final String httpRequestMessage) {
        String requestStartLine = parseRequestStartLine(httpRequestMessage);
        String header = parseHeader(httpRequestMessage);
        String body = parseBody(httpRequestMessage);

        return new HttpRequest(requestStartLine, header, body);
    }

    private static String parseRequestStartLine(final String message) {
        int startLineEndIndex = message.indexOf(NEW_LINE);
        return message.substring(MESSAGE_BEGIN_INDEX, startLineEndIndex);
    }

    private static String parseHeader(final String message) {
        int headerStartIndex = message.indexOf(NEW_LINE) + NEW_LINE.length();
        int bodyStartIndex = message.indexOf(BODY_SEPARATOR);
        return message.substring(headerStartIndex, bodyStartIndex);
    }

    private static String parseBody(final String message) {
        int bodyStartIndex = message.indexOf(BODY_SEPARATOR);
        return message.substring(bodyStartIndex);
    }

    public boolean matches(final HttpMethod method, final String uri) {
        boolean methodMatches = requestLine.getMethod().equals(method);
        boolean uriMatches = requestLine.getUri().matches(uri);

        return methodMatches && uriMatches;
    }

    public RequestUri getRequestUri() {
        return requestLine.getUri();
    }
}
