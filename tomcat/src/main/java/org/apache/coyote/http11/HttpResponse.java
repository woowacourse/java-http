package org.apache.coyote.http11;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final Map<HttpHeaders, String> DEFAULT_HTTP_HEADERS = new EnumMap<>(HttpHeaders.class);
    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";
    private static final String SPACE = " ";

    private final StatusLine statusLine;
    private final String responseBody;
    private final Map<HttpHeaders, String> headers;

    public HttpResponse(String responseBody, MediaType mediaType) {
        this(new StatusLine(), responseBody, DEFAULT_HTTP_HEADERS);
        addHeader(HttpHeaders.CONTENT_TYPE, mediaType.getValue());
        addHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
    }

    public HttpResponse(StatusLine statusLine, String responseBody, Map<HttpHeaders, String> headers) {
        this.statusLine = statusLine;
        this.responseBody = responseBody;
        this.headers = headers;
    }

    public void addHeader(HttpHeaders header, String value) {
        headers.put(header, value);
    }

    public byte[] convertToBytes() {
        StringBuilder stringBuilder = new StringBuilder();
        String headerMessages = headers.entrySet().stream()
                .map(this::formatHeaderEntry)
                .collect(Collectors.joining(CRLF));

        stringBuilder.append(statusLine.getStatusLineMessage())
                .append(CRLF)
                .append(headerMessages)
                .append(CRLF)
                .append(CRLF)
                .append(responseBody);

        return stringBuilder.toString().getBytes();
    }

    private String formatHeaderEntry(Map.Entry<HttpHeaders, String> entry) {
        return entry.getKey().getHeader() + HEADER_DELIMITER + entry.getValue() + SPACE;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
