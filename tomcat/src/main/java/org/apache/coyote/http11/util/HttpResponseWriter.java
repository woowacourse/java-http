package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.response.ResponseHeaders;

public final class HttpResponseWriter {

    private HttpResponseWriter() {}

    public static String ok(final String mimeType, final String body, final ResponseHeaders extraHeaders) {
        final var headers = new ResponseHeaders();
        final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        headers.addHeader("Content-Type", mimeType + ";charset=utf-8");
        headers.addHeader("Content-Length", String.valueOf(bodyBytes.length));
        headers.addAll(extraHeaders);

        return buildResponse("200 OK", body, headers);
    }

    public static String redirect(final String location, final ResponseHeaders extraHeaders) {
        final var headers = new ResponseHeaders();
        headers.addHeader("Location", location);
        headers.addAll(extraHeaders);

        return buildResponse("302 Found", "", headers);
    }

    public static String notFound(final String body) {
        return ok("text/html", body, ResponseHeaders.empty());
    }

    public static String serverError(final String body) {
        final var headers = new ResponseHeaders();
        final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        headers.addHeader("Content-Type", "text/html;charset=utf-8");
        headers.addHeader("Content-Length", String.valueOf(bodyBytes.length));

        return buildResponse("500 Internal Server Error", body, headers);
    }

    public static void write(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String buildResponse(final String statusCode, final String body, final ResponseHeaders headers) {
        final var response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append("\r\n");
        response.append(headers.toHeaderString());
        response.append("\r\n");
        response.append(body);
        return response.toString();
    }
}
