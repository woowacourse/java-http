package org.apache.coyote.http11.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class HttpResponseWriter {

    private HttpResponseWriter() {}

    public static String ok(String mimeType, String body, Map<String, String> extraHeaders) {
        return buildResponse("200 OK", mimeType, body, extraHeaders);
    }

    public static String redirect(String location, Map<String, String> extraHeaders) {
        extraHeaders.put("Location", location);
        return buildResponse("302 Found", "text/html", "", extraHeaders);
    }

    public static String notFound(String body, Map<String, String> extraHeaders) {
        return buildResponse("404 Not Found", "text/html", body, extraHeaders);
    }

    public static String serverError(String body, Map<String, String> extraHeaders) {
        return buildResponse("500 Internal Server Error", "text/html", body, extraHeaders);
    }

    public static void write(OutputStream outputStream, String response) throws IOException {
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String buildResponse(String statusCode,
                                        String mimeType,
                                        String body,
                                        Map<String, String> extraHeaders) {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append("\r\n");
        response.append("Content-Type: ").append(mimeType).append(";charset=utf-8\r\n");
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        response.append("Content-Length: ").append(bodyBytes.length).append("\r\n");

        if (extraHeaders != null) {
            for (Map.Entry<String, String> header : extraHeaders.entrySet()) {
                response.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
            }
        }

        response.append("\r\n");
        response.append(body);
        return response.toString();
    }
}
