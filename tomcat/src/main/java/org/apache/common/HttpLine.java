package org.apache.common;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpLine {

    private static final String REQUEST_SPLIT_DELIMITER = " ";
    private static final String DEFAULT_RESOURCE_LOCATION = "static";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int RESOURCE_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int MIN_LINE_SIZE = 3;

    private final HttpMethod httpMethod;
    private final String requestTarget;
    private final String httpVersion;

    private HttpLine(HttpMethod httpMethod, String requestTarget, String httpVersion) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    public static HttpLine of(String line) {
        String[] splits = splitLine(line);
        validate(splits);
        String httpMethod = splits[HTTP_METHOD_INDEX];
        String requestTarget = splits[RESOURCE_INDEX];
        String httpVersion = splits[HTTP_VERSION_INDEX];
        return new HttpLine(HttpMethod.of(httpMethod), requestTarget, httpVersion);
    }

    private static String[] splitLine(String line) {
        return line.split(REQUEST_SPLIT_DELIMITER);
    }

    private static void validate(String[] splits) {
        if (splits.length < MIN_LINE_SIZE) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청입니다.");
        }
    }

    public URI convertPathToUri() {
        URL url = getClass().getClassLoader().getResource(DEFAULT_RESOURCE_LOCATION + requestTarget);
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
