package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.URL;

public class Request {
    public static final String SPACE_DELIMITER = " ";

    private final StartLine startLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    private Request(final StartLine startLine, final RequestHeaders headers,
                   final RequestBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Request of(final BufferedReader bufferedReader) throws IOException {
        final String startLineString = bufferedReader.readLine();
        final StartLine startLine = StartLine.of(startLineString);
        final RequestHeaders headers = RequestHeaders.of(bufferedReader);
        final int contentLength = headers.getContentLength();
        final RequestBody requestBody = RequestBody.from(bufferedReader, contentLength);
        return new Request(startLine, headers, requestBody);
    }

    public boolean isForStaticFile() {
        return startLine.isForStaticFile();
    }

    public boolean isDefaultUrl() {
        return startLine.isDefault();
    }

    public boolean hasPath(final String path) {
        return startLine.hasPath(path);
    }

    public boolean hasJsessionid() {
        return headers.hasJsessionid();
    }

    public boolean hasMethod(final HttpMethod method) {
        return startLine.hasMethod(method);
    }

    public URL getURL() {
        return startLine.getURL();
    }

    public RequestBody getBody() {
        return body;
    }
}
