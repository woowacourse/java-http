package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.exception.PageNotFoundException;

public class Http11Response {
    private static final String LINE_SEPARATOR = "\r\n";

    final Status status;
    final Map<String, String> headers;
    final String body;

    public Http11Response(final Status status, final String body) {
        this.status = status;
        this.headers = new LinkedHashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8 ");

        this.body = body;
        if (body.length() > 0) {
            headers.put("Content-Length", body.getBytes().length + " ");
        }
    }

    public Http11Response(final Status status) {
        this(status, "");
    }

    public Http11Response(final Status status, final URL resource) {
        this(status, readResponseBody(resource));
    }

    private static String readResponseBody(final URL resource) {
        try {
            final Path filePath = new File(resource.getFile()).toPath();
            return new String(Files.readAllBytes(filePath));

        } catch (final NullPointerException | IOException e) {
            throw new PageNotFoundException(resource.toString());
        }
    }

    public void addHeader(final String headerName, final String value) {
        headers.put(headerName, value);
    }

    public String getResponse() {
        final String formattedStatus = formatStatus(status);
        final String formattedHeaders = headers.keySet().stream()
                .map(this::formatHeader)
                .collect(Collectors.joining(LINE_SEPARATOR));

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 " + formattedStatus,
                formattedHeaders,
                "",
                body);
    }

    private String formatStatus(final Status status) {
        return status.getCode() + " " + status.getName() + " ";
    }

    private String formatHeader(final String header) {
        return header + ": " + headers.get(header);
    }
}
