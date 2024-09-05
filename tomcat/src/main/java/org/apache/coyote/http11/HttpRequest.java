package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private final String method;
    private final String requestUrl;
    private final HttpHeader header;
    private final HttpCookie httpCookie;
    private final Map<String, String> body;

    public HttpRequest(HttpHeader header, String method, String requestUrl, HttpCookie httpCookie, Map<String, String> body) {
        this.header = header;
        this.method = method;
        this.requestUrl = requestUrl;
        this.httpCookie = httpCookie;
        this.body = body;
    }

    public static HttpRequest from(List<String> request) {
        String requestLine = request.getFirst();
        String method = requestLine.split(" ")[0];
        String requestUrl = requestLine.split(" ")[1];

        Map<String, String> body = getBody(request);
        HttpHeader httpHeader = HttpHeader.from(request);
        HttpCookie httpCookie = HttpCookie.from(httpHeader.getHeader("Cookie"));

        return new HttpRequest(httpHeader, method, requestUrl, httpCookie, body);
    }

    private static Map<String, String> getBody(List<String> request) {
        String rawBody = request.getLast();

        if (rawBody.isEmpty()) {
            return new HashMap<>();
        }

        return Arrays.stream(rawBody.split("&"))
                .collect(Collectors.toMap(s -> s.split("=")[0], s -> s.split("=")[1]));
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public HttpHeader getHeader() {
        return header;
    }

    public Map<String, String> getBody() {
        return body;
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }
}
