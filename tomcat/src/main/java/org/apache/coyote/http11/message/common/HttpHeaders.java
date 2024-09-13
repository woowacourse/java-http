package org.apache.coyote.http11.message.common;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookies;

public class HttpHeaders {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String DEFAULT_CONTENT_LENGTH = "0";
    private static final String CRLF = "\r\n";
    private static final String LINE_FEED = "\n";
    private static final String HEADER_REGEX = ": ";
    private static final String DEFAULT_VALUE = "";

    private final Map<String, String> headers;

    public HttpHeaders(String headers) {
        this(parseHeaders(headers));
    }

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    private static Map<String, String> parseHeaders(String header) {
        Map<String, String> map = new LinkedHashMap<>();

        String[] headers = header.split(LINE_FEED);
        for (String value : headers) {
            String[] keyValue = value.split(HEADER_REGEX);
            map.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        }
        return map;
    }

    public HttpHeaders() {
        this(new LinkedHashMap<>());
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault(HttpHeaderField.CONTENT_LENGTH.getName(), DEFAULT_CONTENT_LENGTH));
    }

    public void setHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public HttpCookies getCookies() {
        return new HttpCookies(headers.getOrDefault(HttpHeaderField.COOKIE.getName(), DEFAULT_VALUE));
    }

    public ContentType getContentType() {
        return ContentType.from(
                headers.getOrDefault(HttpHeaderField.CONTENT_TYPE.getName(), ContentType.TEXT_HTML.getType()));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(HEADER_REGEX).append(entry.getValue()).append(CRLF);
        }
        return sb.toString();
    }
}