package org.apache.coyote.httprequest.header;

import org.apache.coyote.httprequest.exception.InvalidHeaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {

    private static final Logger log = LoggerFactory.getLogger(RequestHeaders.class);

    private static final String DELIMITER = ": ";
    private static final String COOKIE_HEADER_NAME = "Cookie";
    private static final int PARSED_HEADER_SIZE = 2;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Map<RequestHeaderType, RequestHeader> headers;
    private final CookieRequestHeader cookieRequestHeader;

    private RequestHeaders(final Map<RequestHeaderType, RequestHeader> headers, final CookieRequestHeader cookieRequestHeader) {
        this.headers = headers;
        this.cookieRequestHeader = cookieRequestHeader;
    }

    public static RequestHeaders from(final String lines, final String delimiter) {
        log.debug("Request Headers: ");
        final Map<RequestHeaderType, RequestHeader> headers = new EnumMap<>(RequestHeaderType.class);
        CookieRequestHeader cookieHeader = CookieRequestHeader.blank();
        for(final String line : lines.split(delimiter)) {
            final List<String> parsedHeader = parseByDelimiter(line);
            final String headerName = parsedHeader.get(HEADER_KEY_INDEX);
            final String headerValue = parsedHeader.get(HEADER_VALUE_INDEX);
            if (headerName.equals(COOKIE_HEADER_NAME)) {
                cookieHeader = CookieRequestHeader.from(parsedHeader.get(HEADER_VALUE_INDEX));
                continue;
            }
            final RequestHeaderType headerType = RequestHeaderType.from(headerName);
            if (!headerType.isUnsupportedHeader()) {
                log.debug("\t\tHeader : {}", line);
                headers.put(headerType, headerType.saveRequestHeader(headerValue.trim()));
            } else log.trace("지원하지 않는 헤더 타입. 헤더 이름: {}, 헤더 값: {}", headerName, headerValue);
        }
        return new RequestHeaders(headers, cookieHeader);
    }

    private static List<String> parseByDelimiter(final String line) {
        final List<String> parsedRequestHeader = List.of(line.split(DELIMITER));
        if (parsedRequestHeader.size() != PARSED_HEADER_SIZE) {
            throw new InvalidHeaderException();
        }
        return parsedRequestHeader;
    }

    public boolean hasJSessionId() {
        return cookieRequestHeader.hasJSessionId();
    }

    public int getContentLength() {
        if (headers.containsKey(RequestHeaderType.CONTENT_LENGTH)) {
            return Integer.parseInt(headers.get(RequestHeaderType.CONTENT_LENGTH).getValue());
        }
        return 0;
    }

    public CookieRequestHeader getCookieHeader() {
        return this.cookieRequestHeader;
    }
}
