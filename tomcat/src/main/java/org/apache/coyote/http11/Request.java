package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.tomcat.http.common.body.Body;
import org.apache.tomcat.http.common.body.BodyMapper;
import org.apache.tomcat.http.request.RequestHeaders;
import org.apache.tomcat.http.request.RequestLine;
import org.apache.tomcat.http.response.Cookie;


public class Request {
    private static final String LINE_DELIMITER = "\r\n";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Body body;
    private final Cookie cookie;

    public Request(final String plaintext) {
        Objects.requireNonNull(plaintext);
        final var lines = List.of(plaintext.split(LINE_DELIMITER));
        this.requestLine = new RequestLine(lines.getFirst());
        this.requestHeaders = new RequestHeaders(extractHeader(lines));
        this.cookie = new Cookie(requestHeaders.get("Cookie"));
        this.body = BodyMapper.getMapping(requestHeaders.get("Content-Type"))
                .apply(extractBody(lines));
    }

    private String extractHeader(final List<String> lines) {
        final var headerTexts = new ArrayList<String>();
        for (var i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                break;
            }
            headerTexts.add(lines.get(i));
        }
        return String.join(LINE_DELIMITER, headerTexts).replaceAll(" ", "");
    }

    private String extractBody(final List<String> lines) {
        final var bodyTexts = new ArrayList<String>();
        final var clrfIndex = lines.indexOf("");
        for (var i = clrfIndex + 1; i < lines.size(); i++) {
            bodyTexts.add(lines.get(i));
        }
        return String.join(LINE_DELIMITER, bodyTexts).replaceAll(" ", "");
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public Body getBody() {
        return body;
    }

    public Cookie getCookie() {
        return cookie;
    }
}
