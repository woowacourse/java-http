package org.apache.coyote.http11;

import org.apache.coyote.http.response.HttpResponse;

public class Http11Parser {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String STATUS_LINE_DELIMITER = " ";
    private static final String HEADER_DELIMITER = ": ";
    private static final String BLANK_CRLF = " \r\n";
    private static final String CRLF = "\r\n";
    private static final int NO_CONTENT_LENGTH = 0;

    public static String writeHttpResponse(final HttpResponse response) {
        StringBuilder serializedResponse = new StringBuilder();
        appendStatusLine(serializedResponse, response);
        appendHeaders(serializedResponse, response);
        appendBody(serializedResponse, response);
        return serializedResponse.toString();
    }

    private static void appendStatusLine(StringBuilder serializedResponse, HttpResponse response) {
        serializedResponse.append(HTTP_VERSION)
                .append(STATUS_LINE_DELIMITER)
                .append(response.getStatusCode())
                .append(BLANK_CRLF);
    }

    private static void appendHeaders(StringBuilder serializedResponse, HttpResponse response) {
        response.getHeader()
                .forEach((key, value) -> appendHeader(serializedResponse, key, value));
    }

    private static void appendHeader(StringBuilder serializedResponse, String key, String value) {
        serializedResponse.append(key)
                .append(HEADER_DELIMITER)
                .append(value)
                .append(BLANK_CRLF);
    }

    private static void appendBody(StringBuilder serializedResponse, HttpResponse response) {
        if (response.getContentLength() != NO_CONTENT_LENGTH) {
            serializedResponse.append(CRLF);
            serializedResponse.append(response.getBody());
        }
    }
}
