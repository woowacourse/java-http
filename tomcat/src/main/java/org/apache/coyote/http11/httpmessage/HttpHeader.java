package org.apache.coyote.http11.httpmessage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeader {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String CRLF = "\r\n";
    private static final String RESPONSE_FORMAT = "%s: %s ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> header;

    public HttpHeader(final Map<String, String> header) {
        this.header = header;
    }

    public static HttpHeader fromRequest(final String[] headers) {
        final Map<String, String> header = Arrays.stream(headers)
            .map(headerContent -> headerContent.split(HEADER_DELIMITER))
            .collect(Collectors.toMap(
                headerContent -> headerContent[KEY_INDEX],
                headerContent -> headerContent[VALUE_INDEX]));
        return new HttpHeader(header);
    }

    public String makeResponseForm() {
        return header.entrySet()
            .stream()
            .map(entry -> String.format(RESPONSE_FORMAT, entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(CRLF));
    }

    public HttpCookie getCookies() {
        if (header.containsKey(COOKIE)) {
            return HttpCookie.from(header.get(COOKIE));
        }
        return new HttpCookie(new HashMap<>());
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
