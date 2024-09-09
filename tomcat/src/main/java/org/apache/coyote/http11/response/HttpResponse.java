package org.apache.coyote.http11.response;

import org.apache.coyote.http11.FileType;
import org.apache.coyote.http11.HttpCookie;

public class HttpResponse {
    private static final String DELIMITER = "\r\n";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK ";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 Found ";
    public static final String EMPTY_LINE = "";

    public static byte[] ok(final FileType fileType, final String responseBody) {
        return String.join(DELIMITER, OK_STATUS_LINE,
                setContentTypeHeader(fileType),
                setContentLengthHeader(responseBody),
                EMPTY_LINE,
                responseBody
        ).getBytes();
    }

    public static byte[] found(final String location) {
        return String.join(DELIMITER, FOUND_STATUS_LINE,
                setLocationHeader(location),
                EMPTY_LINE
        ).getBytes();
    }

    public static byte[] found(final String location, final HttpCookie cookie) {
        return String.join(DELIMITER, FOUND_STATUS_LINE,
                setCookieHeader(cookie),
                setLocationHeader(location),
                EMPTY_LINE
        ).getBytes();
    }

    private static String setContentTypeHeader(final FileType fileType) {
        return "Content-Type: " + fileType.getValue() + ";charset=utf-8 ";
    }

    private static String setContentLengthHeader(final String responseBody) {
        return "Content-Length: " + responseBody.getBytes().length + " ";
    }

    private static String setLocationHeader(final String location) {
        return "Location: " + location + " ";
    }

    private static String setCookieHeader(final HttpCookie cookie) {
        return "Set-Cookie: JSESSIONID=" + cookie.getCookieValue("JSESSIONID") + " ";
    }

    private HttpResponse() {
    }
}
