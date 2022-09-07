package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

    private static final String HEADER_SEPARATOR = ":";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int SPLIT_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private Map<String, String> values;

    private HttpHeader(final Map<String, String> headers) {
        this.values = headers;
    }

    public static HttpHeader from(final List<String> headerList) {
        Map<String, String> headers = new HashMap<>();
        for (String header : headerList) {
            String[] headerLine = header.split(HEADER_SEPARATOR, SPLIT_SIZE);
            headers.put(headerLine[KEY_INDEX], headerLine[VALUE_INDEX]);
        }
        return new HttpHeader(headers);
    }

    public static HttpHeader init(final ContentType contentType, final String responseBody) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType.getContentType() + ";charset-utf-8");
        headers.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        return new HttpHeader(headers);
    }

    public static HttpHeader init(final ContentType contentType, final String responseBody, final String redirectUrl) {
        final HttpHeader headers = init(contentType, responseBody);
        headers.addHeader("Location", redirectUrl);
        return headers;
    }

    private void addHeader(final String key, final String value) {
        values.put(key, value);
    }

    public boolean isContainContentLength() {
        return values.containsKey(CONTENT_LENGTH);
    }

    public int getContentLength() {
        return Integer.parseInt(values.get(CONTENT_LENGTH).trim());
    }

    public String print() {
        return values.entrySet()
                .stream()
                .map(entry -> String.format("%s: %s ", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String getJSessionId() {
        return getCookiesData().getJSessionId();
    }

    public void addJSessionId(final String jSessionId) {
        values.put("Set-Cookie", "JSESSIONID=" + jSessionId);
    }

    public HttpCookie getCookiesData() {
        return HttpCookie.from(values.get("Cookie"));
    }
}
