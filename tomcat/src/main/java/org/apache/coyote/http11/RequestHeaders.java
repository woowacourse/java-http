package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestHeaders {
    private static final String HEADER_DELIMITER = ":";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int NOT_FOUND = -1;

    private final Map<String, String> headers;

    private RequestHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(List<String> headers) {

        final Map<String, String> parsed = new HashMap<>();

        for (final String line : headers) {
            if (line.isBlank()) {
                continue;
            }
            final int delimiterIndex = line.indexOf(HEADER_DELIMITER);
            if (delimiterIndex == NOT_FOUND) {
                throw new InvalidRequestException("잘못된 형식의 헤더: " + line);
            }
            final String name = line.substring(0, delimiterIndex);
            final String value = line.substring(delimiterIndex + 1).strip();

            if (CONTENT_LENGTH.equals(name) && parsed.containsKey(name)) {
                throw new InvalidRequestException("Content-Length 헤더가 중복되었습니다.");
            }
            parsed.putIfAbsent(name, value);
        }
        return new RequestHeaders(Map.copyOf(parsed));
    }

    public int getContentLength() {
        final String value = headers.get(CONTENT_LENGTH);
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("Content-Length가 숫자가 아닙니다.");
        }
    }
}
