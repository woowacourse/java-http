package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;

public class InputStreamHandler {

    public static HttpRequest createRequest(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestLine requestLine = HttpRequestLine.of(bufferedReader.readLine());
        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.of(readHttpRequestHeader(bufferedReader));

        final String requestBody = readHttpRequestBody(bufferedReader, httpRequestHeader);

        return HttpRequest.of(requestLine, httpRequestHeader, requestBody);
    }

    private static List<String> readHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final List<String> httpRequest = new ArrayList<>();

        String line = bufferedReader.readLine();
        while (line != null && !line.equals("")) {
            httpRequest.add(line);
            line = bufferedReader.readLine();
        }

        return httpRequest;
    }

    private static String readHttpRequestBody(final BufferedReader bufferedReader,
                                              final HttpRequestHeader httpRequestHeader) throws IOException {
        if (!httpRequestHeader.contains("Content-Length")) {
            return "";
        }

        final int contentLength = Integer.parseInt(httpRequestHeader.getHeader("Content-Length"));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }
}
