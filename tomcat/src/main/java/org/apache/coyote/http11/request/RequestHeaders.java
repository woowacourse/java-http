package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpCookie;

import java.io.BufferedReader;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHeaders {

    private static final String KEY_VALUE_DELIMITER = ": ";
    private static final String COOKIE = "Cookie";
    private static final String CONTENT_LENGTH = "Content-Length";
    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final BufferedReader br) {
        final Map<String, String> headers = br.lines().takeWhile(it -> !it.isEmpty())
                .distinct()
                .map(it -> it.split(KEY_VALUE_DELIMITER))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));

        return new RequestHeaders(headers);
    }

    public String getValue(final String key) {
        return headers.get(key);
    }

    public String getContentLength(){
        return headers.get(CONTENT_LENGTH);
    }

    public HttpCookie getCookie() {
        return HttpCookie.parseCookie(getValue(COOKIE));
    }
}
