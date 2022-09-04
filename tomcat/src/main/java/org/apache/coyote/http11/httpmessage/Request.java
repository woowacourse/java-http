package org.apache.coyote.http11.httpmessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class Request {

    String requestLine;
    Headers headers;
    String body;

    public Request(final String requestLine, final Headers headers) {
        this.requestLine = requestLine;
        this.headers = headers;
    }

    public static Request of(final String requestMessage) throws IOException {
        if (requestMessage == null) {
            throw new IllegalStateException("잘못된 요청입니다.");
        }

        final BufferedReader bufferedReader = new BufferedReader(new StringReader(requestMessage));
        final String requestLine = bufferedReader.readLine();
        final Headers headers = new Headers(bufferedReader);

        return new Request(requestLine, headers);
    }

    public boolean isGetMethod() {
        return requestLine.split(" ")[0].equals("GET");
    }

    public String getUri() {
        return requestLine.split(" ")[1];
    }
}
