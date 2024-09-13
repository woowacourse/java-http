package org.apache.catalina.request;

import java.util.Map;

import org.apache.catalina.http.VersionOfProtocol;

public class RequestLine {

    private static final String SPACE = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URL_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;
    private static final int REQUEST_LINE_PARTS_COUNT = 3;

    private final HttpMethod httpMethod;
    private final VersionOfProtocol versionOfProtocol;
    private final RequestUri requestUri;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(SPACE);
        if (parts.length != REQUEST_LINE_PARTS_COUNT) {
            throw new IllegalArgumentException(requestLine + ": 요청 헤더의 형식이 올바르지 않습니다.");
        }
        this.httpMethod = HttpMethod.of(parts[HTTP_METHOD_INDEX]);
        this.versionOfProtocol = new VersionOfProtocol(parts[PROTOCOL_VERSION_INDEX]);
        this.requestUri = new RequestUri(parts[REQUEST_URL_INDEX]);
    }

    public boolean checkQueryParamIsEmpty() {
        return requestUri.checkQueryParamIsEmpty();
    }

    public boolean isSameHttpMethod(HttpMethod httpMethod) {
        return httpMethod == this.httpMethod;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public String getPathWithoutQuery() {
        return requestUri.getPathWithoutQuery();
    }

    public VersionOfProtocol getVersionOfProtocol() {
        return versionOfProtocol;
    }

    public Map<String, String> getQueryParam() {
        return requestUri.getQueryParam();
    }
}
