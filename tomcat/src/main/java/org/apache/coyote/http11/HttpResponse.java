package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpResponse {

    private final OutputStream outputStream;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];
    private HttpResponseStatus httpResponseStatus = HttpResponseStatus.OK;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        headers.put("Content-Type", "text/html;charset=utf-8");
    }

    public void setContentType(String contentType) {
        headers.put("Content-Type", contentType);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void sendRedirect(HttpResponseStatus httpResponseStatus, String location) throws IOException {
        this.httpResponseStatus = httpResponseStatus;
        this.headers.put("Location", location);
        send();
    }

    public void send() throws IOException {
        headers.put("Content-Length", String.valueOf(body.length));

        final String firstLine = "HTTP/1.1 " + httpResponseStatus.toString() + " \r\n";

        final StringBuilder headerBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            headerBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }

        final String responseHeaders = firstLine + headerBuilder + "\r\n";

        outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
