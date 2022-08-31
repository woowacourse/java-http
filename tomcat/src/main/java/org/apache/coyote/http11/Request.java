package org.apache.coyote.http11;

import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;

public class Request {

    private final RequestStartLine startLine;

    public Request(final RequestStartLine startLine) {
        this.startLine = startLine;
    }

    public static Request from(final String input) {
        checkBlank(input);
        final var lines = extractLines(input);
        return new Request(RequestStartLine.from(lines.get(0)));
    }

    private static void checkBlank(final String input) {
        if (input.isBlank()) {
            throw new UncheckedServletException("Http Request가 비어있습니다.");
        }
    }

    private static List<String> extractLines(final String input) {
        return List.of(input.trim().split("\n"));
    }

    public RequestStartLine getStartLine() {
        return startLine;
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPath();
    }
}
