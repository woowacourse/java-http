package web.response;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import web.request.RequestUri;
import web.util.StaticResourceFinder;

public class HttpResponse {

    public static final String EMPTY_VALUE = "EMPTY_VALUE";
    public static final String SPACE = " ";
    public static final String MESSAGE_BOUNDARY = "";
    public static final String HEADER_ELEMENT_DELIMITER = ": ";

    private StatusLine statusLine;
    private final Map<String, String> header;
    private String body;

    public HttpResponse() {
        this.statusLine = new StatusLine(EMPTY_VALUE, EMPTY_VALUE, EMPTY_VALUE);
        this.header = new LinkedHashMap<>();
        this.body = EMPTY_VALUE;
    }

    public void setStaticResource(final RequestUri requestUri) {
        try {
            Optional<String> staticResource = StaticResourceFinder.findStaticResource(requestUri);
            if (staticResource.isEmpty()) {
                throw new RuntimeException("[ERROR] 리소스가 존재하지 않습니다.");
            }
            String body = staticResource.get();
            setBody(body);
            setStatusLine(new StatusLine("HTTP/1.1", "200", "OK"));
            putHeader(
                    "Content-Type",
                    "text/" + requestUri.findStaticResourceType().get() + ";charset=utf-8"
            );
            putHeader("Content-Length", String.valueOf(body.getBytes().length));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void set302Redirect(final String location) {
        String body = "";
        setBody(body);
        setStatusLine(new StatusLine("HTTP/1.1", "302", "FOUND"));
        putHeader("Location", location);
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
