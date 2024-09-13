package org.apache.coyote.http11;

import java.util.Map.Entry;
import java.util.Objects;

public class HttpResponseWriter {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private HttpResponseWriter() {
    }

    public static byte[] writeAsBytes(HttpResponse response) {
        StatusCode statusCode = response.getStatusCode();
        HttpHeaders headers = response.getHeaders();
        String content = response.getContent();
        validateNonNull(statusCode, headers, content);
        return writeAsString(statusCode, headers, content).getBytes();
    }

    private static void validateNonNull(StatusCode statusCode, HttpHeaders headers, String content) {
        Objects.requireNonNull(statusCode, "StatusCode must not be null.");
        Objects.requireNonNull(headers, "Headers must not be null.");
        Objects.requireNonNull(content, "Content must not be null");
    }

    private static String writeAsString(StatusCode statusCode, HttpHeaders headers, String content) {
        StringBuilder builder = new StringBuilder();
        builder.append("%s %s %s".formatted(HTTP_VERSION, statusCode.getCode(), statusCode.getMessage()));
        builder.append(CRLF);
        for (Entry<String, String> entry : headers.getFields().entrySet()) {
            builder.append("%s: %s".formatted(entry.getKey(), entry.getValue()));
            builder.append(CRLF);
        }
        builder.append(CRLF);
        builder.append(content);
        return builder.toString();
    }
}
