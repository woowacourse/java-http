package org.apache.coyote.http.util;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.request.HttpRequestBody;
import org.apache.coyote.http.request.HttpRequestHeaders;
import org.apache.coyote.http.request.Parameters;
import org.apache.coyote.http.request.Request;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.util.exception.InvalidRequestBodyException;

public class RequestGenerator {

    private static final int INVALID_READ_COUNT = -1;

    public Request generate(final BufferedReader bufferedReader) throws IOException {
        final String requestLineContent = bufferedReader.readLine();
        final RequestLine requestLine = RequestLine.from(requestLineContent);
        final HttpRequestHeaders headers = readRequestHeaders(bufferedReader);
        final HttpRequestBody body = readRequestBody(bufferedReader, headers);
        final Parameters parameters = readQueryParameters(requestLine, body);
        final HttpCookie cookie = convertCookie(headers);

        return new Request(headers, requestLine, body, parameters, cookie);
    }

    private HttpRequestHeaders readRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final StringBuilder requestHeaderBuilder = new StringBuilder();

        for (String line = bufferedReader.readLine(); !HttpConsts.BLANK.equals(line); line = bufferedReader.readLine()) {
            requestHeaderBuilder.append(line)
                                .append(HttpConsts.CRLF);
        }

        return HttpRequestHeaders.from(requestHeaderBuilder.toString());
    }

    private HttpRequestBody readRequestBody(
            final BufferedReader bufferedReader,
            final HttpRequestHeaders headers
    ) throws IOException {
        if (headers.isRequestBodyEmpty()) {
            return HttpRequestBody.EMPTY;
        }

        final int contentLength = Integer.parseInt(headers.findValue(HttpHeaderConsts.CONTENT_LENGTH));

        if (contentLength == 0) {
            return HttpRequestBody.EMPTY;
        }

        char[] bodyBuffer = new char[contentLength];
        final int result = bufferedReader.read(bodyBuffer, 0, contentLength);

        if (result == INVALID_READ_COUNT) {
            throw new InvalidRequestBodyException();
        }

        final String bodyContent = new String(bodyBuffer);

        return new HttpRequestBody(bodyContent);
    }

    private Parameters readQueryParameters(final RequestLine requestLine, final HttpRequestBody body) {
        final String content = requestLine.url().url();
        final Parameters parameters = Parameters.fromUrlContent(content);

        if (parameters != Parameters.EMPTY || body.isEmpty()) {
            return parameters;
        }

        return Parameters.fromBodyContent(body.body());
    }

    private HttpCookie convertCookie(final HttpRequestHeaders headers) {
        final String cookieValue = headers.findValue(HttpHeaderConsts.COOKIE);

        if (cookieValue == null) {
            return HttpCookie.EMPTY;
        }

        return HttpCookie.fromCookieHeaderValue(cookieValue);
    }
}
