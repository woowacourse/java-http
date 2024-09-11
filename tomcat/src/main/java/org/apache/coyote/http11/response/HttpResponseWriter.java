package org.apache.coyote.http11.response;

import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

public class HttpResponseWriter {
    private static final String RESPONSE_LINE_DELIMITER = "\r\n";

    private HttpResponseWriter() {
    }

    public static void write(OutputStream outputStream, HttpResponse response) throws IOException {
        String responseText = new StringJoiner(RESPONSE_LINE_DELIMITER)
                .add(createStartLine(response))
                .add(createHeaders(response))
                .add("")
                .add(response.getBody())
                .toString();
        outputStream.write(responseText.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private static String createStartLine(HttpResponse response) {
        return new StringJoiner(" ")
                .add(response.getHttpVersion())
                .add(Integer.toString(response.getStatusCode()))
                .add(response.getStatusMessage())
                .toString();
    }

    private static String createHeaders(HttpResponse response) {
        return response.getHeaders()
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(joining(RESPONSE_LINE_DELIMITER));
    }
}
