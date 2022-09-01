package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.exception.HttpFormatException;

public class RequestHeader {

    private final Map<String, String> value;

    public RequestHeader(final Map<String, String> value) {
        this.value = value;
    }

    public static RequestHeader from(final String lines) {
        final var header = new HashMap<String, String>();
        if (!lines.isBlank()) {
            parseHeader(lines, header);
        }
        return new RequestHeader(header);
    }

    private static void parseHeader(final String lines, final HashMap<String, String> header) {
        for (var line : lines.split("\r\n")) {
            parseEachHeader(line, header);
        }
    }

    private static void parseEachHeader(final String line, final HashMap<String, String> header) {
        try {
            final var index = line.indexOf(":");
            header.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
        } catch (StringIndexOutOfBoundsException e) {
            throw new HttpFormatException("HTTP Request Header 형식이 올바르지 않습니다.");
        }
    }

    public Map<String, String> getValue() {
        return value;
    }

    public String get(final String key) {
        return value.get(key);
    }

    @Override
    public String toString() {
        return value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue() + " ")
                .collect(Collectors.joining("\r\n"));
    }
}
