package org.apache.coyote.http11.request;


import static org.apache.coyote.Constants.CRLF;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.apache.coyote.http11.response.element.HttpMethod;

public class HttpRequest {

    private static final String EMPTY_REQUEST = "요청이 비어있습니다.";

    private final HttpMethod method;
    private final String path;

    public HttpRequest(String request) {
        if (request == null || request.isEmpty()) {
            throw new NoSuchElementException(EMPTY_REQUEST);
        }
        List<String> lines = Arrays.stream(request.split(CRLF))
                .collect(Collectors.toList());
        String[] firstLineElements = lines.get(0).split(" ");
        this.method = HttpMethod.valueOf(firstLineElements[0]);
        this.path = firstLineElements[1];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
