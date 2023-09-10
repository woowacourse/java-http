package org.apache.coyote.header;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class HttpHeaders {

    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String LOCATION = "Location";
    public static final String SET_COOKIE = "Set-Cookie";

    private Map<String, String> headers = new LinkedHashMap<>();

    public void add(String name, String value) {
        headers.put(name, value);
    }

    public boolean contains(String name) {
        return headers.containsKey(name);
    }

    public @Nullable String getValue(String name) {
        return headers.get(name);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ");
            stringBuilder.append("\r\n");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        return stringBuilder.toString();
    }
}
