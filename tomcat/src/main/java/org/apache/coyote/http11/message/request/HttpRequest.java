package org.apache.coyote.http11.message.request;

import java.util.Optional;
import org.apache.coyote.http11.message.HttpHeaders;

public class HttpRequest {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_URL_INDEX = 1;

    private final HttpMethod method;
    private final HttpUrl url;
    private final HttpHeaders headers;
    private final byte[] body;

    public HttpRequest(HttpMethod method, HttpUrl url, HttpHeaders headers, byte[] body) {
        this.method = method;
        this.url = url;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest of(String requestLine, HttpHeaders headers, byte[] body) {
        String[] requestLineElements = requestLine.split(REQUEST_LINE_DELIMITER);
        HttpMethod method = HttpMethod.from(requestLineElements[HTTP_METHOD_INDEX]);
        String url = requestLineElements[HTTP_URL_INDEX];

        return new HttpRequest(method, HttpUrlParser.parseUrl(url), headers, body);
    }

    public boolean hasQueryString() {
        return url.hasQueryString();
    }

    public HttpRequestInfo getRequestInfo() {
        return new HttpRequestInfo(method, getUrlPath());
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrlPath() {
        return url.getPath();
    }

    public Optional<String> getHeaderFieldByName(String name) {
        return headers.getFieldByHeaderName(name);
    }

    public byte[] getBody() {
        return body;
    }

    public String getQueryParameter(String key) {
        return url.getQueryParameter(key);
    }
}
