package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class ResponseHeader {

    private final Map<String, String> elements;

    private ResponseHeader(final Map<String, String> elements) {
        this.elements = elements;
    }

    public static ResponseHeader from(final ResponseBody responseBody) {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", responseBody.getHttpExtensionType().getContentType());
        headers.put("Content-Length", String.valueOf(responseBody.getLength()));
        return new ResponseHeader(headers);
    }

    public void addCookie(final HttpCookie cookie) {
        elements.put("Set-Cookie", cookie.toString());
    }

    public String getValue(final String key) {
        if (elements.containsKey(key)) {
            return elements.get(key);
        }
        return "";
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        elements.forEach((key, value) -> stringBuilder.append(key).append(": ").append(value).append(" \r\n"));
        return stringBuilder.toString();
    }
}
