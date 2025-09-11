package org.apache.coyote.http11.message.parser;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.RequestLine;
import org.apache.coyote.http11.message.request.RequestUri;

public class RequestLineParser implements Parser<RequestLine> {

    public static final int REQUEST_LINE_TOKEN_COUNT = 3;
    public static final int HTTP_METHOD_INDEX = 0;
    public static final int REQUEST_LINE_INDEX = 1;
    public static final int PROTOCOL_VERSION_INDEX = 2;

    @Override
    public RequestLine parse(BufferedReader reader) throws IOException {
        String raw = reader.readLine();
        validateNotEmpty(raw);

        String[] tokens = raw.split(" ");
        validateTokenCount(tokens, raw);

        return new RequestLine(HttpMethod.from(tokens[HTTP_METHOD_INDEX]), RequestUri.from(tokens[REQUEST_LINE_INDEX]),
                tokens[PROTOCOL_VERSION_INDEX]);
    }

    private void validateTokenCount(String[] tokens, String raw) {
        if (tokens.length != REQUEST_LINE_TOKEN_COUNT) {
            throw new IllegalArgumentException("Invalid request line: " + raw);
        }
    }

    private void validateNotEmpty(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("Empty request line");
        }
    }
}

