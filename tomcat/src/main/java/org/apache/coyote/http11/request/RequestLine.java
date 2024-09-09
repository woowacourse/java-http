package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RequestLine {

    private static final int SPILT_REQUEST_LINE_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final String protocol;

    public RequestLine(HttpMethod method, RequestUri requestUri, String protocol) {
        this.method = method;
        this.requestUri = requestUri;
        this.protocol = protocol;
    }

    public static RequestLine of(String requestLine) {
        List<String> splitRequestLine = Arrays.stream(requestLine.split(" ")).toList();
        if (splitRequestLine.size() != SPILT_REQUEST_LINE_COUNT) {
            throw new IllegalArgumentException("올바르지 않은 request line 형식입니다.");
        }

        return new RequestLine(
                getMethod(splitRequestLine),
                getUri(splitRequestLine),
                splitRequestLine.get(PROTOCOL_INDEX)
        );
    }

    private static HttpMethod getMethod(List<String> splitRequestLine) {
        String methodName = splitRequestLine.get(METHOD_INDEX);
        return HttpMethod.getByName(methodName);
    }

    private static RequestUri getUri(List<String> splitRequestLine) {
        String uri = splitRequestLine.get(URI_INDEX);
        return RequestUri.of(uri);
    }

    public String getQuery(String key) {
        return requestUri.getQuery(key);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public Queries getQueries() {
        return requestUri.getQueries();
    }

    public String getProtocol() {
        return protocol;
    }

    public Set<String> getQueryKeys() {
        return requestUri.getQueryKeys();
    }
}
