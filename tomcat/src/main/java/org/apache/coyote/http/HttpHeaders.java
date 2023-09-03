package org.apache.coyote.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.coyote.http.util.HeaderDto;
import org.apache.coyote.http.util.HttpConsts;
import org.apache.coyote.http.util.exception.InvalidHeaderContentException;
import org.apache.coyote.http.util.exception.InvalidHeaderSizeException;

public class HttpHeaders {

    private static final int MINIMUM_HEADER_SIZE = 1;
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int MINIMUM_HEADER_TOKEN_SIZE = 2;

    private final Map<String, String> headers;

    public HttpHeaders(final Map<String, String> headers) {
        validateHeaderSize(headers);

        this.headers = headers;
    }

    private void validateHeaderSize(final Map<String, String> headers) {
        if (headers.entrySet().size() < MINIMUM_HEADER_SIZE) {
            throw new InvalidHeaderSizeException();
        }
    }

    public static HttpHeaders from(final String headerContents) {
        final Map<String, String> headers = new LinkedHashMap<>();

        final String[] headerLines = headerContents.split(HttpConsts.CRLF);

        for (final String headerLine : headerLines) {
            final String[] headerContent = headerLine.split(HttpConsts.COLON);

            validateHeaderContent(headerContent);

            headers.put(headerContent[HEADER_KEY_INDEX].trim(), headerContent[HEADER_VALUE_INDEX].trim());
        }

        return new HttpHeaders(headers);
    }

    private static void validateHeaderContent(final String[] headerContent) {
        if (headerContent.length < MINIMUM_HEADER_TOKEN_SIZE) {
            throw new InvalidHeaderContentException();
        }

        for (final String content : headerContent) {
            validateBlank(content);
        }
    }

    private static void validateBlank(final String content) {
        if (content.isBlank()) {
            throw new InvalidHeaderContentException();
        }
    }

    public static HttpHeaders from(final List<HeaderDto> headerDtos) {
        final Map<String, String> headers = new LinkedHashMap<>();

        for (final HeaderDto headerDto : headerDtos) {
            headers.put(headerDto.key(), headerDto.value());
        }

        return new HttpHeaders(headers);
    }

    public String findValue(final String headerKey) {
        return headers.get(headerKey);
    }

    public String convertHeaders() {
        return headers.entrySet().stream()
                      .map(this::convertHeaderMessage)
                      .collect(Collectors.joining(HttpConsts.CRLF));
    }

    private String convertHeaderMessage(final Entry<String, String> entry) {
        return entry.getKey() + HttpConsts.COLON + HttpConsts.SPACE + entry.getValue() + HttpConsts.SPACE;
    }
}
