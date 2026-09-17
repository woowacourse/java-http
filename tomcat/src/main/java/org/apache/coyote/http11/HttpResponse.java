package org.apache.coyote.http11;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public record HttpResponse(
        HttpStatusLine statusLine,
        String contentType,
        int contentLength,
        byte[] responseBody

) {
    private static final Charset HEADER_CHARSET = StandardCharsets.US_ASCII;

    public static HttpResponse of(HttpStatusLine statusLine, String contentType, byte[] responseBody) {
        return new HttpResponse(statusLine, contentType, responseBody.length, responseBody);
    }

    public byte[] toBytes() {
        final byte[] header = String.join(
                "\r\n",
                statusLine.toString() + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                ""
        ).getBytes(HEADER_CHARSET);

        final byte[] response = Arrays.copyOf(header, header.length + responseBody.length);
        System.arraycopy(responseBody, 0, response, header.length, responseBody.length);

        return response;
    }
}
