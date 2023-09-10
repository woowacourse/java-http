package org.apache.coyote.http11.request;

import org.apache.coyote.http11.session.HttpCookie;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toMap;

public class HttpRequestHeader {

    private static final String COOKIE_REQUEST_HEADER = "Cookie";
    private Map<String, String> headers;

    private HttpRequestHeader(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    private HttpRequestHeader() {
        this(new HashMap<>());
    }

    public static HttpRequestHeader from(String headers) {
        return Arrays.stream(headers.split("\r\n"))
                .map(header -> header.split(": "))
                .collect(collectingAndThen(
                        toMap(header -> header[0], header -> header[1]),
                        HttpRequestHeader::new
                ));
    }

    public String find(String header) {
        return headers.get(header);
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(find(COOKIE_REQUEST_HEADER));
    }

    public String contentLength() {
        return find("Content-Length");
    }
}
