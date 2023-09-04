package org.apache.coyote.http11.support;

import org.apache.coyote.http11.request.body.HttpBodyLine;
import org.apache.coyote.http11.request.header.HttpHeadersLine;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpMethod;
import org.apache.coyote.http11.request.start.HttpStartLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpRequestExtractor {
    public static final String END_LINE = "";


    public static HttpRequest extract(final InputStream inputStream) throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return readRequest(bufferedReader);
    }

    private static HttpRequest readRequest(final BufferedReader bufferedReader) throws IOException {
        final HttpStartLine httpStartLine = readHttpStartLine(bufferedReader);
        final HttpHeadersLine httpHeadersLine = readHttpHeadersLine(bufferedReader);
        final HttpBodyLine httpBodyLine = readHttpBody(bufferedReader, httpStartLine, httpHeadersLine);
        return new HttpRequest(httpStartLine, httpHeadersLine, httpBodyLine);
    }

    private static HttpStartLine readHttpStartLine(final BufferedReader bufferedReader) throws IOException {
        final String httpRequestLine = bufferedReader.readLine();
        validateHttpLine(httpRequestLine);
        return HttpStartLine.from(httpRequestLine);
    }

    private static HttpHeadersLine readHttpHeadersLine(final BufferedReader bufferedReader) throws IOException {
            final StringBuilder httpRequest = new StringBuilder();
            String httpRequestLine;
            while (!END_LINE.equals(httpRequestLine = bufferedReader.readLine())) {
                validateHttpLine(httpRequestLine);
                httpRequest.append(httpRequestLine).append("\r\n");
            }
        return HttpHeadersLine.from(httpRequest.toString().split("\r\n"));
        }

    private static HttpBodyLine readHttpBody(final BufferedReader bufferedReader, final HttpStartLine httpStartLine, final HttpHeadersLine httpHeadersLine) throws IOException {
        if (httpStartLine.getHttpMethod().equals(HttpMethod.POST)) {
            final int contentLength = httpHeadersLine.getContentLength();
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            String requestBody = new String(buffer);
            validateHttpLine(requestBody);
            return HttpBodyLine.from(requestBody);
        }
        return HttpBodyLine.from("");
    }

    private static void validateHttpLine(final String httpRequestLine) {
        if (httpRequestLine == null) {
            throw new IllegalArgumentException("잘못된 http요청입니다.");
        }
    }
}
