package org.apache.http.request;

import java.util.Arrays;

import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.apache.http.MimeType;
import org.apache.http.header.HttpHeader;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.header.HttpHeaders;

public class HttpRequest {

    private static final String FORM_DATA_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_COUNT = 2;
    private static final int KEY_ORDER = 0;
    private static final int VALUE_ORDER = 1;

    private final RequestLine requestLine;
    private final HttpHeaders headers;
    private final String body;
    private final HttpCookie httpCookie;

    public HttpRequest(RequestLine requestLine, HttpHeaders headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.httpCookie = headers != null ? headers.parseCookie() : null;
    }

    public HttpRequest(RequestLine requestLine) {
        this.requestLine = requestLine;
        this.headers = null;
        this.body = null;
        this.httpCookie = null;
    }

    public String getFormBodyByKey(String key) {
        validateContentTypeFormLogin();
        return Arrays.stream(body.split(FORM_DATA_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER, KEY_VALUE_COUNT))
                .filter(keyValue -> keyValue.length == KEY_VALUE_COUNT && keyValue[KEY_ORDER].equals(key))
                .map(keyValue -> keyValue[VALUE_ORDER])
                .findFirst()
                .orElse(null);
    }

    private void validateContentTypeFormLogin() {
        String requestContentType = getHeaderValue(HttpHeaderName.CONTENT_TYPE.getValue());
        if (MimeType.APPLICATION_X_WWW_FORM_URLENCODED != MimeType.getMimeType(requestContentType)) {
            throw new UnsupportedOperationException("application/x-www-form-urlencoded 형식이 아닌 미디어 타입입니다.");
        }
    }

    public boolean isSameMethod(HttpMethod httpMethod) {
        return requestLine.hasSameMethod(httpMethod);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpVersion getVersion() {
        return requestLine.getVersion();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getHeaderValue(String key) {
        return headers.getHeaders().stream()
                .filter(httpHeader -> httpHeader.getKey().equalsIgnoreCase(key))
                .map(HttpHeader::getValue)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재 하지 않는 Header " + key + "입니다."));
    }

    public String getBody() {
        return body;
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }
}
