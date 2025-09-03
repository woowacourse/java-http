package org.apache.coyote.http11.message.request;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.exception.InvalidRequestLineException;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpRequest {
    public static final int REQUEST_LINE_ELEMENT_COUNT = 3;
    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final String version;
    private final HttpHeaders httpHeaders;

    private HttpRequest(HttpMethod httpMethod, RequestUri requestUri, String version, HttpHeaders httpHeaders) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.version = version;
        this.httpHeaders = httpHeaders;
    }

    public static HttpRequest from(String rawRequest) {
        String[] lines = rawRequest.split("\r\n");

        // 요청 라인 파싱
        String[] requestLineTokens = getRequestLineTokens(lines);
        HttpMethod method = HttpMethod.from(requestLineTokens[0]);
        RequestUri path = RequestUri.from(requestLineTokens[1]);
        String version = requestLineTokens[2];

        // 헤더 라인 파싱
        List<String> headerLines = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isEmpty()) {
                break;
            }
            headerLines.add(lines[i]);
        }
        HttpHeaders headers = HttpHeaders.fromLines(headerLines);

        return new HttpRequest(method, path, version, headers);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestUri getRequestPath() {
        return requestUri;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    private static String[] getRequestLineTokens(String[] lines) {
        String requestLine = lines[0];
        String[] requestLineTokens = requestLine.split(" ");
        if (requestLineTokens.length != REQUEST_LINE_ELEMENT_COUNT) {
            throw new InvalidRequestLineException(requestLine);
        }
        return requestLineTokens;
    }
}
