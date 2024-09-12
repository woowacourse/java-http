package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.http11.cookie.HttpCookie;

public class RequestHeader {
    private static final String HTTP_LINE_SEPARATOR = "\r\n";
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
                .map(headerLine -> Arrays.asList(headerLine.split(":")))
                .collect(Collectors.toMap(
                        entrySet -> entrySet.get(0).trim(),
                        entrySet -> entrySet.get(1).trim()
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

    public String getSession() {
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
}
