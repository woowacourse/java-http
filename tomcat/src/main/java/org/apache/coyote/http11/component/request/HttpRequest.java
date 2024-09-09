package org.apache.coyote.http11.component.request;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HttpRequest {
    private static final String LINE_DELIMITER = "\r\n";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;

    public HttpRequest(final String plaintext) {
        Objects.requireNonNull(plaintext);
        final var lines = List.of(plaintext.split(LINE_DELIMITER));
        this.requestLine = new RequestLine(lines.getFirst());
        this.requestHeaders = new RequestHeaders(extractHeader(lines));
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

    public RequestHeaders getHeaders() {
        return requestHeaders;
    }
}
