package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.http.domain.Headers;
import org.apache.coyote.http11.http.domain.HttpMethod;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.apache.coyote.http11.http.domain.RequestLine;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final MessageBody messageBody;

    private HttpRequest(final RequestLine requestLine, final Headers headers, final MessageBody messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            return new HttpRequest(
                    RequestLine.from(bufferedReader),
                    Headers.from(bufferedReader),
                    MessageBody.from(bufferedReader));
        } catch (IOException e) {
            throw new IllegalArgumentException("HttpRequest creation failed.");
        }
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getUri() {
        return requestLine.getRequestTarget().getUri();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public MessageBody getMessageBody() {
        return messageBody;
    }
}
