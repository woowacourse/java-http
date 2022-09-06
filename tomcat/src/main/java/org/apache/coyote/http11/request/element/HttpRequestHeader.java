package org.apache.coyote.http11.request.element;

import static org.apache.coyote.Constants.CRLF;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.element.HttpMethod;

public class HttpRequestHeader {

    private static final String EMPTY_REQUEST = "요청이 비어있습니다.";
    private static final String HEADER_DELIMITER = ": ";

    private final HttpMethod method;
    private final Path path;
    private final Query query;
    private final Map<String, String> headers;

    public HttpRequestHeader(HttpMethod method, Path path, Query query,
                             Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.query = query;
        this.headers = headers;
    }

    public static HttpRequestHeader of(String request) {
        if (request == null || request.isEmpty()) {
            throw new NoSuchElementException(EMPTY_REQUEST);
        }

        String[] split = request.split(CRLF);
        String[] firstLine = split[0].split(" ");

        HttpMethod method = HttpMethod.valueOf(firstLine[0]);
        Path path = Path.of(firstLine[1]);
        Query query = new Query(firstLine[1]);

        Map<String, String> headers = extractHeaders(split);
        return new HttpRequestHeader(method, path, query, headers);
    }

    private static Map<String, String> extractHeaders(String[] split) {
        List<String> headers = Arrays.stream(split)
                .collect(Collectors.toList())
                .subList(1, split.length);
        return mapHeaders(headers);
    }

    private static Map<String, String> mapHeaders(List<String> headers) {
        return headers
                .stream()
                .map(element -> element.split(HEADER_DELIMITER))
                .collect(Collectors.toMap(split -> split[0], split -> split[1].trim()));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Path getPath() {
        return path;
    }

    public Query getQuery() {
        return query;
    }

    public String find(String headerKey) {
        return headers.get(headerKey);
    }
}
