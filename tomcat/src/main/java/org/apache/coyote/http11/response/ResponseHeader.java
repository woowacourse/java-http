package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.ContentType.HTML;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseHeader {
    private static final String DELIMITER = ": ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String REDIRECT_CONTENT_LENGTH = "0";
    private final Map<String, String> header;

    public ResponseHeader(final Map<String, String> header) {
        this.header = header;
    }

    public static ResponseHeader of(final ContentType contentType, final int contentLength) {
        final Map<String, String> header = new LinkedHashMap<>();
        header.put(CONTENT_TYPE, contentType.getType());
        header.put(CONTENT_LENGTH, String.valueOf(contentLength));
        return new ResponseHeader(header);
    }

    public static ResponseHeader generateRedirectHeader(final String location) {
        final Map<String, String> header = new LinkedHashMap<>();
        header.put(CONTENT_TYPE, HTML.getType());
        header.put(CONTENT_LENGTH, REDIRECT_CONTENT_LENGTH);
        header.put(LOCATION, location);
        return new ResponseHeader(header);
    }

    public String parse() {
        return header.keySet()
                .stream()
                .map(key -> String.join(DELIMITER, key, header.get(key) + " "))
                .collect(Collectors.joining("\r\n"));
    }

    public void setCookie(String cookie) {
        header.put("Set-Cookie", cookie);
    }
}
