package org.apache.coyote.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpHeaders implements HttpComponent {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private static final String SEPARATOR = ": ";

    public static final String ACCEPT = "Accept";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers;

    public HttpHeaders() {
        headers = new ConcurrentHashMap<>();
    }

    public HttpHeaders(final String httpRequest) {
        this();
        String[] lines = httpRequest.split(LINE_FEED);
        int i = 1;
        while ((i < lines.length) && !lines[i].isEmpty()) {
            String[] headerLine = lines[i].split(SEPARATOR);
            headers.put(headerLine[NAME_INDEX], headerLine[VALUE_INDEX]);
            i++;
        }
    }

    public String get(final String name) {
        if (headers.containsKey(name)) {
            return headers.get(name);
        }
        return null;
    }

    public void put(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public String asString() {
        final var result = new StringBuilder();
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            result.append(key)
                    .append(SEPARATOR)
                    .append(value)
                    .append(SPACE)
                    .append(LINE_FEED);
        }
        return result.toString();
    }
}
