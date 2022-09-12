package web.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String EMPTY_VALUE = "EMPTY_VALUE";
    private static final String SPACE = " ";
    private static final String MESSAGE_BOUNDARY = "";
    private static final String HEADER_ELEMENT_DELIMITER = ": ";

    private StatusLine statusLine;
    private final Map<String, String> header;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE);
        this.header = new LinkedHashMap<>();
        this.body = EMPTY_VALUE;
    }

    public void putHeader(final String key, final String value) {
        this.header.put(key, value);
    }

    public String createMessage() {
        String statusLine = this.statusLine.getHttpVersion() + SPACE +
                this.statusLine.getStatusCode() + SPACE +
                this.statusLine.getReasonPhrase() + SPACE;
        String header = this.header.keySet().stream()
                .map(i -> i + HEADER_ELEMENT_DELIMITER + this.header.get(i) + SPACE)
                .collect(Collectors.joining("\r\n"));
        return String.join("\r\n", statusLine, header, MESSAGE_BOUNDARY, body);
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setBody(final String body) {
        this.body = body;
    }
}
