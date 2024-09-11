package org.apache.http.request;

import java.util.Arrays;
import java.util.Optional;

import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.apache.http.header.HttpHeader;
import org.apache.http.header.StandardHttpHeader;

public class HttpRequest {

    private static final String FORM_DATA_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_VALUE_COUNT = 2;
    private static final int KEY_ORDER = 0;
    private static final int VALUE_ORDER = 1;

    private final RequestLine requestLine;
    private final HttpHeader[] headers;
    private final String body;
    private final HttpCookie httpCookie;

    public HttpRequest(RequestLine requestLine, HttpHeader[] headers, String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.httpCookie = parseCookie(headers);
    }

    public static HttpRequest from(String requestLine, HttpHeader[] headers, String body) {
        return new HttpRequest(RequestLine.from(requestLine), headers, body);
    }

    private HttpCookie parseCookie(HttpHeader[] headers) {
        return Optional.ofNullable(headers)
                .flatMap(hs -> Arrays.stream(hs)
                        .filter(header -> StandardHttpHeader.COOKIE.equalsIgnoreCase(header.getKey()))
                        .findFirst()
                        .map(header -> HttpCookie.from(header.getValue())))
                .orElse(null);
    }

    // TODO: mime 체크하기
    public String getFormBodyByKey(String key) {
        return Arrays.stream(body.split(FORM_DATA_DELIMITER))
                .map(param -> param.split(KEY_VALUE_DELIMITER, KEY_VALUE_COUNT))
                .filter(keyValue -> keyValue.length == KEY_VALUE_COUNT && keyValue[KEY_ORDER].equals(key))
                .map(keyValue -> keyValue[VALUE_ORDER])
                .findFirst()
                .orElse(null);
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

    public HttpHeader[] getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return Arrays.stream(headers)
                .filter(httpHeader -> httpHeader.getKey().equals(key))
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
