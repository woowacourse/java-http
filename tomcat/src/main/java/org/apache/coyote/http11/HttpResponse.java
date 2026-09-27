package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setBody(String text, String contentType) {
        setBody(text.getBytes(UTF_8), contentType);
    }

    public void setBody(byte[] bytes, String contentType) {
        body = bytes.clone();
        setHeader("Content-Type", contentType);
    }

    public void sendRedirect(String location) {
        status = HttpStatus.FOUND;
        setHeader("Location", location);
        headers.remove("Content-Type");
        body = new byte[0];
    }

    public void sendError(HttpStatus errorStatus) {
        status = errorStatus;
        headers.remove("Location");
        headers.remove("Content-Type");
        body = new byte[0];
    }

    public void writeTo(OutputStream output) throws IOException {
        StringBuilder head = new StringBuilder();

        head.append("HTTP/1.1 ")
                .append(status.code())
                .append(' ')
                .append(status.reason())
                .append(CRLF);

        for (Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equalsIgnoreCase("Content-Length")) {
                continue;
            }

            head.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(CRLF);
        }

        head.append("Content-Length: ")
                .append(body.length)
                .append(CRLF)
                .append(CRLF);

        output.write(head.toString().getBytes(ISO_8859_1));
        output.write(body);
        output.flush();
    }
}
