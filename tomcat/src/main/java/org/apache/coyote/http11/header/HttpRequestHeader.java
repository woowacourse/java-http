package org.apache.coyote.http11.header;

import static org.apache.coyote.http11.header.HeaderContent.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.HeaderContent.COOKIE;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Cookie;

public class HttpRequestHeader {
    private static final String HEADER_PAYLOAD_DELIMITER = ":";

    private final Map<String, String> payLoads;
    private final Cookie cookie;

    public HttpRequestHeader(Map<String, String> payLoads, Cookie cookie) {
        this.payLoads = payLoads;
        this.cookie = cookie;
    }

    public HttpRequestHeader(Map<String, String> payLoads) {
        this(payLoads, null);
    }

    public HttpRequestHeader() {
        this(new HashMap<>());
    }

    public static HttpRequestHeader readRequestHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> payLoads = bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split(HEADER_PAYLOAD_DELIMITER, 2))
                .collect(Collectors.toMap(
                        split -> split[0].strip(),
                        split -> split[1].strip()
                ));

        return new HttpRequestHeader(payLoads, initializeCookie(payLoads));
    }

    private static Cookie initializeCookie(Map<String, String> payLoad) {
        if (payLoad.containsKey(COOKIE.getMessage())) {
            String rawValue = payLoad.get(COOKIE.getMessage());
            return Cookie.read(rawValue);
        }
        return null;
    }

    public boolean hasCookie() {
        return cookie != null;
    }

    public int contentLength() {
        return Integer.parseInt(payLoads.get(CONTENT_LENGTH.getMessage()));
    }

    public Cookie getCookie() {
        return cookie;
    }
}
