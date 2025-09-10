package org.apache.coyote.http11.parser.request;

import static org.apache.coyote.http11.HttpConstants.EMPTY;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.dto.request.RequestBody;

public final class RequestBodyParser {

    public static RequestBody parse(final BufferedReader reader, final int contentLength) throws IOException {
        if (contentLength == 0) {
            return new RequestBody(EMPTY);
        }

        final char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new RequestBody(new String(buffer));
    }
}
