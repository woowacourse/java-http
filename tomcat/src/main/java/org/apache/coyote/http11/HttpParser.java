package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpParser {

    public static Request getRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        RequestHeader requestHeader = parseHeader(reader);
        RequestBody requestBody = parseBody(reader, requestHeader);
        return Request.from(requestHeader, requestBody);
    }

    private static RequestHeader parseHeader(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        Headers headers = Headers.of(reader);
        return RequestHeader.from(requestLine, headers);
    }

    private static RequestBody parseBody(BufferedReader reader, RequestHeader requestHeader) throws IOException {
        int contentLength = requestHeader.contentLength();
        if (contentLength <= 0) {
            return new RequestBody();
        }
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        String requestBody = new String(buffer);
        if (requestHeader.contentLength() == 0) {
            return RequestBody.empty();
        }
    }
}
