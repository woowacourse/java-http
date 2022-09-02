package org.apache.coyote.http11.request;


import static org.apache.coyote.Constants.CRLF;

import java.util.List;
import java.util.NoSuchElementException;

public class HttpRequest {

    private static final String EMPTY_REQUEST = "요청이 비어있습니다.";

    private final List<String> lines;
    private final String method;
    private final String path;


    public HttpRequest(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new NoSuchElementException(EMPTY_REQUEST);
        }
        this.lines = lines;
        String[] firstLineElements = lines.get(0).split(" ");
        this.method = firstLineElements[0];
        this.path = firstLineElements[1];
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getLines() {
        return String.join(CRLF, lines);
    }
}
