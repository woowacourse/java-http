package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String HEADER_DELIMITER = ": ";
    private static final String SPACE = " ";

    private final StatusLine statusLine;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private String body;

    public HttpResponse(HttpRequest request) {
        this(new StatusLine(request.getHttpVersion()), "");
    }

    private HttpResponse(StatusLine statusLine, String body) {
        this.statusLine = statusLine;
        this.body = body;
    }

    public void sendRedirect(String path) {
        statusLine.setHttpStatus(HttpStatus.FOUND);
        addHeader(HttpHeaders.LOCATION, path);
    }

    public void addCookie(HttpCookie httpCookie) {
        addHeader(HttpHeaders.SET_COOKIE, httpCookie.getCookieToMessage());
    }

    public void addHeader(String header, String value) {
        headers.put(header, value);
    }

    public boolean notHasLocation() {
        return headers.keySet().stream()
                .noneMatch(key -> key.equals(HttpHeaders.LOCATION));
    }

    public String convertToMessage() {
        String headerMessages = headers.entrySet().stream().map(this::formatHeaderEntry)
                .collect(Collectors.joining(CRLF));

        return String.join(
                CRLF,
                statusLine.convertToMessage(),
                headerMessages,
                "",
                body
        );
    }

    private String formatHeaderEntry(Map.Entry<String, String> entry) {
        return entry.getKey() + HEADER_DELIMITER + entry.getValue() + SPACE;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
