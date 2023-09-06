package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ResponseParser {
    public static String parse(final HttpResponse response) {
        final StringBuilder sb = new StringBuilder();
        addFirstLine(response, sb);
        addHeader(response, sb);
        addBody(response, sb);

        return sb.toString();
    }

    private static void addBody(final HttpResponse response, final StringBuilder sb) {
        if (response.isNotEmptyBody()) {
            final byte[] body = response.getBody();
            sb.append("\r\n").append(new String(body, StandardCharsets.UTF_8));
        }
    }

    private static void addHeader(final HttpResponse response, final StringBuilder sb) {
        if (response.isNotEmptyHeader()) {
            final HttpHeader httpHeader = response.getHttpHeader();
            final Map<String, String> headers = httpHeader.getHeaders();
            headers.forEach((key, value) -> sb.append(key).append(": ").append(value).append(" ").append("\r\n"));
        }
    }

    private static void addFirstLine(final HttpResponse response, final StringBuilder sb) {
        final String firstLine = parseResponseInfo(response);
        sb.append(firstLine).append("\r\n");
    }

    private static String parseResponseInfo(final HttpResponse response) {
        final ResponseInfo info = response.getInfo();

        final HttpVersion httpVersion = info.getHttpVersion();
        final HttpStatus httpStatus = info.getHttpStatus();

        final String version = httpVersion.getVersion();
        final int status = httpStatus.getStatus();
        final String statusName = httpStatus.name();

        return version + " " + status + " " + statusName + " ";
    }

    private ResponseParser() {
    }
}
