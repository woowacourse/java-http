package org.apache.coyote.httpRequest.httpHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.coyote.Cookie;

public class HttpHeader {

    private final RequestLine requestLine;
    private final Map<String, String> headers;

    public HttpHeader(final String requestLine, final List<String> headers) {
        this.requestLine = new RequestLine(requestLine);
        this.headers = HttpHeaderParser.getHeaders(headers);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getPurePath() {
        return requestLine.getPurePath();
    }

    public Map<String, String> getQueries() {
        return requestLine.getQueryValues();
    }

    public boolean hasCookie(final String key) {
        List<Cookie> cookies = getCookies();
        if (cookies.isEmpty()) {
            return false;
        }
        for (final Cookie cookie : cookies) {
            if (cookie.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    private List<Cookie> getCookies() {
        final List<Cookie> findCookies = new ArrayList<>();
        final String cookieValues = headers.get("Cookie");
        if (cookieValues == null) {
            return findCookies;
        }
        final String[] cookies = cookieValues.split(";");
        for (String cookie : cookies) {
            String[] cookieValue = cookie.split("=");
            findCookies.add(new Cookie(cookieValue[0], cookieValue[1]));
        }
        return findCookies;
    }

    public String getHeader(final String key) {
        return headers.getOrDefault(key, null);
    }
}
