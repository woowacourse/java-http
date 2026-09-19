package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.http11.BadRequestException;

public class HttpHeaders {
    private static final String HEADER_DELIMITER = ":";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String COOKIE = "Cookie";
    private static final String PARAMETER_DELIMITER = ";";

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = Map.copyOf(headers);
    }

    public static HttpHeaders empty() {
        return new HttpHeaders(Map.of());
    }

    public static HttpHeaders from(List<String> headerLines) {
        Map<String, String> headers = new HashMap<>();

        for (String headerLine : headerLines) {
            String[] headerParts = parseHeaderLine(headerLine);
            headers.put(normalize(headerParts[0]), headerParts[1]);
        }

        return new HttpHeaders(headers);
    }

    public String get(String name) {
        return headers.get(normalize(name));
    }

    public HttpCookie getCookie() {
        return HttpCookie.from(get(COOKIE));
    }

    public String getMediaType() {
        String contentType = get(CONTENT_TYPE);
        if (contentType == null) {
            return null;
        }

        return contentType.split(PARAMETER_DELIMITER)[0].trim().toLowerCase(Locale.ROOT);
    }

    public int getContentLength() {
        String contentLength = get(CONTENT_LENGTH);
        if (contentLength == null) {
            return 0;
        }

        try {
            int length = Integer.parseInt(contentLength);
            if (length < 0) {
                throw new BadRequestException("잘못된 Content-Length 값입니다: " + contentLength);
            }
            return length;
        } catch (NumberFormatException e) {
            throw new BadRequestException("잘못된 Content-Length 값입니다: " + contentLength, e);
        }
    }

    private static String[] parseHeaderLine(String headerLine) {
        String[] headerParts = headerLine.split(HEADER_DELIMITER, 2);

        if (headerParts.length != 2 || headerParts[0].isBlank()) {
            throw new BadRequestException("잘못된 http 헤더 형태입니다: " + headerLine);
        }

        return new String[]{headerParts[0].trim(), headerParts[1].trim()};
    }

    private static String normalize(String name) {
        return name.toLowerCase(Locale.ROOT);
    }
}
