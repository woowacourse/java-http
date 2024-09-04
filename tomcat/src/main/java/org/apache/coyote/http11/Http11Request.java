package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.HttpRequest;

public class Http11Request implements HttpRequest {

    private final Http11RequestLine requestLine;
    private final Http11RequestHeaders headers;

    public Http11Request(List<String> lines) {
        validate(lines);
        this.requestLine = parseRequestLine(lines);
        this.headers = parseHeaders(lines);
    }

    private void validate(List<String> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException();
        }
    }

    private Http11RequestLine parseRequestLine(List<String> lines) {
        String line = lines.get(0);
        lines.removeFirst();
        return new Http11RequestLine(line);
    }

    private Http11RequestHeaders parseHeaders(List<String> lines) {
        List<String> headers = lines.stream()
                .filter(line -> !line.isEmpty())
                .toList();
        return new Http11RequestHeaders(headers);

    }

    @Override
    public String parsePath() {
        return requestLine.getPath();
    }
}
