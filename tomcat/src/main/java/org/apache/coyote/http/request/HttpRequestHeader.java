package org.apache.coyote.http.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http.HttpCookies;

public class HttpRequestHeader {

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
        String cookieLine = headers.get("Cookie");
        cookies = new HttpCookies(cookieLine);
    }

    public int getContentLength() {
        String contentLength = get("Content-Length");
        return Integer.parseInt(contentLength);
    }

    public String get(String name) {
        return headers.getOrDefault(name, "0");
    }
}
