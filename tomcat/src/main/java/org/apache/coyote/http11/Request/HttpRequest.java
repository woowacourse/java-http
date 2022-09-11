package org.apache.coyote.http11.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private RequestLine requestLine;
    private RequestHeader requestHeader;
    private String requestBody;

    private HttpRequest() {
    }

    private HttpRequest(final RequestLine requestLine, final RequestHeader requestHeader,
                        final String requestBody) {
        this.requestLine = requestLine;
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) throws IOException {
        // RequestLine
        final RequestLine requestLine = new RequestLine(bufferedReader.readLine());

        // RequestHeader
        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "0");
        while (bufferedReader.ready()) {
            final String input = bufferedReader.readLine();
            if (input.equals("")) {
                break;
            }
            final String[] header = input.split(":");
            headers.put(header[0].trim(), header[1].trim());
        }
        final RequestHeader requestHeader = new RequestHeader(headers);

        // RequestBody
        final int contentLength = Integer.parseInt(requestHeader.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);

        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
