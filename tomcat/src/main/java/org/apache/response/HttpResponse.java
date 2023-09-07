package org.apache.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CHARACTER_SET = ";charset=utf-8 ";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SPACE = " ";
    private static final String LINE_SEPARATOR = "";
    private static final String HEADER_JOINER = ": ";

    private final HttpStatus httpStatus;
    private final String body;
    private final HashMap<String, String> headers;

    public HttpResponse(HttpStatus httpStatus, String body) {
        this.httpStatus = httpStatus;
        this.body = body;
        this.headers = new LinkedHashMap<>();
        headers.put(CONTENT_LENGTH, body.getBytes().length + SPACE);
    }

    public String getResponse() {
        return String.join("\r\n",
                HTTP_VERSION + httpStatus.getCode() + SPACE + httpStatus.name() + SPACE,
                parseHeaders(),
                LINE_SEPARATOR,
                body);
    }

    public String parseHeaders() {
        List<String> headerLines = headers.entrySet().stream()
                .map(entrySet -> entrySet.getKey() + HEADER_JOINER + entrySet.getValue())
                .collect(Collectors.toList());
        return String.join("\r\n", headerLines);
    }

    public void setCookie(String cookie) {
        headers.put(SET_COOKIE, cookie);
    }

    public void setLocation(String location) {
        headers.put(LOCATION, location);
    }

    public void setContentType(ContentType contentType) {
        headers.put(CONTENT_TYPE, contentType.getValue() + CHARACTER_SET);
    }
}
