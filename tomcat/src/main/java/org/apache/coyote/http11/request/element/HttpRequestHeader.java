package org.apache.coyote.http11.request.element;

import static org.apache.coyote.Constants.CRLF;

import java.net.URI;
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

        List<String> split = Arrays.asList(request.split(CRLF));
        String[] firstLine = split.get(0).split(" ");
        Map<String, String> headers = extractHeaders(split);

        HttpMethod method = HttpMethod.valueOf(firstLine[0]);
        URI uri = URI.create(firstLine[1]);

        Path path = Path.of(uri.getPath());
        Query query = Query.of(uri.getQuery());

        return new HttpRequestHeader(method, path, query, headers);
    }

    private static Map<String, String> extractHeaders(List<String> split) {
        List<String> headers = split.subList(1, split.size());
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
