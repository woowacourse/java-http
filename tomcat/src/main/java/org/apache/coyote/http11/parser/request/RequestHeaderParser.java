package org.apache.coyote.http11.parser.request;

import static org.apache.coyote.http11.HttpConstants.COLON;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.dto.request.RequestHeader;

public final class RequestHeaderParser {

    private RequestHeaderParser() {
    }

    public static RequestHeader parse(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final int idx = line.indexOf(COLON);
            if (idx > 0) {
                final String name = line.substring(0, idx).trim();
                final String value = line.substring(idx + 1).trim();
                headers.put(name, value);
            }
        }
        return new RequestHeader(headers);
    }
}
