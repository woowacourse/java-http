package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private static final String HEADER_DELIMITER = "\\s+";

    private final HttpMethod method;
    private final String requestUri;
    private final String protocol;
    private final Map<String, String> headers;
    private final String body;
    private final HttpCookie httpCookie;

    private HttpRequest(HttpMethod method, String requestUri, String protocol, Map<String, String> headers, String body, HttpCookie httpCookie) {
        this.method = method;
        this.requestUri = requestUri;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
        this.httpCookie = httpCookie;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public static HttpRequest from(String requestLine, Map<String, String> headers, String body) {
        validateRequestLine(requestLine);
        String[] requestParts = requestLine.split(HEADER_DELIMITER);
        validateSplitRequestLine(requestParts);
        String methodToken = requestParts[0];
        String requestUri = requestParts[1];
        String protocol = requestParts[2];

        HttpMethod method = HttpMethod.from(methodToken);
        HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
        return new HttpRequest(method, requestUri, protocol, headers, body, httpCookie);
    }

    public boolean isStaticResourceRequest() {
        return requestUri.contains(".") && !requestUri.contains("/login") && !requestUri.contains("/register");
    }

    public boolean isPost() {
        return getMethod() == HttpMethod.POST;
    }

    private static void validateSplitRequestLine(String[] requestParts) {
        if (requestParts.length < 3) {
            throw new IllegalArgumentException("요청 라인 파싱 실패");
        }
    }

    private static void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청 라인");
        }
    }
}
