package org.apache.coyote.http11;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final String HEADER_SEPARATOR = ": ";
    public static final int FIELD_NAME_INDEX = 0;
    public static final int FIELD_VALUE_INDEX = 1;

    private final Map<HttpHeaderField, String> fields;

    public HttpHeaders(final List<String> headerLines) throws IOException {
        try {
            this.fields = headerLines.stream()
                    .map(line -> line.split(HEADER_SEPARATOR))
                    .filter(splitByColon -> HttpHeaderField.anyMatch(splitByColon[FIELD_NAME_INDEX]))
                    .collect(
                            Collectors.toMap(
                                    splitByColon -> HttpHeaderField.of(splitByColon[FIELD_NAME_INDEX]),
                                    splitByColon -> splitByColon[FIELD_VALUE_INDEX]
                            )
                    );
        } catch (final PatternSyntaxException | ArrayIndexOutOfBoundsException e) {
            throw new IOException("Request Header 파싱 오류", e);
        }
    }

    public String get(final HttpHeaderField target) {
        return fields.get(target);
    }

    public boolean contains(final HttpHeaderField target) {
        return fields.containsKey(target);
    }
}
