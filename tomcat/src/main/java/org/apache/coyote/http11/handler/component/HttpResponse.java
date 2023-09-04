package org.apache.coyote.http11.handler.component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HttpResponse {

    private final String statusLine;
    private final List<String> headers;
    private final String body;

    public HttpResponse(final String statusLine, final String body, final List<String> headers) {
        this.statusLine = statusLine;
        this.body = body;
        this.headers = headers;
    }

    public String getResponse() {
        if (Objects.nonNull(body)) {
            return String.join(System.lineSeparator(),
                statusLine,
                headers.stream().collect(Collectors.joining(System.lineSeparator())),
                "",
                body
            );
        }

        return String.join(System.lineSeparator(),
            statusLine,
            headers.stream().collect(Collectors.joining(System.lineSeparator()))
        );
    }
}
