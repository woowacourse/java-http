package org.apache.coyote.http11.dto.response;

import static org.apache.coyote.http11.HttpConstants.COLON;
import static org.apache.coyote.http11.HttpConstants.CRLF;
import static org.apache.coyote.http11.HttpConstants.SPACE;

import java.util.Map;

public record ResponseHeader(
        Map<String, String> headers
) {

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        headers.forEach((k, v) -> sb.append(k).append(COLON).append(SPACE).append(v).append(CRLF));
        return sb.toString();
    }
}

