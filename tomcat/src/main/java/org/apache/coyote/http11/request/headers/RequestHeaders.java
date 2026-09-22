package org.apache.coyote.http11.request.headers;

import org.apache.coyote.http11.session.HttpCookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://developer.mozilla.org/ko/docs/Web/HTTP/Reference/Headers"> MDN HTTP 헤더 </a>
 * */

public class RequestHeaders {

    private final Map<String, String> headers;

    public RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : lines) {
            String[] header = line.split(":", 2);
            if (header.length == 2) {
                headers.put(header[0].trim(), header[1].trim());
            }
        }
        return new RequestHeaders(headers);
    }

    public String getValue(String name) {
        return headers.get(name);
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    public String getContentType() {
        return headers.get("Content-Type");
    }

    public String getCookie(String cookieName) {
        return new HttpCookie(headers.get("Cookie")).getAttribute(cookieName);
    }

}
