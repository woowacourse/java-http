package org.apache.coyote.http11.parser.request;

import static org.apache.coyote.http11.HttpConstants.SPACE;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.dto.request.RequestLine;
import org.apache.coyote.http11.dto.request.RequestPath;

public final class RequestLineParser {

    private static final String REQUEST_LINE_DELIMITER = SPACE;

    private RequestLineParser() {
    }

    public static RequestLine parse(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null || line.isBlank()) {
            throw new IOException("Request line is empty");
        }

        final String[] parts = line.split(REQUEST_LINE_DELIMITER);
        final String method = parts[0].trim();
        final RequestPath requestPath = RequestPathParser.parse(parts[1].trim());
        final String protocolVersion = parts[2].trim();

        return new RequestLine(method, requestPath, protocolVersion);
    }
}
