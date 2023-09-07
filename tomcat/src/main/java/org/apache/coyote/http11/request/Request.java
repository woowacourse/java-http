package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.exception.NotFoundQueryStringException;

import java.io.BufferedReader;
import java.io.IOException;

public class Request {

    private final StartLine startLine;
    private final Headers headers;

    public Request(final StartLine startLine, final Headers headers) {
        this.startLine = startLine;
        this.headers = headers;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final StartLine requestStartLine = StartLine.from(bufferedReader.readLine());
        final Headers requestHeaders = Headers.from(
                bufferedReader.lines()
                              .takeWhile(line -> !line.isEmpty())
        );

        return new Request(requestStartLine, requestHeaders);
    }

    public boolean hasQueryString() {
        return startLine.getQueryString().isPresent();
    }

    public String getPath() {
        return startLine.getPath();
    }

    public StartLine getStartLine() {
        return startLine;
    }

    public String getQueryValue(final String key) {
        final QueryString queryString = startLine.getQueryString()
                                                 .orElseThrow(() ->
                                                         new NotFoundQueryStringException("쿼리 스트링이 들어오지 않았습니다.")
                                                 );
        return queryString.getQueryValue(key);
    }

    public Headers getHeaders() {
        return headers;
    }
}
