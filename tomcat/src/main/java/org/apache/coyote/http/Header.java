package org.apache.coyote.http;

import java.util.Map;
import java.util.Map.Entry;

public class Header {

    private static final String MESSAGE_DELIMITER = " : ";
    private static final String BLANK = " ";

    private HttpCookie cookie;
    private Map<String, String> headerMap;

    public Header(final Map<String, String> header) {
        this.cookie = HttpCookie.from(header.get("Cookie"));
        this.headerMap = header;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public HttpCookie getCookie() {
        return cookie;
    }

    public String getHeaderMapForMessage() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> entry : headerMap.entrySet()) {
            stringBuilder.append(entry.getKey() + MESSAGE_DELIMITER + entry.getValue() + BLANK);
        }
        return stringBuilder.toString();
    }
}
