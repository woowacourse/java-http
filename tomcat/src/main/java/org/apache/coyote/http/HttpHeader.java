package org.apache.coyote.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http.session.Session;

public class HttpHeader {

    private static final String HEADER_SEPARATOR = ":";
    private static final String CONTENT_LENGTH_KEY = "Content-Length";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String LOCATION_KEY = "Location";
    private static final String COOKIE_KEY = "Cookie";
    public static final String JSESSIONID_KEY = "JSESSIONID";
    public static final String SET_COOKIE = "Set-Cookie";
    private static final String PRINT_FORMAT = "%s: %s ";
    public static final String KEY_VALUE_DELIMITER = "=";
    private static final int SPLIT_SIZE = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

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
        headers.put(CONTENT_TYPE_KEY, contentType.getType() + ";charset-utf-8");
        headers.put(CONTENT_LENGTH_KEY, String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        return new HttpHeader(headers);
    }

    public static HttpHeader init(final ContentType contentType, final String responseBody, final String redirectUrl) {
        HttpHeader headers = init(contentType, responseBody);
        headers.addHeader(LOCATION_KEY, redirectUrl);
        return headers;
    }

    private void addHeader(final String key, final String value) {
        values.put(key, value);
    }

    public void addJSessionId(final Session session) {
        values.put(SET_COOKIE, JSESSIONID_KEY + KEY_VALUE_DELIMITER + session.getId());
    }

    public boolean isContainContentLength() {
        return values.containsKey(CONTENT_LENGTH_KEY);
    }

    public int getContentLength() {
        return Integer.parseInt(values.get(CONTENT_LENGTH_KEY).trim());
    }

    public String print() {
        return values.entrySet()
                .stream()
                .map(entry -> String.format(PRINT_FORMAT, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    public String getJSessionId() {
        return getCookiesData().getJSessionId();
    }

    public HttpCookie getCookiesData() {
        return HttpCookie.from(values.get(COOKIE_KEY));
    }
}
