package org.apache.coyote.http11.request;

import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.common.HttpMethod;

public class RequestStartLine {

    private final HttpMethod method;
    private final String uri;
    private final String version;

    public RequestStartLine(HttpMethod method, String uri, String version) {
        this.method = method;
        this.uri = uri;
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

    public String getUri() {
        return uri;
    }

    public String getPath() {
        return uri.split("\\?")[0];
    }

    public String getQueryString() {
        final var elements = uri.split("\\?");
        if (elements.length == 2) {
            return elements[1];
        }
        return null;
    }

    public String getVersion() {
        return version;
    }
}
