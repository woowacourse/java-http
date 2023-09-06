package org.apache.coyote;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpProtocol;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class HttpRequest {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;
    private static final int HTTP_PROTOCOL_INDEX = 2;

    private final String path;
    private final HttpMethod method;
    private final HttpProtocol protocol;
    private final Map<String, String> headers;

    private HttpRequest(String path, HttpMethod method, HttpProtocol protocol, Map<String, String> headers) {
        this.path = path;
        this.method = method;
        this.protocol = protocol;
        this.headers = headers;
    }

    public static HttpRequest of(String request, String headers) {
        validateNull(request, headers);
        List<String> httpElements = Arrays.stream(request.split(" ")).collect(Collectors.toList());
        validateHttpFormat(httpElements);

        var path = httpElements.get(HTTP_PATH_INDEX);
        var method = HttpMethod.from(httpElements.get(HTTP_METHOD_INDEX));
        var protocol = HttpProtocol.from(httpElements.get(HTTP_PROTOCOL_INDEX));

        return new HttpRequest(path, method, protocol, readHeaders(headers));
    }

    private static HashMap<String, String> readHeaders(String headers) {
        if (headers == null) {
            return new HashMap<>();
        }
        return Arrays.stream(headers.split(System.lineSeparator()))
                .map(line -> line.split(": "))
                .collect(collectingAndThen(
                        toMap(header -> header[0], header -> header[1]),
                        HashMap::new
                ));
    }

    private static void validateHttpFormat(List<String> httpElements) {
        if (httpElements.size() != 3) {
            throw new IllegalArgumentException("잘못된 HTTP 요청 형태입니다.");
        }
    }

    private static void validateNull(String line, String headers) {
        if (line == null || headers == null) {
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

    public Map<String, String> getHeaders() {
        return headers;
    }
}
