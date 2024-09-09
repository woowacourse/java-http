package org.apache.coyote.http11.component.request;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.coyote.http11.component.common.Body;
import org.apache.coyote.http11.component.common.FormUrlEncodedBody;
import org.apache.coyote.http11.component.common.Method;
import org.apache.coyote.http11.component.common.Version;

public class HttpRequest {
    private static final String LINE_DELIMITER = "\r\n";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Body body;

    public HttpRequest(final String plaintext) {
        Objects.requireNonNull(plaintext);
        final var lines = List.of(plaintext.split(LINE_DELIMITER));
        this.requestLine = new RequestLine(lines.getFirst());
        this.requestHeaders = new RequestHeaders(extractHeader(lines));
        this.body = new FormUrlEncodedBody(requestLine.getUri().getQuery());
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

    public URI getUri() {
        return requestLine.getUri();
    }

    public boolean isSameUri(String uriText) {
        return getUri().getPath().equals(uriText);
    }

    public Version getVersion() {
        return requestLine.getVersion();
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public RequestHeaders getHeaders() {
        return requestHeaders;
    }

    public Body getBody() {
        return body;
    }
}
