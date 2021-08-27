package nextstep.jwp.infrastructure.http;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, List<String>> elements;

    public HttpHeaders(final Map<String, List<String>> elements) {
        this.elements = elements;
    }

    public static HttpHeaders of(final List<String> headers) {
        final Map<String, List<String>> values = new HashMap<>();

        for (String header : headers) {
            final List<String> splitHeader = Arrays.asList(header.split(": "));
            validateHeaderFormat(splitHeader);
            values.put(splitHeader.get(0), splitHeaderValues(splitHeader.get(1)));
        }

        return new HttpHeaders(values);
    }

    private static List<String> splitHeaderValues(final String splitValues) {
        return Arrays.asList(splitValues.split(", "));
    }

    private static void validateHeaderFormat(final List<String> splitHeader) {
        if (splitHeader.size() != 2) {
            throw new IllegalArgumentException("잘못된 Headers 형식 입니다.");
        }
    }

    public List<String> getValue(final String key) {
        return elements.get(key);
    }
}
