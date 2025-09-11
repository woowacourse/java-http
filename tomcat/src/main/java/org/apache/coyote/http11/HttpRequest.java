package org.apache.coyote.http11;

import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final ContentType contentType;
    private final int contentLength;
    private final List<HttpCookie> cookies;
    private final Map<String, String> queryParameter;
    private final Map<String, String> body;

    public HttpRequest(Method method,
                       String path,
                       ProtocolVersion protocolVersion,
                       ContentType contentType,
                       int contentLength, List<HttpCookie> httpCookie,
                       Map<String, String> queryParameter,
                       Map<String, String> body) {
        this.requestLine = new RequestLine(method, path, protocolVersion);
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.cookies = httpCookie;
        this.queryParameter = queryParameter;
        this.body = body;
    }

    public String getSessionId() {
        return getCookieValue("JSESSIONID");
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getQueryParameterValue(String key) {
        return queryParameter.getOrDefault(key, "");
    }

    public boolean hasCookie(String key) {
        return cookies.stream()
                .anyMatch(cookie -> cookie.isKey(key));
    }

    public String getCookieValue(String key) {
        return cookies.stream()
                .filter(cookie -> cookie.isKey(key))
                .map(HttpCookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public Map<String, String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", contentType=" + contentType +
                ", contentLength=" + contentLength +
                ", cookies=" + cookies +
                ", queryParameter=" + queryParameter +
                ", body=" + body +
                '}';
    }
}
