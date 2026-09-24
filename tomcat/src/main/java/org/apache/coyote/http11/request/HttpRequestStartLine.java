package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public record HttpRequestStartLine(
        String method,
        RequestTarget requestTarget,
        String httpVersion
) {
    public static HttpRequestStartLine from(BufferedReader br) throws IOException {
        String line = br.readLine();
        Objects.requireNonNull(line);
        String[] parts = line.split(" ", 3);
        if (parts.length < 3) {
            throw new IllegalArgumentException("request format error");
        }
        return new HttpRequestStartLine(parts[0], RequestTarget.from(parts[1]), parts[2]);
    }

    public String path() {
        Objects.requireNonNull(requestTarget);

        return requestTarget().path();
    }
}
