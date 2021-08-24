package nextstep.jwp.model;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpRequest {
    private final String method;
    private final String uri;
    private final Map<String, List<String>> headers;

    private HttpRequest(String method, String uri,
                       Map<String, List<String>> headers) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
    }

    public static HttpRequest of(List<String> lines) {
        String[] firstTokens = lines.get(0).split(" ");
        String method = firstTokens[0];
        String uri = firstTokens[1];

        List<String> headersLines = lines.subList(1, lines.size());
        Map<String, List<String>> headers = extractHeaders(headersLines);

        return new HttpRequest(method, uri, headers);
    }

    private static Map<String, List<String>> extractHeaders(List<String> lines) {
        Map<String, List<String>> headers = new HashMap<>();

        for (String line: lines) {
            if ("".equals(line)) {
                break;
            }
            String[] splits = line.split(": ", 2);
            String key = splits[0];
            List<String> values = Stream.of(splits[1].split(",")).map(String::trim).collect(Collectors.toList());
            headers.put(key, values);
        }
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }
}
