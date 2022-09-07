package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMessageDelimiter;
import org.apache.coyote.http11.request.header.RequestLine;

public class HttpRequestGenerator {

    private HttpRequestGenerator() {
    }

    public static HttpRequest createHttpRequest(final BufferedReader bufferedReader) throws IOException {
        final RequestLine requestLine = createRequestLine(bufferedReader);
        final HttpHeaders headers = createHeaders(bufferedReader);
        final HttpRequestBody bodies = createBody(bufferedReader, headers);

        return HttpRequest.of(requestLine, headers, bodies);
    }

    private static RequestLine createRequestLine(final BufferedReader bufferedReader) throws IOException {
        return RequestLine.from(bufferedReader.readLine());
    }

    private static HttpHeaders createHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> headers = new ArrayList<>();

        String message;
        while (true) {
            message = bufferedReader.readLine();
            if (message == null || HttpMessageDelimiter.HEADER_BODY.isSame(message)) {
                return HttpHeaders.request(headers);
            }
            headers.add(message);
        }
    }

    private static HttpRequestBody createBody(final BufferedReader bufferedReader, final HttpHeaders headers) throws IOException {
        final int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return HttpRequestBody.empty();
        }

        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return HttpRequestBody.formData(new String(buffer));
    }
}
