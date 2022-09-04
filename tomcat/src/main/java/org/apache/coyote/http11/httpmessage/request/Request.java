package org.apache.coyote.http11.httpmessage.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.httpmessage.common.Headers;
import org.apache.coyote.http11.httpmessage.request.requestline.RequestLine;
import org.apache.coyote.http11.httpmessage.request.requestline.RequestUri;

public class Request {

    private final RequestLine requestLine;
    private final Headers headers;
    private String body;

    public Request(final RequestLine requestLine, final Headers headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static Request of(final String requestMessage) throws IOException {
        if (requestMessage == null) {
            throw new IllegalStateException("잘못된 요청입니다.");
        }

        final BufferedReader bufferedReader = new BufferedReader(new StringReader(requestMessage));
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final Headers headers = new Headers(bufferedReader);

        return new Request(requestLine, headers);
    }

    public boolean isGetMethod() {
        return requestLine.isGetMethod();
    }

    public RequestUri getUri() {
        return requestLine.getRequestUri();
    }

    public boolean isMatchUri(String requestUri) {
        return requestLine.isMatchUri(requestUri);
    }

    public boolean isFileRequest() {
        return requestLine.getRequestUri().isFileRequest();
    }

    public boolean hasQueryString() {
        return requestLine.getRequestUri().hasQueryStrings();
    }
}
