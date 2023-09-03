package org.apache.coyote.http11.support;

import org.apache.coyote.http11.request.body.HttpBodyLine;
import org.apache.coyote.http11.request.header.HttpHeadersLine;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpStartLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpRequestExtractor {
    private static final int REQUEST_LINE_INDEX = 0;
    private static final int HEADER_LINES_INDEX = 1;
    private static final int BODY_INDEX = 2;
    public static final String END_LINE = "";


    public static HttpRequest extract(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final String[] splitHttpRequest = readRequest(bufferedReader);

        return new HttpRequest(
                HttpStartLine.from(splitHttpRequest[REQUEST_LINE_INDEX]),
                HttpHeadersLine.from(splitHttpRequest[HEADER_LINES_INDEX]),
                HttpBodyLine.from(validateBody(splitHttpRequest))
        );
    }

    private static String validateBody(final String[] splitHttpRequest) {
        if (splitHttpRequest.length == 2) {
            return END_LINE;
        }
        return splitHttpRequest[BODY_INDEX];
    }

    private static String[] readRequest(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder httpRequest = new StringBuilder();
        readHttpRequest(bufferedReader, httpRequest);
        return httpRequest.toString().split("\r\n");
    }

    private static void readHttpRequest(final BufferedReader bufferedReader, final StringBuilder httpRequest) throws IOException {
        String httpRequestLine;
        while (!END_LINE.equals(httpRequestLine = bufferedReader.readLine())) {
            validateHttpLine(httpRequestLine);
            httpRequest.append(httpRequestLine).append("\r\n");
        }
        validateHttpLine(httpRequestLine);
        httpRequest.append(httpRequestLine);
    }

    private static void validateHttpLine(final String httpRequestLine) {
        if (httpRequestLine == null) {
            throw new IllegalArgumentException("잘못된 http요청입니다.");
        }
    }
}
