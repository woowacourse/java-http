package org.apache.coyote.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLine.class);
    private static final int REQUEST_LINE_TOKENS_SIZE = 3;
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String VERSION_COMMON_PREFIX = "HTTP";

    private final HttpMethod httpMethod;
    private final HttpRequestUri httpRequestUri;
    private final String httpVersion;

    private HttpRequestLine(HttpMethod httpMethod, HttpRequestUri httpRequestUri, String httpVersion) {
        this.httpMethod = httpMethod;
        this.httpRequestUri = httpRequestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestLine decode(String requestLineString) {
        log.info("request line: {}", requestLineString);

        String[] tokens = requestLineString.split(REQUEST_LINE_DELIMITER);
        validateRequestLine(tokens);

        return new HttpRequestLine(
            HttpMethod.from(tokens[0].strip()),
            HttpRequestUri.from(tokens[1].strip()),
            tokens[2].strip()
        );
    }

    private static void validateRequestLine(String[] tokens) {
        if (tokens.length != REQUEST_LINE_TOKENS_SIZE || !tokens[2].startsWith(VERSION_COMMON_PREFIX)) {
            String requestLine = String.join(REQUEST_LINE_DELIMITER, tokens);
            log.error("request line: {}", requestLine);
        }
    }

    public HttpMethod getMethod() {
        return httpMethod;
    }

    public String getPath() {
        return httpRequestUri.getPath();
    }

    public HttpParameters getParameters() {
        return httpRequestUri.getParameters();
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
