package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
    private HttpCookie httpCookie;
    private byte[] body;

    public HttpResponse(
            HttpStatus status,
            Map<String, String> headers,
            HttpCookie httpCookie,
            byte[] body
    ) {
        this.status = status;
        this.headers = headers;
        this.httpCookie = httpCookie;
        this.body = body;
    }

    public static HttpResponse create() {
        return new HttpResponse(HttpStatus.OK, new HashMap<>(), new HttpCookie(), new byte[0]);
    }

    public static HttpResponse redirect(String location) {
        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND, new LinkedHashMap<>(), new HttpCookie(),
                new byte[0]);
        httpResponse.addHeader("Location", location);

        return httpResponse;
    }

    public String asHeaderString() {
        StringBuilder response = new StringBuilder();

        int statusCode = status.code();
        String message = status.reason();
        response.append(String.format(RESPONSE_LINE_FORMAT, HttpVersion.HTTP_1_1.getName(), statusCode, message));

        for (Entry<String, String> header : headers().entrySet()) {
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

        addHeader(SET_COOKIE, totalSetCookie);
        return uuid;
    }

    public void setHttpResponse(HttpResponse response) {
        this.status = response.status;
        this.headers = response.headers;
        this.httpCookie = response.httpCookie;
        this.body = response.body;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public byte[] body() {
        return body;
    }
}
