package org.apache.coyote.http11.request;

import java.util.List;
import org.apache.coyote.http11.request.header.Headers;
import org.apache.coyote.http11.request.requestline.RequestLine;

public class Request {

    private final RequestLine requestLine;
    private final Headers headers;

    public Request(final RequestLine requestLine, final Headers headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static Request from(final List<String> requestLines) {
        final RequestLine requestLine = RequestLine.from(requestLines.get(0));
        final Headers headers = Headers.from(requestLines.subList(1, requestLines.size()));
        return new Request(requestLine, headers);
    }

    public boolean isPath(final String path) {
        return requestLine.isPath(path);
    }

    public boolean isForResource() {
        return requestLine.isForResource();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public QueryParams getQueryParams() {
        return requestLine.getQueryParams();
    }
}
