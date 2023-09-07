package org.apache.coyote.http11.request;

import org.apache.coyote.http11.request.exception.NotFoundQueryStringException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

import static org.apache.coyote.http11.request.Body.EMPTY_BODY;

public class Request {

    private static final String CONTENT_LENGTH = "Content-Length";

    private final StartLine startLine;
    private final Headers headers;
    private final Body body;

    public Request(final StartLine startLine, final Headers headers, final Body body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final StartLine requestStartLine = StartLine.from(bufferedReader.readLine());
        final Headers requestHeaders = getHeaders(bufferedReader);
        final Body requestBody = getBody(requestHeaders, bufferedReader);

        return new Request(requestStartLine, requestHeaders, requestBody);
    }

    private static Headers getHeaders(final BufferedReader bufferedReader) {
        return Headers.from(
                bufferedReader.lines()
                              .takeWhile(line -> !line.isEmpty())
        );
    }

    private static Body getBody(final Headers headers, final BufferedReader bufferedReader) throws IOException {
        final Optional<Header> contentLengthHeader = headers.getHeaders()
                                                      .stream()
                                                      .filter(header -> header.getName().equals(CONTENT_LENGTH))
                                                      .findFirst();

        if (contentLengthHeader.isPresent()) {
            final int contentLength = Integer.parseInt(contentLengthHeader.get().getValue());
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);

            return Body.from(new String(buffer));
        }

        return EMPTY_BODY;
    }

    public boolean hasQueryString() {
        return startLine.getQueryString().isPresent();
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
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

    public String getBodyValue(final String key) {
        return body.getValues().get(key);
    }

    public Body getBody() {
        return body;
    }

    public Headers getHeaders() {
        return headers;
    }
}
