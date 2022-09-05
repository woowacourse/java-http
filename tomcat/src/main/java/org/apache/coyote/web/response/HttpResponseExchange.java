package org.apache.coyote.web.response;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpStatus;

public class HttpResponseExchange {

    private static final String DEFAULT_VERSION = "HTTP/1.1";
    protected static final String HEADER_TEMPLATE = "%s: %s \r\n";

    private final OutputStream outputStream;

    public HttpResponseExchange(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void response(final HttpStatus httpStatus,
                         final HttpHeaders httpHeaders,
                         final String responseBody) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(parseStartLine(httpStatus))
                .append(parseHeaders(httpHeaders))
                .append("\r\n")
                .append(responseBody);
        String httpMessage = stringBuilder.toString();
        outputStream.write(httpMessage.getBytes(StandardCharsets.UTF_8));
    }

    private String parseStartLine(final HttpStatus httpStatus) {
        return String.format("%s %d %s \r\n", DEFAULT_VERSION, httpStatus.getStatusCode(),
                httpStatus.getMessage());
    }

    private String parseHeaders(final HttpHeaders httpHeaders) {
        return httpHeaders.getHeaders()
                .entrySet().stream()
                .map(entry -> String.format(HEADER_TEMPLATE, entry.getKey(), entry.getValue()))
                .collect(Collectors.joining());
    }
}
