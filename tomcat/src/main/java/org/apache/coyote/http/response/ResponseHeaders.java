package org.apache.coyote.http.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.HttpHeader;

public class ResponseHeaders implements Assemblable {

    private static final String ENCODING = "charset=utf-8";
    private static final String CRLF = "\r\n";
    private static final String PREFIX = "";

    private final Map<String, String> headers;

    private final ResponseCookies cookies;

    protected ResponseHeaders() {
        this.headers = new LinkedHashMap<>();
        this.cookies = new ResponseCookies();
    }

    protected void setContentType(String contentType) {
        headers.put(HttpHeader.CONTENT_TYPE.value(), "%s;%s".formatted(contentType, ENCODING));
    }

    protected void setContentLength(int contentLength) {
        headers.put(HttpHeader.CONTENT_LENGTH.value(), String.valueOf(contentLength));
    }

    protected void setLocation(String location) {
        headers.put(HttpHeader.LOCATION.value(), location);
    }

    protected void addSessionCookie(Session session) {
        cookies.addSessionCookie(session);
    }

    @Override
    public void assemble(StringBuilder builder) {
        cookies.assemble(builder);
        builder.append(headers.entrySet()
                        .stream()
                        .map(this::convert)
                        .collect(Collectors.joining(CRLF, PREFIX, CRLF)))
                .append(CRLF);
    }

    private String convert(Entry<String, String> entry) {
        return "%s: %s ".formatted(entry.getKey(), entry.getValue());
    }
}
