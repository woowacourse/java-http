package org.apache.coyote.http11;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public record HttpResponse(
        HttpStatusLine statusLine,
        Map<String, String> headers,
        byte[] responseBody
) {
    private static final Charset HEADER_CHARSET = StandardCharsets.US_ASCII;
    private static final String CRLF = "\r\n";

    public byte[] toBytes() {
        final String startLineString = statusLine.toString() + " ";

        final String headerLinesString = headers.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue() + " ")
                .collect(Collectors.joining(CRLF));

        final String emptyLineString = CRLF;

        final byte[] head = String.join(CRLF,
                startLineString, headerLinesString, emptyLineString).getBytes(HEADER_CHARSET);

        final byte[] response = Arrays.copyOf(head, head.length + responseBody.length);
        System.arraycopy(responseBody, 0, response, head.length, responseBody.length);

        return response;
    }
}
