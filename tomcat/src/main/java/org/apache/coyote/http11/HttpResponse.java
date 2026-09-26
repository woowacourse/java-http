package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public record HttpResponse(
        StatusLine statusLine,
        Map<String, String> headers,
        byte[] body
) {

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder responseHead = new StringBuilder();
        responseHead.append(statusLine.toLine()).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHead.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        responseHead.append("Content-Length: ").append(body.length).append("\r\n\r\n");

        outputStream.write(responseHead.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
    }
}
