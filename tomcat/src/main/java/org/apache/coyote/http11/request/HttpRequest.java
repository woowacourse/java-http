package org.apache.coyote.http11.request;


import static org.apache.coyote.Constants.CRLF;

import java.util.NoSuchElementException;
import org.apache.coyote.http11.response.element.HttpMethod;

public class HttpRequest {

    private static final String EMPTY_REQUEST = "요청이 비어있습니다.";

    private final HttpMethod method;
    private final String path;

    public HttpRequest(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public static HttpRequest of(String request) {
        if (request == null || request.isEmpty()) {
            throw new NoSuchElementException(EMPTY_REQUEST);
        }
        String[] firstLineElements = request
                .split(CRLF)[0]
                .split(" ");
        return new HttpRequest(HttpMethod.valueOf(firstLineElements[0]), firstLineElements[1]);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
