package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final OutputStream outputStream;
    private final List<String> headers = new ArrayList<>();
    private String statusLine;
    private byte[] body = new byte[0];

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setStatus(final String statusLine) {
        this.statusLine = statusLine;
    }

    public void addHeader(final String name, final String value) {
        headers.add(name + ": " + value);
    }

    public void addCookie(final String name, final String value) {
        addHeader("Set-Cookie", name + "=" + value);
    }

    public void setBody(final byte[] body) {
        this.body = body;
    }

    public void sendRedirect(final String location) throws IOException {
        setStatus("302 Found");
        addHeader("Location", location);
        setBody(new byte[0]);
        send();
    }

    public void send() throws IOException {
        final StringBuilder head = new StringBuilder();
        head.append("HTTP/1.1 ").append(statusLine).append(" \r\n");
        for (String header : headers) {
            head.append(header).append(" \r\n");
        }
        head.append("Content-Length: ").append(body.length).append(" \r\n");
        head.append("\r\n");

        outputStream.write(head.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
