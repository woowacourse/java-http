package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidHttpRequestLineException;

import java.util.List;

public class HttpRequestLine {

    private static final String DELIMITER = " ";
    public static final int REQUEST_LINE_ELEMENT_COUNT = 3;
    public static final int REQUEST_METHOD_INDEX = 0;
    public static final int REQUEST_URI_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;

    private final RequestMethod requestMethod;
    private final String requestUri;
    private final String httpVersion;

    private HttpRequestLine(final RequestMethod requestMethod, final String requestUri, final String httpVersion) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine from(final String requestLine) {
        final List<String> parsedRequestLine = parseByDelimiter(requestLine);
        return new HttpRequestLine(
                RequestMethod.from(parsedRequestLine.get(REQUEST_METHOD_INDEX)),
                parsedRequestLine.get(REQUEST_URI_INDEX),
                parsedRequestLine.get(HTTP_VERSION_INDEX));
    }

    private static List<String> parseByDelimiter(final String requestLine) {
        final List<String> parsedRequestLine = List.of(requestLine.split(DELIMITER));
        if (parsedRequestLine.size() != REQUEST_LINE_ELEMENT_COUNT) {
            throw new InvalidHttpRequestLineException();
        }
        return parsedRequestLine;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
