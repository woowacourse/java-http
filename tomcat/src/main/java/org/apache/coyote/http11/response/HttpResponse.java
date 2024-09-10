package org.apache.coyote.http11.response;

import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.header.HttpHeader;

public class HttpResponse {
    private static final String DELIMITER = "\r\n";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK ";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 Found ";
    public static final String EMPTY_LINE = "";

    public static byte[] ok(final FileType fileType, final String responseBody) {
        return String.join(DELIMITER, OK_STATUS_LINE,
                HttpHeader.contentType(fileType),
                HttpHeader.contentLength(responseBody),
                EMPTY_LINE,
                responseBody
        ).getBytes();
    }

    public static byte[] found(final String location) {
        return String.join(DELIMITER, FOUND_STATUS_LINE,
                HttpHeader.location(location),
                EMPTY_LINE
        ).getBytes();
    }

    public static byte[] found(final String location, final HttpCookie cookie) {
        return String.join(DELIMITER, FOUND_STATUS_LINE,
                HttpHeader.setCookie(cookie),
                HttpHeader.location(location),
                EMPTY_LINE
        ).getBytes();
    }

    private HttpResponse() {
    }
}
