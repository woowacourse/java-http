package org.apache.coyote.httprequest;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.httprequest.exception.InvalidHttpRequestLineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class HttpRequestLine {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String DELIMITER = " ";
    public static final int REQUEST_LINE_ELEMENT_COUNT = 3;
    public static final int REQUEST_METHOD_INDEX = 0;
    public static final int REQUEST_URI_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;

    private final RequestMethod requestMethod;
    private final Path requestUri;
    private final String httpVersion;

    private HttpRequestLine(final RequestMethod requestMethod, final Path requestUri, final String httpVersion) {
        this.requestMethod = requestMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine from(final String requestLine) {
        final List<String> parsedRequestLine = parseByDelimiter(requestLine);
        return new HttpRequestLine(
                RequestMethod.from(parsedRequestLine.get(REQUEST_METHOD_INDEX)),
                convertToPath(parsedRequestLine.get(REQUEST_URI_INDEX)),
                parsedRequestLine.get(HTTP_VERSION_INDEX));
    }

    private static List<String> parseByDelimiter(final String requestLine) {
        final List<String> parsedRequestLine = List.of(requestLine.split(DELIMITER));
        if (parsedRequestLine.size() != REQUEST_LINE_ELEMENT_COUNT) {
            throw new InvalidHttpRequestLineException();
        }
        return parsedRequestLine;
    }

    private static Path convertToPath(final String pathText) {
        try {
            return Paths.get(HttpRequestLine.class.getResource(pathText).toURI());
        } catch (URISyntaxException | NullPointerException e) {
            log.error(e.getMessage(), e);
            return Path.of("/");
        }
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    public Path getRequestUri() {
        return requestUri;
    }
}
