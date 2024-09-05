package org.apache.coyote.http11.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final Body body;

    public HttpRequest(final String plaintext) {
        Objects.requireNonNull(plaintext);
        final var lines = List.of(plaintext.split(System.lineSeparator()));
        this.requestLine = new RequestLine(lines.getFirst());
        this.headers = new Headers(extractHeader(lines));
        // TODO
        this.body = null;
    }

    private String extractHeader(final List<String> lines) {
        final var headerTexts = Collections.synchronizedList(new ArrayList<String>());
        for (var i = 1; i < lines.size(); i++) {
            if (lines.get(i).isBlank()) {
                break;
            }
            headerTexts.add(lines.get(i).replaceAll(" ", ""));
        }
        return String.join("\r\n", headerTexts);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Body getBody() {
        return body;
    }
}
