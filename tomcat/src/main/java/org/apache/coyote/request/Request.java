package org.apache.coyote.request;

import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.common.PathUrl;

import java.io.IOException;

public class Request {
    private final RequestStartLine requestStartLine;
    private final RequestHeader requestHeader;
    private final RequestBody requestBody;

    public Request(
            final RequestStartLine requestStartLine,
            final RequestHeader requestHeader,
            final RequestBody requestBody
    ) {
        this.requestStartLine = requestStartLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static Request from(final Reader reader) throws IOException {
        final RequestStartLine startLine = RequestStartLine.from(reader.getFirstLine());
        final RequestHeader requestHeader = RequestHeader.from(reader.getHeader());
        final String bodyString = reader.getBody(requestHeader.getContentLength());
        final RequestBody requestBody = RequestBody.from(bodyString);
        return new Request(startLine, requestHeader, requestBody);
    }

    public boolean hasQueryString() {
        return requestStartLine.hasQueryString();
    }

    public String getQueryValueBy(final String key) {
        return requestStartLine.getQueryValueBy(key);
    }

    public String getPath() {
        return requestStartLine.getPath();
    }

    public boolean isStatic() {
        return requestStartLine.isStatic();
    }

    public boolean isPost() {
        return requestStartLine.isPost();
    }

    public boolean isGet() {
        return requestStartLine.isGet();
    }

    public PathUrl getRequestUrl() {
        return requestStartLine.getRequestUrl();
    }

    public HttpVersion httpVersion() {
        return requestStartLine.getHttpVersion();
    }

    public String getContentType() {
        return requestStartLine.getContentType();
    }

    public String getBodyValue(final String key) {
        return requestBody.getBodyValue(key);
    }

    @Override
    public String toString() {
        return requestStartLine + System.lineSeparator() +
                requestHeader + System.lineSeparator() + System.lineSeparator() +
                requestBody;
    }

    public boolean hasJsessionid() {
        return requestHeader.hasJsessionid();
    }
}
