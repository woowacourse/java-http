package org.apache.coyote.header;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private Map<String, String> cookies = new LinkedHashMap<>();

    public HttpCookie() {
    }

    public HttpCookie(String raw) {
        String[] values = raw.split("; ");
        for (String value : values) {
            String[] keyValue = value.split("=");
            cookies.put(keyValue[0], keyValue[1]);
        }
    }

    public void addSessionId(String sessionId) {
        cookies.put("JSESSIONID", sessionId);
    }

    public String getValue(String name) {
        return cookies.get(name);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : cookies.entrySet()) {
            stringBuilder.append(entry.getKey()).append("=").append(entry.getValue());
            stringBuilder.append("; ");
        }
        return stringBuilder.toString();
    }
}
