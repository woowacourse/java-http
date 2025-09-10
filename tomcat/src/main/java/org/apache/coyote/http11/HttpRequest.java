package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

    private static final String HEADER_DELIMITER = "\\s+";

    private final HttpMethod method;
    private final String requestUri;
    private final String protocol;
    private final HttpHeaders headers;
    private final String body;
    private final HttpCookie httpCookie;

    private HttpRequest(HttpMethod method, String requestUri, String protocol, HttpHeaders headers, String body, HttpCookie httpCookie) {
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
        HttpHeaders httpHeaders = HttpHeaders.of(headers);
        HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
        return new HttpRequest(method, requestUri, protocol, httpHeaders, body, httpCookie);
    }
    
    public Map<String, String> getFormData() {
        if (body == null || body.isBlank()) {
            return Map.of();
        }
        return FormDataParser.parse(body);
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
