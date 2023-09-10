package org.apache.coyote.http11.http.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.apache.coyote.http11.http.message.RequestHeaders.COOKIE;


public class HttpRequest {

    private static final int REQUEST_LINE_INDEX = 0;
    private static final int REQUEST_HEADER_START_INDEX = 1;

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final MessageBody messageBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders headers, final MessageBody messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        final List<String> httpRequestMessage = parseHttpRequestHeaders(bufferedReader);
        final RequestLine requestLine = RequestLine.from(httpRequestMessage.get(REQUEST_LINE_INDEX));
        final RequestHeaders requestHeaders = RequestHeaders.from(
                httpRequestMessage.subList(REQUEST_HEADER_START_INDEX, httpRequestMessage.size())
        );
        final MessageBody messageBody = MessageBody.from(bufferedReader, requestHeaders);
        return new HttpRequest(requestLine, requestHeaders, messageBody);
    }

    private static List<String> parseHttpRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            lines.add(line);
            line = bufferedReader.readLine();
        }
        return lines;
    }

    public boolean containsCookie() {
        return headers.containsKey(COOKIE);
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public Map<String, String> getBody() {
        return messageBody.getBody();
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestHeader=" + headers +
                ", requestBody=" + messageBody +
                '}';
    }
}
