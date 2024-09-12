package org.apache.coyote.http.response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.apache.coyote.http.Cookie;
import org.apache.coyote.http.Header;
import org.apache.coyote.http.StatusCode;

public class HttpResponse {

    private static final String DEFAULT_PROTOCOL = "HTTP/1.1";
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String BLANK_SPACE = " ";

    private final String protocol;
    private StatusCode status;
    private final Map<String, String> headers;
    private Cookie cookies;
    private String body;

    private HttpResponse(String protocol, StatusCode status, Map<String, String> headers, Cookie cookies, String body) {
        this.protocol = protocol;
        this.status = status;
        this.headers = headers;
        this.cookies = cookies;
        this.body = body;
    }

    public HttpResponse(StatusCode status, Map<String, String> headers, String body) {
        this(DEFAULT_PROTOCOL, status, headers, Cookie.empty(), body);
    }

    public static HttpResponse create() {
        return new HttpResponse(StatusCode.OK, new HashMap<>(), "");
    }

    public byte[] serialize() {
        this.setHeader(Header.CONTENT_LENGTH.value(), String.valueOf(body.getBytes().length));
        return this.getBytes();
    }

    public void sendRedirect(String path) {
        setStatus(StatusCode.FOUND);
        setHeader(Header.LOCATION.value(), path);
    }

    public void setStatus(StatusCode status) {
        this.status = status;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public void setBody(byte[] responseBody) {
        this.body = new String(responseBody);
    }

    public byte[] getBytes() {
        String statusLine = getStatusLine();
        List<String> headerLines = getHeaderLines();
        String bodyLines = getBodyLines();

        return String.join(
                LINE_SEPARATOR,
                Stream.concat(Stream.of(statusLine),
                              Stream.concat(
                                      headerLines.stream(),
                                      Stream.of(bodyLines)
                              )
                ).toArray(String[]::new)
        ).getBytes();
    }

    private String getStatusLine() {
        return protocol + BLANK_SPACE + status + BLANK_SPACE;
    }

    private List<String> getHeaderLines() {
        return headers.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + ": " + entry.getValue() + BLANK_SPACE).toList();
    }

    private String getBodyLines() {
        return LINE_SEPARATOR + body;
    }

    public void setCookie(Cookie cookie) {
        this.cookies = cookie;
    }

    @Override
    public String toString() {
        return "Response{" +
               "protocol='" + protocol + '\'' +
               ", status=" + status +
               ", headers=" + headers +
               ", cookies=" + cookies.toString() +
               '}';
    }
}
