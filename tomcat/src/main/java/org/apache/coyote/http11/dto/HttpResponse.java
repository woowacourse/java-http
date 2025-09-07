package org.apache.coyote.http11.dto;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.http11.handler.Status;

public record HttpResponse(
        String protocol,
        Status status,
        Map<String, String> headers
) {

    private static final String SPACE = " ";
    private static final String COLON = ":";
    private static final String CRLF = "\r\n";

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public byte[] toBytes() {
        final StringBuilder sb = new StringBuilder();
        sb.append(protocol).append(SPACE).append(status.line()).append(CRLF);
        headers.forEach((k, v) -> sb.append(k).append(COLON).append(SPACE).append(v).append(CRLF));
        sb.append(CRLF);

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
