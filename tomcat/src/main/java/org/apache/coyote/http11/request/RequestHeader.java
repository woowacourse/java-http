package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.http11.cookie.HttpCookie;

public class RequestHeader {
    private static final String HTTP_LINE_SEPARATOR = "\r\n";
    private static final String KEY_VALUE_SEPARATOR = ": ";
    private static final int KEY_VALUE_SIZE = 2;
    private static final int KEY_IDX = 0;
    private static final int VALUE_IDX = 1;
    private static final String ACCEPT_LINE_SEPARATOR = ",";
    private static final String CONTENT_LENGTH_FIELD = "Content-Length";
    private static final String COOKIE_FIELD = "Cookie";
    private static final String ACCEPT_FIELD = "Accept";

    private final Map<String, String> header;

    public RequestHeader(String inputHeader) {
        this.header = createHeader(inputHeader);
    }

    private Map<String, String> createHeader(String inputHeader) {
        return Arrays.stream(inputHeader.split(HTTP_LINE_SEPARATOR))
                .map(headerLine -> Arrays.asList(headerLine.split(KEY_VALUE_SEPARATOR)))
                .filter(keyValue -> keyValue.size() == KEY_VALUE_SIZE)
                .filter(keyValue -> !keyValue.get(KEY_IDX).isBlank() && !keyValue.get(VALUE_IDX).isBlank())
                .collect(Collectors.toMap(
                        keyValue -> keyValue.get(KEY_IDX).trim(),
                        keyValue -> keyValue.get(VALUE_IDX).trim()
                ));
    }

    public int getContentLength() {
        return Optional.ofNullable(header.get(CONTENT_LENGTH_FIELD))
                .map(this::parseIntOrThrow)
                .orElse(0);
    }

    private Integer parseIntOrThrow(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Content-Length는 숫자 형식이어야 합니다.");
        }
    }

    public String getJSessionId() {
        return Optional.ofNullable(header.get(COOKIE_FIELD))
                .map(HttpCookie::new)
                .map(HttpCookie::getJSessionId)
                .orElse("");
    }

    public String getMimeType() {
        String acceptLine = getAccept();
        return Arrays.asList(acceptLine.split(ACCEPT_LINE_SEPARATOR)).getFirst();
    }

    private String getAccept() {
        return Optional.ofNullable(header.get(ACCEPT_FIELD))
                .orElse("");
    }

    public Map<String, String> getHeader() {
        return header;
    }
}
