package org.apache.response;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.common.ContentType;
import org.apache.common.HttpStatus;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String body;
    private final HashMap<String, String> headers;

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, String body) {
        this.headers = new LinkedHashMap<>();
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
        headers.put(CONTENT_TYPE, contentType.getValue() + ";charset=utf-8 ");
        headers.put(CONTENT_LENGTH, body.getBytes().length + " ");
    }

    public String getResponse() {
        return String.join("\r\n",
                HTTP_VERSION + httpStatus.getCode() + " " + httpStatus.name() + " ",
                parseHeaders(),
                "",
                body);
    }

    public String parseHeaders() {
        List<String> headerLines = headers.entrySet().stream()
                .map(entrySet -> entrySet.getKey() + ": " + entrySet.getValue())
                .collect(Collectors.toList());
        return String.join("\r\n", headerLines);
    }

    public void setCookie(String value) {
        headers.put(SET_COOKIE, value);
    }
}
