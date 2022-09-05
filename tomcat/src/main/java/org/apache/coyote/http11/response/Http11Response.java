package org.apache.coyote.http11.response;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.coyote.http11.Cookie;

public class Http11Response {

    private static final String SET_COOKIE = "Set-Cookie";
    private static final String HEADER_DELIMITER = ": ";

    private final String protocolVersion;
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> responseHeader;
    private final Cookie cookie;
    private final String body;

    public Http11Response(String protocolVersion, int statusCode, String statusMessage,
                          Map<String, String> responseHeader,
                          String body) {
        this.protocolVersion = protocolVersion;
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.responseHeader = responseHeader;
        this.body = body;
        this.cookie = new Cookie();
    }

    public Http11Response(int statusCode, String statusMessage,
                          Map<String, String> responseHeader,
                          String body) {
        this("HTTP/1.1", statusCode, statusMessage, responseHeader, body);
    }

    public String toMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(protocolVersion + " " + statusCode + " " + statusMessage + " \r\n");
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

    public void addHeader(String headerName, String value) {
        responseHeader.put(headerName, value);
    }

    public void addCookie(String cookieKey, String cookieValue) {
        cookie.setCookie(cookieKey, cookieValue);
    }
}
