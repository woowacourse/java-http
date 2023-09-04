package org.apache.coyote.header;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private Map<String, String> cookies;

    public HttpCookie(String rawValue) {
        if (rawValue.isBlank()) {
            this.cookies = new HashMap<>();
            return;
        }
        
        Map<String, String> cookies = new HashMap<>();
        String[] values = rawValue.split("; ");
        for (String value : values) {
            String[] keyValue = value.split("=");
            cookies.put(keyValue[0], keyValue[1]);
        }
        this.cookies = cookies;
    }

    public static HttpCookie from(String requestHeaders) {
        String[] headers = requestHeaders.split("\r\n");
        for (String header : headers) {
            if (header.contains("Cookie")) {
                String rawValue = header.substring(header.indexOf(": ") + 1);
                return new HttpCookie(rawValue);
            }
        }

        return new HttpCookie("");
    }

    public String getValue(String key) {
        return cookies.getOrDefault(key, null);
    }
}
