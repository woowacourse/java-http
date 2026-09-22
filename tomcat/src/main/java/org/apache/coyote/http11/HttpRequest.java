package org.apache.coyote.http11;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final String body;

    public HttpRequest(String rawRequest) {
        String[] sections = splitHeadAndBody(rawRequest);
        String head = sections[0];

        String[] requestLines = head.split("\r\n");

        this.requestLine = new RequestLine(requestLines[0]);
        this.headers = parseHeaders(requestLines);
        this.httpCookie = createHttpCookie(headers);
        this.body = sections.length > 1 ? sections[1] : "";
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public HttpCookie getHttpCookie() {
        return httpCookie;
    }

    public int getContentLength() {
        return httpCookie.getContentLength();
    }

    public boolean hasJsessionId() {
        return httpCookie.hasJsessionId();
    }

    public String getJsessionId() {
        return httpCookie.getJsessionId();
    }

    private String[] splitHeadAndBody(String rawRequest) {
        return rawRequest.split("\r\n\r\n", 2);
    }

    private Map<String, String> parseHeaders(String[] requestLines) {
        Map<String, String> headers = new HashMap<>();

        for (int i = 1; i < requestLines.length; i++) {
            String[] header = requestLines[i].split(":", 2);
            headers.put(header[0].trim(), header[1].trim());
        }
        return headers;
    }

    private HttpCookie createHttpCookie(Map<String, String> headers) {
        String cookieHeader = headers.get("Cookie");

        return cookieHeader == null ? new HttpCookie() : new HttpCookie(cookieHeader);
    }
}
