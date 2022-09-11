package org.apache.coyote.http11.response;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.coyote.http11.constant.HttpContent;
import org.apache.coyote.http11.constant.HttpHeader;
import org.apache.coyote.http11.constant.HttpStatus;
import org.apache.coyote.http11.cookie.Cookie;

public class HttpResponse {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String HEADER_DELIMITER = ": ";

    private final HttpStatusLine httpStatusLine;
    private final Map<String, String> responseHeader;
    private final Cookie cookie;
    private String body;

    public HttpResponse() {
        this.httpStatusLine = new HttpStatusLine();
        this.responseHeader = new LinkedHashMap<>();
        this.cookie = new Cookie();
    }

    public void body(String body) {
        this.body = body;
    }

    public void statusCode(HttpStatus status) {
        httpStatusLine.status(status);
    }

    public void addHeader(String headerName, String value) {
        responseHeader.put(headerName, value);
    }

    public void addCookie(String cookieKey, String cookieValue) {
        cookie.setCookie(cookieKey, cookieValue);
    }

    public String toMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(httpStatusLine.getStatusLineMessage() + " \r\n");
        for (Entry<String, String> entry : responseHeader.entrySet()) {
            stringBuilder.append(entry.getKey() + HEADER_DELIMITER + entry.getValue() + " \r\n");
        }

        if (!cookie.isEmpty()) {
            stringBuilder.append(SET_COOKIE + HEADER_DELIMITER + cookie.generateCookieEntries());
        }

        stringBuilder.append("\r\n");
        stringBuilder.append(body);

        return stringBuilder.toString();
    }
}
