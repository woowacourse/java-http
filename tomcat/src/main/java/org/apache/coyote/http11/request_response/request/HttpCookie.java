package org.apache.coyote.http11.request_response.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.http11.request_response.HttpHeader;

public class HttpCookie {

    private final Map<String, String> cookies = new HashMap<>();

    // TODO indexoutofbound 고려
    public HttpCookie(List<HttpHeader> cookieHeaders) {
        for (HttpHeader cookieHeader : cookieHeaders) {
            String cookieHeaderValue = cookieHeader.value();
            Arrays.stream(cookieHeaderValue.split(";"))
                .map(cookie -> cookie.split("="))
                .forEach(keyValue -> cookies.put(keyValue[0].trim(), keyValue[1].trim()));
        }
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }
}
