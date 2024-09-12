package org.apache.coyote.request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.HttpMessageBodyInfo;
import org.apache.coyote.util.CookieUtil;

public class HttpRequestHeader {

    private final Map<String, List<String>> values;

    public HttpRequestHeader(Map<String, List<String>> values) {
        this.values = Map.copyOf(values);
    }

    public int getContentLength() {
        if (values.containsKey(HttpMessageBodyInfo.CONTENT_LENGTH.getValue())) {
            return Integer.parseInt(values.get(HttpMessageBodyInfo.CONTENT_LENGTH.getValue()).getFirst());
        }
        return 0;
    }

    public Cookie getCookie() {
        if (hasCookie()) {
            return CookieUtil.read(values.get(HttpMessageBodyInfo.COOKIE.getValue()));
        }
        return null;
    }

    public boolean hasCookie() {
        return values.containsKey(HttpMessageBodyInfo.COOKIE.getValue());
    }

    public boolean hasSession() {
        if (hasCookie()) {
            Cookie cookie = CookieUtil.read(values.get(HttpMessageBodyInfo.COOKIE.getValue()));
            return cookie.containsSession();
        }
        return false;
    }

    @Override
    public String toString() {
        return values.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + String.join(";", entry.getValue()))
                .collect(Collectors.joining("\r\n"));
    }
}
