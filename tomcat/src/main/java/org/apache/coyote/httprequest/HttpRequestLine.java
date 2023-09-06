package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.exception.InvalidHttpRequestLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class HttpRequestLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLine.class);

    private static final String DELIMITER = " ";
    public static final int REQUEST_LINE_ELEMENT_COUNT = 3;
    public static final int REQUEST_METHOD_INDEX = 0;
    public static final int REQUEST_URI_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;

    private final RequestMethod requestMethod;
    private final RequestUri requestUri;
    private final String httpVersion;

    private HttpRequestLine(final RequestMethod requestMethod, final RequestUri requestUri, final String httpVersion) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine from(final String requestLine) {
        log.debug("Request line : {}", requestLine);
        final List<String> parsedRequestLine = parseByDelimiter(requestLine);
        return new HttpRequestLine(
                RequestMethod.from(parsedRequestLine.get(REQUEST_METHOD_INDEX)),
                RequestUri.from(parsedRequestLine.get(REQUEST_URI_INDEX)),
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

    public String getPath() {
        return requestUri.getPath();
    }

    public QueryString getQueryString() {
        return requestUri.getQueryString();
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
