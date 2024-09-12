package org.apache.coyote.http11.file;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class ResponseFactory {
    private static final String HTTP_LINE_SEPARATOR = "\r\n";
    private static final String STATUS_LINE_DELIMITER = " ";
    private static final String HEADER_LINE_DELIMITER = ": ";
    private static final String STATIC_PATH = "static";

    public static void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        final String rawResponse = String.join(HTTP_LINE_SEPARATOR,
                writeStatusLine(response),
                writeResponseHeaders(response),
                "",
                writeResponseBody(response));

        outputStream.write(rawResponse.getBytes());
    }

    private static String writeStatusLine(HttpResponse response) {
        return String.join(STATUS_LINE_DELIMITER, response.getVersion(), response.getStatusCode(), response.getStatusMessage());
    }

    private static String writeResponseHeaders(HttpResponse response) {
        Map<String, String> headers = response.getResponseHeader();
        return headers.entrySet().stream()
                .map(entry -> entry.getKey() + HEADER_LINE_DELIMITER + entry.getValue())
                .collect(Collectors.joining(HTTP_LINE_SEPARATOR));
    }

    private static String writeResponseBody(HttpResponse response) {
        return response.getBody();
    }

    public static String getResponseBody(HttpRequest request) throws IOException {
        String requestUri = request.getRequestUri();
        ClassLoader classLoader = RequestFactory.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(STATIC_PATH + requestUri);
        if (Objects.isNull(resourceUrl)) {
            return "";
        }

        File file = new File(resourceUrl.getFile());
        return new String(Files.readAllBytes(file.toPath()));
    }
}
