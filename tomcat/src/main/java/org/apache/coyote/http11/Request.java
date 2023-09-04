package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class Request {
    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestForms requestForms;

    public Request(final RequestLine requestLine, final RequestHeaders requestHeaders,
                   final RequestForms requestForms) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestForms = requestForms;
    }

    public static Request from(final BufferedReader br) throws IOException {
        final RequestLine requestLine = RequestLine.from(br.readLine());
        final RequestHeaders requestHeaders = RequestHeaders.from(br);
        final RequestForms requestForms = createRequestBody(br, requestHeaders);
        return new Request(requestLine, requestHeaders, requestForms);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    private static RequestForms createRequestBody(BufferedReader br, RequestHeaders requestHeaders)
            throws IOException {
        if (!requestHeaders.hasContentType()) {
            return new RequestForms(null);
        }
        final int contentLength = Integer.parseInt((String) requestHeaders.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);
        return RequestForms.from(requestBody);
    }

    public String getSessionId() {
        return requestHeaders.getCookieValue("JSESSIONID");
    }

    public RequestHeaders getRequestHeaders() {
        return requestHeaders;
    }

    public RequestForms getRequestForms() {
        return requestForms;
    }
}
