package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class ResponseHeader {

    private static final String EMPTY_STRING = "";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final Map<String, String> elements;

    private ResponseHeader(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static ResponseHeader from(final ResponseBody responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put(CONTENT_TYPE, responseBody.getHttpExtensionType().getContentType());
        headers.put(CONTENT_LENGTH, String.valueOf(responseBody.getLength()));
        return new ResponseHeader(headers);
    }

    public void addCookie(final HttpCookie cookie) {
        elements.put(SET_COOKIE, cookie.toString());
    }

    public String getValue(final String key) {
        if (elements.containsKey(key)) {
            return elements.get(key);
        }
        return EMPTY_STRING;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        elements.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append(" \r\n"));
        return stringBuilder.toString();
    }
}
