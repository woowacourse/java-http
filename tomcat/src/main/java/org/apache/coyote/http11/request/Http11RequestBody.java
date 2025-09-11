package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class Http11RequestBody {

    private final String content;

    public Http11RequestBody(final String content) {
        this.content = content;
    }

    public static Http11RequestBody from(
            final BufferedReader reader,
            final Http11RequestHeaders headers
    ) throws IOException {
        int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return new Http11RequestBody("");
        }

        char[] bodyChars = new char[contentLength];
        int readCount = reader.read(bodyChars);

        return new Http11RequestBody(readCount == -1 ? "" : new String(bodyChars));
    }

    public Map<String, String> parseFormParams() {
        if (content == null || content.isEmpty()) {
            return Collections.emptyMap();
        }

        return Arrays.stream(content.split("&"))
                .map(s -> s.split("="))
                .collect(Collectors.toMap(kv -> kv[0], kv -> kv[1]));
    }
}
