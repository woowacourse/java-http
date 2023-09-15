package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpCookie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.coyote.http11.common.constant.Constants.EMPTY;

public class RequestHeader {

    private static final String COOKIE_HEADER = "Cookie";
    private final Map<String, String> headers;

    public RequestHeader(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeader from(final List<String> lines) {
        Map<String, String> headers = new HashMap<>();
        for (String line : lines) {
            String[] headerInfo = line.split(": ");
            headers.put(headerInfo[0], headerInfo[1]);
        }
        return new RequestHeader(headers);
    }

    public String getHeaderValue(final String header) {
        if (!headers.containsKey(header)) {
            return null;
        }
        return headers.get(header);
    }

    public HttpCookie getCookie() {
        String cookie = headers.getOrDefault(COOKIE_HEADER, EMPTY);
        return HttpCookie.from(cookie);
    }
}

