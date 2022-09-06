package nextstep.jwp.http;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpHeaders {

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders parse(List<String> lines) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (String line : lines) {
            List<String> parsingLine = List.of(line.split(": ?", 2));
            String header = parsingLine.get(0);
            String value = parsingLine.get(1);
            headers.put(header, value);
        }
        return new HttpHeaders(headers);
    }

    public Optional<String> get(String key) {
        String value = headers.get(key);
        return Optional.ofNullable(value);
    }
}
