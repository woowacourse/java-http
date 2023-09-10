package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String LOCATION = "Location";
    private static final String EMPTY = "";
    private static final String BLANK = " ";
    private static final String COLON = ": ";
    private static final String EQUAL = "=";
    private static final String SEPARATOR = "\r\n";

    private final Map<String, String> headers = new LinkedHashMap<>();
    private final HttpCookie httpCookie = HttpCookie.empty();
    private HttpStatus httpStatus;
    private String body;

    public void setStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setContentType(ContentType contentType) {
        headers.put(CONTENT_TYPE, contentType.value());
    }

    public void addCookie(String name, String value) {
        httpCookie.addCookie(name, value);
    }

    public void sendRedirect(String redirectUri) {
        headers.put(LOCATION, redirectUri);
    }

    public void setBody(String body) {
        this.body = body;
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public byte[] getBytes() {
        List<String> response = new ArrayList<>();
        response.add(HTTP_VERSION + BLANK + httpStatus.statusCode() + BLANK + httpStatus + BLANK);
        response.add(stringify(headers));
        httpCookie.getCookie(JSESSIONID)
                .ifPresent(id -> response.add(SET_COOKIE + COLON + JSESSIONID + EQUAL + id + BLANK));
        response.add(EMPTY);
        response.add(body);
        return String.join(SEPARATOR, response).getBytes();
    }

    private String stringify(Map<String, String> headers) {
        List<String> formattedHeaders = headers.keySet().stream()
                .map(key -> key + COLON + headers.get(key) + BLANK)
                .collect(Collectors.toList());
        return String.join(SEPARATOR, formattedHeaders);
    }
}
