package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpCookies;

public class HttpRequestHeader {

    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> headers = new HashMap<>();
    private final HttpCookies cookies;

    public HttpRequestHeader(List<String> lines) {
        for (String line : lines) {
            int index = line.indexOf(":");
            if (index == -1) {
                break;
            }
            String key = line.substring(0, index).trim();
            String value = line.substring(index + 1).trim();
            headers.put(key, value);
        }
        String cookieLine = headers.get(COOKIE);
        cookies = new HttpCookies(cookieLine);
    }

    public HttpCookie getCookie(String name) {
        return cookies.get(name);
    }

    public int getContentLength() {
        String contentLength = headers.getOrDefault(CONTENT_LENGTH, "0");
        return Integer.parseInt(contentLength);
    }

    public String get(String name) {
        return headers.get(name);
    }
}
