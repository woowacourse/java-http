package org.apache.coyote.http11.component;

import java.net.URI;
import java.util.List;

public class RequestLine {

    private static final String DELIMITER = " ";

    private final Method method;
    private final URI uri;
    private final Version version;

    public RequestLine(final String plaintext) {
        final var split = List.of(plaintext.split(" "));
        validate(split);
        this.method = Method.from(split.getFirst());
        this.uri = URI.create(split.get(1));
        this.version = new Version(split.get(2));
    }

    public RequestLine(final Method method, final URI uri, final Version version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    private void validate(final List<String> splitLine) {
        if (splitLine.size() == 3) {
            return;
        }
        throw new IllegalArgumentException("올바른지 않은 요청 시작");
    }
}
