package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(String rawCookies) {
        this();
        String[] cookiesElements = rawCookies.split("; ");
        for (int i = 0; i < cookiesElements.length; i++) {
            String[] cookiePair = cookiesElements[i].split("=");
            cookies.put(cookiePair[0], cookiePair[1]);
        }
    }

    public HttpCookie() {
        cookies = new HashMap<>();
    }

    public void setJSESSIONID() {
        if (!cookies.containsKey("JSESSIONID")) {
            UUID uuid = UUID.randomUUID();
            cookies.put("JSESSIONID", uuid.toString());
        }
    }

    public String getResponse() {
        if (cookies.isEmpty()) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        for (Entry<String, String> stringStringEntry : cookies.entrySet()) {
            response.append(stringStringEntry.getKey())
                    .append("=")
                    .append(stringStringEntry.getValue())
                    .append(";");
        }
        return String.valueOf(response);
    }
}
