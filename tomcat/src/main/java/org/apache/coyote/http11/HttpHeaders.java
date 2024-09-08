package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private final Map<String, String> payLoads;
    private final Optional<Cookie> cookie;

    public HttpHeaders(Map<String, String> payLoads) {
        this(payLoads, Optional.empty());
    }

    public HttpHeaders(Map<String, String> payLoads, Optional<Cookie> cookie) {
        this.payLoads = payLoads;
        this.cookie = cookie;
    }

    public static HttpHeaders readRequestHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> payLoads = new HashMap<>();
        Optional<Cookie> cookie = Optional.empty();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.isBlank()) {
                break;
            }

            String[] split = line.split(":");

            if (split.length != 2) {
                continue;
            }

            String key = split[0].trim();
            String value = split[1].trim();

            if (key.equals("Cookie")) {
                cookie = Optional.of(Cookie.read(value));
                continue;
            }

            payLoads.put(key, value);
        }

        return new HttpHeaders(payLoads, cookie);
    }

    public Map<String, String> getPayLoads() {
        return payLoads;
    }

    public boolean hasCookie() {
        return cookie.isPresent();
    }

    public int contentLength() {
        return Integer.parseInt(payLoads.get("Content-Length"));
    }

    public Cookie getCookie() {
        return cookie.get();
    }
}
