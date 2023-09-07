package org.apache.coyote.view;

import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.response.HttpStatus;

public class Resource {

    private final String value;
    private final HttpStatus httpStatus;
    private final HttpCookie httpCookie;

    public Resource(String value, HttpStatus httpStatus, HttpCookie httpCookie) {
        this.value = value;
        this.httpStatus = httpStatus;
        this.httpCookie = httpCookie;
    }

    public boolean hasCookie() {
        return !httpCookie.isEmpty();
    }

    public String getValue() {
        return value;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getHttpCookie() {
        Map<String, String> cookies = httpCookie.getCookies();
        return cookies.keySet().stream()
                .map(key -> key + "=" + cookies.get(key))
                .collect(Collectors.joining(";"));
    }
}
