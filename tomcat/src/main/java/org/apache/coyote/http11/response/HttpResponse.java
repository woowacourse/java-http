package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.util.StaticResourceResolver;

public class HttpResponse {

    private final OutputStream outputStream;
    private final ResponseHeaders headers;

    public HttpResponse(final OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new ResponseHeaders();
    }

    public void addHeader(final String key, final String value) {
        this.headers.addHeader(key, value);
    }

    public void sendOk(final String mimeType, final String body) throws IOException {
        if(body == null) {
            final var response = buildResponse("200 OK", "", this.headers);
            write(response);
            return;
        }

        final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        this.headers.addHeader("Content-Type", mimeType + ";charset=utf-8");
        this.headers.addHeader("Content-Length", String.valueOf(bodyBytes.length));

        final var response = buildResponse("200 OK", body, this.headers);
        write(response);
    }

    public void sendRedirect(final String location) throws IOException {
        this.headers.addHeader("Location", location);
        final var response = buildResponse("302 Found", "", this.headers);
        write(response);
    }

    public void sendNotFound() throws IOException {
        var body = StaticResourceResolver.read("/404.html");
        if (body == null) {
            sendRawNotFoundError();
            return;
        }
        final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        this.headers.addHeader("Content-Type", "text/html;charset=utf-8");
        this.headers.addHeader("Content-Length", String.valueOf(bodyBytes.length));

        final var response = buildResponse("404 Not Found", body, this.headers);
        write(response);
    }

    private void sendRawNotFoundError() throws IOException {
        final var response = "HTTP/1.1 404 Not Found Error\r\nContent-Length: 0\r\n\r\n";
        write(response);
    }

    public void sendServerError() throws IOException {
        final var body = StaticResourceResolver.read("/500.html");
        if (body == null) {
            sendRawServerError();
            return;
        }
        final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        this.headers.addHeader("Content-Type", "text/html;charset=utf-8");
        this.headers.addHeader("Content-Length", String.valueOf(bodyBytes.length));

        final var response = buildResponse("500 Internal Server Error", body, this.headers);
        write(response);
    }

    private void sendRawServerError() throws IOException {
        final var response = "HTTP/1.1 500 Internal Server Error\r\nContent-Length: 0\r\n\r\n";
        write(response);
    }

    private void write(final String response) throws IOException {
        this.outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        this.outputStream.flush();
    }

    private String buildResponse(final String statusCode, final String body, final ResponseHeaders headers) {
        final StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append("\r\n");
        response.append(headers.toHeaderString());
        response.append("\r\n");
        response.append(body);
        return response.toString();
    }
}
