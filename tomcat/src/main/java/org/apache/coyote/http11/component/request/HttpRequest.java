package org.apache.coyote.http11.component.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.coyote.http11.component.common.body.Body;
import org.apache.coyote.http11.component.common.body.BodyMapper;


public class HttpRequest {
    private static final String LINE_DELIMITER = "\r\n";

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final Body<?> body;

    public HttpRequest(final String plaintext) {
        Objects.requireNonNull(plaintext);
        final var lines = List.of(plaintext.split(LINE_DELIMITER));
        this.requestLine = new RequestLine(lines.getFirst());
        this.requestHeaders = new RequestHeaders(extractHeader(lines));
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
        final int clrfIndex = lines.indexOf("");
        for (var i = clrfIndex + 1; i < lines.size(); i++) {
            bodyTexts.add(lines.get(i));
        }
        return String.join(LINE_DELIMITER, bodyTexts).replaceAll(" ", "");
    }

    public String getQueryParam(final String key) {
        return requestLine.getQueryValue(key);
    }

    public boolean hasNotQuery() {
        return Objects.isNull(requestLine.getQuery()) || requestLine.getQuery().isBlank();
    }

    public String getPath() {
        return requestLine.getPath();
    }
}
