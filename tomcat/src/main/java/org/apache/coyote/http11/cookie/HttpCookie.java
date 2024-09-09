package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(String cookies) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = cookies.split("; ");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1]);
            }
        }
        this.cookies = result;
    }

    public boolean hasJSESSIONID() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJSESSIONID() {
        return cookies.get("JSESSIONID");
    }
}
