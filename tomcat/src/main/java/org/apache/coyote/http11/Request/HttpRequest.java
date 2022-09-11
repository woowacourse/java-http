package org.apache.coyote.http11.Request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.model.Headers;

public class HttpRequest {

    private static final String END_OF_HEADER = "";
    private static final String HEADER_DELIMITER = ":";

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
        final RequestLine requestLine = getRequestLine(bufferedReader);
        final RequestHeader requestHeader = getRequestHeader(bufferedReader);
        final String requestBody = getRequestBody(bufferedReader, requestHeader);

        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private static RequestLine getRequestLine(final BufferedReader bufferedReader) throws IOException {
        return new RequestLine(bufferedReader.readLine());
    }

    private static RequestHeader getRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        while (bufferedReader.ready()) {
            final String input = bufferedReader.readLine();
            if (input.equals(END_OF_HEADER)) {
                break;
            }
            final String[] header = input.split(HEADER_DELIMITER);
            headers.put(header[0].trim(), header[1].trim());
        }

        return new RequestHeader(headers);
    }

    private static String getRequestBody(final BufferedReader bufferedReader, final RequestHeader requestHeader)
            throws IOException {
        final String input = requestHeader.get(Headers.CONTENT_LENGTH);
        if (input == null) {
            return "";
        }
        final int contentLength = Integer.parseInt(input);
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
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
