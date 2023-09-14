package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.request.line.RequestLine;

public class HttpRequestGenerator {

    public static HttpRequest generate(BufferedReader bufferedReader) throws IOException {
        final String firstLine = bufferedReader.readLine();
        if (firstLine == null) {
            return null;
        }
        final RequestLine requestLine = RequestLine.from(firstLine);
        final HttpHeader httpHeader = getHeader(bufferedReader);
        final RequestBody requestBody = getBody(bufferedReader, httpHeader);
        return HttpRequest.of(requestLine, httpHeader, requestBody);
    }

    private static HttpHeader getHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> requestHeaders = new ArrayList<>();
        for (String line = bufferedReader.readLine(); !"".equals(line); line = bufferedReader.readLine()) {
            requestHeaders.add(line);
        }
        return HttpHeader.from(requestHeaders);
    }

    private static RequestBody getBody(final BufferedReader bufferedReader, final HttpHeader httpHeader)
            throws IOException {
        List<String> contentLengths = httpHeader.headers().get("Content-Length");
        if (contentLengths == null) {
            return RequestBody.from(null);
        }
        int contentLength = Integer.parseInt(contentLengths.get(0));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

}
