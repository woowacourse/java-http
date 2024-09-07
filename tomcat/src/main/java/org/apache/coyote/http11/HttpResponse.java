package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse<T> {

    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";
    private static final String SPACE = " ";

    private final StatusLine statusLine;
    private final T body;
    private final Map<String, String> headers = new LinkedHashMap<>();

    public HttpResponse(HttpStatus httpStatus, T body, Map<String, String> headers) {
        this(new StatusLine(httpStatus), body);
        headers.forEach(this::addHeader);
    }

    public HttpResponse(StatusLine statusLine, T body, Map<String, String> headers) {
        this(statusLine, body);
        headers.forEach(this::addHeader);
    }

    public HttpResponse(StatusLine statusLine, T body) {
        this.statusLine = statusLine;
        this.body = body;
    }

    public void addHeader(String header, String value) {
        headers.put(header, value);
    }

    public HttpResponse<String> getFileResponse(String body) {
        return new HttpResponse<>(statusLine, body, headers);
    }

    public String convertToMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        String headerMessages = headers.entrySet().stream().map(this::formatHeaderEntry)
                .collect(Collectors.joining(CRLF));

        stringBuilder.append(statusLine.getStatusLineMessage()).append(CRLF).append(headerMessages).append(CRLF)
                .append(CRLF).append(body);

        return stringBuilder.toString();
    }

    private String formatHeaderEntry(Map.Entry<String, String> entry) {
        return entry.getKey() + HEADER_DELIMITER + entry.getValue() + SPACE;
    }

    public T getBody() {
        return body;
    }
}
