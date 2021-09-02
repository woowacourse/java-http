package nextstep.jwp.request;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.jwp.constants.Http;

public class RequestHeader {
    private final Map<String, String> headers;

    public RequestHeader(String headerLines) {
        this.headers = parseHeaders(headerLines);
    }

    private Map<String, String> parseHeaders(String lines) {
        return Stream.of(lines.split(Http.NEW_LINE))
                .map(line -> line.split(Http.COLUMN_SEPARATOR))
                .collect(Collectors.toMap(x -> x[Http.KEY].trim(), x -> x[Http.VALUE].trim()));
    }

    public boolean contains(String key) {
        return headers.containsKey(key);
    }

    public String get(String key) {
        return headers.get(key);
    }
}
