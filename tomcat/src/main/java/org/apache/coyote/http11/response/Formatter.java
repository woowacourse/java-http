package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpVersion;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

public class Formatter {

    private static final int INDEX_ZERO = 0;
    private static final String CRLF = "\r\n";
    private static final String KEY_DELIMITER = ":";
    private static final String SPACE_SEPARATOR = " ";

    public static byte[] toResponseFormat(HttpResponse httpResponse) {
        StringJoiner stringJoiner = new StringJoiner(CRLF);

        stringJoiner.add(toStatusLineFormat(httpResponse.getStatusLine()));
        stringJoiner.add(toHeaderFormat(httpResponse.getResponseHeader()));
        stringJoiner.add(CRLF);

        byte[] headers = stringJoiner.toString().getBytes(StandardCharsets.UTF_8);
        ResponseBody responseBody = httpResponse.getResponseBody();

        if (responseBody.isEmpty()) {
            return headers;
        }

        return createFullResponse(headers, responseBody);
    }

    private static byte[] createFullResponse(byte[] headerBytes, ResponseBody responseBody) {
        byte[] response = new byte[headerBytes.length + responseBody.getLength()];

        System.arraycopy(headerBytes, INDEX_ZERO, response, INDEX_ZERO, headerBytes.length);
        System.arraycopy(responseBody.getValues(), INDEX_ZERO, response, headerBytes.length, responseBody.getLength());

        return response;
    }

    private static String toStatusLineFormat(StatusLine statusLine) {
        HttpVersion httpVersion = statusLine.getHttpVersion();
        HttpStatus httpStatus = statusLine.getHttpStatus();

        return httpVersion.getHttpVersion() + SPACE_SEPARATOR + httpStatus.getCode() + SPACE_SEPARATOR + httpStatus.getMessage();
    }

    private static String toHeaderFormat(Map<String, String> headers) {
        StringJoiner stringJoiner = new StringJoiner(CRLF);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringJoiner.add(entry.getKey() + KEY_DELIMITER + SPACE_SEPARATOR + entry.getValue());
        }
        return stringJoiner.toString();
    }
}
