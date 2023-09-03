package org.apache.coyote.http11.request;

import org.apache.coyote.http11.header.Headers;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.coyote.http11.header.EntityHeader.CONTENT_LENGTH;
import static org.apache.coyote.http11.header.EntityHeader.CONTENT_TYPE;

public class Request {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestParameters requestParameters;
    private final String body;

    private Request(final RequestLine requestLine,
                    final Headers headers,
                    final String body,
                    final RequestParameters requestParameters) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.requestParameters = requestParameters;
    }

    public static Request from(final BufferedReader bufferedReader) throws IOException {
        final List<String> requestHeaderLines = new ArrayList<>();
        String nextLine;
        while (!"".equals(nextLine = bufferedReader.readLine())) {
            if (nextLine == null) {
                throw new RuntimeException("헤더가 잘못되었습니다.");
            }
            requestHeaderLines.add(nextLine);
        }

        final String requestFirstLine = requestHeaderLines.get(0);
        final RequestLine requestLine = RequestLine.from(requestFirstLine);
        final Headers headers = new Headers();
        headers.addRequestHeaders(requestHeaderLines);

        final String contentLengthValue = headers.getValue(CONTENT_LENGTH) ;
        final int contentLength = "".equals(contentLengthValue) ? 0 : Integer.parseInt(contentLengthValue);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String body = new String(buffer);

        final String queryStrings = extractQueryStrings(requestFirstLine, body, headers);
        final RequestParameters requestParameters = RequestParameters.from(queryStrings);

        return new Request(requestLine, headers, body, requestParameters);
    }

    private static String extractQueryStrings(final String requestFirstLine,
                                              final String body,
                                              final Headers headers) {

        final String[] requestFirstLineElements = requestFirstLine.split(" ");
        final String requestUriValue = requestFirstLineElements[1];

        if (requestUriValue.contains("?")) {
            return requestUriValue.substring(requestUriValue.indexOf("?") + 1);
        }

        if ("application/x-www-form-urlencoded".equalsIgnoreCase(headers.getValue(CONTENT_TYPE))) {
            return body;
        }
        return "";
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getHeaders() {
        return headers;
    }

    public RequestParameters getRequestParameters() {
        return requestParameters;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Request{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", requestParameters=" + requestParameters +
                ", body='" + body + '\'' +
                '}';
    }
}
