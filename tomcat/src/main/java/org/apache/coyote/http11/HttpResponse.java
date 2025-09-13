package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.coyote.http11.cookie.HttpCookie;

public final class HttpResponse {

    private static final String CRLF = "\r\n";
    private static final String RESPONSE_LINE_FORMAT = "%s %d %s\r\n";
    public static final String JSESSIONID = "JSESSIONID";
    private static final String EQUAL = "=";
    private static final String SET_COOKIE = "Set-Cookie";

    private HttpStatus status;
    private Map<String, String> headers;
    private byte[] body;

    public HttpResponse(
            HttpStatus status,
            Map<String, String> headers,
            byte[] body
    ) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponse create() {
        return new HttpResponse(HttpStatus.OK, new HashMap<>(), new byte[0]);
    }

    public void redirect(String location) {
        this.status = HttpStatus.FOUND;
        addHeader("Location", location);
    }

    public String asHeaderString() {
        StringBuilder response = new StringBuilder();

        int statusCode = status.code();
        String message = status.reason();
        response.append(String.format(RESPONSE_LINE_FORMAT, HttpVersion.HTTP_1_1.getName(), statusCode, message));

        for (Entry<String, String> header : getHeaders().entrySet()) {
            response.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append(CRLF);
        }
        response.append(CRLF);

        return response.toString();
    }

    public void addHeader(final String key, final String value) {
        headers.put(key, value);
    }

    public String addSessionIfAbsent() {
        String uuid = UUID.randomUUID().toString();
        String totalSetCookie = JSESSIONID + EQUAL + uuid;

        addCookie(SET_COOKIE, totalSetCookie);

        return uuid;
    }

    private void addCookie(final String key, final String value) {
        headers.put(key, value);
    }

    public void setStatus(final HttpStatus httpStatus) {
        this.status = httpStatus;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setBody(final byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }
}
