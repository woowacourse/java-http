package org.apache.coyote.http11.message.response.header;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.message.Regex;
import org.apache.coyote.http11.message.header.Header;

public class Headers {

    private final LinkedHashMap<Header, String> values;

    private Headers(final LinkedHashMap<Header, String> values) {
        this.values = new LinkedHashMap<>(values);
    }

    public static Headers of(final ContentType contentType, final String body) {
        final LinkedHashMap<Header, String> headers = new LinkedHashMap<>();
        headers.put(Header.CONTENT_TYPE, contentType.toString());
        headers.put(Header.CONTENT_LENGTH, Integer.toString(body.getBytes().length));
        return new Headers(headers);
    }

    public void put(Header header, String value) {
        values.put(header, value);
    }

    public void putAll(Map<Header, String> values) {
        this.values.putAll(values);
    }

    public String toText() {
        return values.entrySet().stream()
                .map(entry -> String.join(Regex.HEADER_VALUE.getValue(),
                        entry.getKey()
                                .getName(),
                        entry.getValue() + Regex.BLANK.getValue())
                ).collect(Collectors.joining("\r\n"));
    }
}
