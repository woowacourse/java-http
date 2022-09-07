package org.apache.coyote.model.response;

import org.apache.coyote.model.session.Cookie;

import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {

    public static final String SET_COOKIE = "Set-Cookie";
    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> responseHeader;

    private ResponseHeader(final Map<String, String> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public static ResponseHeader of(final Map<String, String> responseHeader) {
        return new ResponseHeader(responseHeader);
    }

    public void addCookie(final Cookie cookie) {
        responseHeader.put(SET_COOKIE, cookie.toString());
    }

    public String getResponseHeaders() {
        return responseHeader.keySet()
                .stream()
                .map(key -> key + HEADER_DELIMITER + responseHeader.get(key))
                .collect(Collectors.joining(" \r\n"));
    }
}
