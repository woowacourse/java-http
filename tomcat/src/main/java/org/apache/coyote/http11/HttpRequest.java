package org.apache.coyote.http11;

import java.util.Map;

public class HttpRequest {

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

    public static HttpRequest from(RequestLineInfo requestLineInfo, HttpHeaders headers, String body) {
        HttpCookie httpCookie = new HttpCookie(headers.get("Cookie"));
        return new HttpRequest(
                requestLineInfo.method(),
                requestLineInfo.path(),
                requestLineInfo.protocolVersion(),
                headers,
                body,
                httpCookie
        );
    }
    
    public Map<String, String> getFormData() {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("폼 데이터가 비어있습니다.");
        }
        return FormDataParser.parse(body);
    }
}
