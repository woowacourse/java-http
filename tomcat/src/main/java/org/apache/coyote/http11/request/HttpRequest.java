package org.apache.coyote.http11.request;

import org.apache.coyote.http11.types.HttpMethod;
import org.apache.coyote.http11.types.HttpProtocol;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;
    private static final int HTTP_PROTOCOL_INDEX = 2;

    private final String path;
    private final HttpMethod method;
    private final HttpProtocol protocol;
    private final String body;
    private final Map<String, String> headers;

    private HttpRequest(String path, HttpMethod method, HttpProtocol protocol, Map<String, String> headers, String body) {
        this.path = path;
        this.method = method;
        this.protocol = protocol;
        this.body = body;
        this.headers = headers;
    }

    public static HttpRequest of(String request, Map<String, String> headers, String body) {
        validateNull(request);
        List<String> httpElements = Arrays.stream(request.split(" ")).collect(Collectors.toList());
        validateHttpFormat(httpElements);

        var path = httpElements.get(HTTP_PATH_INDEX);
        var method = HttpMethod.from(httpElements.get(HTTP_METHOD_INDEX));
        var protocol = HttpProtocol.from(httpElements.get(HTTP_PROTOCOL_INDEX));

        return new HttpRequest(path, method, protocol, headers, body);
    }

    private static void validateHttpFormat(List<String> httpElements) {
        if (httpElements.size() != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청 형태입니다.");
        }
    }

    private static void validateNull(String line) {
        if (line == null) {
            throw new IllegalArgumentException("잘못된 HTTP 요청 형태입니다.");
        }
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpProtocol getProtocol() {
        return protocol;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
