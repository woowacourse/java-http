package org.apache.request;

import org.apache.common.HttpMethod;

public class HttpStartLine {

    private static final String REQUEST_SPLIT_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int RESOURCE_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int MIN_LINE_SIZE = 3;

    private final HttpMethod method;
    private final String path;
    private final String version;

    private HttpStartLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpStartLine of(String line) {
        String[] splits = splitLine(line);
        validate(splits);
        String httpMethod = splits[HTTP_METHOD_INDEX];
        String requestTarget = splits[RESOURCE_INDEX];
        String httpVersion = splits[HTTP_VERSION_INDEX];
        return new HttpStartLine(HttpMethod.of(httpMethod), requestTarget, httpVersion);
    }

    private static String[] splitLine(String line) {
        return line.split(REQUEST_SPLIT_DELIMITER);
    }

    private static void validate(String[] splits) {
        if (splits.length < MIN_LINE_SIZE) {
            throw new IllegalArgumentException("잘못된 HTTP 요청입니다.");
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
