package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestHeaders {
    private static final String HEADER_PAYLOAD_DELIMITER = ":";

    private final Map<String, String> payLoads;
    private final Cookie cookie;

    public HttpRequestHeaders(Map<String, String> payLoads, Cookie cookie) {
        this.payLoads = payLoads;
        this.cookie = cookie;
    }

    public HttpRequestHeaders(Map<String, String> payLoads) {
        this(payLoads, null);
    }

    public HttpRequestHeaders() {
        this(new LinkedHashMap<>());
    }

    public static HttpRequestHeaders readRequestHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> payLoads = bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split(HEADER_PAYLOAD_DELIMITER, 2))
                .collect(Collectors.toMap(
                        split -> split[0].strip(),
                        split -> split[1].strip()
                ));

        return new HttpRequestHeaders(payLoads, initializeCookie(payLoads));
    }

    private static Cookie initializeCookie(Map<String, String> payLoad) {
        if (payLoad.containsKey("Cookie")) {
            String rawValue = payLoad.get("Cookie");
            return Cookie.read(rawValue);
        }
        return null;
    }

    public void contentType(String contentType) {
        payLoads.put("Content-Type", contentType);
    }

    public void contentLength(int contentLength) {
        payLoads.put("Content-Length", String.valueOf(contentLength));
    }

    public void setCookie(Cookie cookie) {
        payLoads.put("Set-Cookie", cookie.serialize());
    }

    public void location(String location) {
        payLoads.put("Location", location);
    }

    public Map<String, String> getPayLoads() {
        return payLoads;
    }

    public boolean hasCookie() {
        return cookie != null;
    }

    public int contentLength() {
        return Integer.parseInt(payLoads.get("Content-Length"));
    }

    public Cookie getCookie() {
        return cookie;
    }
}
