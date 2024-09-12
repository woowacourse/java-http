package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.stream.Collectors;

public class ResponseFactory {
    private static final String HTTP_LINE_SEPARATOR = "\r\n";
    private static final String HEADER_LINE_DELIMITER = ": ";

    public static String writeResponse(HttpResponse response) {
        return String.join(HTTP_LINE_SEPARATOR,
                writeStatusLine(response),
                writeHeaders(response),
                "",
                writeBody(response));
    }

    private static String writeStatusLine(HttpResponse response) {
        return String.join(" ", response.getVersion(), response.getStatusCode(), response.getStatusMessage());
    }

    private static String writeHeaders(HttpResponse response) {
        Map<String, String> headers = response.getResponseHeader();
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + HEADER_LINE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(HTTP_LINE_SEPARATOR));
    }

    private static String writeBody(HttpResponse response) {
        return response.getBody();
    }
}
