package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.common.Constants.EMPTY;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.common.Headers;

public class HttpRequestParser {

    public HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
        final Headers requestHeader = parseRequestHeader(bufferedReader);
        final RequestBody requestBody = parseRequestBody(bufferedReader, requestHeader);
        return new HttpRequest(requestLine, requestHeader, requestBody);
    }

    private Headers parseRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final Headers headers = new Headers();
        for (String line = bufferedReader.readLine(); !EMPTY.equals(line); line = bufferedReader.readLine()) {
            headers.addHeader(line);
        }
        return headers;
    }

    private RequestBody parseRequestBody(
            final BufferedReader bufferedReader,
            final Headers headers
    ) throws IOException {
        final String contentLength = headers.get("Content-Length");
        if (contentLength.isEmpty()) {
            return new RequestBody();
        }
        final int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return RequestBody.from(new String(buffer));
    }
}
