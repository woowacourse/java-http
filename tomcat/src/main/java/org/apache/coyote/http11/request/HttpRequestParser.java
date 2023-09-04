package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.EMPTY;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {

    public HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final RequestHeader requestHeader = parseRequestHeader(bufferedReader);
        final RequestBody requestBody = parseRequestBody(bufferedReader, requestHeader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private RequestHeader parseRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();
        for (String line = bufferedReader.readLine(); !EMPTY.equals(line); line = bufferedReader.readLine()) {
            stringBuilder.append(line).append(CRLF);
        }
        return RequestHeader.from(stringBuilder.toString());
    }

    private RequestBody parseRequestBody(
            final BufferedReader bufferedReader,
            final RequestHeader requestHeader
    ) throws IOException {
        final String contentLength = requestHeader.get("Content-Length");
        if (contentLength == null) {
            return RequestBody.empty();
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return RequestBody.from(new String(buffer));
    }
}
