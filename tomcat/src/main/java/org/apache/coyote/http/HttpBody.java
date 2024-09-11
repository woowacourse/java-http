package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringJoiner;

public class HttpBody implements HttpComponent {

    private final String content;
    private final int length;

    public HttpBody(final BufferedReader reader) throws IOException {
        final var body = new StringJoiner(LINE_FEED);
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            body.add(line);
        }
        content = body.toString();
        length = content.getBytes().length;
    }

    public HttpBody(final String content) {
        this.content = content;
        this.length = content.getBytes().length;
    }

    public String getContent() {
        return content;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String asString() {
        return getContent();
    }
}
