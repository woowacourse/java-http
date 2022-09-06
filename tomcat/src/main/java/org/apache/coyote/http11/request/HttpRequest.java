package org.apache.coyote.http11.request;


import static org.apache.coyote.Constants.CRLF;

import java.util.NoSuchElementException;
import org.apache.coyote.http11.request.element.Path;
import org.apache.coyote.http11.request.element.Query;
import org.apache.coyote.http11.response.element.HttpMethod;

public class HttpRequest {

    private static final String EMPTY_REQUEST = "요청이 비어있습니다.";

    private final HttpMethod method;
    private final Path path;
    private final Query query;

    public HttpRequest(HttpMethod method, Path path, Query query) {
        this.method = method;
        this.path = path;
        this.query = query;
    }

    public static HttpRequest of(String request) {
        if (request == null || request.isEmpty()) {
            throw new NoSuchElementException(EMPTY_REQUEST);
        }
        String[] firstLine = request
                .split(CRLF)[0]
                .split(" ");

        HttpMethod method = HttpMethod.valueOf(firstLine[0]);
        Path path = Path.of(firstLine[1]);
        Query query = new Query(firstLine[1]);

        return new HttpRequest(method, path, query);
    }


    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path.getPath();
    }

    public Query getQuery() {
        return query;
    }
}
