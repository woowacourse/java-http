package org.apache.coyote.http11.message.common;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class HttpHeaders {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String HEADER_REGEX = ": ";
    private static final String LINE_FEED = "\n";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpHeaders(String headers) {
        this(parseHeaders(headers));
    }

    public HttpHeaders() {
        this(new HashMap<>());
    }

    private static Map<String, String> parseHeaders(String header) {
        Map<String, String> map = new HashMap<>();

        String[] headers = header.split(LINE_FEED);
        for (String value : headers) {
            String[] keyValue = value.split(HEADER_REGEX);
            map.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        }
        return map;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    public void setHeaders(String key, String value) {
        this.headers.put(key, value);
    }

    public HttpCookie getCookie() {
        return new HttpCookie(headers.getOrDefault("Cookie", ""));
    }

    public ContentType getContentType() {
        return ContentType.from(headers.getOrDefault("Content-Type", ""));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(HEADER_REGEX).append(entry.getValue()).append(LINE_FEED);
        }
        return sb.toString();
    }
}
