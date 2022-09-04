package org.apache.coyote.http11.httpmessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.httpmessage.requestline.RequestLine;

public class Request {

    private final RequestLine requestLine;
    private Headers headers;
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

    public String getUri() {
        return requestLine.getRequestUri();
    }
}
