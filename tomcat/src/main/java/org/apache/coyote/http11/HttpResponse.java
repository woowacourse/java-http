package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpStatus.OK;

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

    private final HttpStatus httpStatus;
    private final Map<String, String> headers;
    private final HttpCookie httpCookie;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, Map<String, String> headers, String body) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.httpCookie = HttpCookie.empty();
        this.body = body;
    }

    public static HttpResponse from(HttpStatus httpStatus) {
        return new HttpResponse(httpStatus, new LinkedHashMap<>(), EMPTY);
    }

    public static HttpResponse from(HttpStatus httpStatus, ContentType contentType, String body) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, contentType.value());
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return new HttpResponse(httpStatus, headers, body);
    }

    public static HttpResponse ok(String body) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, ContentType.TEXT_HTML.value());
        headers.put(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        return new HttpResponse(OK, headers, body);
    }

    public void setContentType(ContentType contentType) {
        headers.put(CONTENT_TYPE, contentType.value());
    }

    public void sendRedirect(String redirectUri) {
        headers.put(LOCATION, redirectUri);
    }

    public void addCookie(String cookieName, String cookieValue) {
        httpCookie.addCookie(cookieName, cookieValue);
    }

    public byte[] getBytes() {
        List<String> response = new ArrayList<>();
        response.add(HTTP_VERSION + BLANK + httpStatus.statusCode() + BLANK + httpStatus + BLANK);
        response.add(getHeaders());
        httpCookie.getCookie(JSESSIONID).ifPresent(jsessionid ->
                response.add(SET_COOKIE + COLON + JSESSIONID + EQUAL + jsessionid + BLANK)
        );
        response.add(EMPTY);
        response.add(body);
        String join = String.join(SEPARATOR, response);
        return join.getBytes();
    }

    private String getHeaders() {
        List<String> formattedHeaders = headers.keySet().stream()
                .map(key -> key + COLON + headers.get(key) + BLANK)
                .collect(Collectors.toList());
        return String.join(SEPARATOR, formattedHeaders);
    }
}
