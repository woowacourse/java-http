package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Headers {
    private final Map<String, Header> headers;

    public Headers() {
        this.headers = new HashMap<>();
    }

    public Headers(final List<String> headerStrings) {
        this.headers = new HashMap<>();

        for (final String header : headerStrings) {
            final int index = header.indexOf(":");
            final String name = header.substring(0, index).trim();
            final String value = header.substring(index + 1).trim();

            final Header existingHeader = headers.get(name);
            if (existingHeader != null) {
                existingHeader.addValue(value);
                continue;
            }
            headers.put(name, new Header(name, value));
        }
    }

    public void add(final Header header) {
        final String headerName = header.getName();
        final Optional<Header> existHeader = find(headerName);

        if (existHeader.isPresent()) {
            existHeader.get().addValue(header);
            return;
        }
        headers.put(headerName, header);
    }

    public void add(final String name, final String value) {
        add(new Header(name, value));
    }

    public int getContentLength() {
        final String contentLength = find("Content-Length")
                .map(Header::getValueString)
                .orElse("0");

        try {
            return Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Content-Length 값이 잘못되었습니다.");
        }
    }

    public String getValueString(final String name) {
        return find(name)
                .map(Header::getValueString)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 헤더가 존재하지 않습니다."));
    }

    public String toHeaderString() {
        return headers.values().stream()
                       .map(Header::toHeaderString)
                       .collect(Collectors.joining("\r\n")) + "\r\n";
    }

    private Optional<Header> find(final String name) {
        return Optional.ofNullable(headers.get(name));
    }
}
