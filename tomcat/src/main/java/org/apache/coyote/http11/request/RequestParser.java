package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestParser {

    private RequestParser() {
    }

    public static Request createRequest(BufferedReader bufferedReader) throws IOException {
        final StartLine startLine = new StartLine(bufferedReader.readLine());

        RequestHeaders requestHeaders = RequestHeaders.of(readRequestHeaders(bufferedReader));
        return new Request(
                startLine,
                requestHeaders,
                RequestBody.of(readRequestBody(requestHeaders, bufferedReader))
        );
    }

    private static String readRequestBody(RequestHeaders requestHeaders, BufferedReader bufferedReader)
            throws IOException {
        int contentLength = requestHeaders.getContentLength();
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private static List<String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        String line;
        List<String> requestHeaders = new ArrayList<>();
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            requestHeaders.add(line);
        }
        return requestHeaders;
    }
}
