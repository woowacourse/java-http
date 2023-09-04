package org.apache.response;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.common.ContentType;
import org.apache.common.HttpStatus;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1 ";
    private static final String SET_COOKIE = "Set-Cookie";

    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final String body;
    private final HashMap<String, String> headers;

    public HttpResponse(HttpStatus httpStatus, ContentType contentType, String body) {
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.body = body;
        this.headers = new HashMap<>();
    }

    public String getResponse() {
        return String.join("\r\n",
                HTTP_VERSION + httpStatus.getCode() + httpStatus.name() + "",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
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
