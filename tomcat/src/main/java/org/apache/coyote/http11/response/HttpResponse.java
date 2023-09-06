package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.ResponseHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.response.ResponseHeader.CONTENT_TYPE;
import static org.apache.coyote.http11.utils.Constant.EMPTY;
import static org.apache.coyote.http11.utils.Constant.HEADER_DELIMITER;
import static org.apache.coyote.http11.utils.Constant.LINE_SEPARATOR;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.exception.PageNotFoundException;

public class HttpResponse {

    private final Status status;
    private final Map<String, String> headers;
    private final String body;

    public HttpResponse(final Status status, final String body) {
        this.status = status;
        this.headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE.getName(), "text/html;charset=utf-8 ");

        this.body = body;
        if (body.length() > 0) {
            headers.put(CONTENT_LENGTH.getName(), body.getBytes().length + " ");
        }
    }

    public HttpResponse(final Status status) {
        this(status, EMPTY);
    }

    public HttpResponse(final Status status, final URL resource) {
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

    public void addHeader(final ResponseHeader header, final String value) {
        headers.put(header.getName(), value);
    }

    public String getResponse() {
        final String formattedStatus = status.getStatusLine();
        final String formattedHeaders = headers.keySet().stream()
                .map(this::formatHeader)
                .collect(Collectors.joining(LINE_SEPARATOR));

        return String.join(LINE_SEPARATOR,
                "HTTP/1.1 " + formattedStatus,
                formattedHeaders,
                EMPTY,
                body);
    }

    private String formatHeader(final String header) {
        return header + HEADER_DELIMITER + headers.get(header);
    }
}
