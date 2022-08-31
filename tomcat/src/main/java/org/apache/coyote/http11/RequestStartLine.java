package org.apache.coyote.http11;

import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;

public class RequestStartLine {

    private final HttpMethod method;
    private final String path;
    private final String version;

    public RequestStartLine(final HttpMethod method, final String path, final String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static RequestStartLine from(final String input) {
        checkBlank(input);
        final var elements = extractElements(input);
        return new RequestStartLine(
                HttpMethod.valueOf(elements.get(0)),
                elements.get(1),
                elements.get(2)
        );
    }

    private static void checkBlank(final String input) {
        if (input.isBlank()) {
            throw new UncheckedServletException("Http Request Start Line이 비어있습니다.");
        }
    }

    private static List<String> extractElements(final String input) {
        final var elements = List.of(input.trim().split(" "));
        if (elements.size() != 3) {
            throw new UncheckedServletException("Http Request Start Line이 형식에 맞지 않습니다.");
        }
        return elements;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }
}
