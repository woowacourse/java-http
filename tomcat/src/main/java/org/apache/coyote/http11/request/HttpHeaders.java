package org.apache.coyote.http11.request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private static final char KEY_VALUE_DELIMITER = ':';

    private final Map<String, HeaderValue> headers;

    private HttpHeaders(Map<String, HeaderValue> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(List<String> headers) {
        Map<String, HeaderValue> headerValueByKey = new HashMap<>();
        for (String header : headers) {
            int keyValueDelimiterIndex = header.indexOf(KEY_VALUE_DELIMITER);
            String key = header.substring(0, keyValueDelimiterIndex);
            String value = header.substring(keyValueDelimiterIndex + 1).trim();

            headerValueByKey.put(key, HeaderValue.from(value));
        }
        return new HttpHeaders(headerValueByKey);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(new HashMap<>());
    }

    public void add(String key, String value) {
        headers.put(key, HeaderValue.from(value));
    }

    public List<String> get(String key) {
        return headers.getOrDefault(key, HeaderValue.empty()).getValues();
    }

    public int contentLength() {
        List<String> contentLength = get("Content-Length");
        if (contentLength.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(contentLength.get(0));
    }

    public Optional<String> sessionId() {
        List<String> cookies = get("Cookie");
        return cookies.stream()
                      .filter(cookie -> cookie.startsWith("JSESSIONID="))
                      .map(jsessionid -> jsessionid.substring(jsessionid.indexOf('=') + 1))
                      .findFirst();
    }

    public String format() {
        StringBuilder headerBuilder = new StringBuilder();

        List<String> keys = new ArrayList<>(headers.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            headerBuilder.append(key).append(KEY_VALUE_DELIMITER + " ").append(headers.get(key).format());
            headerBuilder.append(" ").append("\r\n");
        }
        return headerBuilder.toString();
    }
}
